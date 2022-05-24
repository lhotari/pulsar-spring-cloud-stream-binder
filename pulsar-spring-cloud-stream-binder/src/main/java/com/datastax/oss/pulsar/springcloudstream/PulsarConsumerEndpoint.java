/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.oss.pulsar.springcloudstream;

import com.datastax.oss.pulsar.springcloudstream.properties.PulsarConsumerProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.SchemaSpec;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.ConsumerBuilderImpl;
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.core.Pausable;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.ReflectionUtils;

class PulsarConsumerEndpoint implements MessageProducer, Pausable {
	private final Consumer<Object> pulsarConsumer;
	private MessageChannel outputChannel;

	private static final Field CONF_FIELD = ReflectionUtils.findField(
			ConsumerBuilderImpl.class, "conf", ConsumerConfigurationData.class);
	static {
		ReflectionUtils.makeAccessible(CONF_FIELD);
	}

	private volatile boolean running;

	private final SchemaSpec schemaSpec;

	private final String contentType;

	PulsarConsumerEndpoint(PulsarClient pulsarClient, ConsumerDestination destination,
			String group,
			ExtendedConsumerProperties<PulsarConsumerProperties> properties) {
		try {
			schemaSpec = properties.getExtension().getSchema();
			contentType = properties.getExtension().getContentType();
			ConsumerBuilder<Object> consumerBuilder = pulsarClient
					.newConsumer((Schema<Object>) schemaSpec.asPulsarSchema());
			ConsumerConfigurationData consumerProperties = properties.getExtension()
				.clone();
			// Use reflection since the Pulsar API doesn't have a public way to apply the
			// configuration object
			ReflectionUtils.setField(CONF_FIELD, consumerBuilder, consumerProperties);
			consumerBuilder.topic(destination.getName())
					.messageListener(this::consumeMessage);
			if (consumerProperties.getSubscriptionName() == null) {
				if (group == null || group.isBlank()) {
					consumerBuilder.subscriptionName("anonymous");
				}
				else {
					consumerBuilder.subscriptionName(group);
				}
			}
			pulsarConsumer = consumerBuilder.subscribe();
		}
		catch (PulsarClientException e) {
			throw new RuntimeException(e);
		}
	}

	private void consumeMessage(Consumer<Object> consumer,
			org.apache.pulsar.client.api.Message<Object> message) {
		HashMap<String, Object> headers = new HashMap<>(message.getProperties());
		if (contentType != null) {
			headers.put("contentType", contentType);
		}
		GenericMessage<Object> msg = new GenericMessage<>(message.getValue(), headers);
		outputChannel.send(msg);
		try {
			consumer.acknowledge(message);
		}
		catch (PulsarClientException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setOutputChannel(MessageChannel outputChannel) {
		this.outputChannel = outputChannel;
	}

	@Override
	public MessageChannel getOutputChannel() {
		return outputChannel;
	}

	@Override
	public void pause() {
		pulsarConsumer.pause();
	}

	@Override
	public void resume() {
		pulsarConsumer.resume();
	}

	@Override
	public void start() {
		running = true;
		pulsarConsumer.resume();
	}

	@Override
	public void stop() {
		try {
			running = false;
			pulsarConsumer.close();
		}
		catch (PulsarClientException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}
}

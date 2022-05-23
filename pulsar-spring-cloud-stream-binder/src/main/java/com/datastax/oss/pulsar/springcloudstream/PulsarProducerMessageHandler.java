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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.ProducerBuilderImpl;
import org.apache.pulsar.client.impl.conf.ProducerConfigurationData;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.context.Lifecycle;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.util.ReflectionUtils;

import com.datastax.oss.pulsar.springcloudstream.properties.PulsarProducerProperties;

class PulsarProducerMessageHandler implements MessageHandler, Lifecycle {
	private static final Field CONF_FIELD = ReflectionUtils.findField(
			ProducerBuilderImpl.class, "conf", ProducerConfigurationData.class);
	static {
		ReflectionUtils.makeAccessible(CONF_FIELD);
	}

	private final Producer<byte[]> pulsarProducer;
	private volatile boolean running;

	public PulsarProducerMessageHandler(PulsarClient pulsarClient,
			ProducerDestination destination,
			ExtendedProducerProperties<PulsarProducerProperties> producerProperties,
			MessageChannel errorChannel) {
		try {
			ProducerBuilder<byte[]> producerBuilder = pulsarClient.newProducer();
			// Use reflection since the Pulsar API doesn't have a public way to apply the
			// configuration object
			ReflectionUtils.setField(CONF_FIELD, producerBuilder,
					producerProperties.getExtension().clone());
			pulsarProducer = producerBuilder.topic(destination.getName()).create();
		}
		catch (PulsarClientException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		try {
			pulsarProducer.newMessage()
					// support just byte[] for now
					.value((byte[]) message.getPayload())
					// map headers to Map<String, String>
					.properties(message.getHeaders().entrySet().stream()
							.map(entry -> Map.entry(entry.getKey(),
									entry.getValue() != null
											? String.valueOf(entry.getValue())
											: null))
							.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
									Map.Entry::getValue)))
					// TODO - sending will be slow if it's always synchronous. Is there a way to enable pipelining?
					.send();
		}
		catch (PulsarClientException e) {
			throw new MessageDeliveryException(message, e);
		}
	}

	@Override
	public void start() {
		running = true;
	}

	@Override
	public void stop() {
		running = false;
		try {
			pulsarProducer.close();
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

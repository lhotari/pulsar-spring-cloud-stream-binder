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

import java.util.Arrays;

import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.datastax.oss.pulsar.springcloudstream.properties.PulsarBinderConfigurationProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarConsumerProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarExtendedBindingProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarProducerProperties;
import com.datastax.oss.pulsar.springcloudstream.provisioning.PulsarTopicProvisioner;

/**
 *
 * The Spring Cloud Stream Binder implementation for Apache Pulsar.
 *
 * @author Lari Hotari
 *
 */
public class PulsarMessageChannelBinder extends
		AbstractMessageChannelBinder<ExtendedConsumerProperties<PulsarConsumerProperties>, ExtendedProducerProperties<PulsarProducerProperties>, PulsarTopicProvisioner>
		implements
		ExtendedPropertiesBinder<MessageChannel, PulsarConsumerProperties, PulsarProducerProperties> {

	private final PulsarBinderConfigurationProperties configurationProperties;
	private final PulsarClient pulsarClient;

	private PulsarExtendedBindingProperties extendedBindingProperties = new PulsarExtendedBindingProperties();

	public PulsarMessageChannelBinder(
			PulsarBinderConfigurationProperties configurationProperties,
			PulsarTopicProvisioner provisioningProvider, PulsarClient pulsarClient) {

		super(headersToMap(configurationProperties), provisioningProvider);
		this.configurationProperties = configurationProperties;
		this.pulsarClient = pulsarClient;
	}

	public void setExtendedBindingProperties(
			PulsarExtendedBindingProperties extendedBindingProperties) {
		this.extendedBindingProperties = extendedBindingProperties;
	}

	@Override
	public PulsarConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public PulsarProducerProperties getExtendedProducerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
	}

	@Override
	public String getDefaultsPrefix() {
		return this.extendedBindingProperties.getDefaultsPrefix();
	}

	@Override
	public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
		return this.extendedBindingProperties.getExtendedPropertiesEntryClass();
	}

	private static String[] headersToMap(
			PulsarBinderConfigurationProperties configurationProperties) {
		Assert.notNull(configurationProperties,
				"'configurationProperties' must not be null");
		if (ObjectUtils.isEmpty(configurationProperties.getHeaders())) {
			return BinderHeaders.STANDARD_HEADERS;
		}
		else {
			String[] combinedHeadersToMap = Arrays.copyOfRange(
					BinderHeaders.STANDARD_HEADERS, 0,
					BinderHeaders.STANDARD_HEADERS.length
							+ configurationProperties.getHeaders().length);
			System.arraycopy(configurationProperties.getHeaders(), 0,
					combinedHeadersToMap, BinderHeaders.STANDARD_HEADERS.length,
					configurationProperties.getHeaders().length);
			return combinedHeadersToMap;
		}
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ExtendedProducerProperties<PulsarProducerProperties> producerProperties,
			MessageChannel errorChannel) throws Exception {

		return new PulsarProducerMessageHandler(pulsarClient, destination,
				producerProperties, errorChannel);
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination,
			String group,
			ExtendedConsumerProperties<PulsarConsumerProperties> properties) {
		return new PulsarConsumerEndpoint(pulsarClient, destination, group, properties);
	}

}

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

package com.datastax.oss.pulsar.springcloudstream.provisioning;

import com.datastax.oss.pulsar.springcloudstream.properties.PulsarBinderConfigurationProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarConsumerProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarProducerProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;

/**
 * The {@link ProvisioningProvider} implementation for Apache Pulsar.
 *
 * @author Lari Hotari
 */
public class PulsarTopicProvisioner implements
		ProvisioningProvider<ExtendedConsumerProperties<PulsarConsumerProperties>,
				ExtendedProducerProperties<PulsarProducerProperties>> {

	private static final Log logger = LogFactory.getLog(PulsarTopicProvisioner.class);

	private final PulsarBinderConfigurationProperties configurationProperties;

	public PulsarTopicProvisioner(
			PulsarBinderConfigurationProperties pulsarBinderConfigurationProperties) {
		this.configurationProperties = pulsarBinderConfigurationProperties;
	}

	@Override
	public ProducerDestination provisionProducerDestination(String name,
			ExtendedProducerProperties<PulsarProducerProperties> properties)
			throws ProvisioningException {

		if (logger.isInfoEnabled()) {
			logger.info("Using Pulsar topic for outbound: " + name);
		}

		return new PulsarProducerDestination(name);
	}

	@Override
	public ConsumerDestination provisionConsumerDestination(String name, String group,
			ExtendedConsumerProperties<PulsarConsumerProperties> properties)
			throws ProvisioningException {

		if (logger.isInfoEnabled()) {
			logger.info("Using Pulsar topic for inbound: " + name);
		}

		return new PulsarConsumerDestination(name);
	}
}

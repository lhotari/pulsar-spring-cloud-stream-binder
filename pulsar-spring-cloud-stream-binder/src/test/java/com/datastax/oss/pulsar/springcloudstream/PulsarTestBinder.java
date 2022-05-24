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

import com.datastax.oss.pulsar.springcloudstream.properties.PulsarBinderConfigurationProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarConsumerProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarExtendedBindingProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarProducerProperties;
import com.datastax.oss.pulsar.springcloudstream.provisioning.PulsarTopicProvisioner;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.cloud.stream.binder.AbstractTestBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.PartitionTestSupport;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.config.EnableIntegration;

/**
 * An {@link AbstractTestBinder} implementation for the
 * {@link PulsarMessageChannelBinder}.
 *
 * @author Lari Hotari
 */
public class PulsarTestBinder extends
		AbstractTestBinder<PulsarMessageChannelBinder, ExtendedConsumerProperties<PulsarConsumerProperties>, ExtendedProducerProperties<PulsarProducerProperties>> {

	private final GenericApplicationContext applicationContext;
	private final PulsarClient pulsarClient;

	public PulsarTestBinder(PulsarClient pulsarClient,
			PulsarBinderConfigurationProperties pulsarBinderConfigurationProperties) {
		this.pulsarClient = pulsarClient;

		this.applicationContext = new AnnotationConfigApplicationContext(Config.class);

		PulsarTopicProvisioner provisioningProvider = new PulsarTopicProvisioner(
				pulsarBinderConfigurationProperties);

		PulsarMessageChannelBinder binder = new TestPulsarMessageChannelBinder(
				pulsarClient, pulsarBinderConfigurationProperties, provisioningProvider);

		binder.setApplicationContext(this.applicationContext);

		setBinder(binder);
	}

	public GenericApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	@Override
	public void cleanup() {

	}

	/**
	 * Test configuration.
	 */
	@Configuration
	@EnableIntegration
	static class Config {

		@Bean
		public PartitionTestSupport partitionSupport() {
			return new PartitionTestSupport();
		}

	}

	private static class TestPulsarMessageChannelBinder
			extends PulsarMessageChannelBinder {

		TestPulsarMessageChannelBinder(PulsarClient pulsarClient,
				PulsarBinderConfigurationProperties pulsarBinderConfigurationProperties,
				PulsarTopicProvisioner provisioningProvider) {

			super(pulsarBinderConfigurationProperties, provisioningProvider,
					pulsarClient, new PulsarExtendedBindingProperties());
		}

		/*
		 * Some tests use multiple instance indexes for the same topic; we need to make
		 * the error infrastructure beans unique.
		 */
		@Override
		protected String errorsBaseName(ConsumerDestination destination, String group,
				ExtendedConsumerProperties<PulsarConsumerProperties> consumerProperties) {
			return super.errorsBaseName(destination, group, consumerProperties) + "-"
					+ consumerProperties.getInstanceIndex();
		}
	}
}

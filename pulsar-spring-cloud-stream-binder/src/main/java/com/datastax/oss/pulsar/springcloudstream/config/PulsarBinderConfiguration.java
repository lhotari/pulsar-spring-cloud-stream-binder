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

package com.datastax.oss.pulsar.springcloudstream.config;

import com.datastax.oss.pulsar.springcloudstream.properties.PulsarBinderConfigurationProperties;
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarExtendedBindingProperties;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.Binder;
import com.datastax.oss.pulsar.springcloudstream.PulsarMessageChannelBinder;
import com.datastax.oss.pulsar.springcloudstream.provisioning.PulsarTopicProvisioner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The auto-configuration for Apache Pulsar components and Spring Cloud Stream Pulsar Binder.
 *
 * @author Lari Hotari
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(Binder.class)
@EnableConfigurationProperties({ PulsarBinderConfigurationProperties.class, PulsarExtendedBindingProperties.class})
@Import(PulsarClientAutoConfiguration.class)
public class PulsarBinderConfiguration {

	private final PulsarBinderConfigurationProperties configurationProperties;

	public PulsarBinderConfiguration(PulsarBinderConfigurationProperties configurationProperties) {

		this.configurationProperties = configurationProperties;
	}

	@Bean
	public PulsarTopicProvisioner pulsarTopicProvisioner() {
		return new PulsarTopicProvisioner(configurationProperties);
	}

	@Bean
	public PulsarMessageChannelBinder pulsarMessageChannelBinder(
			PulsarClient pulsarClient,
			PulsarTopicProvisioner provisioningProvider,
			PulsarExtendedBindingProperties pulsarExtendedBindingProperties) {

		PulsarMessageChannelBinder pulsarMessageChannelBinder =
				new PulsarMessageChannelBinder(this.configurationProperties, provisioningProvider, pulsarClient);
		return pulsarMessageChannelBinder;
	}
}

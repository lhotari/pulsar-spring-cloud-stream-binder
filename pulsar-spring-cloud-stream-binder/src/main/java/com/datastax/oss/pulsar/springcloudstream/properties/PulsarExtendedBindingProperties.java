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

package com.datastax.oss.pulsar.springcloudstream.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;

/**
 * The extended Pulsar-specific binding configuration properties.
 *
 * @author Lari Hotari
 *
 */
@ConfigurationProperties("pulsar.spring.cloud.stream")
public class PulsarExtendedBindingProperties implements
		ExtendedBindingProperties<PulsarConsumerProperties, PulsarProducerProperties> {

	private static final String DEFAULTS_PREFIX = "pulsar.spring.cloud.stream.default";

	private Map<String, PulsarBindingProperties> bindings = new HashMap<>();

	public Map<String, PulsarBindingProperties> getBindings() {
		return this.bindings;
	}

	public void setBindings(Map<String, PulsarBindingProperties> bindings) {
		this.bindings = bindings;
	}

	@Override
	public PulsarConsumerProperties getExtendedConsumerProperties(String channelName) {
		if (this.bindings.containsKey(channelName)
				&& this.bindings.get(channelName).getConsumer() != null) {
			return this.bindings.get(channelName).getConsumer();
		}
		else {
			return new PulsarConsumerProperties();
		}
	}

	@Override
	public PulsarProducerProperties getExtendedProducerProperties(String channelName) {
		if (this.bindings.containsKey(channelName)
				&& this.bindings.get(channelName).getProducer() != null) {
			return this.bindings.get(channelName).getProducer();
		}
		else {
			return new PulsarProducerProperties();
		}
	}

	@Override
	public String getDefaultsPrefix() {
		return DEFAULTS_PREFIX;
	}

	@Override
	public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
		return PulsarBindingProperties.class;
	}

}

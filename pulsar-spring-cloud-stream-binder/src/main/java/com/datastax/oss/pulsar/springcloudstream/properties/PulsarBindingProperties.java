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

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * The Pulsar-specific binding configuration properties.
 *
 * @author Lari Hotari
 */
public class PulsarBindingProperties implements BinderSpecificPropertiesProvider {

	private PulsarConsumerProperties consumer = new PulsarConsumerProperties();

	private PulsarProducerProperties producer = new PulsarProducerProperties();

	public PulsarConsumerProperties getConsumer() {
		return this.consumer;
	}

	public void setConsumer(PulsarConsumerProperties consumer) {
		this.consumer = consumer;
	}

	public PulsarProducerProperties getProducer() {
		return this.producer;
	}

	public void setProducer(PulsarProducerProperties producer) {
		this.producer = producer;
	}

}

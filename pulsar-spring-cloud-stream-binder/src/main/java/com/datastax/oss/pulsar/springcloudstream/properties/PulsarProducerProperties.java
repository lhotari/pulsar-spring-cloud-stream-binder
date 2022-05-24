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

import org.apache.pulsar.client.impl.conf.ProducerConfigurationData;

/**
 * The Pulsar-specific producer binding configuration properties.
 *
 * @author Lari Hotari
 *
 */
public class PulsarProducerProperties extends ProducerConfigurationData {
	private boolean useSendAsync;
	private SchemaSpec schema = new SchemaSpec();
	public PulsarProducerProperties() {
		setBlockIfQueueFull(true);
	}

	public boolean isUseSendAsync() {
		return useSendAsync;
	}

	public void setUseSendAsync(boolean useSendAsync) {
		this.useSendAsync = useSendAsync;
	}

	public SchemaSpec getSchema() {
		return schema;
	}

	public void setSchema(SchemaSpec schema) {
		this.schema = schema;
	}
}

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

import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData;

/**
 * The Pulsar-specific consumer binding configuration properties.
 *
 * @author Lari Hotari
 *
 */
public class PulsarConsumerProperties extends ConsumerConfigurationData<Object> {
	private SchemaSpec schema = new SchemaSpec();
	private String contentType;

	public PulsarConsumerProperties() {
		setStartPaused(true);
		setSubscriptionInitialPosition(SubscriptionInitialPosition.Earliest);
		setSubscriptionType(SubscriptionType.Shared);
	}

	public SchemaSpec getSchema() {
		return schema;
	}

	public void setSchema(SchemaSpec schema) {
		this.schema = schema;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}

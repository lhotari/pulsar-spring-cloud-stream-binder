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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The Pulsar Binder specific configuration properties.
 *
 * @author Lari Hotari
 */
@ConfigurationProperties(prefix = "pulsar.spring.cloud.stream.binder")
public class PulsarBinderConfigurationProperties {
	private String[] headers = new String[] { };

	public String[] getHeaders() {
		return this.headers;
	}

	public void setHeaders(String... headers) {
		this.headers = headers;
	}
}

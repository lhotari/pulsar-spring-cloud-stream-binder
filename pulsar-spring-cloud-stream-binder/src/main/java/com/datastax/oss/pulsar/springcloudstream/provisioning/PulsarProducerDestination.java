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

import org.springframework.cloud.stream.provisioning.ProducerDestination;

/**
 * The Pulsar-specific {@link ProducerDestination} implementation.
 *
 * @author Lari Hotari
 *
 */
public final class PulsarProducerDestination implements ProducerDestination {

	private final String topicName;

	PulsarProducerDestination(String topicName) {
		this.topicName = topicName;
	}

	@Override
	public String getName() {
		return this.topicName;
	}

	@Override
	public String getNameForPartition(int partition) {
		return this.topicName + "-partition-" + partition;
	}

}

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

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PulsarContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * @author Lari Hotari
 */
@Testcontainers(disabledWithoutDocker = true)
public interface PulsarContainerTest {

	@Container
	PulsarContainer pulsarContainer = new PulsarContainer(
			DockerImageName.parse("apachepulsar/pulsar:2.10.0"));

	static PulsarClient pulsarClient() throws PulsarClientException {
		PulsarClient pulsarClient = PulsarClient.builder()
				.serviceUrl(pulsarContainer.getPulsarBrokerUrl()).build();
		return pulsarClient;
	}

	public static void register(DynamicPropertyRegistry registry) {
		registry.add("pulsar.client.serviceUrl", pulsarContainer::getPulsarBrokerUrl);
		// TODO: this property is currently unused
		registry.add("pulsar.admin.serviceHttpUrl", pulsarContainer::getHttpServiceUrl);
	}
}

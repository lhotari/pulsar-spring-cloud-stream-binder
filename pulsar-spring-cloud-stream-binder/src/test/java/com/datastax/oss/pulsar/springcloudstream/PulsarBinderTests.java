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
import com.datastax.oss.pulsar.springcloudstream.properties.PulsarProducerProperties;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInfo;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.PartitionCapableBinderTests;
import org.springframework.cloud.stream.binder.Spy;
import org.springframework.expression.common.LiteralExpression;

/**
 * The tests for Pulsar Binder.
 *
 * @author Lari Hotari
 */
public class PulsarBinderTests extends
		PartitionCapableBinderTests<PulsarTestBinder, ExtendedConsumerProperties<PulsarConsumerProperties>, ExtendedProducerProperties<PulsarProducerProperties>>
		implements PulsarContainerTest {

	private static final String CLASS_UNDER_TEST_NAME = PulsarBinderTests.class
			.getSimpleName();

	private static PulsarClient PULSAR_CLIENT;

	public PulsarBinderTests() {
		this.timeoutMultiplier = 10D;
	}

	@BeforeAll
	public static void setup() throws PulsarClientException {
		PULSAR_CLIENT = PulsarContainerTest.pulsarClient();
	}

	@AfterAll
	public static void closePulsarClient() throws PulsarClientException {
		PULSAR_CLIENT.close();
	}

	@Override
	protected boolean usesExplicitRouting() {
		return false;
	}

	@Override
	protected String getClassUnderTestName() {
		return CLASS_UNDER_TEST_NAME;
	}

	@Override
	protected PulsarTestBinder getBinder() {
		return getBinder(new PulsarBinderConfigurationProperties());
	}

	private PulsarTestBinder getBinder(
			PulsarBinderConfigurationProperties pulsarBinderConfigurationProperties) {
		if (this.testBinder == null) {
			this.testBinder = new PulsarTestBinder(PULSAR_CLIENT,
					pulsarBinderConfigurationProperties);
			this.timeoutMultiplier = 20;
		}
		return this.testBinder;
	}

	@Override
	protected ExtendedConsumerProperties<PulsarConsumerProperties> createConsumerProperties() {
		ExtendedConsumerProperties<PulsarConsumerProperties> pulsarConsumerProperties = new ExtendedConsumerProperties<>(
				new PulsarConsumerProperties());
		// set the default values that would normally be propagated by Spring Cloud Stream
		pulsarConsumerProperties.setInstanceCount(1);
		pulsarConsumerProperties.setInstanceIndex(0);
		return pulsarConsumerProperties;
	}

	private ExtendedProducerProperties<PulsarProducerProperties> createProducerProperties() {
		return this.createProducerProperties(null);
	}

	@Override
	protected ExtendedProducerProperties<PulsarProducerProperties> createProducerProperties(
			TestInfo testInto) {
		ExtendedProducerProperties<PulsarProducerProperties> producerProperties = new ExtendedProducerProperties<>(
				new PulsarProducerProperties());
		producerProperties.setPartitionKeyExpression(new LiteralExpression("1"));
		return producerProperties;
	}

	@Override
	public Spy spyOn(String name) {
		throw new UnsupportedOperationException("'spyOn' is not used by Pulsar tests");
	}

	@Override
	@Disabled
	public void testAnonymousGroup(TestInfo testInfo) throws Exception {
		// this doesn't make sense in Pulsar because of the receive queue that consumes
		// messages
	}

	@Override
	@Disabled
	public void testClean(TestInfo testInfo) {
	}

	@Override
	@Disabled
	public void testPartitionedModuleSpEL(TestInfo testInfo) {

	}
}

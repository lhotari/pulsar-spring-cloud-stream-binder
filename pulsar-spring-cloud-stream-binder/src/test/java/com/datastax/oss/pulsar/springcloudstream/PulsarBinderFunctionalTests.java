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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.MimeType;

/**
 * @author Lari Hotari
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
		"spring.cloud.stream.bindings.myEventConsumer-in-0.destination="
				+ PulsarBinderFunctionalTests.PULSAR_TOPIC,
		"spring.cloud.stream.bindings.myEventProducer-out-0.destination="
				+ PulsarBinderFunctionalTests.PULSAR_TOPIC,
		"pulsar.spring.cloud.stream.binder.headers = event.eventType" })
@DirtiesContext
public class PulsarBinderFunctionalTests implements PulsarContainerTest {
	@DynamicPropertySource
	static void registerPulsarProperties(DynamicPropertyRegistry registry) {
		PulsarContainerTest.register(registry);
	}

	static final String PULSAR_TOPIC = "test_topic";
	public static final int NUMBER_OF_MESSAGES = 10;

	@Autowired
	private CountDownLatch messageBarrier;

	@Autowired
	private BlockingQueue<Message<?>> receivedMessages;

	@Autowired
	private PulsarClient pulsarClient;

	@Autowired
	private StreamBridge streamBridge;

	@Test
	void testSendingAndReceivingMessages()
			throws InterruptedException, PulsarClientException {

		// Send test messages
		for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
			streamBridge.send("myEventProducer-out-0",
					MessageBuilder.withPayload("Message" + i)
							.setHeader("event.eventType", "createEvent").build(),
					MimeType.valueOf("text/plain"));
		}

		assertThat(this.messageBarrier.await(10, TimeUnit.SECONDS)).isTrue();

		List<Message<?>> messages = receivedMessages.stream().toList();

		for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
			Object item = messages.get(i);
			assertThat(item).isInstanceOf(GenericMessage.class);
			Message<?> message = (Message<?>) item;
			assertThat(message.getPayload()).isEqualTo("Message" + i);
			assertThat(message.getHeaders()).containsEntry("event.eventType",
					"createEvent");
		}
	}

	@Configuration
	@EnableAutoConfiguration
	static class TestConfiguration {

		@Bean
		public BlockingQueue<Message<?>> receivedMessages() {
			return new ArrayBlockingQueue<>(NUMBER_OF_MESSAGES);
		}

		@Bean
		public CountDownLatch messageBarrier() {
			return new CountDownLatch(NUMBER_OF_MESSAGES);
		}

		@Bean
		public Consumer<Message<?>> myEventConsumer() {
			return message -> {
				receivedMessages().add(message);
				messageBarrier().countDown();
			};
		}
	}

}

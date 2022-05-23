package com.datastax.oss.pulsar.springcloudstream.config;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.ClientBuilderImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PulsarClientConfigurationProperties.class)
public class PulsarClientAutoConfiguration {
	@Bean
	@Lazy
	@ConditionalOnMissingBean
	PulsarClient pulsarClient(
			PulsarClientConfigurationProperties pulsarClientConfigurationProperties)
			throws PulsarClientException {
		return new ClientBuilderImpl(pulsarClientConfigurationProperties).build();
	}
}

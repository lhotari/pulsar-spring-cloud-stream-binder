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

package com.datastax.oss.pulsar.springcloudstream.config;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pulsar.client")
public class PulsarClientConfigurationProperties extends ClientConfigurationData implements InitializingBean {

    public PulsarClientConfigurationProperties() {
        setServiceUrl("pulsar://localhost:6650/");
    }

    void setIoThreads(int ioThreads) {
        setNumIoThreads(ioThreads);
    }

    void setListenerThreads(int listenerThreads) {
        setNumListenerThreads(listenerThreads);
    }

    void setMaxLookupRequests(int maxLookupRequests) {
        setMaxLookupRequest(maxLookupRequests);
    }

    void setMaxConcurrentLookupRequests(int maxConcurrentLookupRequests) {
        setConcurrentLookupRequest(maxConcurrentLookupRequests);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getAuthPluginClassName() != null) {
            if (getAuthParams() != null) {
                setAuthentication(AuthenticationFactory.create(getAuthPluginClassName(), getAuthParams()));
            } else if (getAuthParamMap() != null) {
                setAuthentication(AuthenticationFactory.create(getAuthPluginClassName(), getAuthParamMap()));
            }
        }
    }
}

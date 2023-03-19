/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataprovider.kafka.model;

import java.util.Optional;

import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;

/**
 * Request that contains the information required to retrieve metrics.
 *
 */
public class KafkaMetricsRequest {

    MetricsTarget metricsTarget;

    private String host;
    private String rmiPort;

    String filter;
    String clientId;
    String nodeId;
    String topic;
    String partition;

    public static class Builder {

        KafkaMetricsRequest request;

        public static Builder newBuilder(String host, String rmiPort) {
            Builder builder = new Builder();
            builder.request = new KafkaMetricsRequest(host, rmiPort);
            return builder;
        }

        public static Builder newConsumerBuilder(String host, String rmiPort, String clientId) {
            return newBuilder(host, rmiPort).target(MetricsTarget.CONSUMER).clientId(clientId);
        }

        public static Builder newProducerBuilder(String host, String rmiPort, String clientId) {
            return newBuilder(host, rmiPort).target(MetricsTarget.PRODUCER).clientId(clientId);
        }

        public Builder target(MetricsTarget target) {
            this.request.metricsTarget = target;
            return this;
        }

        public Builder filter(String filter) {
            this.request.filter = filter;
            return this;
        }

        public Builder clientId(String clientId) {
            this.request.clientId = clientId;
            return this;
        }

        public Builder nodeId(String nodeId) {
            this.request.nodeId = nodeId;
            return this;
        }

        public Builder topic(String topic) {
            this.request.topic = topic;
            return this;
        }

        public Builder partition(String partition) {
            this.request.partition = partition;
            return this;
        }

        public KafkaMetricsRequest build() {
            return this.request;
        }

    }

    public KafkaMetricsRequest(String host, String rmiPort) {
        this.host = host;
        this.rmiPort = rmiPort;
        this.metricsTarget = MetricsTarget.BROKER;
    }

    public MetricsTarget getMetricsTarget() {
        return metricsTarget;
    }

    public String getHost() {
        return host;
    }

    public String getRmiPort() {
        return rmiPort;
    }

    public Optional<String> clientId() {
        return Optional.ofNullable(clientId);
    }

    public Optional<String> nodeId() {
        return Optional.ofNullable(nodeId);
    }

    public Optional<String> topic() {
        return Optional.ofNullable(topic);
    }

    public Optional<String> partition() {
        return Optional.ofNullable(partition);
    }

    public Optional<String> filter() {
        return Optional.ofNullable(filter);
    }

    @Override
    public String toString() {
        return "KafkaMetricsRequest [metricsTarget=" + metricsTarget + ", host=" + host + ", rmiPort=" + rmiPort + ", filter=" + filter + ", clientId=" + clientId + ", nodeId=" + nodeId + ", topic=" + topic +
               ", partition=" + partition + "]";
    }

}
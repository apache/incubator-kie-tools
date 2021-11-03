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
package org.dashbuilder.dataprovider.kafka.metrics.group;

import java.util.Collections;
import java.util.List;

import org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions;
import org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype;
import org.dashbuilder.dataprovider.kafka.metrics.KafkaMetricCollector;
import org.dashbuilder.dataprovider.kafka.metrics.MBeanMetricCollector;
import org.dashbuilder.dataprovider.kafka.model.KafkaMetricsRequest;

import static org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype.withDomainAndType;

/**
 * Group of metrics for requests targeting Kafka Producer
 *
 */
class ProducerMetricsGroup implements MetricsCollectorGroup {

    private static final ObjectNamePrototype KAFKA_PRODUCER_TOPIC = withDomainAndType(MBeanDefinitions.KAFKA_PRODUCER_DOMAIN, "producer-topic-metrics");
    private static final ObjectNamePrototype KAFKA_PRODUCER_NODE = withDomainAndType(MBeanDefinitions.KAFKA_PRODUCER_DOMAIN, "producer-node-metrics,client-id");
    private static final ObjectNamePrototype KAFKA_PRODUCER = withDomainAndType(MBeanDefinitions.KAFKA_PRODUCER_DOMAIN, "producer-metrics");

    @Override
    public List<KafkaMetricCollector> getMetricsCollectors(KafkaMetricsRequest request) {
        String clientId = request.clientId().orElseThrow(() -> new IllegalArgumentException("Client Id is required to retrieve producer metrics."));

        if (request.topic().isPresent()) {
            String mbeanName = KAFKA_PRODUCER_TOPIC.copy()
                                                   .hyfenClientId(clientId)
                                                   .topic(request.topic().get())
                                                   .build();
            return Collections.singletonList(MBeanMetricCollector.metricCollector(mbeanName));
        }

        if (request.nodeId().isPresent()) {
            String mbeanName = KAFKA_PRODUCER_NODE.copy()
                                                  .hyfenClientId(clientId)
                                                  .hyfenNodeId(request.nodeId().get())
                                                  .build();
            return Collections.singletonList(MBeanMetricCollector.metricCollector(mbeanName));
        }

        String mbeanName = KAFKA_PRODUCER.copy()
                                         .hyfenClientId(clientId)
                                         .build();
        return Collections.singletonList(MBeanMetricCollector.metricCollector(mbeanName));
    }
    
}
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions;
import org.dashbuilder.dataprovider.kafka.mbean.ObjectNameBuilder;
import org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype;
import org.dashbuilder.dataprovider.kafka.metrics.KafkaMetricCollector;
import org.dashbuilder.dataprovider.kafka.model.KafkaMetricsRequest;

import static org.dashbuilder.dataprovider.kafka.mbean.MBeanNameFactory.withProducerConsumerClientId;
import static org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype.withDomainAndType;
import static org.dashbuilder.dataprovider.kafka.metrics.MBeanMetricCollector.metricCollector;

/**
 * Group of metrics for requests targeting Kafka Consumer
 *
 */
class ConsumerMetricsGroup implements MetricsCollectorGroup {

    private static final ObjectNamePrototype KAFKA_CONSUMER = withDomainAndType(MBeanDefinitions.KAFKA_CONSUMER_DOMAIN, "consumer-metrics");
    private static final ObjectNamePrototype KAFKA_CONSUMER_FETCH_MANAGER = withDomainAndType(MBeanDefinitions.KAFKA_CONSUMER_DOMAIN, "producer-node-metrics");
    private static final ObjectNamePrototype KAFKA_CONSUMER_COORDINATOR = withDomainAndType(MBeanDefinitions.KAFKA_CONSUMER_DOMAIN, "consumer-coordinator-metrics");

    private static final ObjectNamePrototype KAFKA_CONSUMER_NODE = withDomainAndType(MBeanDefinitions.KAFKA_CONSUMER_DOMAIN, "consumer-node-metrics");

    private static final ObjectNamePrototype KAFKA_CONSUMER_TOPIC_FETCH_MANAGER = withDomainAndType(MBeanDefinitions.KAFKA_CONSUMER_DOMAIN, "consumer-fetch-manager-metrics");

    @Override
    public List<KafkaMetricCollector> getMetricsCollectors(KafkaMetricsRequest request) {
        String clientId = request.clientId().orElseThrow(() -> new IllegalArgumentException("Client Id is required to retrieve consumer metrics."));

        if (request.nodeId().isPresent()) {
            String mbean = KAFKA_CONSUMER_NODE.copy()
                                              .hyfenClientId(clientId)
                                              .hyfenNodeId(request.nodeId().get())
                                              .build();
            return Collections.singletonList(metricCollector(mbean));
        }

        if (request.topic().isPresent()) {
            ObjectNameBuilder mbeanBuilder = KAFKA_CONSUMER_TOPIC_FETCH_MANAGER.copy()
                                                                               .hyfenClientId(clientId)
                                                                               .topic(request.topic().get());
            request.partition().ifPresent(mbeanBuilder::partition);
            return Collections.singletonList(metricCollector(mbeanBuilder.build()));
        }

        return Arrays.asList(metricCollector(withProducerConsumerClientId(KAFKA_CONSUMER, clientId)),
                             metricCollector(withProducerConsumerClientId(KAFKA_CONSUMER_FETCH_MANAGER, clientId)),
                             metricCollector(withProducerConsumerClientId(KAFKA_CONSUMER_COORDINATOR, clientId)));
    }

}

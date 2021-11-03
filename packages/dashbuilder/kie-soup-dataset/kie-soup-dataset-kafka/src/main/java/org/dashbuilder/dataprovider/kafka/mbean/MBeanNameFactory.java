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
package org.dashbuilder.dataprovider.kafka.mbean;

import org.dashbuilder.dataprovider.kafka.metrics.KafkaMetricCollector;

import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.TIME_MS_ATTRS;
import static org.dashbuilder.dataprovider.kafka.metrics.MBeanMetricCollector.metricCollector;

/**
 * Produce MBean metric collectors for monitoring Kafka
 *
 */
public class MBeanNameFactory {

    private MBeanNameFactory() {
        // do nothing
    }

    public static String withProducerConsumerClientId(ObjectNamePrototype prototype, String clientId) {
        return prototype.copy().hyfenClientId(clientId).build();
    }

    public static String withName(ObjectNamePrototype kafkaControllerType, String name) {
        return kafkaControllerType.copy().name(name).build();
    }

    public static KafkaMetricCollector[] withProduceFetchConsumerAndFetchFollowerRequest(ObjectNamePrototype kafkaControllerType) {
        return new KafkaMetricCollector[]{
                                           metricCollector(kafkaControllerType.copy().request("Produce").build(), TIME_MS_ATTRS),
                                           metricCollector(kafkaControllerType.copy().request("FetchConsumer").build(), TIME_MS_ATTRS),
                                           metricCollector(kafkaControllerType.copy().request("FetchFollower").build(), TIME_MS_ATTRS),
        };
    }

    public static KafkaMetricCollector[] withProduceDelayedAndFetchDelayedOperation(ObjectNamePrototype kafkaControllerType) {
        return new KafkaMetricCollector[]{
                                           metricCollector(kafkaControllerType.copy().delayedOperation("Produce").build()),
                                           metricCollector(kafkaControllerType.copy().delayedOperation("Fetch").build())
        };
    }

}

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

import java.util.EnumMap;

import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;

/**
 * Factory for the specific metrics collector group according to a given request
 *
 */
public class MetricsCollectorGroupFactory {

    private static MetricsCollectorGroupFactory instance;

    private static EnumMap<MetricsTarget, MetricsCollectorGroup> groups = new EnumMap<>(MetricsTarget.class);

    static {
        instance = new MetricsCollectorGroupFactory();
        groups.put(MetricsTarget.BROKER, new BrokerMetricsGroup());
        groups.put(MetricsTarget.CONSUMER, new ConsumerMetricsGroup());
        groups.put(MetricsTarget.PRODUCER, new ProducerMetricsGroup());
    }

    MetricsCollectorGroupFactory() {
        // do nothing
    }

    public static MetricsCollectorGroupFactory get() {
        return instance;
    }

    public MetricsCollectorGroup forTarget(MetricsTarget target) {
        return groups.get(target);
    }

}
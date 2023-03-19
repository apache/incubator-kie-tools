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

/**
 * Line of collected metrics from Kafka
 *
 */
public class KafkaMetric {

    private String domain;
    private String type;
    private String name;
    private String attribute;
    private Object value;

    public static KafkaMetric from(String domain, String type, String name, String attribute, Object value) {
        KafkaMetric metric = new KafkaMetric();
        metric.domain = domain;
        metric.name = name;
        metric.attribute = attribute;
        metric.value = value;
        metric.type = type;
        return metric;
    }

    private KafkaMetric() {
        // do nothing
    }

    public String getDomain() {
        return domain;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KafkaMetric [domain=" + domain + ", type=" + type + ", name=" + name + ", attribute=" + attribute + ", value=" + value + "]";
    }

}
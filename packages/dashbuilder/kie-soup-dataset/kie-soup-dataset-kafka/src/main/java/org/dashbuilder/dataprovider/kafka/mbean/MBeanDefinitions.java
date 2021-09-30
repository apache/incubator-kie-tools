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

/**
 * Definitions for Kafka metrics MBeans
 *
 */
public final class MBeanDefinitions {

    // domains
    public static final String KAFKA_NETWORK_DOMAIN = "kafka.network";
    public static final String KAFKA_SERVER_DOMAIN = "kafka.server";
    public static final String KAFKA_CONTROLLER_DOMAIN = "kafka.controller";
    public static final String KAFKA_PRODUCER_DOMAIN = "kafka.producer";
    public static final String KAFKA_CONSUMER_DOMAIN = "kafka.consumer";

    // types 
    public static final String REQUEST_METRICS = "RequestMetrics";

    // Attributes
    public static final String[] PER_TIME_ATTRS = {"OneMinuteRate", "FifteenMinuteRate", "Count", "FiveMinuteRate", "MeanRate"};
    public static final String[] TIME_MS_ATTRS = {"75thPercentile", "StdDev", "Mean", "98thPercentile", "Min", "99thPercentile",
                                                  "95thPercentile", "Max", "999thPercentile", "Count", "50thPercentile"};

    private MBeanDefinitions() {
        // empty
    }

}

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

import java.util.List;

import org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype;
import org.dashbuilder.dataprovider.kafka.metrics.KafkaMetricCollector;
import org.dashbuilder.dataprovider.kafka.model.KafkaMetricsRequest;

import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.KAFKA_CONTROLLER_DOMAIN;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.KAFKA_NETWORK_DOMAIN;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.KAFKA_SERVER_DOMAIN;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.PER_TIME_ATTRS;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.REQUEST_METRICS;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanDefinitions.TIME_MS_ATTRS;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanNameFactory.withName;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanNameFactory.withProduceDelayedAndFetchDelayedOperation;
import static org.dashbuilder.dataprovider.kafka.mbean.MBeanNameFactory.withProduceFetchConsumerAndFetchFollowerRequest;
import static org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype.withDomainAndType;
import static org.dashbuilder.dataprovider.kafka.mbean.ObjectNamePrototype.withDomainTypeAndName;
import static org.dashbuilder.dataprovider.kafka.metrics.MBeanMetricCollector.metricCollector;
import static org.dashbuilder.dataprovider.kafka.metrics.group.MetricsCollectorGroup.merge;
import static org.dashbuilder.dataprovider.kafka.metrics.group.MetricsCollectorGroup.mergeAttrs;

/**
 * Group of metrics for requests targeting Kafka broker
 *
 */
class BrokerMetricsGroup implements MetricsCollectorGroup {

    // types prototypes
    private static final ObjectNamePrototype KAFKA_CONTROLLER_TYPE = withDomainAndType(KAFKA_CONTROLLER_DOMAIN, "KafkaController");
    private static final ObjectNamePrototype CONTROLLER_STATS_TYPE = withDomainAndType(KAFKA_CONTROLLER_DOMAIN, "ControllerStats");
    private static final ObjectNamePrototype KAFKA_REQUEST_HANDLER_POOL_TYPE = withDomainAndType(KAFKA_SERVER_DOMAIN, "KafkaRequestHandlerPool");
    private static final ObjectNamePrototype REQUEST_CHANNEL_TYPE = withDomainAndType(KAFKA_NETWORK_DOMAIN, "RequestChannel");
    private static final ObjectNamePrototype SOCKET_SERVER_TYPE = withDomainAndType(KAFKA_NETWORK_DOMAIN, "SocketServer");
    private static final ObjectNamePrototype REPLICA_MANAGER = withDomainAndType(KAFKA_SERVER_DOMAIN, "ReplicaManager");
    private static final ObjectNamePrototype REPLICA_FETCHER_MANAGER = withDomainAndType(KAFKA_SERVER_DOMAIN, "ReplicaFetcherManager");
    private static final ObjectNamePrototype BROKER_TOPIC_METRICS = withDomainAndType(KAFKA_SERVER_DOMAIN, "BrokerTopicMetrics");

    // types with name prototypes
    private static final ObjectNamePrototype REQUEST_METRICS_TOTAL_TIME_MS = withDomainTypeAndName(KAFKA_NETWORK_DOMAIN, REQUEST_METRICS, "TotalTimeMs");
    private static final ObjectNamePrototype REQUEST_METRICS_LOCAL_TIME_MS = withDomainTypeAndName(KAFKA_NETWORK_DOMAIN, REQUEST_METRICS, "LocalTimeMs");
    private static final ObjectNamePrototype REQUEST_METRICS_REMOTE_TIME_MS = withDomainTypeAndName(KAFKA_NETWORK_DOMAIN, REQUEST_METRICS, "RemoteTimeMs");
    private static final ObjectNamePrototype REQUEST_METRICS_RESPONSE_QUEUE_TIME_MS = withDomainTypeAndName(KAFKA_NETWORK_DOMAIN, REQUEST_METRICS, "ResponseQueueTimeMs");
    private static final ObjectNamePrototype REQUEST_METRICS_RESPONSE_SEND_TIME_MS = withDomainTypeAndName(KAFKA_NETWORK_DOMAIN, REQUEST_METRICS, "ResponseSendTimeMs");
    private static final ObjectNamePrototype REQUEST_METRICS_REQUEST_QUEUE_TIME_MS = withDomainTypeAndName(KAFKA_NETWORK_DOMAIN, REQUEST_METRICS, "RequestQueueTimeMs");
    private static final ObjectNamePrototype DELAYED_OPERATION_PURGATORY_PURGATORY_SIZE = withDomainTypeAndName(KAFKA_SERVER_DOMAIN, "DelayedOperationPurgatory", "PurgatorySize");

    private static final List<KafkaMetricCollector> COLLECTORS;

    static {

        KafkaMetricCollector[] collectors = {
                                              metricCollector(withName(KAFKA_CONTROLLER_TYPE, "GlobalPartitionCount")),
                                              metricCollector(withName(KAFKA_CONTROLLER_TYPE, "ActiveControllerCount")),
                                              metricCollector(withName(KAFKA_CONTROLLER_TYPE, "OfflinePartitionsCount")),

                                              metricCollector(withName(CONTROLLER_STATS_TYPE, "LeaderElectionRateAndTimeMs"),
                                                              mergeAttrs(TIME_MS_ATTRS, PER_TIME_ATTRS)),
                                              metricCollector(withName(CONTROLLER_STATS_TYPE, "UncleanLeaderElectionsPerSec"), PER_TIME_ATTRS),

                                              metricCollector(withName(REPLICA_MANAGER, "UnderReplicatedPartitions")),
                                              metricCollector(withName(REPLICA_MANAGER, "UnderMinIsrPartitionCount")),
                                              metricCollector(withName(REPLICA_MANAGER, "ReassigningPartitions")),
                                              metricCollector(withName(REPLICA_MANAGER, "IsrShrinksPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(REPLICA_MANAGER, "IsrExpandsPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(REPLICA_MANAGER, "PartitionCount")),
                                              metricCollector(withName(REPLICA_MANAGER, "LeaderCount")),

                                              metricCollector(withName(SOCKET_SERVER_TYPE, "NetworkProcessorAvgIdlePercent")),

                                              metricCollector(withName(BROKER_TOPIC_METRICS, "BytesInPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "BytesOutPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "TotalProduceRequestsPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "TotalFetchRequestsPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "FailedProduceRequestsPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "ReassignmentBytesInPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "ReassignmentBytesOutPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "MessagesInPerSec"), PER_TIME_ATTRS),
                                              metricCollector(withName(BROKER_TOPIC_METRICS, "FailedFetchRequestsPerSec"), PER_TIME_ATTRS),

                                              metricCollector(withName(REQUEST_CHANNEL_TYPE, "RequestQueueSize")),
                                              metricCollector(withName(REQUEST_CHANNEL_TYPE, "ResponseQueueSize")),

                                              metricCollector(withName(KAFKA_REQUEST_HANDLER_POOL_TYPE, "RequestHandlerAvgIdlePercent"), PER_TIME_ATTRS),
                                              metricCollector(REPLICA_FETCHER_MANAGER.copy().name("MaxLag").clientId("Replica").build()),

        };

        COLLECTORS = merge(collectors,
                           withProduceDelayedAndFetchDelayedOperation(DELAYED_OPERATION_PURGATORY_PURGATORY_SIZE),
                           withProduceFetchConsumerAndFetchFollowerRequest(REQUEST_METRICS_TOTAL_TIME_MS),
                           withProduceFetchConsumerAndFetchFollowerRequest(REQUEST_METRICS_REQUEST_QUEUE_TIME_MS),
                           withProduceFetchConsumerAndFetchFollowerRequest(REQUEST_METRICS_LOCAL_TIME_MS),
                           withProduceFetchConsumerAndFetchFollowerRequest(REQUEST_METRICS_REMOTE_TIME_MS),
                           withProduceFetchConsumerAndFetchFollowerRequest(REQUEST_METRICS_RESPONSE_QUEUE_TIME_MS),
                           withProduceFetchConsumerAndFetchFollowerRequest(REQUEST_METRICS_RESPONSE_SEND_TIME_MS));
    }

    @Override
    public List<KafkaMetricCollector> getMetricsCollectors(KafkaMetricsRequest request) {
        return COLLECTORS;
    }

}
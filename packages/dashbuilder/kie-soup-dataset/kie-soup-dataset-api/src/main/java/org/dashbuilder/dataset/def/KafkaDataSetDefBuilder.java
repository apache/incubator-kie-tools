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
package org.dashbuilder.dataset.def;

import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;

/**
 * A builder for defining Kafka data sets.
 *
 * <pre>
 *    DataSetDef dataSetDef = DataSetDefFactory.newKafkaDataSetDef()
 *     .host("localhost")
 *     .port(9999)   
 *     .buildDef();
 * </pre>
 */
public interface KafkaDataSetDefBuilder<T extends DataSetDefBuilder> extends DataSetDefBuilder<T> {

    /**
     * Set the data set kafka host
     *
     * @param host The Kafka server with JMX enabled where this data set should collect metrics from
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T host(String host);

    /**
     * Set the RMI port
     *
     * @param port The Kafka RMI port
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T port(String port);

    /**
     * Set the data set kafka metrics target
     *
     * @param host The metrics target
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T target(MetricsTarget target);

    /**
     * Set the data set client Id
     *
     * @param clientId Optional parameter to set a clientId to collect metrics
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T clientId(String clientId);

    /**
     * Set the data set filter
     *
     * @param clientId Optional parameter to set a clientId to collect metrics
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T filter(String filter);

    /**
     * Set the data set node id
     *
     * @param nodeId The node Id that can be used when collecting metrics
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T nodeId(String nodeId);

    /**
     * Set the data set topic
     *
     * @param topic The topic that will be used to collect metrics against it
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T topic(String topic);

    /**
     * Set the data set partition
     *
     * @param partition The partition that can be used to collect certain types of metrics.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T partition(String partition);

}
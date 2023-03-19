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
package org.dashbuilder.dataset.impl;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.KafkaDataSetDef;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;
import org.dashbuilder.dataset.def.KafkaDataSetDefBuilder;

public class KafkaDataSetDefBuilderImpl extends AbstractDataSetDefBuilder<KafkaDataSetDefBuilderImpl> implements KafkaDataSetDefBuilder<KafkaDataSetDefBuilderImpl> {

    @Override
    public KafkaDataSetDefBuilderImpl host(String host) {
        ((KafkaDataSetDef) super.def).setHost(host);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl port(String port) {
        ((KafkaDataSetDef) super.def).setPort(port);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl target(MetricsTarget target) {
        ((KafkaDataSetDef) super.def).setTarget(target);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl clientId(String clientId) {
        ((KafkaDataSetDef) super.def).setClientId(clientId);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl nodeId(String nodeId) {
        ((KafkaDataSetDef) super.def).setNodeId(nodeId);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl topic(String topic) {
        ((KafkaDataSetDef) super.def).setTopic(topic);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl partition(String partition) {
        ((KafkaDataSetDef) super.def).setPartition(partition);
        return this;
    }

    @Override
    public KafkaDataSetDefBuilderImpl filter(String filter) {
        ((KafkaDataSetDef) super.def).setFilter(filter);
        return null;
    }

    @Override
    protected DataSetDef createDataSetDef() {
        return new KafkaDataSetDef();
    }

}
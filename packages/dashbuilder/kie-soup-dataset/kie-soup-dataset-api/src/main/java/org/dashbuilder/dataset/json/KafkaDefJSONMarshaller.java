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
package org.dashbuilder.dataset.json;

import org.dashbuilder.dataset.def.KafkaDataSetDef;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.ALL_COLUMNS;
import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.transferStringValue;

public class KafkaDefJSONMarshaller implements DataSetDefJSONMarshallerExt<KafkaDataSetDef> {

    public static final KafkaDefJSONMarshaller INSTANCE = new KafkaDefJSONMarshaller();

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String TARGET = "target";
    public static final String FILTER = "filter";
    public static final String CLIENT_ID = "clientId";
    public static final String NODE_ID = "nodeId";
    public static final String TOPIC = "topic";
    public static final String PARTITION = "partition";

    @Override
    public void fromJson(KafkaDataSetDef def, JsonObject json) {
        transferStringValue(HOST, json, def::setHost);
        transferStringValue(PORT, json, def::setPort);
        transferStringValue(TARGET, json, v -> def.setTarget(MetricsTarget.valueOf(v)));
        transferStringValue(FILTER, json, def::setFilter);
        transferStringValue(CLIENT_ID, json, def::setClientId);
        transferStringValue(NODE_ID, json, def::setNodeId);
        transferStringValue(TOPIC, json, def::setTopic);
        transferStringValue(PARTITION, json, def::setPartition);
    }

    @Override
    public void toJson(KafkaDataSetDef def, JsonObject json) {
        json.put(HOST, def.getHost());
        json.put(PORT, def.getPort());
        json.put(TARGET, def.getTarget().name());
        json.put(FILTER, def.getFilter());
        json.put(CLIENT_ID, def.getClientId());
        json.put(NODE_ID, def.getNodeId());
        json.put(TOPIC, def.getTopic());
        json.put(PARTITION, def.getPartition());
        json.put(ALL_COLUMNS, def.isAllColumnsEnabled());
    }

}

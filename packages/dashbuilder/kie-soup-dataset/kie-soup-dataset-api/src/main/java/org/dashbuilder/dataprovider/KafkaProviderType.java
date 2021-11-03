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
package org.dashbuilder.dataprovider;

import org.dashbuilder.dataset.def.KafkaDataSetDef;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshallerExt;
import org.dashbuilder.dataset.json.KafkaDefJSONMarshaller;

/**
 * For accessing data sets that are the result of Kafka metrics collection
 */
public class KafkaProviderType extends AbstractProviderType<KafkaDataSetDef> {

    @Override
    public String getName() {
        return "Kafka";
    }

    @Override
    public KafkaDataSetDef createDataSetDef() {
        return new KafkaDataSetDef();
    }

    @Override
    public DataSetDefJSONMarshallerExt<KafkaDataSetDef> getJsonMarshaller() {
        return KafkaDefJSONMarshaller.INSTANCE;
    }
}
/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.StringJsonDeserializer;

public class NumCompletedJsonTypeDeserializer extends JsonbDeserializer<Object> {

    @Override
    public Object deserialize(JsonValue value, DeserializationContext ctx) {
        switch (value.getValueType()) {
            case STRING:
                return new StringJsonDeserializer().deserialize(value, ctx);
            case NUMBER:
                return new BaseNumberJsonDeserializer.IntegerJsonDeserializer().deserialize(value, ctx);
            default:
                return null;
        }
    }
}

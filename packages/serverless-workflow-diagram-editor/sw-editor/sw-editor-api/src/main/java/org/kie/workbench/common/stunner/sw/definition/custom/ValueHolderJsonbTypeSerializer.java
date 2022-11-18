/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import elemental2.core.Reflect;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.sw.definition.ValueHolder;


public class ValueHolderJsonbTypeSerializer implements JsonbSerializer<ValueHolder> {

    private final String objName;

    public ValueHolderJsonbTypeSerializer(String objName) {
        this.objName = objName;
    }

    @Override
    public void serialize(ValueHolder obj, JsonGenerator generator, SerializationContext ctx) {
        if (obj != null) {
            List<Reflect.OwnKeysArrayUnionType> keys = Reflect.ownKeys(obj).asList();
            if (!keys.isEmpty()) {
                JsonGenerator objBuilder = generator.writeStartObject(objName);
                for (Reflect.OwnKeysArrayUnionType k : keys) {
                    String key = k.asString();
                    Object jsonValue = Js.asPropertyMap(obj).get(key);
                    if (jsonValue instanceof String) {
                        objBuilder.write(key, (String) jsonValue);
                    } else if (jsonValue instanceof Integer) {
                        objBuilder.write(key, (Integer) jsonValue);
                    } else if (jsonValue instanceof Double) {
                        objBuilder.write(key, (Double) jsonValue);
                    } else if (jsonValue instanceof Float) {
                        objBuilder.write(key, (Float) jsonValue);
                    } else if (jsonValue instanceof Long) {
                        objBuilder.write(key, (Long) jsonValue);
                    } else if (jsonValue instanceof Boolean) {
                        objBuilder.write(key, (Boolean) jsonValue);
                    } else {
                        throw new UnsupportedOperationException("unknown type " + jsonValue.getClass().getCanonicalName());
                    }
                }
                objBuilder.writeEnd();
            }
        }
    }
}

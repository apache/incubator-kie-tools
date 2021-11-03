/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;

import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonException;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.*;

public class BeanDefJSONMarshaller implements DataSetDefJSONMarshallerExt<BeanDataSetDef> {

    public static BeanDefJSONMarshaller INSTANCE = new BeanDefJSONMarshaller();

    public static final String GENERATOR_CLASS = "generatorClass";
    public static final String GENERATOR_PARAMS = "generatorParams";
    public static final String PARAM = "param";
    public static final String VALUE = "value";

    @Override
    public void fromJson(BeanDataSetDef def, JsonObject json) {
        String generator = json.getString(GENERATOR_CLASS);

        if (!isBlank(generator)) {
            def.setGeneratorClass(generator);
        }
        if (json.has(GENERATOR_PARAMS)) {
            JsonArray array = json.getArray(GENERATOR_PARAMS);
            for (int i=0; i<array.length(); i++) {
                JsonObject param = array.getObject(i);
                String paramId = param.getString(PARAM);
                String value = param.getString(VALUE);

                if (!isBlank(paramId)) {
                    def.getParamaterMap().put(paramId, value);
                }
            }
        }
    }

    @Override
    public void toJson(BeanDataSetDef dataSetDef, JsonObject json) {
        // Generator class.
        json.put(GENERATOR_CLASS, dataSetDef.getGeneratorClass());

        // Generator parameters.
        Map<String, String> parameters = dataSetDef.getParamaterMap();
        if (parameters != null && !parameters.isEmpty()) {
            final JsonArray array = Json.createArray();
            int idx = 0;
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                final JsonObject paramObject = toJsonParameter(param.getKey(), param.getValue());
                array.set(idx++, paramObject);
            }
            json.put(GENERATOR_PARAMS, array);
        }
    }

    protected JsonObject toJsonParameter(final String key, final String value) throws JsonException {
        JsonObject json = Json.createObject();

        // Param.
        json.put(PARAM, key);

        // Value.
        json.put(VALUE, value);

        return json;
    }
}

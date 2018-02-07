/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.ScriptTypeList;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@ApplicationScoped
public class ScriptTypeListTypeSerializer implements Bpmn2OryxPropertySerializer<ScriptTypeListValue> {

    private static final String DELIMITER = ":@:";

    @Override
    public boolean accepts(final PropertyType type) {
        return ScriptTypeList.name.equals(type.getName());
    }

    @Override
    public ScriptTypeListValue parse(final Object property,
                                     final String value) {
        return parse(value);
    }

    public ScriptTypeListValue parse(final String value) {
        return new ScriptTypeListValue(parseScriptTokens(value));
    }

    @Override
    public String serialize(final Object property,
                            final ScriptTypeListValue value) {

        return serialize(value);
    }

    public String serialize(final ScriptTypeListValue value) {
        final StringBuilder serializedValue = new StringBuilder();
        ScriptTypeTypeSerializer scriptTypeSerializer = new ScriptTypeTypeSerializer();
        if (value.isEmpty()) {
            serializedValue.append("[]");
        } else {
            value.getValues().forEach(scriptTypeValue -> {
                if (serializedValue.length() > 0) {
                    serializedValue.append(DELIMITER);
                }
                serializedValue.append(scriptTypeSerializer.serialize(scriptTypeValue));
            });
        }
        return serializedValue.toString();
    }

    private List<ScriptTypeValue> parseScriptTokens(final String value) {
        final ScriptTypeTypeSerializer scriptSerializer = new ScriptTypeTypeSerializer();
        final List<ScriptTypeValue> tokens = new ArrayList<>();
        if (value == null || value.equals("[]")) {
            return tokens;
        } else {
            String remainder = value;
            String token;
            int index;
            while ((index = remainder.indexOf(DELIMITER)) >= 0) {
                token = remainder.substring(0,
                                            index);
                tokens.add(scriptSerializer.parse(token));
                remainder = remainder.substring(index + DELIMITER.length(),
                                                remainder.length());
            }
            if (!remainder.isEmpty()) {
                tokens.add(scriptSerializer.parse(remainder));
            }
        }
        return tokens;
    }
}
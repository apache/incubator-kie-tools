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

import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.ScriptType;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@ApplicationScoped
public class ScriptTypeTypeSerializer implements Bpmn2OryxPropertySerializer<ScriptTypeValue> {

    private static final char DELIMITER = '|';
    private static final String EMPTY_TOKEN = "";

    @Override
    public boolean accepts(final PropertyType type) {
        return ScriptType.name.equals(type.getName());
    }

    @Override
    public ScriptTypeValue parse(final Object property,
                                 final String value) {
        return parse(value);
    }

    public ScriptTypeValue parse(final String value) {
        final List<String> tokens = parseScriptTokens(value);
        final String language = tokens.get(0);
        final String script = tokens.get(1);
        return new ScriptTypeValue(!language.isEmpty() ? language : null,
                                   !script.isEmpty() ? script : null);
    }

    @Override
    public String serialize(final Object property,
                            final ScriptTypeValue value) {

        return serialize(value);
    }

    public String serialize(final ScriptTypeValue value) {
        final StringBuffer serializedValue = new StringBuffer();
        appendValue(serializedValue,
                    value.getLanguage());
        serializedValue.append(DELIMITER);
        appendValue(serializedValue,
                    value.getScript());
        return serializedValue.toString();
    }

    private List<String> parseScriptTokens(final String value) {
        final List<String> tokens = new ArrayList<>();
        String language = EMPTY_TOKEN;
        String script = EMPTY_TOKEN;
        if (value != null) {
            int index = value.indexOf('|');
            if (index >= 0) {
                language = value.substring(0,
                                           index);
                script = value.substring(index + 1,
                                         value.length());
            }
            tokens.add(language);
            tokens.add(script);
        }
        return tokens;
    }

    private void appendValue(final StringBuffer stringBuffer,
                             final String value) {
        if (value != null) {
            stringBuffer.append(value);
        } else {
            stringBuffer.append(EMPTY_TOKEN);
        }
    }
}

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

package org.kie.workbench.common.stunner.bpmn.backend.service.marshaller.json.oryx.property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ScriptTypeListTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.junit.Assert.assertEquals;

public class ScriptTypeListTypeSerializerTest {

    private static final String DELIMITER = ":@:";

    private static final String SCRIPT = "SCRIPT";

    private static final String LANGUAGE = "LANGUAGE";

    private static int SCRIPTS_COUNT = 10;

    private List<ScriptTypeValue> currentScripts;

    private ScriptTypeListTypeSerializer serializer;

    private ScriptTypeListValue scriptTypeList;

    @Before
    public void setUp() {
        serializer = new ScriptTypeListTypeSerializer();
        currentScripts = mockScripts(SCRIPTS_COUNT);
        scriptTypeList = new ScriptTypeListValue(currentScripts);
    }

    @Test
    public void testParse() {
        String serializedValue = buildExpectedSerialization(currentScripts);
        ScriptTypeListValue result = serializer.parse(serializedValue);
        assertEquals(scriptTypeList,
                     result);
    }

    @Test
    public void testSerialize() {
        String result = serializer.serialize(scriptTypeList);
        String expectedValue = buildExpectedSerialization(currentScripts);
        assertEquals(expectedValue,
                     result);
    }

    @Test
    public void testSerializeWithProperty() {
        String result = serializer.serialize(new Object(),
                                             scriptTypeList);
        String expectedValue = buildExpectedSerialization(currentScripts);
        assertEquals(expectedValue,
                     result);
    }

    @Test
    public void testSerializeWithNoValues() {
        String serializedValue = serializer.serialize(new ScriptTypeListValue());
        assertEquals("[]",
                     serializedValue);
    }

    @Test
    public void testParseWithNoValues() {
        ScriptTypeListValue result = serializer.parse("[]");
        assertEquals(new ScriptTypeListValue(),
                     result);
    }

    private String buildExpectedSerialization(List<ScriptTypeValue> scriptTypeListValues) {
        return scriptTypeListValues.stream()
                .map(scriptType -> buildExpectedScriptSerialization(scriptType.getLanguage(),
                                                                    scriptType.getScript()))
                .collect(Collectors.joining(DELIMITER));
    }

    private String buildExpectedScriptSerialization(String language,
                                                    String script) {
        return language + "|" + script;
    }

    private List<ScriptTypeValue> mockScripts(int size) {
        List<ScriptTypeValue> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new ScriptTypeValue(LANGUAGE + i,
                                           SCRIPT + i));
        }
        return result;
    }
}

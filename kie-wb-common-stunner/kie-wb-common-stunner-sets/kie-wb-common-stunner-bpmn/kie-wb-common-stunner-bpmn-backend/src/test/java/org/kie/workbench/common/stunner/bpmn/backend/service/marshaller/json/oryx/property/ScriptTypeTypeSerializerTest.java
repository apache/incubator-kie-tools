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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ScriptTypeTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.junit.Assert.assertEquals;

public class ScriptTypeTypeSerializerTest {

    private static final char DELIMITER = '|';

    private static final String SCRIPT = "SCRIPT";

    private static final String LANGUAGE = "LANGUAGE";

    private static final String scriptTypeSerialized = LANGUAGE + DELIMITER + SCRIPT;

    private ScriptTypeTypeSerializer serializer;

    private ScriptTypeValue scriptType;

    @Before
    public void setUp() {
        serializer = new ScriptTypeTypeSerializer();
        scriptType = new ScriptTypeValue(LANGUAGE,
                                         SCRIPT);
    }

    @Test
    public void testParse() {
        ScriptTypeValue result = serializer.parse(scriptTypeSerialized);
        assertEquals(LANGUAGE,
                     result.getLanguage());
        assertEquals(SCRIPT,
                     result.getScript());
    }

    @Test
    public void testSerialize() {
        String result = serializer.serialize(scriptType);
        assertEquals(scriptTypeSerialized,
                     result);
    }

    @Test
    public void testSerializeWithProperty() {
        String result = serializer.serialize(new Object(),
                                             scriptType);
        assertEquals(scriptTypeSerialized,
                     result);
    }
}

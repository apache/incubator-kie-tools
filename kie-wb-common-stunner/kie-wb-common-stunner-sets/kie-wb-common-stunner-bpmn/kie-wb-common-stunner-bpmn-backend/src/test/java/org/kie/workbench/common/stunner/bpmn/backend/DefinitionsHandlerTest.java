/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.BaseDirectDiagramMarshaller.DefinitionsHandler;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsHandlerTest {

    @Mock
    private DocumentRoot documentRoot;

    @Mock
    private Definitions definitions;

    @Before
    public void setUp() {
        when(documentRoot.getDefinitions()).thenReturn(definitions);
    }

    @Test
    public void testIsJbpmn1() {
        testWithExporterValue("jBPM", true);
        testWithExporterValue("JBPM", true);
        testWithExporterValue("jbpm", true);
    }

    @Test
    public void testIsNotJbpmn1() {
        testWithExporterValue("aris", false);
        testWithExporterValue("camunda", false);
        testWithExporterValue("other", false);
    }

    private void testWithExporterValue(String exporter, boolean expectedResult) {
        when(definitions.getExporter()).thenReturn(exporter);
        DefinitionsHandler handler = new DefinitionsHandler(documentRoot);
        assertEquals(expectedResult, handler.isJbpm());
    }

    @Test
    public void testIsJbpm2() {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("someKey", "http://www.jboss.org/drools");
        when(documentRoot.getXMLNSPrefixMap()).thenReturn(prefixMap);
        DefinitionsHandler handler = new DefinitionsHandler(documentRoot);
        assertTrue(handler.isJbpm());
    }

    @Test
    public void testIsJbpm3() {
        Map<String, String> schemaLocation = new HashMap<>();
        schemaLocation.put("http://www.jboss.org/drools", "someLocation");
        when(documentRoot.getXSISchemaLocation()).thenReturn(schemaLocation);
        DefinitionsHandler handler = new DefinitionsHandler(documentRoot);
        assertTrue(handler.isJbpm());
    }

    @Test
    public void testIsNoJbpm2() {
        when(documentRoot.getXMLNSPrefixMap()).thenReturn(Collections.emptyMap());
        when(documentRoot.getXSISchemaLocation()).thenReturn(Collections.emptyMap());
        DefinitionsHandler handler = new DefinitionsHandler(documentRoot);
        assertFalse(handler.isJbpm());
    }
}

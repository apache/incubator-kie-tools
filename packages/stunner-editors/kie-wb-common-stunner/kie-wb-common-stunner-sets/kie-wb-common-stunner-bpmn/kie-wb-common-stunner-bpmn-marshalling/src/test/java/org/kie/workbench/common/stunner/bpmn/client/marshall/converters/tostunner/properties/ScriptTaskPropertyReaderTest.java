/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.common.util.EList;
import org.jboss.drools.DroolsPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TestUtils.mockExtensionValues;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScriptTaskPropertyReaderTest {

    private static final String METADATA_ELEMENT_NAME = "customAsync";
    private static final String SCRIPT = "SCRIPT";

    @Mock
    private ScriptTask task;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    private ScriptTaskPropertyReader propertyReader;

    @Before
    public void setUp() {
        propertyReader = new ScriptTaskPropertyReader(task, diagram, definitionResolver);
    }

    @Test
    public void testGetScript() {
        for (Scripts.LANGUAGE language : Scripts.LANGUAGE.values()) {
            testGetScript(new ScriptTypeValue(language.language(), SCRIPT), language.format(), SCRIPT);
        }
    }

    @Test
    public void testGetScriptNotConfigured() {
        testGetScript(new ScriptTypeValue(Scripts.LANGUAGE.JAVA.language(), null), null, null);
    }

    private void testGetScript(ScriptTypeValue expectedValue, String currentLanguage, String currentScript) {
        when(task.getScriptFormat()).thenReturn(currentLanguage);
        when(task.getScript()).thenReturn(currentScript);
        assertEquals(expectedValue, propertyReader.getScript());
    }

    @Test
    public void testIsAsyncTrue() {
        testIsAsync(true, "true");
    }

    @Test
    public void testIsAsyncFalse() {
        testIsAsync(false, "false");
        testIsAsync(false, null);
    }

    private void testIsAsync(boolean expectedValue, String extensionValue) {
        EList<ExtensionAttributeValue> extensionValues = mockExtensionValues(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, METADATA_ELEMENT_NAME, extensionValue);
        when(task.getExtensionValues()).thenReturn(extensionValues);
        assertEquals(expectedValue, propertyReader.isAsync());
    }
}

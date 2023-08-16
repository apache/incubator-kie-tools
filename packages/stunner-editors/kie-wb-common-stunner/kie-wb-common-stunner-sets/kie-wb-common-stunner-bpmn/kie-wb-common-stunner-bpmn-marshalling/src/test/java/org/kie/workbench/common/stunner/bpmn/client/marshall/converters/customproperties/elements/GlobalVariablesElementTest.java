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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.GlobalType;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class GlobalVariablesElementTest {

    private final String GLOBAL_VARIABLES = "GV1:Boolean,GV2:Boolean,GV3:Integer,GV4:Map<String,String>";
    private final String NAME = "DefaultImports";
    private final String GLOBAL_VARIABLE = "GV1:Boolean";

    @Test
    public void testGetValue() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.globalVariables.of(baseElement).set(GLOBAL_VARIABLES);
        assertEquals(GLOBAL_VARIABLES, CustomElement.globalVariables.of(baseElement).get());
    }

    @Test
    public void testSetValue() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.globalVariables.of(baseElement).set(GLOBAL_VARIABLES);
        assertEquals(GLOBAL_VARIABLES, CustomElement.globalVariables.of(baseElement).get());
    }

    @Test
    public void testExtensionOf() {
        GlobalVariablesElement globalVariablesElement = new GlobalVariablesElement(NAME);
        GlobalType globalType = globalVariablesElement.globalTypeDataOf(GLOBAL_VARIABLE);
        FeatureMap.Entry entry = globalVariablesElement.extensionOf(GLOBAL_VARIABLE);

        assertNotNull(entry);
        assertTrue(entry instanceof EStructuralFeatureImpl.SimpleFeatureMapEntry);
        assertEquals(DOCUMENT_ROOT__GLOBAL, entry.getEStructuralFeature());

        assertNotNull(entry.getValue());
        assertEquals(globalType.getIdentifier(), ((GlobalType) entry.getValue()).getIdentifier());
        assertEquals(globalType.getType(), ((GlobalType) entry.getValue()).getType());
    }

    @Test
    public void testGlobalTypeDataOf() {
        GlobalVariablesElement globalVariablesElement = new GlobalVariablesElement(NAME);
        GlobalType globalType = globalVariablesElement.globalTypeDataOf(GLOBAL_VARIABLE);

        assertTrue(GLOBAL_VARIABLE.startsWith(globalType.getIdentifier()));
        assertTrue(GLOBAL_VARIABLE.endsWith(globalType.getType()));
    }
}
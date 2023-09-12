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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.ImportType;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class DefaultImportsElementTest {

    private final static String NAME = "DefaultImports";
    private final static String CLASS_NAME = "ClassName";

    @Test
    public void testGetValue() {
        BaseElement baseElement = bpmn2.createProcess();
        assertEquals(new ArrayList<>(), CustomElement.defaultImports.of(baseElement).get());
    }

    @Test
    public void testSetValue() {
        List<DefaultImport> defaultImports = new ArrayList<>();
        defaultImports.add(new DefaultImport(CLASS_NAME + 1));
        defaultImports.add(new DefaultImport(CLASS_NAME + 2));
        defaultImports.add(new DefaultImport(CLASS_NAME + 3));

        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.defaultImports.of(baseElement).set(defaultImports);

        List<DefaultImport> result = CustomElement.defaultImports.of(baseElement).get();
        assertEquals(3, result.size());
        assertEquals(CLASS_NAME + 1, result.get(0).getClassName());
        assertEquals(CLASS_NAME + 2, result.get(1).getClassName());
        assertEquals(CLASS_NAME + 3, result.get(2).getClassName());
    }

    @Test
    public void extensionOf() {
        DefaultImportsElement defaultImportsElement = new DefaultImportsElement(NAME);
        DefaultImport defaultImport = new DefaultImport(CLASS_NAME);
        ImportType importType = defaultImportsElement.importTypeDataOf(defaultImport);

        FeatureMap.Entry entry = defaultImportsElement.extensionOf(defaultImport);

        assertNotNull(entry);
        assertTrue(entry instanceof EStructuralFeatureImpl.SimpleFeatureMapEntry);
        assertEquals(DOCUMENT_ROOT__IMPORT, entry.getEStructuralFeature());

        assertNotNull((ImportType) entry.getValue());
        assertEquals(importType.getName(), ((ImportType) entry.getValue()).getName());
    }

    @Test
    public void importTypeDataOf() {
        DefaultImportsElement defaultImportsElement = new DefaultImportsElement(NAME);
        DefaultImport defaultImport = new DefaultImport(CLASS_NAME);
        ImportType importType = defaultImportsElement.importTypeDataOf(defaultImport);
        assertEquals(CLASS_NAME, importType.getName());
    }
}
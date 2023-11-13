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
import org.jboss.drools.MetaDataType;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class MetaDataAttributesElementTest {

    private final static String ATTRIBUTES = "att1ßval1Øatt2ßval2Øatt3ßval3";
    private final static String ATTRIBUTE = "att1ßval1";
    private final static String NAME = "metaDataElement";
    private final static String SPECIAL_CHAR_ATTRIBUTES = "att1ß#{[($%&@!*|)]}Øatt2ß/-_.,?`'^\"\\~<>=+Øatt3ß:;çÇáàÁÀãÃüÜ";

    @Test
    public void testSetValue() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set(ATTRIBUTES);
        assertEquals("att1ß<![CDATA[val1]]>Øatt2ß<![CDATA[val2]]>Øatt3ß<![CDATA[val3]]>",
                     CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testSetValueSpecialChar() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set(SPECIAL_CHAR_ATTRIBUTES);
        assertEquals("att1ß<![CDATA[#{[($%&@!*|)]}]]>Øatt2ß<![CDATA[/-_.,?`'^\"\\~<>=+]]>Øatt3ß<![CDATA[:;çÇáàÁÀãÃüÜ]]>",
                     CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testGetDefaultValue() {
        BaseElement baseElement = bpmn2.createProcess();
        assertEquals("", CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testGetStringValueNameNull() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set(null);
        assertEquals("", CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testGetStringValueNameEmpty() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set("ßValue");
        assertEquals("", CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testGetStringValueEmpty() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set("att1");
        assertEquals("att1ß", CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testGetStringValueValueEmpty() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set("attributeßßatt2");
        assertEquals("attributeß<![CDATA[]]>", CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testGetStringValueNotMetadata() {
        BaseElement baseElement = bpmn2.createProcess();
        CustomElement.metaDataAttributes.of(baseElement).set(CustomElement.async.name());
        assertEquals("", CustomElement.metaDataAttributes.of(baseElement).get());
    }

    @Test
    public void testExtensionOf() {
        MetaDataAttributesElement metaDataAttributesElement = new MetaDataAttributesElement(NAME);
        MetaDataType metaDataType = metaDataAttributesElement.metaDataTypeDataOf(ATTRIBUTE);
        FeatureMap.Entry entry = metaDataAttributesElement.extensionOf(ATTRIBUTE);

        assertNotNull(entry);
        assertTrue(entry instanceof EStructuralFeatureImpl.SimpleFeatureMapEntry);
        assertEquals(DOCUMENT_ROOT__META_DATA, entry.getEStructuralFeature());

        assertNotNull(entry.getValue());
        assertEquals(metaDataType.getName(), ((MetaDataType) entry.getValue()).getName());
        assertEquals(metaDataType.getMetaValue(), ((MetaDataType) entry.getValue()).getMetaValue());
    }

    @Test
    public void testImportTypeDataOf() {
        MetaDataAttributesElement metaDataAttributesElement = new MetaDataAttributesElement(NAME);
        MetaDataType metaDataType = metaDataAttributesElement.metaDataTypeDataOf(ATTRIBUTE);

        assertTrue("att1ß<![CDATA[val1]]>".startsWith(metaDataType.getName()));
        assertTrue("att1ß<![CDATA[val1]]>".endsWith(metaDataType.getMetaValue()));
    }

    @Test
    public void testIsMetaDataAttribute() {
        MetaDataAttributesElement metaDataAttributesElement = new MetaDataAttributesElement(NAME);
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(""));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.async.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.autoStart.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.autoConnectionSource.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.autoConnectionTarget.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.customTags.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.description.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.scope.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.name.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.caseIdPrefix.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.caseRole.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.slaDueDate.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.isCase.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.customActivationCondition.name()));
        assertFalse(metaDataAttributesElement.isMetaDataAttribute(CustomElement.abortParent.name()));
        assertTrue(metaDataAttributesElement.isMetaDataAttribute("randomMetadataAttribute"));
    }
}
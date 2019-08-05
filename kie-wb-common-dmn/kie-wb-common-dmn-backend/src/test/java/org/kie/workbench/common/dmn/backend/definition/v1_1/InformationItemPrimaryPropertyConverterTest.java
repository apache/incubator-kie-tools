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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InformationItemPrimaryPropertyConverterTest {

    @Test
    public void testWbFromDMNWhenDMNIsNull() {

        final InformationItem dmn = null;
        final InformationItemPrimary informationItemPrimary = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn, dmn);

        assertNull(informationItemPrimary);
    }

    @Test
    public void testWbFromDMNWhenDMNIsNotNull() {

        final String id = "id";
        final Id expectedId = new Id(id);
        final String qNameNamespaceURI = "qName namespaceURI";
        final String qNameLocalPart = "qName local part";
        final String qNamePrefix = "qName prefix";
        final QName expectedTypeRef = new QName(qNameNamespaceURI, qNameLocalPart, qNamePrefix);
        final javax.xml.namespace.QName qName = mock(javax.xml.namespace.QName.class);
        final org.kie.dmn.model.api.InformationItem dmn = mock(org.kie.dmn.model.api.InformationItem.class);

        when(dmn.getId()).thenReturn(id);
        when(dmn.getTypeRef()).thenReturn(qName);

        when(qName.getNamespaceURI()).thenReturn(qNameNamespaceURI);
        when(qName.getLocalPart()).thenReturn(qNameLocalPart);
        when(qName.getPrefix()).thenReturn(qNamePrefix);

        final InformationItemPrimary informationItemPrimary = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn, dmn);
        final Id actualId = informationItemPrimary.getId();
        final QName actualTypeRef = informationItemPrimary.getTypeRef();

        assertEquals(expectedId, actualId);
        assertEquals(expectedTypeRef, actualTypeRef);
    }

    @Test
    public void testDmnFromWBWhenWBIsNull() {

        final InformationItemPrimary wb = null;
        final InformationItem informationItem = InformationItemPrimaryPropertyConverter.dmnFromWB(wb, wb);

        assertNull(informationItem);
    }

    @Test
    public void testDmnFromWBWhenWBIsNotNull() {

        final String expectedId = "id";
        final String expectedName = "name";
        final Id id = new Id(expectedId);
        final Name name = new Name(expectedName);
        final String qNameNamespaceURI = "qName namespaceURI";
        final String qNameLocalPart = "qName local part";
        final String qNamePrefix = "qName prefix";
        final InformationItemPrimary wb = mock(InformationItemPrimary.class);

        final QName qName = mock(QName.class);
        final javax.xml.namespace.QName expectedQName = new javax.xml.namespace.QName(qNameNamespaceURI, qNameLocalPart, qNamePrefix);
        final NamedElement parentElement = mock(NamedElement.class);

        when(wb.getId()).thenReturn(id);
        when(wb.getName()).thenReturn(name);
        when(wb.getTypeRef()).thenReturn(qName);
        when(wb.getParent()).thenReturn(parentElement);

        when(parentElement.getName()).thenReturn(name);
        when(qName.getNamespaceURI()).thenReturn(qNameNamespaceURI);
        when(qName.getLocalPart()).thenReturn(qNameLocalPart);
        when(qName.getPrefix()).thenReturn(qNamePrefix);

        final TInformationItem informationItem = InformationItemPrimaryPropertyConverter.dmnFromWB(wb, wb);
        final String actualId = informationItem.getId();
        final String actualName = informationItem.getName();
        final javax.xml.namespace.QName actualQName = informationItem.getTypeRef();

        assertEquals(expectedId, actualId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedQName, actualQName);
    }

    @Test
    public void testDMNGetNameWhenParentDoesNotHaveName() {
        final org.kie.dmn.model.api.InformationItem parent = mock(org.kie.dmn.model.api.InformationItem.class);

        final String name = InformationItemPrimaryPropertyConverter.getParentName(parent);

        assertTrue(name.isEmpty());
    }

    @Test
    public void testDMNGetNameWhenParentHasNullName() {
        final org.kie.dmn.model.api.InputData parent = mock(org.kie.dmn.model.api.InputData.class);

        when(parent.getName()).thenReturn(null);

        final String name = InformationItemPrimaryPropertyConverter.getParentName(parent);

        assertTrue(name.isEmpty());
    }

    @Test
    public void testDMNGetNameWhenParentHasName() {
        final org.kie.dmn.model.api.InputData parent = mock(org.kie.dmn.model.api.InputData.class);
        final String expectedName = "name";

        when(parent.getName()).thenReturn(expectedName);

        final String actualName = InformationItemPrimaryPropertyConverter.getParentName(parent);

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testWBGetNameWhenParentDoesNotHaveName() {
        final InformationItemPrimary parent = mock(InformationItemPrimary.class);

        final String name = InformationItemPrimaryPropertyConverter.getParentName(parent);

        assertTrue(name.isEmpty());
    }

    @Test
    public void testWBGetNameWhenParentHasNullName() {
        final InputData parent = mock(InputData.class);

        when(parent.getName()).thenReturn(null);

        final String name = InformationItemPrimaryPropertyConverter.getParentName(parent);

        assertTrue(name.isEmpty());
    }

    @Test
    public void testWBGetNameWhenParentHasName() {
        final InputData parent = mock(InputData.class);
        final Name parentName = mock(Name.class);
        final String expectedName = "name";

        when(parent.getName()).thenReturn(parentName);
        when(parentName.getValue()).thenReturn(expectedName);

        final String actualName = InformationItemPrimaryPropertyConverter.getParentName(parent);

        assertEquals(expectedName, actualName);
    }
}


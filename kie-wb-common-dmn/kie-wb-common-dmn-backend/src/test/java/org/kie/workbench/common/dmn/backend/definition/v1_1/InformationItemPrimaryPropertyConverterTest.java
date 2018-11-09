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

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({QNamePropertyConverter.class, InformationItemPrimaryPropertyConverter.class})
@RunWith(PowerMockRunner.class)
public class InformationItemPrimaryPropertyConverterTest {

    @Test
    public void testWbFromDMNWhenDMNIsNull() {

        final InformationItem dmn = null;
        final InformationItemPrimary informationItemPrimary = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn);

        assertNull(informationItemPrimary);
    }

    @Test
    public void testWbFromDMNWhenDMNIsNotNull() {

        final String id = "id";
        final Id expectedId = new Id(id);
        final QName expectedTypeRef = mock(QName.class);
        final javax.xml.namespace.QName qName = mock(javax.xml.namespace.QName.class);
        final org.kie.dmn.model.api.InformationItem dmn = mock(org.kie.dmn.model.api.InformationItem.class);

        when(dmn.getId()).thenReturn(id);
        when(dmn.getTypeRef()).thenReturn(qName);

        PowerMockito.mockStatic(QNamePropertyConverter.class);
        PowerMockito.when(QNamePropertyConverter.wbFromDMN(qName, dmn)).thenReturn(expectedTypeRef);

        final InformationItemPrimary informationItemPrimary = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn);
        final Id actualId = informationItemPrimary.getId();
        final QName actualTypeRef = informationItemPrimary.getTypeRef();

        assertEquals(expectedId, actualId);
        assertEquals(expectedTypeRef, actualTypeRef);
    }

    @Test
    public void testDmnFromWBWhenWBIsNull() {

        final InformationItemPrimary wb = null;
        final InformationItem informationItem = InformationItemPrimaryPropertyConverter.dmnFromWB(wb);

        assertNull(informationItem);
    }

    @Test
    public void testDmnFromWBWhenWBIsNotNull() {

        final String expectedId = "id";
        final String expectedName = "name";
        final Id id = new Id(expectedId);
        final InformationItemPrimary wb = mock(InformationItemPrimary.class);
        final QName qName = PowerMockito.mock(QName.class);
        final javax.xml.namespace.QName expectedQName = mock(javax.xml.namespace.QName.class);
        final Optional<javax.xml.namespace.QName> optionalExpectedQName = Optional.of(expectedQName);

        when(wb.getId()).thenReturn(id);
        when(wb.getTypeRef()).thenReturn(qName);

        PowerMockito.stub(PowerMockito.method(InformationItemPrimaryPropertyConverter.class, "getName", InformationItemPrimary.class)).toReturn(expectedName);
        PowerMockito.stub(PowerMockito.method(QNamePropertyConverter.class, "dmnFromWB", QName.class)).toReturn(optionalExpectedQName);

        final TInformationItem informationItem = InformationItemPrimaryPropertyConverter.dmnFromWB(wb);
        final String actualId = informationItem.getId();
        final String actualName = informationItem.getName();
        final javax.xml.namespace.QName actualQName = informationItem.getTypeRef();

        assertEquals(expectedId, actualId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedQName, actualQName);
    }

    @Test
    public void testGetNameWhenParentDoesNotHaveName() {

        final InformationItemPrimary informationItem = mock(InformationItemPrimary.class);
        final InformationItemPrimary parent = mock(InformationItemPrimary.class);

        when(informationItem.getParent()).thenReturn(parent);

        final String name = InformationItemPrimaryPropertyConverter.getName(informationItem);

        assertTrue(name.isEmpty());
    }

    @Test
    public void testGetNameWhenParentHasNullName() {

        final InformationItemPrimary informationItem = mock(InformationItemPrimary.class);
        final InputData parent = mock(InputData.class);

        when(informationItem.getParent()).thenReturn(parent);
        when(parent.getName()).thenReturn(null);

        final String name = InformationItemPrimaryPropertyConverter.getName(informationItem);

        assertTrue(name.isEmpty());
    }

    @Test
    public void testGetNameWhenParentHasName() {

        final InformationItemPrimary informationItem = mock(InformationItemPrimary.class);
        final InputData parent = mock(InputData.class);
        final Name parentName = mock(Name.class);
        final String expectedName = "name";

        when(informationItem.getParent()).thenReturn(parent);
        when(parent.getName()).thenReturn(parentName);
        when(parentName.getValue()).thenReturn(expectedName);

        final String actualName = InformationItemPrimaryPropertyConverter.getName(informationItem);

        assertEquals(expectedName, actualName);
    }
}

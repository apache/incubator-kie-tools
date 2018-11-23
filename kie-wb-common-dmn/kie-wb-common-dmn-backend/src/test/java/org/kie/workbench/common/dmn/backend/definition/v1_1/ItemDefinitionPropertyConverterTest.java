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

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter.setItemComponent;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter.setUnaryTests;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter.wbChildFromDMN;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter.wbDescriptionFromDMN;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter.wbFromDMN;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.ItemDefinitionPropertyConverter.wbTypeRefFromDMN;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({ItemDefinitionPropertyConverter.class, UnaryTestsPropertyConverter.class, DescriptionPropertyConverter.class, QNamePropertyConverter.class})
@RunWith(PowerMockRunner.class)
public class ItemDefinitionPropertyConverterTest {

    @Test
    public void testWbFromDMNWhenDMNIsNull() {
        assertNull(wbFromDMN(null));
    }

    @Test
    public void testWbFromDMNWhenDMNIsNotNull() {

        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final String id = "id";
        final String name = "name";
        final Id expectedId = new Id(id);
        final Name expectedName = new Name(name);
        final String expectedTypeLanguage = "typeLanguage";
        final boolean expectedIsCollection = true;
        final Description expectedDescription = mock(Description.class);
        final QName expectedTypeRef = mock(QName.class);

        when(dmn.getId()).thenReturn(id);
        when(dmn.getName()).thenReturn(name);
        when(dmn.getTypeLanguage()).thenReturn(expectedTypeLanguage);
        when(dmn.isIsCollection()).thenReturn(expectedIsCollection);

        stubWbDescriptionFromDMNToReturn(expectedDescription);
        stubWbTypeRefFromDMNToReturn(expectedTypeRef);

        final ItemDefinition actualItemDefinition = wbFromDMN(dmn);

        final Id actualId = actualItemDefinition.getId();
        final Name actualName = actualItemDefinition.getName();
        final String actualTypeLanguage = actualItemDefinition.getTypeLanguage();
        final boolean actualIsCollection = actualItemDefinition.isIsCollection();
        final Description actualDescription = actualItemDefinition.getDescription();
        final QName actualTypeRef = actualItemDefinition.getTypeRef();

        assertEquals(expectedId, actualId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedTypeLanguage, actualTypeLanguage);
        assertEquals(expectedIsCollection, actualIsCollection);
        assertEquals(expectedDescription, actualDescription);
        assertEquals(expectedTypeRef, actualTypeRef);
    }

    @Test
    public void testSetItemComponent() {

        final ItemDefinition wb = new ItemDefinition();
        final ItemDefinition wbChild = new ItemDefinition();
        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final org.kie.dmn.model.api.ItemDefinition dmnChild = mock(org.kie.dmn.model.api.ItemDefinition.class);

        stubWbChildFromDMNToReturn(wbChild);
        when(dmn.getItemComponent()).thenReturn(singletonList(dmnChild));

        setItemComponent(wb, dmn);

        final List<ItemDefinition> expectedItemDefinitions = singletonList(wbChild);
        final List<ItemDefinition> actualItemDefinitions = wb.getItemComponent();

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testSetUnaryTestsWhenUnaryTestsIsNotNull() {

        final ItemDefinition wb = mock(ItemDefinition.class);
        final UnaryTests wbAllowedValues = mock(UnaryTests.class);
        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final org.kie.dmn.model.api.UnaryTests dmnAllowedValues = mock(org.kie.dmn.model.api.UnaryTests.class);

        when(dmn.getAllowedValues()).thenReturn(dmnAllowedValues);
        PowerMockito.mockStatic(UnaryTestsPropertyConverter.class);
        PowerMockito.when(UnaryTestsPropertyConverter.wbFromDMN(dmnAllowedValues)).thenReturn(wbAllowedValues);

        setUnaryTests(wb, dmn);

        verify(wb).setAllowedValues(wbAllowedValues);
        verify(wbAllowedValues).setParent(wb);
    }

    @Test
    public void testSetUnaryTestsWhenUnaryTestsIsNull() {

        final ItemDefinition wb = mock(ItemDefinition.class);
        final UnaryTests wbAllowedValues = mock(UnaryTests.class);
        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final org.kie.dmn.model.api.UnaryTests dmnAllowedValues = mock(org.kie.dmn.model.api.UnaryTests.class);

        when(dmn.getAllowedValues()).thenReturn(dmnAllowedValues);
        PowerMockito.mockStatic(UnaryTestsPropertyConverter.class);
        PowerMockito.when(UnaryTestsPropertyConverter.wbFromDMN(dmnAllowedValues)).thenReturn(null);

        setUnaryTests(wb, dmn);

        verify(wb, never()).setAllowedValues(wbAllowedValues);
        verify(wbAllowedValues, never()).setParent(wb);
    }

    @Test
    public void testWbDescriptionFromDMN() {

        final String description = "description";
        final Description expectedDescription = mock(Description.class);
        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);

        when(dmn.getDescription()).thenReturn(description);
        PowerMockito.mockStatic(DescriptionPropertyConverter.class);
        PowerMockito.when(DescriptionPropertyConverter.wbFromDMN(description)).thenReturn(expectedDescription);

        final Description actualDescription = wbDescriptionFromDMN(dmn);

        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    public void testWbTypeRefFromDMNWhenQNameIsUndefined() {

        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final javax.xml.namespace.QName dmnQName = mock(javax.xml.namespace.QName.class);
        final QName wbQName = BuiltInType.UNDEFINED.asQName();

        when(dmn.getTypeRef()).thenReturn(dmnQName);

        PowerMockito.mockStatic(QNamePropertyConverter.class);
        PowerMockito.when(QNamePropertyConverter.wbFromDMN(dmnQName, dmn)).thenReturn(wbQName);

        final QName actualQName = wbTypeRefFromDMN(dmn);

        assertNull(actualQName);
    }

    @Test
    public void testWbTypeRefFromDMNWhenQNameIsNotUndefined() {

        final org.kie.dmn.model.api.ItemDefinition dmn = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final javax.xml.namespace.QName dmnQName = mock(javax.xml.namespace.QName.class);
        final QName expectedQName = BuiltInType.STRING.asQName();

        when(dmn.getTypeRef()).thenReturn(dmnQName);

        PowerMockito.mockStatic(QNamePropertyConverter.class);
        PowerMockito.when(QNamePropertyConverter.wbFromDMN(dmnQName, dmn)).thenReturn(expectedQName);

        final QName actualQName = wbTypeRefFromDMN(dmn);

        assertEquals(actualQName, expectedQName);
    }

    @Test
    public void testWbChildFromDMNWhenWbChildIsNotNull() {

        final ItemDefinition expectedWbParent = new ItemDefinition();
        final ItemDefinition expectedWbChild = new ItemDefinition();
        final org.kie.dmn.model.api.ItemDefinition dmnChild = mock(org.kie.dmn.model.api.ItemDefinition.class);

        stubWbFromDMNToReturn(expectedWbChild);

        final ItemDefinition actualWbChild = wbChildFromDMN(expectedWbParent, dmnChild);
        final ItemDefinition actualParent = (ItemDefinition) actualWbChild.getParent();

        assertEquals(expectedWbChild, actualWbChild);
        assertEquals(expectedWbParent, actualParent);
    }

    @Test
    public void testWbChildFromDMNWhenWbChildIsNull() {

        final ItemDefinition expectedWbParent = new ItemDefinition();
        final org.kie.dmn.model.api.ItemDefinition dmnChild = mock(org.kie.dmn.model.api.ItemDefinition.class);

        stubWbFromDMNToReturn(null);

        final ItemDefinition actualWbChild = wbChildFromDMN(expectedWbParent, dmnChild);

        assertNull(actualWbChild);
    }

    private void stubWbDescriptionFromDMNToReturn(final Description returnValue) {

        final Class<ItemDefinitionPropertyConverter> aClass = ItemDefinitionPropertyConverter.class;
        final Class<org.kie.dmn.model.api.ItemDefinition> parameter = org.kie.dmn.model.api.ItemDefinition.class;

        PowerMockito.stub(PowerMockito.method(aClass, "wbDescriptionFromDMN", parameter)).toReturn(returnValue);
    }

    private void stubWbTypeRefFromDMNToReturn(final QName returnValue) {

        final Class<ItemDefinitionPropertyConverter> aClass = ItemDefinitionPropertyConverter.class;
        final Class<org.kie.dmn.model.api.ItemDefinition> parameter = org.kie.dmn.model.api.ItemDefinition.class;

        PowerMockito.stub(PowerMockito.method(aClass, "wbTypeRefFromDMN", parameter)).toReturn(returnValue);
    }

    private void stubWbFromDMNToReturn(final ItemDefinition returnValue) {

        final Class<ItemDefinitionPropertyConverter> aClass = ItemDefinitionPropertyConverter.class;
        final Class<org.kie.dmn.model.api.ItemDefinition> parameter = org.kie.dmn.model.api.ItemDefinition.class;

        PowerMockito.stub(PowerMockito.method(aClass, "wbFromDMN", parameter)).toReturn(returnValue);
    }

    private void stubWbChildFromDMNToReturn(final ItemDefinition returnValue) {

        final Class<ItemDefinitionPropertyConverter> aClass = ItemDefinitionPropertyConverter.class;
        final Class<ItemDefinition> parameter1 = ItemDefinition.class;
        final Class<org.kie.dmn.model.api.ItemDefinition> parameter2 = org.kie.dmn.model.api.ItemDefinition.class;

        PowerMockito.stub(PowerMockito.method(aClass, "wbChildFromDMN", parameter1, parameter2)).toReturn(returnValue);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }
}

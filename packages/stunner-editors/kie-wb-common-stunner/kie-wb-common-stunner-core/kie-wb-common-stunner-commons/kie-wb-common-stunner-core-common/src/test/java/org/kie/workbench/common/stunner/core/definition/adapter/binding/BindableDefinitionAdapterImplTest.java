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


package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class BindableDefinitionAdapterImplTest {

    private static final BindableTestBean1 BEAN1 = new BindableTestBean1();
    private static final DefinitionAdapterBindings BEAN1_BINDINGS = new DefinitionAdapterBindings()
            .setBaseType(BindableTestProperty1.class)
            .setGraphFactory(NodeFactory.class)
            .setIdField("idField")
            .setCategoryField("categoryField")
            .setTitleField("titleField")
            .setDescriptionField("descriptionField")
            .setLabelsField("labelsField")
            .setPropertiesFieldNames(Arrays.asList("nameField", "stringField", "propertyField"))
            .setTypedPropertyFields(Arrays.asList(false, false, true))
            .setMetaTypes(DefinitionAdapterBindings.PropertyMetaTypes.parse("0,-1,-1,-1,-1"));

    @Mock
    private StunnerTranslationService translationService;

    @Mock
    private BindableAdapterFunctions functions;

    private BindableDefinitionAdapterImpl<Object> tested;

    @Before
    public void setUp() {
        tested = BindableDefinitionAdapterImpl.create(translationService, functions);
        tested.addBindings(BindableTestBean1.class, BEAN1_BINDINGS);
    }

    @Test
    public void testGetId() {
        DefinitionId id = tested.getId(BEAN1);
        assertTrue(id.isDynamic());
        assertEquals(BEAN1.getClass().getName(), id.value());
    }

    @Test
    public void testGetCategory() {
        when(functions.getValue(eq(BEAN1), eq("categoryField"))).thenReturn("categoryValue");
        String category = tested.getCategory(BEAN1);
        assertEquals("categoryValue", category);
    }

    @Test
    public void testGetTitle() {
        when(functions.getValue(eq(BEAN1), eq("titleField"))).thenReturn("titleValue");
        String title = tested.getTitle(BEAN1);
        assertEquals("titleValue", title);
    }

    @Test
    public void testGetDescription() {
        when(functions.getValue(eq(BEAN1), eq("descriptionField"))).thenReturn("descriptionValue");
        String description = tested.getDescription(BEAN1);
        assertEquals("descriptionValue", description);
    }

    @Test
    public void testGetLabelsAsArray() {
        when(functions.getValue(eq(BEAN1), eq("labelsField"))).thenReturn(new String[]{"label1", "label2"});
        String[] labels = tested.getLabels(BEAN1);
        assertArrayEquals(new String[]{"label1", "label2"}, labels);
    }

    @Test
    public void testGetLabelsAsCollection() {
        when(functions.getValue(eq(BEAN1), eq("labelsField"))).thenReturn(Arrays.asList("label1", "label2"));
        String[] labels = tested.getLabels(BEAN1);
        assertArrayEquals(new String[]{"label1", "label2"}, labels);
    }

    @Test
    @SuppressWarnings("all")
    public void testGetGraphFacotry() {
        Class<? extends ElementFactory> graphFactory = tested.getGraphFactory(BEAN1.getClass());
        assertEquals(NodeFactory.class, graphFactory);
    }

    @Test
    public void testGetPropertyFields() {
        when(functions.getValue(eq(BEAN1), eq("nameField"))).thenReturn("nameValue");
        when(functions.getValue(eq(BEAN1), eq("stringField"))).thenReturn("stringValue");
        when(functions.getValue(eq(BEAN1), eq("propertyField"))).thenReturn("propertyValue");
        String[] propertyFields = tested.getPropertyFields(BEAN1);
        assertArrayEquals(new String[]{"nameField", "stringField", "propertyField"}, propertyFields);
    }

    @Test
    @SuppressWarnings("all")
    public void testGetNameProperty() {
        Optional<?> nameValue = tested.getProperty(BEAN1, "nameField");
        assertTrue(nameValue.isPresent());
        DefinitionBindableProperty name = (DefinitionBindableProperty) nameValue.get();
        assertEquals(BEAN1, name.getPojo());
        assertEquals("nameField", name.getField());
    }

    @Test
    @SuppressWarnings("all")
    public void testGetNonexistentProperty() {
        Optional<?> nameValue = tested.getProperty(BEAN1, "someField");
        assertFalse(nameValue.isPresent());
    }

    @Test
    @SuppressWarnings("all")
    public void testGetStringProperty() {
        Optional<?> stringField = tested.getProperty(BEAN1, "stringField");
        assertTrue(stringField.isPresent());
        DefinitionBindableProperty s = (DefinitionBindableProperty) stringField.get();
        assertEquals(BEAN1, s.getPojo());
        assertEquals("stringField", s.getField());
    }

    @Test
    public void testGetTypedProperty() {
        BindableTestProperty1 property = new BindableTestProperty1();
        when(functions.getValue(eq(BEAN1), eq("propertyField"))).thenReturn(property);
        Optional<?> propertyField = tested.getProperty(BEAN1, "propertyField");
        assertTrue(propertyField.isPresent());
        assertEquals(property, propertyField.get());
    }

    @Test
    public void testGetMetaTypes() {
        String nameField = tested.getMetaPropertyField(BEAN1, PropertyMetaTypes.NAME);
        assertEquals("nameField", nameField);
        String widthField = tested.getMetaPropertyField(BEAN1, PropertyMetaTypes.WIDTH);
        assertNull(widthField);
        String heightField = tested.getMetaPropertyField(BEAN1, PropertyMetaTypes.HEIGHT);
        assertNull(heightField);
        String radiusField = tested.getMetaPropertyField(BEAN1, PropertyMetaTypes.RADIUS);
        assertNull(radiusField);
        String idField = tested.getMetaPropertyField(BEAN1, PropertyMetaTypes.ID);
        assertNull(idField);
    }
}

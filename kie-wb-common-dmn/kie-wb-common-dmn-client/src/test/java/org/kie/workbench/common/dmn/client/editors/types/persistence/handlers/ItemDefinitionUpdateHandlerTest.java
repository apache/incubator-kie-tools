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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionUpdateHandlerTest {

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private PropertiesPanelNotifier panelNotifier;

    private ItemDefinitionUpdateHandler handler;

    @Before
    public void setup() {
        itemDefinitionUtils = spy(new ItemDefinitionUtils(dmnGraphUtils));
        handler = spy(new ItemDefinitionUpdateHandler(dataTypeManager, itemDefinitionUtils, panelNotifier));
    }

    @Test
    public void testUpdateWhenDataTypeIsStructure() {

        final String structure = "Structure";
        final String oldNameValue = "name";
        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);
        final Name oldName = mock(Name.class);
        final QName newQName = mock(QName.class);

        when(dataType.getType()).thenReturn(structure);
        when(dataTypeManager.structure()).thenReturn(structure);
        when(itemDefinition.getName()).thenReturn(oldName);
        when(oldName.getValue()).thenReturn(oldNameValue);
        when(panelNotifier.withOldLocalPart(oldNameValue)).thenReturn(panelNotifier);
        when(panelNotifier.withNewQName(newQName)).thenReturn(panelNotifier);

        doReturn(name).when(handler).makeName(dataType);
        doReturn(newQName).when(handler).makeQName(itemDefinition);

        handler.update(dataType, itemDefinition);

        verify(itemDefinition).setTypeRef(null);
        verify(itemDefinition).setName(name);
        verify(panelNotifier).notifyPanel();
    }

    @Test
    public void testUpdateWhenDataTypeIsNotStructure() {

        final String structure = "Structure";
        final String type = "type";
        final String oldNameValue = "name";
        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final QName qName = mock(QName.class);
        final Name name = mock(Name.class);
        final Name oldName = mock(Name.class);
        final QName newQName = mock(QName.class);
        final List<ItemDefinition> itemDefinitions = new ArrayList<ItemDefinition>() {{
            add(mock(ItemDefinition.class));
        }};

        when(dataType.getType()).thenReturn(type);
        when(dataTypeManager.structure()).thenReturn(structure);
        when(itemDefinition.getItemComponent()).thenReturn(itemDefinitions);
        when(itemDefinition.getName()).thenReturn(oldName);
        when(oldName.getValue()).thenReturn(oldNameValue);
        when(panelNotifier.withOldLocalPart(oldNameValue)).thenReturn(panelNotifier);
        when(panelNotifier.withNewQName(newQName)).thenReturn(panelNotifier);

        doReturn(newQName).when(handler).makeQName(itemDefinition);
        doReturn(qName).when(handler).makeQName(dataType);
        doReturn(name).when(handler).makeName(dataType);

        handler.update(dataType, itemDefinition);

        verify(itemDefinition).setTypeRef(qName);
        verify(itemDefinition).setName(name);
        verify(panelNotifier).notifyPanel();
        assertTrue(itemDefinitions.isEmpty());
    }

    @Test
    public void testMakeName() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "name";

        when(dataType.getName()).thenReturn(expectedName);

        final Name name = handler.makeName(dataType);

        assertEquals(expectedName, name.getValue());
    }

    @Test
    public void testMakeQNameWhenDataTypeIsDefault() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "string";

        when(dataType.getType()).thenReturn(expectedName);

        final QName name = handler.makeQName(dataType);
        final String actualName = name.getLocalPart();

        assertEquals(expectedName, actualName);
        assertEquals(QName.NULL_NS_URI, name.getNamespaceURI());
    }

    @Test
    public void testMakeQNameWhenDataTypeIsNotDefault() {

        final DataType dataType = mock(DataType.class);
        final String expectedName = "tAddress";

        when(dataType.getType()).thenReturn(expectedName);

        final QName name = handler.makeQName(dataType);
        final String actual = name.toString();
        final String expected = "tAddress";

        assertEquals(expected, actual);
        assertEquals(QName.NULL_NS_URI, name.getNamespaceURI());
    }

    @Test
    public void testMakeQNameWithItemDefinition() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name itemDefinitionName = mock(Name.class);
        final String expectedName = "tAddress";

        when(itemDefinitionName.getValue()).thenReturn(expectedName);
        when(itemDefinition.getName()).thenReturn(itemDefinitionName);

        final QName name = handler.makeQName(itemDefinition);
        final String actual = name.toString();
        final String expected = "tAddress";

        assertEquals(expected, actual);
        assertEquals(QName.NULL_NS_URI, name.getNamespaceURI());
    }

    @Test
    public void testMakeAllowedValuesWhenDataTypeConstraintIsBlank() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getConstraint()).thenReturn("");

        final UnaryTests actualAllowedValues = handler.makeAllowedValues(dataType, itemDefinition);

        assertNull(actualAllowedValues);
    }

    @Test
    public void testMakeAllowedValuesWhenDataTypeConstraintIsNull() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        when(dataType.getConstraint()).thenReturn(null);
        when(dataType.getConstraintType()).thenReturn(null);

        final UnaryTests actualAllowedValues = handler.makeAllowedValues(dataType, itemDefinition);

        assertNull(actualAllowedValues);
    }

    @Test
    public void testMakeAllowedValuesWhenDataTypeAndItemDefinitionConstraintAreEqual() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final UnaryTests expectedAllowedValues = mock(UnaryTests.class);
        final String expectedText = "(1..20)";
        final ConstraintType expectedConstraintType = ConstraintType.RANGE;

        when(itemDefinition.getAllowedValues()).thenReturn(expectedAllowedValues);
        when(expectedAllowedValues.getConstraintType()).thenReturn(expectedConstraintType);
        when(expectedAllowedValues.getText()).thenReturn(new Text(expectedText));
        when(dataType.getConstraint()).thenReturn(expectedText);

        final UnaryTests actualAllowedValues = handler.makeAllowedValues(dataType, itemDefinition);

        assertEquals(expectedAllowedValues, actualAllowedValues);
        assertEquals(expectedConstraintType, actualAllowedValues.getConstraintType());
    }

    @Test
    public void testMakeAllowedValuesWhenDataTypeAndItemDefinitionConstraintAreNotEqual() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final String expectedText = "(1..20)";
        final ConstraintType expectedConstraintType = ConstraintType.RANGE;

        when(dataType.getConstraint()).thenReturn(expectedText);
        when(dataType.getConstraintType()).thenReturn(expectedConstraintType);

        final UnaryTests actualAllowedValues = handler.makeAllowedValues(dataType, itemDefinition);

        assertNotNull(actualAllowedValues.getId());
        assertNotNull(actualAllowedValues.getDescription());
        assertEquals(expectedText, actualAllowedValues.getText().getValue());
        assertNull(actualAllowedValues.getExpressionLanguage());
        assertEquals(expectedConstraintType, actualAllowedValues.getConstraintType());
    }
}

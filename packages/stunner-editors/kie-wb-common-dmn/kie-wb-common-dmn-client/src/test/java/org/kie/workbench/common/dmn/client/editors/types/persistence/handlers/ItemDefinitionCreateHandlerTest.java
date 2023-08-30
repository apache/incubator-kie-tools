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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.BELOW;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionCreateHandlerTest {

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    private ItemDefinitionCreateHandler handler;

    @Before
    public void setup() {
        handler = spy(new ItemDefinitionCreateHandler(itemDefinitionUtils, itemDefinitionStore));
    }

    @Test
    public void testAppendItemDefinition() {

        when(itemDefinitionUtils.all()).thenReturn(new ArrayList<>());

        final ItemDefinition itemDefinition = handler.appendItemDefinition();

        assertTrue(itemDefinitionUtils.all().contains(itemDefinition));
    }

    @Test
    public void testInsertNestedItemDefinitionWhenAbsoluteParentIsPresent() {

        final DataType record = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final ItemDefinition relativeParent = mock(ItemDefinition.class);
        final Optional<ItemDefinition> absoluteParent = Optional.of(mock(ItemDefinition.class));
        final List<ItemDefinition> itemDefinitions = new ArrayList<>();
        final String referenceUUID = "referenceUUID";

        when(itemDefinitionStore.get(referenceUUID)).thenReturn(relativeParent);
        when(reference.getUUID()).thenReturn(referenceUUID);
        when(absoluteParent.get().getItemComponent()).thenReturn(itemDefinitions);
        doReturn(absoluteParent).when(handler).lookupAbsoluteParent(referenceUUID);

        final ItemDefinition nestedItemDefinition = handler.insertNested(record, reference);

        assertEquals(nestedItemDefinition, itemDefinitions.get(0));
    }

    @Test
    public void testInsertNestedItemDefinitionWhenAbsoluteParentIsNotPresent() {

        final DataType record = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final ItemDefinition relativeParent = mock(ItemDefinition.class);
        final Optional<ItemDefinition> absoluteParent = Optional.empty();
        final List<ItemDefinition> itemDefinitions = new ArrayList<>();
        final String referenceUUID = "referenceUUID";

        when(itemDefinitionStore.get(referenceUUID)).thenReturn(relativeParent);
        when(reference.getUUID()).thenReturn(referenceUUID);
        when(relativeParent.getItemComponent()).thenReturn(itemDefinitions);
        doReturn(absoluteParent).when(handler).lookupAbsoluteParent(referenceUUID);

        final ItemDefinition nestedItemDefinition = handler.insertNested(record, reference);

        verify(relativeParent).setTypeRef(null);

        assertEquals(nestedItemDefinition, itemDefinitions.get(0));
    }

    @Test
    public void testInsertItemDefinition() {

        final DataType record = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final ItemDefinition itemDefinitionReference = mock(ItemDefinition.class);
        final ItemDefinition item = mock(ItemDefinition.class);
        final List<ItemDefinition> actualItemDefinitions = new ArrayList<ItemDefinition>() {{
            add(item);
            add(item);
            add(itemDefinitionReference);
            add(item);
        }};
        final String uuid = "uuid";

        when(reference.getUUID()).thenReturn(uuid);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinitionReference);
        doReturn(actualItemDefinitions).when(handler).getItemDefinitionSiblings(reference);

        final ItemDefinition itemDefinition = handler.insertSibling(record, reference, BELOW);
        final List<ItemDefinition> expectedItemDefinitions = asList(item, item, itemDefinitionReference, itemDefinition, item);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetItemDefinitionSiblingsWhenParentIsPresent() {

        final DataType reference = mock(DataType.class);
        final String parentUUID = "parentUUID";
        final Optional<ItemDefinition> absoluteParent = Optional.of(mock(ItemDefinition.class));
        final List<ItemDefinition> expectedItemDefinitions = new ArrayList<>();

        when(reference.getParentUUID()).thenReturn(parentUUID);
        when(absoluteParent.get().getItemComponent()).thenReturn(expectedItemDefinitions);
        doReturn(absoluteParent).when(handler).lookupAbsoluteParent(parentUUID);

        final List<ItemDefinition> actualItemDefinitions = handler.getItemDefinitionSiblings(reference);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetItemDefinitionSiblingsWhenParentIsNotPresent() {

        final DataType reference = mock(DataType.class);
        final String parentUUID = "parentUUID";
        final Optional<ItemDefinition> absoluteParent = Optional.empty();
        final List<ItemDefinition> expectedItemDefinitions = new ArrayList<>();

        when(reference.getParentUUID()).thenReturn(parentUUID);
        when(itemDefinitionUtils.all()).thenReturn(expectedItemDefinitions);
        doReturn(absoluteParent).when(handler).lookupAbsoluteParent(parentUUID);

        final List<ItemDefinition> actualItemDefinitions = handler.getItemDefinitionSiblings(reference);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testLookupAbsoluteParentWhenParentIsNotPresent() {

        final String referenceUUID = "referenceUUID";

        when(itemDefinitionStore.get(referenceUUID)).thenReturn(null);

        final Optional<ItemDefinition> absoluteParent = handler.lookupAbsoluteParent(referenceUUID);

        assertFalse(absoluteParent.isPresent());
    }

    @Test
    public void testLookupAbsoluteParentWhenParentIsStructure() {

        final String referenceUUID = "referenceUUID";
        final ItemDefinition parent = mock(ItemDefinition.class);

        when(parent.getTypeRef()).thenReturn(null);
        when(itemDefinitionStore.get(referenceUUID)).thenReturn(parent);

        final Optional<ItemDefinition> actualParent = handler.lookupAbsoluteParent(referenceUUID);
        final Optional<ItemDefinition> expectedParent = Optional.of(parent);

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testLookupAbsoluteParentWhenParentIsNotStructure() {

        final String type = "type";
        final String referenceUUID = "referenceUUID";
        final ItemDefinition parent = mock(ItemDefinition.class);
        final Optional<ItemDefinition> expectedParent = Optional.of(mock(ItemDefinition.class));
        final QName qName = mock(QName.class);

        when(qName.getLocalPart()).thenReturn(type);
        when(parent.getTypeRef()).thenReturn(qName);
        when(itemDefinitionStore.get(referenceUUID)).thenReturn(parent);
        when(itemDefinitionUtils.findByName(type)).thenReturn(expectedParent);

        final Optional<ItemDefinition> actualParent = handler.lookupAbsoluteParent(referenceUUID);

        assertEquals(expectedParent, actualParent);
    }
}

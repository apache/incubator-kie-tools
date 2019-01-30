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
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionDestroyHandlerTest {

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private PropertiesPanelNotifier panelNotifier;

    private ItemDefinitionDestroyHandler handler;

    @Before
    public void setup() {
        handler = spy(new ItemDefinitionDestroyHandler(itemDefinitionStore, dmnGraphUtils, panelNotifier));
    }

    @Test
    public void testDestroy() {

        final String uuid = "uuid";
        final String oldItemDefinitionName = "oldItemDefinitionName";
        final DataType dataType = mock(DataType.class);
        final Name name = mock(Name.class);
        final ItemDefinition itemDefinition = makeItemDefinition();
        final ItemDefinition itemDefinitionParent = makeItemDefinition(itemDefinition);
        final List<ItemDefinition> itemDefinitions = new ArrayList<ItemDefinition>() {{
            add(itemDefinition);
        }};

        when(name.getValue()).thenReturn(oldItemDefinitionName);
        when(itemDefinitionStore.get(uuid)).thenReturn(itemDefinition);
        when(dataType.getUUID()).thenReturn(uuid);
        doReturn(Optional.of(itemDefinitionParent)).when(handler).findItemDefinitionParent(dataType);
        doReturn(name).when(itemDefinition).getName();
        doReturn(itemDefinitions).when(handler).itemDefinitions();
        doNothing().when(handler).notifyPropertiesPanel(anyString());

        handler.destroy(dataType);

        assertEquals(emptyList(), itemDefinitionParent.getItemComponent());
        assertEquals(emptyList(), itemDefinitions);
        verify(handler).notifyPropertiesPanel(oldItemDefinitionName);
    }

    @Test
    public void testNotifyPropertiesPanel() {

        final String destroyedItemDefinition = "destroyedItemDefinition";

        when(panelNotifier.withOldLocalPart(destroyedItemDefinition)).thenReturn(panelNotifier);
        when(panelNotifier.withNewQName(eq(new QName()))).thenReturn(panelNotifier);

        handler.notifyPropertiesPanel(destroyedItemDefinition);

        verify(panelNotifier).notifyPanel();
    }

    @Test
    public void testFindItemDefinitionParentWhenParentDoesNotExist() {

        final DataType dataType = mock(DataType.class);
        final String parentUUID = "parentUUID";

        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(itemDefinitionStore.get(parentUUID)).thenReturn(null);

        final Optional<ItemDefinition> expectedParent = Optional.empty();
        final Optional<ItemDefinition> actualParent = handler.findItemDefinitionParent(dataType);

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testFindItemDefinitionParentWhenParentDoesNotHaveTypeRef() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition parent = makeItemDefinition();
        final String parentUUID = "parentUUID";

        when(parent.getTypeRef()).thenReturn(null);
        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(itemDefinitionStore.get(parentUUID)).thenReturn(parent);

        final Optional<ItemDefinition> expectedParent = Optional.of(parent);
        final Optional<ItemDefinition> actualParent = handler.findItemDefinitionParent(dataType);

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testFindItemDefinitionParentWhenItCouldNotBeFound() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition parent = makeItemDefinition();
        final String parentUUID = "parentUUID";
        final String type = "type";
        final String nameValue = "name";
        final QName parentQName = mock(QName.class);
        final ItemDefinition itemDefinition = makeItemDefinition();
        final Name name = mock(Name.class);
        final List<ItemDefinition> itemDefinitions = singletonList(itemDefinition);

        when(name.getValue()).thenReturn(nameValue);
        when(itemDefinition.getName()).thenReturn(name);
        when(parentQName.getLocalPart()).thenReturn(type);
        when(parent.getTypeRef()).thenReturn(parentQName);
        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(itemDefinitionStore.get(parentUUID)).thenReturn(parent);
        doReturn(itemDefinitions).when(handler).itemDefinitions();

        final Optional<ItemDefinition> expectedParent = Optional.empty();
        final Optional<ItemDefinition> actualParent = handler.findItemDefinitionParent(dataType);

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testFindItemDefinitionParentWhenItCouldBeFound() {

        final DataType dataType = mock(DataType.class);
        final ItemDefinition parent = makeItemDefinition();
        final String parentUUID = "parentUUID";
        final String type = "type";
        final QName parentQName = mock(QName.class);
        final ItemDefinition itemDefinition = makeItemDefinition();
        final Name name = mock(Name.class);
        final List<ItemDefinition> itemDefinitions = singletonList(itemDefinition);

        when(name.getValue()).thenReturn(type);
        when(itemDefinition.getName()).thenReturn(name);
        when(parentQName.getLocalPart()).thenReturn(type);
        when(parent.getTypeRef()).thenReturn(parentQName);
        when(dataType.getParentUUID()).thenReturn(parentUUID);
        when(itemDefinitionStore.get(parentUUID)).thenReturn(parent);
        doReturn(itemDefinitions).when(handler).itemDefinitions();

        final Optional<ItemDefinition> expectedParent = Optional.of(itemDefinition);
        final Optional<ItemDefinition> actualParent = handler.findItemDefinitionParent(dataType);

        assertEquals(expectedParent, actualParent);
    }

    @Test
    public void testItemDefinitions() {

        final Definitions definitions = mock(Definitions.class);
        final ItemDefinition itemDefinition = makeItemDefinition();
        final List<ItemDefinition> expectedItemDefinitions = singletonList(itemDefinition);

        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(expectedItemDefinitions);

        final List<ItemDefinition> actualItemDefinitions = handler.itemDefinitions();

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testItemDefinitionsWhenDefinitionsIsNull() {

        final List<ItemDefinition> expectedItemDefinitions = emptyList();

        when(dmnGraphUtils.getDefinitions()).thenReturn(null);

        final List<ItemDefinition> actualItemDefinitions = handler.itemDefinitions();

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    private ItemDefinition makeItemDefinition(final ItemDefinition... itemDefinitions) {
        final ItemDefinition itemDefinition = spy(new ItemDefinition());
        itemDefinition.getItemComponent().addAll(asList(itemDefinitions));
        return itemDefinition;
    }
}

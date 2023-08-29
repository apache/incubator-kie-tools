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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.DEFAULT_NS_PREFIX;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionHandlerTest {

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private EventSourceMock<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    @Mock
    private PropertiesPanelNotifier panelNotifier;

    private ItemDefinitionHandler handler;

    @Before
    public void setup() {
        handler = spy(new ItemDefinitionHandler(itemDefinitionUtils, refreshDataTypesListEvent, panelNotifier));
    }

    @Test
    public void testUpdate() {

        final ItemDefinition item1 = makeItemDefinition("model1.tPerson", null, true);
        final ItemDefinition item2 = makeItemDefinition("model1.id", "number", true);
        final ItemDefinition item3 = makeItemDefinition("model1.name", "string", true);
        final ItemDefinition item4 = makeItemDefinition("person", "model1.tPerson", false);
        final List<ItemDefinition> itemDefinitions = asList(item1, item2, item3, item4);

        doNothing().when(handler).notifyPropertiesPanel(Mockito.<String>any(), Mockito.<String>any());
        when(itemDefinitionUtils.all()).thenReturn(itemDefinitions);

        handler.update("model1", "model2");

        verify(refreshDataTypesListEvent).fire(any(RefreshDataTypesListEvent.class));

        assertEquals("model2.tPerson", item1.getName().getValue());
        assertNull(item1.getTypeRef());
        verify(handler).notifyPropertiesPanel("model1.tPerson", "model2.tPerson");

        assertEquals("model2.id", item2.getName().getValue());
        assertEquals("number", item2.getTypeRef().getLocalPart());
        verify(handler).notifyPropertiesPanel("model1.id", "model2.id");

        assertEquals("model2.name", item3.getName().getValue());
        assertEquals("string", item3.getTypeRef().getLocalPart());
        verify(handler).notifyPropertiesPanel("model1.name", "model2.name");

        assertEquals("person", item4.getName().getValue());
        assertEquals("model2.tPerson", item4.getTypeRef().getLocalPart());
    }

    @Test
    public void testDestroy() {

        final ItemDefinition item1 = makeItemDefinition("model1.tPerson", null, true);
        final ItemDefinition item2 = makeItemDefinition("model1.id", "number", true);
        final ItemDefinition item3 = makeItemDefinition("model1.name", "string", true);
        final ItemDefinition item4 = makeItemDefinition("person", "model1.tPerson", false);
        final ItemDefinition item5 = makeItemDefinition("tUUID", "string", false);
        final List<ItemDefinition> itemDefinitions = new ArrayList<>(asList(item1, item2, item3, item4, item5));

        when(itemDefinitionUtils.all()).thenReturn(itemDefinitions);

        handler.destroy("model1");

        verify(refreshDataTypesListEvent).fire(any(RefreshDataTypesListEvent.class));

        assertEquals(1, itemDefinitions.size());
        assertEquals("tUUID", itemDefinitions.get(0).getName().getValue());
        assertEquals("string", itemDefinitions.get(0).getTypeRef().getLocalPart());
    }

    @Test
    public void testNotifyPropertiesPanel() {

        final String oldName = "oldName";
        final String newName = "newName";
        final QName qName = new QName(NULL_NS_URI, newName);

        when(panelNotifier.withOldLocalPart(oldName)).thenReturn(panelNotifier);
        when(panelNotifier.withNewQName(eq(qName))).thenReturn(panelNotifier);
        when(itemDefinitionUtils.normaliseTypeRef(qName)).thenReturn(qName);

        handler.notifyPropertiesPanel(oldName, newName);

        verify(panelNotifier).notifyPanel();
    }

    private ItemDefinition makeItemDefinition(final String name,
                                              final String type,
                                              final boolean isImported) {

        final ItemDefinition itemDefinition = new ItemDefinition();

        itemDefinition.setName(new Name(name));
        itemDefinition.setTypeRef(type == null ? null : new QName(NULL_NS_URI, type, DEFAULT_NS_PREFIX));
        itemDefinition.setAllowOnlyVisualChange(isImported);

        return itemDefinition;
    }
}

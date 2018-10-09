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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionCreateHandlerTest {

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private ItemDefinitionUpdateHandler updateHandler;

    private ItemDefinitionCreateHandler handler;

    @Before
    public void setup() {
        handler = spy(new ItemDefinitionCreateHandler(dataTypeManager, itemDefinitionUtils, updateHandler));
    }

    @Test
    public void testCreate() {

        final DataType expectedDataType = mock(DataType.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);

        doReturn(itemDefinition).when(handler).makeItemDefinition();
        when(dataTypeManager.withDataType(any())).thenReturn(dataTypeManager);
        when(dataTypeManager.withItemDefinition(any())).thenReturn(dataTypeManager);
        when(dataTypeManager.withIndexedItemDefinition()).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(expectedDataType);

        final DataType actualDataType = handler.create(expectedDataType);
        final InOrder inOrder = Mockito.inOrder(dataTypeManager);

        inOrder.verify(dataTypeManager).withDataType(expectedDataType);
        inOrder.verify(dataTypeManager).withItemDefinition(itemDefinition);
        inOrder.verify(dataTypeManager).withIndexedItemDefinition();
        inOrder.verify(dataTypeManager).get();

        assertEquals(expectedDataType, actualDataType);
    }

    @Test
    public void testCreateItemDefinition() {

        when(itemDefinitionUtils.all()).thenReturn(new ArrayList<>());

        final ItemDefinition itemDefinition = handler.makeItemDefinition();

        assertTrue(itemDefinitionUtils.all().contains(itemDefinition));
    }
}

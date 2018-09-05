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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeFactory;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeModalTest {

    @Mock
    private DataTypeModal.View view;

    @Mock
    private DataTypeList treeList;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private DataTypeFactory dataTypeFactory;

    @Mock
    private ItemDefinitionStore definitionStore;

    @Mock
    private DataTypeStore dataTypeStore;

    @Captor
    private ArgumentCaptor<List<DataType>> dataTypesCaptor;

    private FakeDataTypeModal modal;

    @Before
    public void setup() {
        modal = spy(new FakeDataTypeModal());

        doNothing().when(modal).superSetup();
        doNothing().when(modal).superShow();
    }

    @Test
    public void testSetup() {

        final String expectedWidgetWidth = "800px";

        modal.setup();

        verify(modal).setWidth(eq(expectedWidgetWidth));
        verify(view).setup(treeList);
    }

    @Test
    public void testShow() {

        modal.show();

        final InOrder inOrder = Mockito.inOrder(modal);

        inOrder.verify(modal).cleanDataTypeStore();
        inOrder.verify(modal).loadDataTypes();
        inOrder.verify(modal).superShow();
    }

    @Test
    public void testCleanDataTypeStore() {
        modal.cleanDataTypeStore();

        verify(definitionStore).clear();
        verify(dataTypeStore).clear();
    }

    @Test
    public void testLoadDataTypes() {

        final ItemDefinition itemDefinition1 = makeItem("itemDefinition1");
        final ItemDefinition itemDefinition2 = makeItem("itemDefinition2");
        final ItemDefinition itemDefinition3 = makeItem("itemDefinition3");
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);

        final List<ItemDefinition> itemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3);

        when(itemDefinitionUtils.all()).thenReturn(itemDefinitions);
        doReturn(dataType1).when(dataTypeFactory).makeStandardDataType(itemDefinition1);
        doReturn(dataType2).when(dataTypeFactory).makeStandardDataType(itemDefinition2);
        doReturn(dataType3).when(dataTypeFactory).makeStandardDataType(itemDefinition3);

        modal.loadDataTypes();

        verify(treeList).setupItems(dataTypesCaptor.capture());

        final List<DataType> dataTypes = dataTypesCaptor.getValue();

        assertThat(dataTypes).containsExactly(dataType1, dataType2, dataType3);
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }

    class FakeDataTypeModal extends DataTypeModal {

        public FakeDataTypeModal() {
            super(view, treeList, itemDefinitionUtils, dataTypeFactory, definitionStore, dataTypeStore);
        }

        @Override
        protected void setWidth(final String width) {
            // Nothing.
        }
    }
}

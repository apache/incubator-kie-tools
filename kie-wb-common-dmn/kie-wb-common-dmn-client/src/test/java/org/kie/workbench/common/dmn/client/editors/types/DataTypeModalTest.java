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
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.JQueryEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessages;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
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
    private ItemDefinitionStore definitionStore;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DataTypeManagerStackStore stackIndex;

    @Mock
    private DataTypeFlashMessages flashMessages;

    @Captor
    private ArgumentCaptor<List<DataType>> dataTypesCaptor;

    private DataTypeModal modal;

    @Before
    public void setup() {
        modal = spy(new DataTypeModal(view, treeList, itemDefinitionUtils, definitionStore, dataTypeStore, dataTypeManager, stackIndex, flashMessages));

        doNothing().when(modal).setupOnCloseCallback();
        doNothing().when(modal).superSetup();
        doNothing().when(modal).superShow();
    }

    @Test
    public void testSetup() {

        doNothing().when(modal).setDataTypeModalCSSClasses();

        modal.setup();

        verify(modal).setDataTypeModalCSSClasses();
        verify(view).setup(flashMessages, treeList);
    }

    @Test
    public void testShow() {

        modal.show();

        final InOrder inOrder = Mockito.inOrder(modal);

        inOrder.verify(modal).setupOnCloseCallback();
        inOrder.verify(modal).cleanDataTypeStore();
        inOrder.verify(modal).loadDataTypes();
        inOrder.verify(modal).superShow();
    }

    @Test
    public void testCleanDataTypeStore() {
        modal.cleanDataTypeStore();

        verify(definitionStore).clear();
        verify(dataTypeStore).clear();
        verify(stackIndex).clear();
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
        doReturn(dataType1).when(modal).makeDataType(itemDefinition1);
        doReturn(dataType2).when(modal).makeDataType(itemDefinition2);
        doReturn(dataType3).when(modal).makeDataType(itemDefinition3);

        modal.loadDataTypes();

        verify(treeList).setupItems(dataTypesCaptor.capture());

        final List<DataType> dataTypes = dataTypesCaptor.getValue();

        assertThat(dataTypes).containsExactly(dataType1, dataType2, dataType3);
    }

    @Test
    public void testMakeDataType() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final DataType expectedDataType = mock(DataType.class);

        when(dataTypeManager.from(itemDefinition)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(expectedDataType);

        final DataType actualDataType = modal.makeDataType(itemDefinition);

        assertEquals(expectedDataType, actualDataType);
    }

    @Test
    public void testSetDataTypeModalCSSClasses() {

        final Element modalDialogElement = mock(Element.class);
        final DOMTokenList classList = mock(DOMTokenList.class);

        modalDialogElement.classList = classList;
        doReturn(modalDialogElement).when(modal).getModalDialogElement();

        modal.setDataTypeModalCSSClasses();

        verify(classList).add("kie-data-types-modal");
    }

    @Test
    public void testGetModalDialogElement() {

        final DataTypeModal.View view = mock(DataTypeModal.View.class);
        final HTMLElement body = mock(HTMLElement.class);
        final Node modalBodyNode = mock(Node.class);
        final Node modalContentNode = mock(Node.class);
        final Node modalDialogNode = mock(Node.class);
        final Node modalParentNode = mock(Node.class);
        final Element expectedDialog = mock(Element.class);

        when(modal.getView()).thenReturn(view);
        when(view.getBody()).thenReturn(body);
        when(modalParentNode.querySelector(".modal-dialog")).thenReturn(expectedDialog);
        body.parentNode = modalBodyNode;
        modalBodyNode.parentNode = modalContentNode;
        modalContentNode.parentNode = modalDialogNode;
        modalDialogNode.parentNode = modalParentNode;

        final Element actualDialog = modal.getModalDialogElement();

        assertEquals(expectedDialog, actualDialog);
    }

    @Test
    public void testOnCloseEvent() {

        modal.onCloseEvent(mock(JQueryEvent.class));

        verify(flashMessages).hideMessages();
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }
}

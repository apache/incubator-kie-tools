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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeListItemTest {

    @Mock
    private DataTypeListItem.View view;

    @Mock
    private DataTypeSelect dataTypeSelectComponent;

    @Mock
    private DataType dataType;

    @Mock
    private DataTypeList dataTypeList;

    private DataTypeListItem listItem;

    @Before
    public void setup() {
        listItem = spy(new DataTypeListItem(view, dataTypeSelectComponent));
        listItem.init(dataTypeList);
    }

    @Test
    public void testSetup() {
        listItem.setup();

        verify(view).init(listItem);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        HTMLElement actualElement = listItem.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testSetupDataType() {

        final DataType expectedDataType = this.dataType;
        final int expectedLevel = 1;

        listItem.setupDataType(expectedDataType, expectedLevel);

        final InOrder inOrder = inOrder(listItem);
        inOrder.verify(listItem).setupSelectComponent();
        inOrder.verify(listItem).setupView();

        assertEquals(expectedDataType, listItem.getDataType());
        assertEquals(expectedLevel, listItem.getLevel());
    }

    @Test
    public void testSetupSelectComponent() {

        final DataType dataType = mock(DataType.class);
        when(listItem.getDataType()).thenReturn(dataType);

        listItem.setupSelectComponent();

        verify(dataTypeSelectComponent).init(listItem, dataType);
    }

    @Test
    public void testSetupView() {

        final DataType dataType = mock(DataType.class);
        when(listItem.getDataType()).thenReturn(dataType);

        listItem.setupView();

        verify(view).setupSelectComponent(dataTypeSelectComponent);
        verify(view).setDataType(dataType);
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenViewIsCollapsed() {

        when(view.isCollapsed()).thenReturn(true);

        listItem.expandOrCollapseSubTypes();

        verify(listItem).expand();
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenViewIsNotCollapsed() {

        when(view.isCollapsed()).thenReturn(false);

        listItem.expandOrCollapseSubTypes();

        verify(listItem).collapse();
    }

    @Test
    public void testCollapse() {

        listItem.collapse();

        verify(view).collapse();
    }

    @Test
    public void testExpand() {

        listItem.expand();

        verify(view).expand();
    }

    @Test
    public void testRefreshSubItems() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> dataTypes = Collections.singletonList(dataType);

        listItem.refreshSubItems(dataTypes);

        verify(dataTypeList).refreshSubItems(listItem, dataTypes);
        verify(view).enableFocusMode();
        verify(view).toggleArrow(anyBoolean());
    }

    @Test
    public void testEnableEditMode() {

        listItem.enableEditMode();

        verify(view).showSaveButton();
        verify(view).showDataTypeNameInput();
        verify(view).enableFocusMode();
        verify(dataTypeSelectComponent).enableEditMode();
    }

    @Test
    public void testDisableEditMode() {

        doNothing().when(listItem).discardNewDataType();
        doNothing().when(listItem).closeEditMode();

        listItem.disableEditMode();

        verify(listItem).discardNewDataType();
        verify(listItem).closeEditMode();
    }

    @Test
    public void testSaveAndCloseEditMode() {

        doNothing().when(listItem).saveNewDataType();
        doNothing().when(listItem).closeEditMode();

        listItem.saveAndCloseEditMode();

        verify(listItem).saveNewDataType();
        verify(listItem).closeEditMode();
    }

    @Test
    public void testDiscardNewDataType() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> subDataTypes = Collections.emptyList();

        when(dataType.getSubDataTypes()).thenReturn(subDataTypes);
        doReturn(dataType).when(listItem).getDataType();

        listItem.discardNewDataType();

        verify(view).setDataType(dataType);
        verify(listItem).setupSelectComponent();
        verify(listItem).refreshSubItems(subDataTypes);
    }

    @Test
    public void testCloseEditMode() {

        listItem.closeEditMode();

        verify(view).showEditButton();
        verify(view).hideDataTypeNameInput();
        verify(view).disableFocusMode();
        verify(dataTypeSelectComponent).disableEditMode();
    }

    @Test
    public void testSaveNewDataType() {

        final DataType dataType = spy(makeDataType());
        final String expectedName = "name";
        final String expectedType = "type";
        final List<DataType> expectedSubDataTypes = Arrays.asList(mock(DataType.class), mock(DataType.class));

        when(view.getName()).thenReturn(expectedName);
        when(dataTypeSelectComponent.getValue()).thenReturn(expectedType);
        when(dataTypeSelectComponent.getSubDataTypes()).thenReturn(expectedSubDataTypes);
        doReturn(dataType).when(listItem).getDataType();
        doNothing().when(dataType).update();

        listItem.saveNewDataType();

        assertEquals(expectedName, dataType.getName());
        assertEquals(expectedType, dataType.getType());
        assertEquals(expectedSubDataTypes, dataType.getSubDataTypes());
        verify(dataType).update();
    }

    private DataType makeDataType() {
        return new DataType("", "");
    }
}

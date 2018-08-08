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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.InOrder;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeTreeGridItemTest {

    @Mock
    private DataTypeTreeGridItem.View view;

    @Mock
    private DataTypeSelect typeSelect;

    @Mock
    private DataType dataType;

    private DataTypeTreeGridItem gridItem;

    @Before
    public void setup() {
        gridItem = spy(new DataTypeTreeGridItem(view, typeSelect));
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        HTMLElement actualElement = gridItem.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testSetupDataType() {

        final DataType expectedDataType = this.dataType;
        final int expectedLevel = 1;

        gridItem.setupDataType(expectedDataType, expectedLevel);

        final InOrder inOrder = inOrder(gridItem);
        inOrder.verify(gridItem).setupSelectComponent();
        inOrder.verify(gridItem).setupView();

        assertEquals(expectedDataType, gridItem.getDataType());
        assertEquals(expectedLevel, gridItem.getLevel());
    }

    @Test
    public void testSetupSelectComponent() {

        final DataType dataType = mock(DataType.class);
        when(gridItem.getDataType()).thenReturn(dataType);

        gridItem.setupSelectComponent();

        verify(typeSelect).init(dataType);
    }

    @Test
    public void testSetupView() {

        final DataType dataType = mock(DataType.class);
        when(gridItem.getDataType()).thenReturn(dataType);

        gridItem.setupView();

        verify(view).setupSelectComponent(typeSelect);
        verify(view).setDataType(dataType);
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenViewIsCollapsed() {

        when(view.isCollapsed()).thenReturn(true);

        gridItem.expandOrCollapseSubTypes();

        verify(view).expand();
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenViewIsNotCollapsed() {

        when(view.isCollapsed()).thenReturn(false);

        gridItem.expandOrCollapseSubTypes();

        verify(view).collapse();
    }

    @Test
    public void testCollapseSubDataTypes() {

        final DataType subDataType1 = mock(DataType.class);
        final DataType subDataType2 = mock(DataType.class);

        when(dataType.getSubDataTypes()).thenReturn(asList(subDataType1, subDataType2));

        gridItem.collapseSubDataTypes(dataType);

        verify(view).collapseSubType(subDataType1);
        verify(view).collapseSubType(subDataType2);
    }

    @Test
    public void testExpandSubDataTypes() {

        final DataType subDataType1 = mock(DataType.class);
        final DataType subDataType2 = mock(DataType.class);

        when(dataType.getSubDataTypes()).thenReturn(asList(subDataType1, subDataType2));

        gridItem.expandSubDataTypes(dataType);

        verify(view).expandSubType(subDataType1);
        verify(view).expandSubType(subDataType2);
    }

    @Test
    public void testExpandSubDataTypesWithoutParameter() {

        final DataType dataType = mock(DataType.class);

        doReturn(dataType).when(gridItem).getDataType();

        gridItem.expandSubDataTypes();

        verify(gridItem).expandSubDataTypes(dataType);
    }

    @Test
    public void testCollapseSubDataTypesWithoutParameter() {

        final DataType dataType = mock(DataType.class);

        doReturn(dataType).when(gridItem).getDataType();

        gridItem.collapseSubDataTypes();

        verify(gridItem).collapseSubDataTypes(dataType);
    }
}

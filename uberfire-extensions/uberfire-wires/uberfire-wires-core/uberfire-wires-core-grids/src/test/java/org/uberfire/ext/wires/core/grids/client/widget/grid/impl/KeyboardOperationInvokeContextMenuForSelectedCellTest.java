/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.Collections;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KeyboardOperationInvokeContextMenuForSelectedCellTest {

    @Mock
    private GridLayer gridLayerMock;

    @Mock
    private GridWidget gridWidgetMock;

    @Mock
    private GridData gridDataMock;

    private KeyboardOperationInvokeContextMenuForSelectedCell testedOperation;

    @Before
    public void setUp() {
        testedOperation = new KeyboardOperationInvokeContextMenuForSelectedCell(gridLayerMock);

        doReturn(gridDataMock).when(gridWidgetMock).getModel();
    }

    @Test
    public void testKeysThatFireOperation() {
        assertThat(testedOperation.isControlKeyDown()).isEqualTo(KeyboardOperation.TriStateBoolean.TRUE);
        assertThat(testedOperation.isShiftKeyDown()).isEqualTo(KeyboardOperation.TriStateBoolean.DONT_CARE);
        assertThat(testedOperation.getKeyCode()).isEqualTo(KeyCodes.KEY_SPACE);
    }

    @Test
    public void testIsExecutableNoSelectedCells() {
        doReturn(Collections.emptyList()).when(gridDataMock).getSelectedCells();

        assertThat(testedOperation.isExecutable(gridWidgetMock)).isFalse();
    }

    @Test
    public void testIsExecutableSelectedHeader() {
        doReturn(Collections.singletonList(mock(GridData.SelectedCell.class))).when(gridDataMock).getSelectedHeaderCells();

        assertThat(testedOperation.isExecutable(gridWidgetMock)).isTrue();
    }

    @Test
    public void testIsExecutableSelectedCells() {
        doReturn(Collections.singletonList(mock(GridData.SelectedCell.class))).when(gridDataMock).getSelectedCells();

        assertThat(testedOperation.isExecutable(gridWidgetMock)).isTrue();
    }

    @Test
    public void testPerformWithSelectedHeader() {
        // non null indexes to verify the coordinates translation
        final int headerRowIndex = 1;
        final int headerColumnIndex = 2;
        // translated column index as just one column used in test
        final int uiColumnIndex = 0;

        final GridData.SelectedCell selectedCellMock = mock(GridData.SelectedCell.class);
        doReturn(Collections.singletonList(selectedCellMock)).when(gridDataMock).getSelectedHeaderCells();
        doReturn(headerRowIndex).when(selectedCellMock).getRowIndex();
        doReturn(headerColumnIndex).when(selectedCellMock).getColumnIndex();

        final GridColumn columnMock = mock(GridColumn.class);
        doReturn(headerColumnIndex).when(columnMock).getIndex();

        doReturn(Collections.singletonList(columnMock)).when(gridDataMock).getColumns();

        doReturn(true).when(gridWidgetMock).showContextMenuForHeader(headerRowIndex, uiColumnIndex);

        assertThat(testedOperation.perform(gridWidgetMock, false, true))
                .as("Menu should be shown successfully")
                .isTrue();
        verify(gridWidgetMock).showContextMenuForHeader(headerRowIndex, uiColumnIndex);
    }

    @Test
    public void testPerformWithSelectedCell() {
        // non null indexes to verify the coordinates translation
        final int rowIndex = 1;
        final int columnIndex = 2;
        // translated column index as just one column used in test
        final int uiColumnIndex = 0;

        final GridData.SelectedCell selectedCellMock = mock(GridData.SelectedCell.class);
        doReturn(Collections.singletonList(selectedCellMock)).when(gridDataMock).getSelectedCells();
        doReturn(rowIndex).when(selectedCellMock).getRowIndex();
        doReturn(columnIndex).when(selectedCellMock).getColumnIndex();

        final GridColumn columnMock = mock(GridColumn.class);
        doReturn(columnIndex).when(columnMock).getIndex();

        doReturn(Collections.singletonList(columnMock)).when(gridDataMock).getColumns();

        doReturn(selectedCellMock).when(gridDataMock).getSelectedCellsOrigin();
        doReturn(true).when(gridWidgetMock).showContextMenuForCell(rowIndex, uiColumnIndex);

        assertThat(testedOperation.perform(gridWidgetMock, false, true))
                .as("Menu should be shown successfully")
                .isTrue();
        verify(gridWidgetMock).showContextMenuForCell(rowIndex, uiColumnIndex);
    }

    @Test
    public void testPerformNothingSelected() {
        // non null indexes to verify the coordinates translation
        final int rowIndex = 1;
        final int columnIndex = 2;
        // translated column index as just one column used in test
        final int uiColumnIndex = 0;

        final GridData.SelectedCell selectedCellMock = mock(GridData.SelectedCell.class);
        doReturn(rowIndex).when(selectedCellMock).getRowIndex();
        doReturn(columnIndex).when(selectedCellMock).getColumnIndex();

        final GridColumn columnMock = mock(GridColumn.class);
        doReturn(columnIndex).when(columnMock).getIndex();

        doReturn(Collections.singletonList(columnMock)).when(gridDataMock).getColumns();

        doReturn(null).when(gridDataMock).getSelectedCellsOrigin();
        doReturn(true).when(gridWidgetMock).showContextMenuForCell(rowIndex, uiColumnIndex);

        assertThat(testedOperation.perform(gridWidgetMock, false, true))
                .as("Menu should not be shown successfully")
                .isFalse();
        verify(gridWidgetMock, never()).showContextMenuForCell(anyInt(), anyInt());
    }
}

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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.Arrays;
import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKeyboardEditHandlerTest {

    @Mock
    private GridLayer gridLayerMock;

    @Mock
    private ScenarioGrid scenarioGridMock;

    @Mock
    private ScenarioGridModel scenarioGridModelMock;

    @Mock
    private ScenarioGridColumn gridColumnMock;

    @Mock
    private ScenarioGridColumn gridColumnMock2;

    private ScenarioSimulationKeyboardEditHandler handler;
    private GridData.SelectedCell selectedCell;
    private GridData.SelectedCell selectedCell2;

    @Before
    public void setup() {
        selectedCell = new GridData.SelectedCell(0, 0);
        selectedCell2 = new GridData.SelectedCell(1, 1);
        handler = spy(new ScenarioSimulationKeyboardEditHandler(gridLayerMock) {
            @Override
            protected boolean startEdit(ScenarioGrid scenarioGrid, int uiColumnIndex, int uiRowIndex, ScenarioGridColumn column, boolean isHeader) {
                return true;
            }
        });
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridModelMock.getColumns()).thenReturn(Arrays.asList(gridColumnMock, gridColumnMock2));
    }

    @Test
    public void isExecutable_NoSelection() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.emptyList());
        assertFalse(handler.isExecutable(scenarioGridMock));
    }

    @Test
    public void isExecutable_CellSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Arrays.asList(selectedCell));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.emptyList());
        assertTrue(handler.isExecutable(scenarioGridMock));
    }

    @Test
    public void isExecutable_CellsSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Arrays.asList(selectedCell, selectedCell2));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.emptyList());
        assertFalse(handler.isExecutable(scenarioGridMock));
    }

    @Test
    public void isExecutable_HeaderCellSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Arrays.asList(selectedCell, selectedCell2));
        assertTrue(handler.isExecutable(scenarioGridMock));
    }

    @Test
    public void isExecutable_HeaderCellsSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Arrays.asList(selectedCell));
        assertTrue(handler.isExecutable(scenarioGridMock));
    }

    @Test
    public void isExecutable_CellsAndHeaderCellsSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Arrays.asList(selectedCell));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Arrays.asList(selectedCell2));
        assertFalse(handler.isExecutable(scenarioGridMock));
    }

    @Test
    public void perform_NoSelection() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.emptyList());
        assertFalse(handler.perform(scenarioGridMock, false, false));
        verify(handler, never()).startEdit(any(), anyInt(), anyInt(), any(), anyBoolean());
    }

    @Test
    public void perform_CellSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Arrays.asList(selectedCell));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedCellsOrigin()).thenReturn(selectedCell);
        handler.perform(scenarioGridMock, false, false);
        verify(handler, times(1)).startEdit(eq(scenarioGridMock), eq(0), eq(0), eq(gridColumnMock), eq(false));
    }

    @Test
    public void perform_CellsSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Arrays.asList(selectedCell, selectedCell2));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedCellsOrigin()).thenReturn(selectedCell);
        assertFalse(handler.perform(scenarioGridMock, false, false));
        verify(handler, never()).startEdit(any(), anyInt(), anyInt(), any(), anyBoolean());
    }

    @Test
    public void perform_HeaderCellSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Arrays.asList(selectedCell));
        handler.perform(scenarioGridMock, false, false);
        verify(handler, times(1)).startEdit(eq(scenarioGridMock), eq(0), eq(0), eq(gridColumnMock), eq(true));
    }

    @Test
    public void perform_HeaderCellsSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Collections.emptyList());
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Arrays.asList(selectedCell, selectedCell2));
        handler.perform(scenarioGridMock, false, false);
        verify(handler, times(1)).startEdit(eq(scenarioGridMock), eq(0), eq(0), eq(gridColumnMock), eq(true));
    }

    @Test
    public void perform_CellAndHeaderCellsSelected() {
        when(scenarioGridModelMock.getSelectedCells()).thenReturn(Arrays.asList(selectedCell));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Arrays.asList(selectedCell2));
        assertFalse(handler.perform(scenarioGridMock, false, false));
        verify(handler, never()).startEdit(any(), anyInt(), anyInt(), any(), anyBoolean());
    }
}

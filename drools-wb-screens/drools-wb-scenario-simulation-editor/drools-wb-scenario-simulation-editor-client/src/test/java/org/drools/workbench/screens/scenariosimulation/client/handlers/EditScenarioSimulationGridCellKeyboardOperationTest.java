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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class EditScenarioSimulationGridCellKeyboardOperationTest extends AbstractScenarioSimulationGridHandlerTest {

    @Captor
    private ArgumentCaptor<GridBodyCellEditContext> editHeaderContextCaptor;

    @Mock
    private GridLayer gridLayer;

    private EditScenarioSimulationGridCellKeyboardOperation editOperation;

    @Before
    public void setUp() {
        super.setUp();

        editOperation = new EditScenarioSimulationGridCellKeyboardOperation(gridLayer);
    }

    @Test
    public void testEditDataCell() {
        final GridData.SelectedCell selectedDataCell = mock(GridData.SelectedCell.class);
        final int selectedRowIndex = 0;
        final int selectedColumnIndex = 0;
        when(selectedDataCell.getRowIndex()).thenReturn(selectedRowIndex);
        when(selectedDataCell.getColumnIndex()).thenReturn(selectedColumnIndex);

        when(scenarioGridModelMock.getSelectedCellsOrigin()).thenReturn(selectedDataCell);

        editOperation.editCell(scenarioGridMock);

        verify(scenarioGridMock).startEditingCell(0, 0);
        verify(informationHeaderMetaDataMock, never()).edit(any());
    }

    @Test
    public void testEditHeaderCell() {
        final GridData.SelectedCell selectedHeaderCell = mock(GridData.SelectedCell.class);
        final int selectedRowIndex = 0;
        final int selectedColumnIndex = 0;
        when(selectedHeaderCell.getRowIndex()).thenReturn(selectedRowIndex);
        when(selectedHeaderCell.getColumnIndex()).thenReturn(selectedColumnIndex);

        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.singletonList(selectedHeaderCell));

        editOperation.editCell(scenarioGridMock);

        verify(scenarioGridMock, never()).startEditingCell(anyInt(), anyInt());
        verify(informationHeaderMetaDataMock).edit(editHeaderContextCaptor.capture());

        final GridBodyCellEditContext editHeaderContext = editHeaderContextCaptor.getValue();
        assertThat(editHeaderContext.getColumnIndex()).isEqualTo(0);
        assertThat(editHeaderContext.getRowIndex()).isEqualTo(0);
    }
}

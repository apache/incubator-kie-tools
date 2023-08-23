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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.List;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridDataTest {

    @Mock
    private ExpressionEditorColumn resizableUiColumn;

    @Mock
    private DMNGridCell uiCell;

    private DMNGridData uiModel;

    @Before
    public void setup() {
        this.uiModel = new DMNGridData();
        IntStream.range(0, 3).forEach(i -> uiModel.appendRow(new BaseGridRow()));
        IntStream.range(0, 2).forEach(i -> {
            final DMNGridColumn uiColumn = mock(DMNGridColumn.class);
            when(uiColumn.getIndex()).thenReturn(i);
            uiModel.appendColumn(uiColumn);
        });
        uiModel.appendColumn(resizableUiColumn);
        when(resizableUiColumn.getIndex()).thenReturn(2);
    }

    @Test
    public void testSelectCell() {
        final GridData.Range r = uiModel.selectCell(0, 1);

        assertThat(r.getMinRowIndex()).isEqualTo(0);
        assertThat(r.getMaxRowIndex()).isEqualTo(0);

        final List<GridData.SelectedCell> selections = uiModel.getSelectedCells();

        assertThat(selections).isNotEmpty();
        assertThat(selections.size()).isEqualTo(1);

        assertThat(selections).contains(new GridData.SelectedCell(0, 1));
    }

    @Test
    public void testSelectCells() {
        final GridData.Range r = uiModel.selectCells(0, 1, 1, 2);

        assertThat(r.getMinRowIndex()).isEqualTo(0);
        assertThat(r.getMaxRowIndex()).isEqualTo(0);

        final List<GridData.SelectedCell> selections = uiModel.getSelectedCells();

        assertThat(selections).isNotEmpty();
        assertThat(selections.size()).isEqualTo(2);

        assertThat(selections).contains(new GridData.SelectedCell(0, 1),
                                        new GridData.SelectedCell(1, 1));
    }

    @Test
    public void testSetCell() {
        final GridData.Range r = uiModel.setCell(0, 1, () -> uiCell);

        assertThat(r.getMinRowIndex()).isEqualTo(0);
        assertThat(r.getMaxRowIndex()).isEqualTo(0);
        assertThat(uiModel.getCell(0, 1)).isSameAs(uiCell);
    }

    @Test
    public void testSetCellValue() {
        final GridData.Range r = uiModel.setCellValue(0, 1, new BaseGridCellValue<>("value"));

        assertThat(r.getMinRowIndex()).isEqualTo(0);
        assertThat(r.getMaxRowIndex()).isEqualTo(0);
        assertThat(uiModel.getCell(0, 1).getValue().getValue()).isEqualTo("value");
    }

    @Test
    public void testDeleteCell() {
        final GridData.Range r = uiModel.deleteCell(0, 1);

        assertThat(r.getMinRowIndex()).isEqualTo(0);
        assertThat(r.getMaxRowIndex()).isEqualTo(0);
        assertThat(uiModel.getCell(0, 1)).isNull();
    }
}

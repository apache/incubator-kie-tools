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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.StringColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class KeyboardOperationEditGridCellTest {

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private GridLayer gridLayer;

    private KeyboardOperationEditGridCell operation;

    private BaseGridData model;

    private BaseGridWidget gridWidget;

    @Before
    public void setUp() throws Exception {
        operation = new KeyboardOperationEditGridCell(gridLayer);

        model = new BaseGridData(false);

        gridWidget = spy(new BaseGridWidget(model,
                                            selectionManager,
                                            pinnedModeManager,
                                            renderer));
    }

    @Test
    public void testReactsOnKey() {
        assertThat(operation.getKeyCode()).isEqualTo(KeyCodes.KEY_ENTER);
    }

    @Test
    public void testMultipleDataCells() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        model.appendColumn(new RowNumberColumn());
        model.appendColumn(testingDmnColumn);
        model.appendRow(new BaseGridRow());

        model.selectCell(0, 0);
        model.selectCell(0, 1);

        operation.editCell(gridWidget);

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(testingDmnColumn, never()).startEditingHeaderCell(anyInt());
    }

    @Test
    public void testMultipleHeaderCells() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        model.appendColumn(new RowNumberColumn());
        model.appendColumn(testingDmnColumn);
        model.appendRow(new BaseGridRow());

        model.selectHeaderCell(0, 0);
        model.selectHeaderCell(0, 1);

        operation.editCell(gridWidget);

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(testingDmnColumn, never()).startEditingHeaderCell(anyInt());
    }

    @Test
    public void testEnterInnerGrid() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        model.appendColumn(new RowNumberColumn());
        model.appendColumn(testingDmnColumn);
        model.appendRow(new BaseGridRow());

        final ExpressionCellValue innerGridCellValue = mock(ExpressionCellValue.class);
        final BaseExpressionGrid innerGrid = mock(BaseExpressionGrid.class);
        when(innerGridCellValue.getValue()).thenReturn(Optional.of(innerGrid));

        model.setCellValue(0, 1, innerGridCellValue);
        model.selectCell(0, 1);

        doReturn(false).when(gridWidget).startEditingCell(0, 1);

        operation.editCell(gridWidget);

        verify(testingDmnColumn, never()).startEditingHeaderCell(anyInt());
        verify(gridWidget).startEditingCell(0, 1);
        verify(gridLayer).select(innerGrid);
        verify(innerGrid).selectFirstCell();
    }

    @Test
    public void testEditDmnColumnHeaderRow() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        model.appendColumn(new RowNumberColumn());
        model.appendColumn(testingDmnColumn);

        model.selectHeaderCell(0, 1);

        operation.editCell(gridWidget);

        final int expectedHeaderRowIndex = 0;
        verify(testingDmnColumn).startEditingHeaderCell(expectedHeaderRowIndex);
        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
    }

    private DMNGridColumn<BaseGridWidget, String> testingDmnColumn() {
        return spy(new DMNGridColumn<BaseGridWidget, String>(new BaseHeaderMetaData("column title"),
                                                             new StringColumnRenderer(),
                                                             gridWidget) {
        });
    }
}

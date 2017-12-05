/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorColumnTest {

    private static final double DEFAULT_WIDTH = 100D;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    private BaseGridData gridData;

    private GridWidget widget;

    private ExpressionEditorColumn column;

    @Before
    public void setUp() throws Exception {
        gridData = new BaseGridData();
        widget = new BaseGridWidget(gridData, selectionManager, pinnedModeManager, renderer);
        column = new ExpressionEditorColumn(new BaseHeaderMetaData("column header"), widget);
    }

    @Test
    public void testMinimalWidthNoContent() throws Exception {
        gridData.appendColumn(column);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(DEFAULT_WIDTH);
    }

    /**
     * [100]
     * [150]
     * [125]
     */
    @Test
    public void testMinimalWidthOneCellInEachRow() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 150);
        mockCells(2, 0, 125);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(150);
    }

    /**
     * [100]
     * [50][60]
     * [105]
     */
    @Test
    public void testMinimalWidthTwoCellsSum() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 50, 60);
        mockCells(2, 0, 105);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(110);
    }

    /**
     * [100]
     * [50][60]
     * [50][60][10]
     */
    @Test
    public void testMinimalWidthThreeCellsSum() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 50, 60);
        mockCells(2, 0, 50, 60, 10);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(120);
    }

    /**
     * [99]
     * [30][30][30]
     * [49][50]
     */
    @Test
    public void testMinimalWidthDefaultWidth() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 99);
        mockCells(1, 0, 30, 30, 30);
        mockCells(2, 0, 49, 50);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(DEFAULT_WIDTH);
    }

    /**
     * [100]
     * -
     * [50][60]
     */
    @Test
    public void testMinimalWidthNoCellsInMiddle() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0);
        mockCells(2, 0, 50, 60);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(110);
    }

    @Test
    public void testUpdateInternalWidth() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 110);
        mockCells(2, 0, 50, 60);
        column.setWidthInternal(200D);
        Assertions.assertThat(getColumnWidth(0, 0, 0)).isEqualTo(200D);
        Assertions.assertThat(getColumnWidth(1, 0, 0)).isEqualTo(200D);
        Assertions.assertThat(getColumnWidth(2, 0, 0)).isEqualTo(50D);
        Assertions.assertThat(getColumnWidth(2, 0, 1)).isEqualTo(150D);
    }

    @Test
    public void testUpdateInternalWidthNoCellsInMiddle() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0);
        mockCells(2, 0, 50, 60);
        column.setWidthInternal(200D);
        Assertions.assertThat(getColumnWidth(0, 0, 0)).isEqualTo(200D);
        Assertions.assertThat(getColumnWidth(2, 0, 0)).isEqualTo(50D);
        Assertions.assertThat(getColumnWidth(2, 0, 1)).isEqualTo(150D);
    }

    @Test
    public void testUpdateInternalWidthResizedToSmaller() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 30, 30, 30);
        mockCells(2, 0, 50, 60);
        column.setWidthInternal(80D);
        Assertions.assertThat(getColumnWidth(0, 0, 0)).isEqualTo(80D);
        Assertions.assertThat(getColumnWidth(1, 0, 0)).isEqualTo(30D);
        Assertions.assertThat(getColumnWidth(1, 0, 1)).isEqualTo(30D);
        Assertions.assertThat(getColumnWidth(1, 0, 2)).isEqualTo(20D);
        Assertions.assertThat(getColumnWidth(2, 0, 0)).isEqualTo(50D);
        Assertions.assertThat(getColumnWidth(2, 0, 1)).isEqualTo(30D);
    }

    private void mockCells(final int rowIndex, final int columnIndex, final double... widthOfCells) {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumnRenderer gridColumnRenderer = mock(GridColumnRenderer.class);
        final GridWidget gridWidget = mock(GridWidget.class);

        gridData.setCell(rowIndex, columnIndex,
                         new ExpressionCellValue(
                                 Optional.of(new BaseGridWidget(new BaseGridData() {{
                                     for (double width : widthOfCells) {
                                         appendColumn(new DMNGridColumn<Object>(headerMetaData, gridColumnRenderer, gridWidget) {{
                                             setMinimumWidth(width);
                                             setWidth(width);
                                         }});
                                     }
                                 }}, null, null, null))));
    }

    private double getColumnWidth(final int rowOfCell, final int columnOfCell, final int columnInCell) {
        return ((ExpressionCellValue) gridData.getCell(rowOfCell, columnOfCell).getValue()).getValue().get().getModel().getColumns().get(columnInCell).getWidth();
    }
}

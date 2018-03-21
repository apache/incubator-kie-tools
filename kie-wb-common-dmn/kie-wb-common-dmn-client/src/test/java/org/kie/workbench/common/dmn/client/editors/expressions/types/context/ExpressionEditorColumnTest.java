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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.mocks.MockHasDOMElementResourcesHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
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
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorColumnTest {

    private static final double DEFAULT_WIDTH = 100D;

    private static final int PADDING = 10;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    private BaseGridData gridData;

    private GridWidget widget;

    private ExpressionEditorColumn column;

    @Before
    public void setUp() throws Exception {
        gridData = new BaseGridData();
        widget = new BaseGridWidget(gridData, selectionManager, pinnedModeManager, renderer);
        column = new ExpressionEditorColumn(gridLayer, new BaseHeaderMetaData("column header"), widget);
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
        mockCells(0, 0, 100d);
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

    /**
     * (10)[100](10)
     * (10)[150](10)
     * (10)[125](10)
     */
    @Test
    public void testMinimalWidthOneCellInEachRowWithPadding() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING, 150);
        mockCellsWithPadding(2, 0, PADDING, 125);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(170);
    }

    /**
     * (10)[100](10)
     * (10)[50][60](10)
     * (10)[105](10)
     */
    @Test
    public void testMinimalWidthTwoCellsSumWithPadding() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING, 50, 60);
        mockCellsWithPadding(2, 0, PADDING, 105);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(130);
    }

    /**
     * (10)[100](10)
     * (10)[50][60](10)
     * (10)[50][60][10](10)
     */
    @Test
    public void testMinimalWidthThreeCellsSumWithPadding() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING, 50, 60);
        mockCellsWithPadding(2, 0, PADDING, 50, 60, 10);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(140);
    }

    /**
     * (10)[99](10)
     * (10)[30][30][30](10)
     * (10)[49][50](10)
     */
    @Test
    public void testMinimalWidthDefaultWidthWithPadding() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCellsWithPadding(0, 0, PADDING, 99);
        mockCellsWithPadding(1, 0, PADDING, 30, 30, 30);
        mockCellsWithPadding(2, 0, PADDING, 49, 50);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(119);
    }

    /**
     * (10)[100](10)
     * (10)-(10)
     * (10)[50][60](10)
     */
    @Test
    public void testMinimalWidthNoCellsInMiddleWithPadding() throws Exception {
        gridData.appendColumn(column);
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        gridData.appendRow(new DMNGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING);
        mockCellsWithPadding(2, 0, PADDING, 50, 60);
        Assertions.assertThat(column.getMinimumWidth()).isEqualTo(130);
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

    @Test
    public void testHeaderDOMElementsAreDestroyed() {
        final MockHasDOMElementResourcesHeaderMetaData mockHeaderMetaData = mock(MockHasDOMElementResourcesHeaderMetaData.class);
        column.getHeaderMetaData().add(mockHeaderMetaData);

        column.destroyResources();

        verify(mockHeaderMetaData).destroyResources();
    }

    private void mockCells(final int rowIndex,
                           final int columnIndex,
                           final double... widthOfCells) {
        mockCellsWithPadding(rowIndex,
                             columnIndex,
                             0,
                             widthOfCells);
    }

    private void mockCellsWithPadding(final int rowIndex,
                                      final int columnIndex,
                                      final int padding,
                                      final double... widthOfCells) {
        gridData.setCellValue(rowIndex,
                              columnIndex,
                              new ExpressionCellValue(Optional.of(mockEditor(padding,
                                                                             widthOfCells))));
    }

    @SuppressWarnings("unchecked")
    private BaseExpressionGrid mockEditor(final double padding,
                                          final double... widthOfCells) {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumnRenderer gridColumnRenderer = mock(GridColumnRenderer.class);
        final BaseExpressionGrid gridWidget = mock(BaseExpressionGrid.class);

        final GridCellTuple parent = new GridCellTuple(0, 0, null);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Optional<LiteralExpression> expression = Optional.of(mock(LiteralExpression.class));
        final Optional<HasName> hasName = Optional.of(mock(HasName.class));

        return new BaseExpressionGrid(parent,
                                      hasExpression,
                                      expression,
                                      hasName,
                                      gridPanel,
                                      gridLayer,
                                      renderer,
                                      sessionManager,
                                      sessionCommandManager,
                                      cellEditorControls,
                                      translationService,
                                      0) {
            @Override
            protected BaseUIModelMapper makeUiModelMapper() {
                return null;
            }

            @Override
            protected void initialiseUiColumns() {
                for (double width : widthOfCells) {
                    model.appendColumn(new DMNGridColumn<GridWidget, Object>(headerMetaData, gridColumnRenderer, gridWidget) {{
                        setMinimumWidth(width);
                        setWidth(width);
                    }});
                }
            }

            @Override
            protected void initialiseUiModel() {
                //Nothing for this test
            }

            @Override
            protected boolean isHeaderHidden() {
                return false;
            }

            @Override
            public double getPadding() {
                return padding;
            }
        };
    }

    private double getColumnWidth(final int rowOfCell, final int columnOfCell, final int columnInCell) {
        return ((ExpressionCellValue) gridData.getCell(rowOfCell, columnOfCell).getValue()).getValue().get().getModel().getColumns().get(columnInCell).getWidth();
    }
}

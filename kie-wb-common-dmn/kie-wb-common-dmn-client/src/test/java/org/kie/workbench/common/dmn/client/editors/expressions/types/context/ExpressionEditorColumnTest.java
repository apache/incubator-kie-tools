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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.mocks.MockHasDOMElementResourcesHeaderMetaData;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorColumnTest {

    private static final double DEFAULT_WIDTH = 100D;

    private static final int PADDING = 10;

    @Mock
    private GridRenderer renderer;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    private BaseGridData gridData;

    private BaseGrid<Expression> widget;

    private ExpressionEditorColumn column;

    @Before
    public void setUp() {
        gridData = new BaseGridData();
        widget = new BaseGrid<Expression>(gridLayer,
                                          gridData,
                                          renderer,
                                          sessionManager,
                                          sessionCommandManager,
                                          canvasCommandFactory,
                                          refreshFormPropertiesEvent,
                                          domainObjectSelectionEvent,
                                          cellEditorControls,
                                          translationService) {

        };
        column = new ExpressionEditorColumn(gridLayer,
                                            new BaseHeaderMetaData("column header"),
                                            ExpressionEditorColumn.DEFAULT_WIDTH,
                                            widget);
    }

    @Test
    public void testMinimalWidthNoContent() {
        gridData.appendColumn(column);
        assertThat(column.getMinimumWidth()).isEqualTo(DEFAULT_WIDTH);
    }

    /**
     * [100]
     * [150]
     * [125]
     */
    @Test
    public void testMinimalWidthOneCellInEachRow() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100d);
        mockCells(1, 0, 150);
        mockCells(2, 0, 125);
        assertThat(column.getMinimumWidth()).isEqualTo(150);
    }

    /**
     * [100]
     * [50][60]
     * [105]
     */
    @Test
    public void testMinimalWidthTwoCellsSum() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 50, 60);
        mockCells(2, 0, 105);
        assertThat(column.getMinimumWidth()).isEqualTo(110);
    }

    /**
     * [100]
     * [50][60]
     * [50][60][10]
     */
    @Test
    public void testMinimalWidthThreeCellsSum() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 50, 60);
        mockCells(2, 0, 50, 60, 10);
        assertThat(column.getMinimumWidth()).isEqualTo(120);
    }

    /**
     * [99]
     * [30][30][30]
     * [49][50]
     */
    @Test
    public void testMinimalWidthDefaultWidth() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 99);
        mockCells(1, 0, 30, 30, 30);
        mockCells(2, 0, 49, 50);
        assertThat(column.getMinimumWidth()).isEqualTo(DEFAULT_WIDTH);
    }

    /**
     * [100]
     * -
     * [50][60]
     */
    @Test
    public void testMinimalWidthNoCellsInMiddle() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0);
        mockCells(2, 0, 50, 60);
        assertThat(column.getMinimumWidth()).isEqualTo(110);
    }

    /**
     * (10)[100](10)
     * (10)[150](10)
     * (10)[125](10)
     */
    @Test
    public void testMinimalWidthOneCellInEachRowWithPadding() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING, 150);
        mockCellsWithPadding(2, 0, PADDING, 125);
        assertThat(column.getMinimumWidth()).isEqualTo(170);
    }

    /**
     * (10)[100](10)
     * (10)[50][60](10)
     * (10)[105](10)
     */
    @Test
    public void testMinimalWidthTwoCellsSumWithPadding() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING, 50, 60);
        mockCellsWithPadding(2, 0, PADDING, 105);
        assertThat(column.getMinimumWidth()).isEqualTo(130);
    }

    /**
     * (10)[100](10)
     * (10)[50][60](10)
     * (10)[50][60][10](10)
     */
    @Test
    public void testMinimalWidthThreeCellsSumWithPadding() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING, 50, 60);
        mockCellsWithPadding(2, 0, PADDING, 50, 60, 10);
        assertThat(column.getMinimumWidth()).isEqualTo(140);
    }

    /**
     * (10)[99](10)
     * (10)[30][30][30](10)
     * (10)[49][50](10)
     */
    @Test
    public void testMinimalWidthDefaultWidthWithPadding() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCellsWithPadding(0, 0, PADDING, 99);
        mockCellsWithPadding(1, 0, PADDING, 30, 30, 30);
        mockCellsWithPadding(2, 0, PADDING, 49, 50);
        assertThat(column.getMinimumWidth()).isEqualTo(119);
    }

    /**
     * (10)[100](10)
     * (10)-(10)
     * (10)[50][60](10)
     */
    @Test
    public void testMinimalWidthNoCellsInMiddleWithPadding() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCellsWithPadding(0, 0, PADDING, 100);
        mockCellsWithPadding(1, 0, PADDING);
        mockCellsWithPadding(2, 0, PADDING, 50, 60);
        assertThat(column.getMinimumWidth()).isEqualTo(130);
    }

    @Test
    public void testUpdateInternalWidth() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 110);
        mockCells(2, 0, 50, 60);
        column.setWidthInternal(200D);
        assertThat(getColumnWidth(0, 0, 0)).isEqualTo(200D);
        assertThat(getColumnWidth(1, 0, 0)).isEqualTo(200D);
        assertThat(getColumnWidth(2, 0, 0)).isEqualTo(50D);
        assertThat(getColumnWidth(2, 0, 1)).isEqualTo(150D);
    }

    @Test
    public void testUpdateInternalWidthNoCellsInMiddle() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0);
        mockCells(2, 0, 50, 60);
        column.setWidthInternal(200D);
        assertThat(getColumnWidth(0, 0, 0)).isEqualTo(200D);
        assertThat(getColumnWidth(2, 0, 0)).isEqualTo(50D);
        assertThat(getColumnWidth(2, 0, 1)).isEqualTo(150D);
    }

    @Test
    public void testUpdateInternalWidthResizedToSmaller() {
        gridData.appendColumn(column);
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        gridData.appendRow(new BaseGridRow());
        mockCells(0, 0, 100);
        mockCells(1, 0, 30, 30, 30);
        mockCells(2, 0, 50, 60);
        column.setWidthInternal(80D);
        assertThat(getColumnWidth(0, 0, 0)).isEqualTo(80D);
        assertThat(getColumnWidth(1, 0, 0)).isEqualTo(30D);
        assertThat(getColumnWidth(1, 0, 1)).isEqualTo(30D);
        assertThat(getColumnWidth(1, 0, 2)).isEqualTo(20D);
        assertThat(getColumnWidth(2, 0, 0)).isEqualTo(50D);
        assertThat(getColumnWidth(2, 0, 1)).isEqualTo(30D);
    }

    @Test
    public void testEditNestedUndefinedExpressionGrid() {
        final GridCell<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cell = mock(GridCell.class);
        final GridCellValue<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cellValue = mock(GridCellValue.class);
        final UndefinedExpressionGrid undefinedExpressionGrid = mock(UndefinedExpressionGrid.class);
        when(cell.getValue()).thenReturn(cellValue);
        when(cellValue.getValue()).thenReturn(Optional.of(undefinedExpressionGrid));

        column.edit(cell, null, null);

        verify(undefinedExpressionGrid).startEditingCell(0, 0);
    }

    @Test
    public void testEditNestedNotUndefinedExpressionGrid() {
        final GridCell<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cell = mock(GridCell.class);
        final GridCellValue<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cellValue = mock(GridCellValue.class);
        final ContextGrid contextGrid = mock(ContextGrid.class);
        when(cell.getValue()).thenReturn(cellValue);
        when(cellValue.getValue()).thenReturn(Optional.of(contextGrid));

        column.edit(cell, null, null);

        verify(contextGrid).selectFirstCell();
    }

    @Test
    public void testEditNestedLiteralExpressionGrid() {
        final GridCell<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cell = mock(GridCell.class);
        final GridCellValue<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cellValue = mock(GridCellValue.class);
        final LiteralExpressionGrid leGrid = mock(LiteralExpressionGrid.class);
        when(cell.getValue()).thenReturn(cellValue);
        when(cellValue.getValue()).thenReturn(Optional.of(leGrid));

        column.edit(cell, null, null);

        verify(leGrid).startEditingCell(0, 0);
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
    private BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper> mockEditor(final double padding,
                                                                                                                 final double... widthOfCells) {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumnRenderer gridColumnRenderer = mock(GridColumnRenderer.class);
        final BaseExpressionGrid gridWidget = mock(BaseExpressionGrid.class);
        when(gridWidget.getExpression()).thenReturn(Optional::empty);

        final GridCellTuple parent = new GridCellTuple(0, 0, null);
        final Optional<HasName> hasName = Optional.of(mock(HasName.class));

        return new BaseExpressionGrid(parent,
                                      Optional.empty(),
                                      HasExpression.NOP,
                                      hasName,
                                      gridPanel,
                                      gridLayer,
                                      new DMNGridData(),
                                      renderer,
                                      definitionUtils,
                                      sessionManager,
                                      sessionCommandManager,
                                      canvasCommandFactory,
                                      editorSelectedEvent,
                                      refreshFormPropertiesEvent,
                                      domainObjectSelectionEvent,
                                      cellEditorControls,
                                      listSelector,
                                      translationService,
                                      false,
                                      0) {
            @Override
            protected BaseUIModelMapper makeUiModelMapper() {
                return null;
            }

            @Override
            protected void initialiseUiColumns() {
                for (double width : widthOfCells) {
                    model.appendColumn(new DMNGridColumn<BaseGrid<Expression>, Object>(headerMetaData,
                                                                                       gridColumnRenderer,
                                                                                       width,
                                                                                       gridWidget) {{
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
            public double getPadding() {
                return padding;
            }
        };
    }

    private double getColumnWidth(final int rowOfCell, final int columnOfCell, final int columnInCell) {
        return ((ExpressionCellValue) gridData.getCell(rowOfCell, columnOfCell).getValue()).getValue().get().getModel().getColumns().get(columnInCell).getWidth();
    }
}

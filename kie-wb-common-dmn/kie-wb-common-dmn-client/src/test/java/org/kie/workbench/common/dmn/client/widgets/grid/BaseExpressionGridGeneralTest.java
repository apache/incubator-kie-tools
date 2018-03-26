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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridGeneralTest extends BaseExpressionGridTest {

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Override
    @SuppressWarnings("unchecked")
    public BaseExpressionGrid getGrid() {
        final HasExpression hasExpression = mock(HasExpression.class);
        final Optional<LiteralExpression> expression = Optional.of(mock(LiteralExpression.class));
        final Optional<HasName> hasName = Optional.of(mock(HasName.class));

        return new BaseExpressionGrid(parentCell,
                                      Optional.empty(),
                                      hasExpression,
                                      expression,
                                      hasName,
                                      gridPanel,
                                      gridLayer,
                                      renderer,
                                      definitionUtils,
                                      sessionManager,
                                      sessionCommandManager,
                                      canvasCommandFactory,
                                      cellEditorControls,
                                      listSelector,
                                      translationService,
                                      0) {
            @Override
            protected BaseUIModelMapper makeUiModelMapper() {
                return mapper;
            }

            @Override
            protected void initialiseUiColumns() {
                //Nothing for this test
            }

            @Override
            protected void initialiseUiModel() {
                //Nothing for this test
            }

            @Override
            protected boolean isHeaderHidden() {
                return false;
            }
        };
    }

    @Test
    public void testGetMinimumWidthNoColumns() {
        assertMinimumWidth(0.0);

        Assertions.assertThat(grid.getMinimumWidth()).isEqualTo(0);
    }

    @Test
    public void testGetMinimumWidthOneColumn() {
        final double COL_0_MIN = 100.0;

        assertMinimumWidth(COL_0_MIN,
                           new MockColumnData(200.0, COL_0_MIN));
    }

    @Test
    public void testGetMinimumWidthTwoColumns() {
        final double COL_0_ACTUAL = 200.0;
        final double COL_1_MIN = 150.0;

        assertMinimumWidth(COL_0_ACTUAL + COL_1_MIN,
                           new MockColumnData(COL_0_ACTUAL, 100.0),
                           new MockColumnData(225.0, COL_1_MIN));
    }

    @Test
    public void testGetMinimumWidthMultipleColumns() {
        final double COL_0_ACTUAL = 50.0;
        final double COL_1_ACTUAL = 65.0;
        final double COL_2_MIN = 150.0;

        assertMinimumWidth(COL_0_ACTUAL + COL_1_ACTUAL + COL_2_MIN,
                           new MockColumnData(COL_0_ACTUAL, 25.0),
                           new MockColumnData(COL_1_ACTUAL, 35.0),
                           new MockColumnData(225.0, COL_2_MIN));
    }

    @Test
    public void testGetViewportGridAttachedToLayer() {
        doReturn(gridParent).when(grid).getParent();
        doReturn(viewport).when(gridParent).getViewport();

        assertEquals(viewport,
                     grid.getViewport());
    }

    @Test
    public void testGetViewportGridNotAttachedToLayer() {
        assertEquals(viewport,
                     grid.getViewport());
    }

    @Test
    public void testGetLayerGridAttachedToLayer() {
        doReturn(gridParent).when(grid).getParent();
        doReturn(gridLayer).when(gridParent).getLayer();

        assertEquals(gridLayer,
                     grid.getLayer());
    }

    @Test
    public void testGetLayerGridNotAttachedToLayer() {
        assertEquals(gridLayer,
                     grid.getLayer());
    }

    @Test
    public void testDeselect() {
        grid.getModel().appendRow(new DMNGridRow());
        appendColumns(GridColumn.class);

        //Select a cell so we can check deselection clears selections
        grid.getModel().selectCell(0, 0);
        assertFalse(grid.getModel().getSelectedCells().isEmpty());

        grid.deselect();

        assertTrue(grid.getModel().getSelectedCells().isEmpty());
    }

    @Test
    public void testSelectFirstCellWithNoRowsOrColumns() {
        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isEmpty();
    }

    @Test
    public void testSelectFirstCellWithRowAndNonRowNumberColumn() {
        grid.getModel().appendRow(new DMNGridRow());
        appendColumns(GridColumn.class);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isNotEmpty();
        assertThat(grid.getModel().getSelectedCells()).contains(new GridData.SelectedCell(0, 0));
    }

    @Test
    public void testSelectFirstCellWithRowAndRowNumberColumn() {
        grid.getModel().appendRow(new DMNGridRow());
        appendColumns(RowNumberColumn.class);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isEmpty();
    }

    @Test
    public void testSelectFirstCellWithRowAndRowNumberColumnAndAnotherColumn() {
        grid.getModel().appendRow(new DMNGridRow());
        appendColumns(RowNumberColumn.class, GridColumn.class);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isNotEmpty();
        assertThat(grid.getModel().getSelectedCells()).contains(new GridData.SelectedCell(0, 1));
    }

    @Test
    public void testPaddingWithParent() {
        doReturn(Optional.of(mock(BaseExpressionGrid.class))).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(BaseExpressionGrid.DEFAULT_PADDING);
    }

    @Test
    public void testPaddingWithNoParent() {
        doReturn(Optional.empty()).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(BaseExpressionGrid.DEFAULT_PADDING);
    }

    @Test
    public void testFindParentGrid() throws Exception {
        final GridWidget parentGrid = mock(BaseExpressionGrid.class);
        doReturn(parentGrid).when(parentCell).getGridWidget();

        assertThat(grid.findParentGrid().get()).isEqualTo(parentGrid);
    }

    @Test
    public void testFindParentGridNoParent() throws Exception {
        assertThat(grid.findParentGrid()).isEmpty();
    }

    @Test
    public void testWidthIncreased() throws Exception {
        testUpdateWidthOfPeers(0, 150);
    }

    @Test
    public void testWidthIncreasedMultipleChildColumnsFirstUpdated() throws Exception {
        testUpdateWidthOfPeers(0, 150, 180);
    }

    @Test
    public void testWidthIncreasedMultipleChildColumnsLastUpdated() throws Exception {
        testUpdateWidthOfPeers(1, 150, 180);
    }

    @Test
    public void testWidthDecreased() throws Exception {
        testUpdateWidthOfPeers(0, 80);
    }

    @Test
    public void testWidthDecreasedMultipleChildColumnsFirstUpdated() throws Exception {
        testUpdateWidthOfPeers(0, 35, 45);
    }

    @Test
    public void testWidthDecreasedMultipleChildColumnsLastUpdated() throws Exception {
        testUpdateWidthOfPeers(1, 35, 45);
    }

    @Test
    public void synchroniseViewWhenExpressionEditorChangedWithEditor() {
        final BaseExpressionGrid editor = mock(BaseExpressionGrid.class);

        grid.synchroniseViewWhenExpressionEditorChanged(editor);

        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(parentCell).onResize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
        verify(gridLayer).select(eq(editor));
    }

    @Test
    public void synchroniseView() {
        grid.synchroniseView();

        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(parentCell).onResize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
        verify(gridLayer, never()).select(any(GridWidget.class));
    }

    /*
     * Test that parent column width is updated to sum of nested columns
     * The update is forced from nested column at position indexOfColumnToUpdate
     * The default width of parent column is 100
     */
    private void testUpdateWidthOfPeers(final int indexOfColumnToUpdate,
                                        final double... widthsOfNestedColumns) {
        // parent column
        final BaseExpressionGrid parentGrid = mock(BaseExpressionGrid.class);
        final GridData parentGridData = mock(GridData.class);
        final DMNGridColumn parentColumn = mockColumn(100, null);
        doReturn(parentGrid).when(parentCell).getGridWidget();
        doReturn(parentGridData).when(parentGrid).getModel();
        doReturn(Collections.singletonList(parentColumn)).when(parentGridData).getColumns();
        doReturn(Collections.singleton(parentGrid)).when(gridLayer).getGridWidgets();

        // nested columns
        final List<DMNGridColumn> columns = Arrays.stream(widthsOfNestedColumns)
                .mapToObj(width -> mockColumn(width, grid))
                .collect(Collectors.toList());
        grid.getModel().appendRow(new DMNGridRow());
        columns.stream().forEach(column -> grid.getModel().appendColumn(column));

        // force the peers width update
        columns.get(indexOfColumnToUpdate).updateWidthOfPeers();

        // assert parent width is equal to sum of nested columns widths
        final double padding = BaseExpressionGrid.DEFAULT_PADDING * 2;
        Assertions.assertThat(parentColumn.getWidth()).isEqualTo(Arrays.stream(widthsOfNestedColumns).sum() + padding);
    }

    private void assertMinimumWidth(final double expectedMinimumWidth,
                                    final MockColumnData... columnData) {
        Arrays.asList(columnData).forEach(cd -> {
            final GridColumn uiColumn = mock(GridColumn.class);
            doReturn(cd.width).when(uiColumn).getWidth();
            doReturn(cd.minWidth).when(uiColumn).getMinimumWidth();
            grid.getModel().appendColumn(uiColumn);
        });

        assertEquals(expectedMinimumWidth,
                     grid.getMinimumWidth(),
                     0.0);
    }

    @SafeVarargs
    private final void appendColumns(final Class<? extends GridColumn>... columnClasses) {
        IntStream.range(0, columnClasses.length).forEach(i -> {
            final GridColumn column = mock(columnClasses[i]);
            doReturn(i).when(column).getIndex();
            doReturn(true).when(column).isVisible();
            doReturn(100.0).when(column).getWidth();
            grid.getModel().appendColumn(column);
        });
    }

    private static class MockColumnData {

        private double width;
        private double minWidth;

        public MockColumnData(final double width,
                              final double minWidth) {
            this.width = width;
            this.minWidth = minWidth;
        }
    }

    @SuppressWarnings("unchecked")
    private DMNGridColumn mockColumn(final double width,
                                     final GridWidget gridWidget) {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumnRenderer columnRenderer = mock(GridColumnRenderer.class);
        return new DMNGridColumn(headerMetaData,
                                 columnRenderer,
                                 gridWidget) {{
            setWidth(width);
        }};
    }
}

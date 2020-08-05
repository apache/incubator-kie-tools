/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl;

import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseCellSelectionManagerTest {

    private static final double HEADER_HEIGHT = 32.0;

    private static final double ROW_HEIGHT = 20.0;

    private static final double GRID_ABSOLUTE_X = 15.0;

    private static final double GRID_ABSOLUTE_Y = 30.0;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private DefaultGridLayer gridLayer;

    @Mock
    private GridRenderer gridWidgetRenderer;

    @Mock
    private Group gridWidgetHeader;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Captor
    private ArgumentCaptor<GridBodyCellRenderContext> contextArgumentCaptor;

    @Captor
    private ArgumentCaptor<GridBodyCellEditContext> editContextArgumentCaptor;

    private GridData gridWidgetData;

    private GridColumn<String> col1 = spy(new BaseGridTest.MockMergableGridColumn<>("col1",
                                                                                    100));
    private GridColumn<String> col2 = spy(new BaseGridTest.MockMergableGridColumn<>("col2",
                                                                                    100));
    private GridColumn<String> col3 = spy(new BaseGridTest.MockMergableGridColumn<>("col3",
                                                                                    100));

    private Bounds visibleBounds = new BaseBounds(-1000,
                                                  -1000,
                                                  2000,
                                                  2000);

    private CellSelectionManager cellSelectionManager;

    @Before
    public void setup() {
        gridWidgetData = new BaseGridData();
        gridWidgetData.appendRow(new BaseGridRow(ROW_HEIGHT));
        gridWidgetData.appendRow(new BaseGridRow(ROW_HEIGHT));
        gridWidgetData.appendColumn(col1);
        gridWidgetData.appendColumn(col2);
        gridWidgetData.appendColumn(col3);

        when(gridWidget.getModel()).thenReturn(gridWidgetData);

        cellSelectionManager = new BaseCellSelectionManager(gridWidget);
        final BaseGridRendererHelper gridWidgetRendererHelper = new BaseGridRendererHelper(gridWidget);

        when(gridWidget.getRenderer()).thenReturn(gridWidgetRenderer);
        when(gridWidget.getRendererHelper()).thenReturn(gridWidgetRendererHelper);
        when(gridWidget.getLayer()).thenReturn(gridLayer);
        when(gridWidget.getWidth()).thenReturn(300.0);
        when(gridWidget.getHeader()).thenReturn(gridWidgetHeader);
        when(gridWidget.getHeight()).thenReturn(HEADER_HEIGHT + (ROW_HEIGHT * 2));
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getX()).thenReturn(0.0);
        when(gridWidget.getY()).thenReturn(0.0);
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(GRID_ABSOLUTE_X, GRID_ABSOLUTE_Y));
        when(gridLayer.getVisibleBounds()).thenReturn(visibleBounds);
        when(gridWidgetRenderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(gridWidgetRenderer.getHeaderRowHeight()).thenReturn(HEADER_HEIGHT);
        when(viewport.getTransform()).thenReturn(transform);
    }

    @Test
    public void selectCellPointCoordinateOutsideGridBounds() {
        cellSelectionManager.selectCell(new Point2D(-10,
                                                    -10),
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertTrue(selectedCells.isEmpty());
    }

    @Test
    public void selectCellPointCoordinateWithinGridBounds() {
        cellSelectionManager.selectCell(new Point2D(50,
                                                    42),
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
    }

    @Test
    public void selectCellPointCoordinateWithinGridBoundsWithShiftKey() {
        cellSelectionManager.selectCell(new Point2D(50,
                                                    42),
                                        false,
                                        false);
        cellSelectionManager.selectCell(new Point2D(150,
                                                    62),
                                        true,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(4,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void selectCellPointCoordinateWithinGridBoundsWithControlKey() {
        cellSelectionManager.selectCell(new Point2D(50,
                                                    42),
                                        false,
                                        false);
        cellSelectionManager.selectCell(new Point2D(150,
                                                    62),
                                        false,
                                        true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void selectCellRowCoordinateLessThanZero() {
        cellSelectionManager.selectCell(-1,
                                        0,
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertTrue(selectedCells.isEmpty());
    }

    @Test
    public void selectCellRowCoordinateGreaterThanRowCount() {
        cellSelectionManager.selectCell(2,
                                        0,
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertTrue(selectedCells.isEmpty());
    }

    @Test
    public void selectCellColumnCoordinateLessThanZero() {
        cellSelectionManager.selectCell(0,
                                        -1,
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertTrue(selectedCells.isEmpty());
    }

    @Test
    public void selectCellColumnCoordinateGreaterThanColumnCount() {
        cellSelectionManager.selectCell(0,
                                        3,
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertTrue(selectedCells.isEmpty());
    }

    @Test
    public void selectCellWithoutShiftKeyWithoutControlKey() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void selectCellWithShiftKey() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.selectCell(1,
                                        1,
                                        true,
                                        false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(4,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void selectCellWithControlKey() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.selectCell(1,
                                        1,
                                        false,
                                        true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    1)));
    }

    @Test
    public void selectHeaderCellWithPoint() {
        cellSelectionManager.selectHeaderCell(new Point2D(col1.getWidth() + col2.getWidth() / 2,
                                                          HEADER_HEIGHT / 2),
                                              false,
                                              false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        assertEquals(1,
                     selectedHeaderCells.size());
        assertEquals(0,
                     selectedHeaderCells.get(0).getRowIndex());
        assertEquals(1,
                     selectedHeaderCells.get(0).getColumnIndex());
        assertNull(gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void selectHeaderCellWithCoordinate() {
        cellSelectionManager.selectHeaderCell(0,
                                              1,
                                              false,
                                              false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        assertEquals(1,
                     selectedHeaderCells.size());
        assertEquals(0,
                     selectedHeaderCells.get(0).getRowIndex());
        assertEquals(1,
                     selectedHeaderCells.get(0).getColumnIndex());
        assertNull(gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionLeft() {
        cellSelectionManager.selectCell(0,
                                        1,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.LEFT,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionLeftAndFindVisible() {
        // DROOLS-5442
        when(col2.isVisible()).thenReturn(false);

        cellSelectionManager.selectCell(0,
                                        2,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.LEFT,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionLeftOutOfBound() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.LEFT,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionLeftWithShiftKey() {
        cellSelectionManager.selectCell(0,
                                        1,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.LEFT,
                                             true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               1),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionRight() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.RIGHT,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               1),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionRightAndFindVisible() {
        // DROOLS-5442
        when(col2.isVisible()).thenReturn(false);

        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.RIGHT,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    2)));
        assertEquals(new GridData.SelectedCell(0,
                                               2),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionRightOutOfBound() {
        cellSelectionManager.selectCell(0,
                                        2,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.RIGHT,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    2)));
        assertEquals(new GridData.SelectedCell(0,
                                               2),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionRightWithShiftKey() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.RIGHT,
                                             true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionUp() {
        cellSelectionManager.selectCell(1,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.UP,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionUpWithShiftKey() {
        cellSelectionManager.selectCell(1,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.UP,
                                             true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(1,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionDown() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.DOWN,
                                             false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(1,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionDownWithShiftKey() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.DOWN,
                                             true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    0)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void adjustSelectionRightDownWithShiftKey() {
        cellSelectionManager.selectCell(0,
                                        0,
                                        false,
                                        false);
        cellSelectionManager.adjustSelection(SelectionExtension.RIGHT,
                                             true);
        cellSelectionManager.adjustSelection(SelectionExtension.DOWN,
                                             true);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        assertEquals(4,
                     selectedCells.size());
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(0,
                                                                    1)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    0)));
        assertTrue(selectedCells.contains(new GridData.SelectedCell(1,
                                                                    1)));
        assertEquals(new GridData.SelectedCell(0,
                                               0),
                     gridWidgetData.getSelectedCellsOrigin());
    }

    @Test
    public void testAdjustSelectionUpFromDataToHeader() {
        cellSelectionManager.selectCell(0,
                                        1,
                                        false,
                                        false);

        cellSelectionManager.adjustSelection(SelectionExtension.UP, false);

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        Assertions.assertThat(selectedCells).isEmpty();

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        Assertions.assertThat(selectedHeaderCells).hasSize(1);

        final GridData.SelectedCell selectedHeaderCell = selectedHeaderCells.get(0);
        Assertions.assertThat(selectedHeaderCell.getColumnIndex()).isEqualTo(1);
        Assertions.assertThat(selectedHeaderCell.getRowIndex()).isEqualTo(0);
    }

    @Test
    public void testAdjustSelectionDownFromHeaderToData() {
        cellSelectionManager.selectHeaderCell(0,
                                              1,
                                              false,
                                              false);

        cellSelectionManager.adjustSelection(SelectionExtension.DOWN, false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        Assertions.assertThat(selectedHeaderCells).isEmpty();

        final List<GridData.SelectedCell> selectedCells = gridWidgetData.getSelectedCells();
        Assertions.assertThat(selectedCells).hasSize(1);

        final GridData.SelectedCell selectedCell = selectedCells.get(0);
        Assertions.assertThat(selectedCell.getColumnIndex()).isEqualTo(1);
        Assertions.assertThat(selectedCell.getRowIndex()).isEqualTo(0);
    }

    @Test
    public void testAdjustSelectionUpInHeader() {
        col2.getHeaderMetaData().add(new BaseHeaderMetaData("col1", "second-row"));

        cellSelectionManager.selectHeaderCell(1,
                                              1,
                                              false,
                                              false);

        cellSelectionManager.adjustSelection(SelectionExtension.UP, false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        Assertions.assertThat(selectedHeaderCells).hasSize(1);

        final GridData.SelectedCell selectedHeaderCell = selectedHeaderCells.get(0);
        Assertions.assertThat(selectedHeaderCell.getColumnIndex()).isEqualTo(1);
        Assertions.assertThat(selectedHeaderCell.getRowIndex()).isEqualTo(0);
    }

    @Test
    public void testAdjustSelectionDownInHeader() {
        col2.getHeaderMetaData().add(new BaseHeaderMetaData("col1", "second-row"));

        cellSelectionManager.selectHeaderCell(0,
                                              1,
                                              false,
                                              false);

        cellSelectionManager.adjustSelection(SelectionExtension.DOWN, false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        Assertions.assertThat(selectedHeaderCells).hasSize(1);

        final GridData.SelectedCell selectedHeaderCell = selectedHeaderCells.get(0);
        Assertions.assertThat(selectedHeaderCell.getColumnIndex()).isEqualTo(1);
        Assertions.assertThat(selectedHeaderCell.getRowIndex()).isEqualTo(1);
    }

    @Test
    public void testAdjustSelectionRightInHeader() {
        cellSelectionManager.selectHeaderCell(0,
                                              0,
                                              false,
                                              false);

        cellSelectionManager.adjustSelection(SelectionExtension.RIGHT, false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        Assertions.assertThat(selectedHeaderCells).hasSize(1);

        final GridData.SelectedCell selectedHeaderCell = selectedHeaderCells.get(0);
        Assertions.assertThat(selectedHeaderCell.getColumnIndex()).isEqualTo(1);
        Assertions.assertThat(selectedHeaderCell.getRowIndex()).isEqualTo(0);
    }

    @Test
    public void testAdjustSelectionLeftInHeader() {
        cellSelectionManager.selectHeaderCell(0,
                                              1,
                                              false,
                                              false);

        cellSelectionManager.adjustSelection(SelectionExtension.LEFT, false);

        final List<GridData.SelectedCell> selectedHeaderCells = gridWidgetData.getSelectedHeaderCells();
        Assertions.assertThat(selectedHeaderCells).hasSize(1);

        final GridData.SelectedCell selectedHeaderCell = selectedHeaderCells.get(0);
        Assertions.assertThat(selectedHeaderCell.getColumnIndex()).isEqualTo(0);
        Assertions.assertThat(selectedHeaderCell.getRowIndex()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void startEditingCellPointCoordinateOutsideGridBounds() {
        cellSelectionManager.startEditingCell(new Point2D(-10,
                                                          -10));
        verify(col1,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellRenderContext.class),
                             any(Consumer.class));
        verify(col2,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellRenderContext.class),
                             any(Consumer.class));

        verify(col1,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellEditContext.class),
                             any(Consumer.class));
        verify(col2,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellEditContext.class),
                             any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void startEditingCellPointCoordinateWithinGridBounds() {
        final Point2D editedAtPoint = new Point2D(col1.getWidth() + col2.getWidth() / 2,
                                                  HEADER_HEIGHT + ROW_HEIGHT / 2);

        cellSelectionManager.startEditingCell(editedAtPoint);

        verify(col2,
               times(1)).edit(any(GridCell.class),
                              contextArgumentCaptor.capture(),
                              any(Consumer.class));
        verify(col2,
               times(1)).edit(any(GridCell.class),
                              editContextArgumentCaptor.capture(),
                              any(Consumer.class));

        final GridBodyCellRenderContext context = contextArgumentCaptor.getValue();
        assertEquals(0,
                     context.getRowIndex());
        assertEquals(1,
                     context.getColumnIndex());

        final GridBodyCellEditContext editContext = editContextArgumentCaptor.getValue();
        assertTrue(editContext.getRelativeLocation().isPresent());
        assertEquals(editedAtPoint.add(gridWidget.getComputedLocation()),
                     editContext.getRelativeLocation().get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void startEditingCellCoordinateOutsideGridBounds() {
        cellSelectionManager.startEditingCell(-1,
                                              -1);
        verify(col1,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellRenderContext.class),
                             any(Consumer.class));
        verify(col2,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellRenderContext.class),
                             any(Consumer.class));

        verify(col1,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellEditContext.class),
                             any(Consumer.class));
        verify(col2,
               never()).edit(any(GridCell.class),
                             any(GridBodyCellEditContext.class),
                             any(Consumer.class));
    }

    @Test
    public void startEditingCoordinateWithinGridBoundsWithHeader() {
        assertStartEditingCoordinateWithinGridBounds(gridWidgetHeader);
    }

    @Test
    public void startEditingCoordinateWithinGridBoundsWithNullHeader() {
        assertStartEditingCoordinateWithinGridBounds(null);
    }

    @SuppressWarnings("unchecked")
    private void assertStartEditingCoordinateWithinGridBounds(final Group header) {
        when(gridWidget.getHeader()).thenReturn(header);

        cellSelectionManager.startEditingCell(0,
                                              1);

        verify(col2,
               times(1)).edit(any(GridCell.class),
                              contextArgumentCaptor.capture(),
                              any(Consumer.class));
        verify(col2,
               times(1)).edit(any(GridCell.class),
                              editContextArgumentCaptor.capture(),
                              any(Consumer.class));

        final GridBodyCellRenderContext context = contextArgumentCaptor.getValue();
        assertEquals(0,
                     context.getRowIndex());
        assertEquals(1,
                     context.getColumnIndex());

        final GridBodyCellEditContext editContext = editContextArgumentCaptor.getValue();
        assertFalse(editContext.getRelativeLocation().isPresent());

        cellSelectionManager.startEditingCell(new Point2D(10, HEADER_HEIGHT + 1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGridBodyCellEditContextConstruction() {
        final GridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>("value"));

        gridWidgetData.setCell(0, 1, () -> cell);

        cellSelectionManager.startEditingCell(0, 1);

        verify(col2).edit(eq(cell),
                          editContextArgumentCaptor.capture(),
                          any(Consumer.class));

        assertGridBodyCellEditContext(editContextArgumentCaptor.getValue(),
                                      0,
                                      1,
                                      ROW_HEIGHT,
                                      col2.getWidth(),
                                      GRID_ABSOLUTE_Y + HEADER_HEIGHT,
                                      GRID_ABSOLUTE_X + col1.getWidth(),
                                      GRID_ABSOLUTE_Y + HEADER_HEIGHT,
                                      visibleBounds.getX());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGridBodyCellEditContextConstructionWithMergedCells() {
        final GridCell<String> cell1 = new BaseGridCell<>(new BaseGridCellValue<>("value"));
        final GridCell<String> cell2 = new BaseGridCell<>(new BaseGridCellValue<>("value"));

        gridWidgetData.setMerged(true);
        gridWidgetData.setCell(0, 1, () -> cell1);
        gridWidgetData.setCell(1, 1, () -> cell2);

        cellSelectionManager.startEditingCell(0, 1);

        verify(col2).edit(eq(cell1),
                          editContextArgumentCaptor.capture(),
                          any(Consumer.class));

        assertGridBodyCellEditContext(editContextArgumentCaptor.getValue(),
                                      0,
                                      1,
                                      ROW_HEIGHT * 2,
                                      col2.getWidth(),
                                      GRID_ABSOLUTE_Y + HEADER_HEIGHT,
                                      GRID_ABSOLUTE_X + col1.getWidth(),
                                      GRID_ABSOLUTE_Y + HEADER_HEIGHT,
                                      visibleBounds.getX());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGridBodyCellEditContextConstructionWithMergedCellsProgrammaticEditingCollapsedCell() {
        final GridCell<String> cell1 = new BaseGridCell<>(new BaseGridCellValue<>("value"));
        final GridCell<String> cell2 = new BaseGridCell<>(new BaseGridCellValue<>("value"));

        gridWidgetData.setMerged(true);
        gridWidgetData.setCell(0, 1, () -> cell1);
        gridWidgetData.setCell(1, 1, () -> cell2);

        cellSelectionManager.startEditingCell(1, 1);

        verify(col2).edit(eq(cell2),
                          editContextArgumentCaptor.capture(),
                          any(Consumer.class));

        assertGridBodyCellEditContext(editContextArgumentCaptor.getValue(),
                                      1,
                                      1,
                                      ROW_HEIGHT * 2,
                                      col2.getWidth(),
                                      GRID_ABSOLUTE_Y + HEADER_HEIGHT,
                                      GRID_ABSOLUTE_X + col1.getWidth(),
                                      GRID_ABSOLUTE_Y + HEADER_HEIGHT,
                                      visibleBounds.getX());
    }

    private void assertGridBodyCellEditContext(final GridBodyCellEditContext context,
                                               final int expectedUiRowIndex,
                                               final int expectedUiColumnIndex,
                                               final double expectedCellHeight,
                                               final double expectedCellWidth,
                                               final double expectedAbsoluteCellY,
                                               final double expectedAbsoluteCellX,
                                               final double expectedClipMinY,
                                               final double expectedClipMinX) {
        assertEquals(expectedUiRowIndex, context.getRowIndex());
        assertEquals(expectedUiColumnIndex, context.getColumnIndex());
        assertEquals(expectedCellHeight, context.getCellHeight(), 0.0);
        assertEquals(expectedCellWidth, context.getCellWidth(), 0.0);
        assertEquals(expectedAbsoluteCellY, context.getAbsoluteCellY(), 0.0);
        assertEquals(expectedAbsoluteCellX, context.getAbsoluteCellX(), 0.0);
        assertEquals(expectedClipMinY, context.getClipMinY(), 0.0);
        assertEquals(expectedClipMinX, context.getClipMinX(), 0.0);
    }
}

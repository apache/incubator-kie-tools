/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridRendererHelperTest {

    private static final double HEADER_HEIGHT = 32.0;

    private static final double HEADER_ROW_HEIGHT = 10.0;

    private static final double BOUNDS_WIDTH = 1000.0;

    private static final double BOUNDS_HEIGHT = 1000.0;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private DefaultGridLayer gridLayer;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridColumnRenderer<String> columnRenderer;

    private GridColumn<Integer> uiColumn1;
    private GridColumn<String> uiColumn2;
    private GridRow uiRow1;
    private GridRow uiRow2;
    private GridRow uiRow3;

    private GridData uiModel;

    private BaseGridRendererHelper helper;

    @Before
    public void setup() {
        this.uiColumn1 = new RowNumberColumn();
        this.uiColumn2 = new BaseGridColumn<>(Arrays.asList(new BaseHeaderMetaData("title1"), new BaseHeaderMetaData("title2")),
                                              columnRenderer,
                                              100.0);
        this.uiRow1 = new BaseGridRow();
        this.uiRow2 = new BaseGridRow();
        this.uiRow3 = new BaseGridRow();
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiColumn1);
        this.uiModel.appendColumn(uiColumn2);
        this.uiModel.appendRow(uiRow1);
        this.uiModel.appendRow(uiRow2);
        this.uiModel.appendRow(uiRow3);
        this.helper = new BaseGridRendererHelper(gridWidget);

        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getLayer()).thenReturn(gridLayer);
        when(gridWidget.getRenderer()).thenReturn(gridRenderer);
        when(gridRenderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(gridRenderer.getHeaderRowHeight()).thenReturn(HEADER_ROW_HEIGHT);
        when(gridWidget.getWidth()).thenReturn(uiColumn1.getWidth() + uiColumn2.getWidth());
        when(gridWidget.getHeight()).thenReturn(HEADER_HEIGHT + uiRow1.getHeight() + uiRow2.getHeight() + uiRow3.getHeight());
    }

    @Test
    public void testGetColumnOffsetByObject() {
        assertThat(helper.getColumnOffset(uiColumn1)).isEqualTo(0.0);
        assertThat(helper.getColumnOffset(uiColumn2)).isEqualTo(uiColumn1.getWidth());
    }

    @Test
    public void testGetColumnOffsetByObjectWithHiddenColumn() {
        uiColumn1.setVisible(false);

        assertThat(helper.getColumnOffset(uiColumn1)).isEqualTo(0.0);
        assertThat(helper.getColumnOffset(uiColumn2)).isEqualTo(0.0);
    }

    @Test
    public void testGetColumnOffsetByIndex() {
        assertThat(helper.getColumnOffset(0)).isEqualTo(0.0);
        assertThat(helper.getColumnOffset(1)).isEqualTo(uiColumn1.getWidth());
    }

    @Test
    public void testGetColumnOffsetByIndexWithHiddenColumn() {
        uiColumn1.setVisible(false);

        assertThat(helper.getColumnOffset(0)).isEqualTo(0.0);
        assertThat(helper.getColumnOffset(1)).isEqualTo(0.0);
    }

    @Test
    public void testGetColumnOffsetByIndexAndSubList() {
        assertThat(helper.getColumnOffset(uiModel.getColumns(), 0)).isEqualTo(0.0);
        assertThat(helper.getColumnOffset(uiModel.getColumns(), 1)).isEqualTo(uiColumn1.getWidth());
    }

    @Test
    public void testGetColumnOffsetByIndexAndSubListWithHiddenColumn() {
        uiColumn1.setVisible(false);

        assertThat(helper.getColumnOffset(uiModel.getColumns(), 0)).isEqualTo(0.0);
        assertThat(helper.getColumnOffset(uiModel.getColumns(), 1)).isEqualTo(0.0);
    }

    @Test
    public void testGetRowOffsetWithObject() {
        assertThat(helper.getRowOffset(uiRow1)).isEqualTo(0.0);
        assertThat(helper.getRowOffset(uiRow2)).isEqualTo(uiRow1.getHeight());
        assertThat(helper.getRowOffset(uiRow3)).isEqualTo(uiRow1.getHeight() + uiRow2.getHeight());
    }

    @Test
    public void testGetRowOffsetWithIndex() {
        assertThat(helper.getRowOffset(0)).isEqualTo(0.0);
        assertThat(helper.getRowOffset(1)).isEqualTo(uiRow1.getHeight());
        assertThat(helper.getRowOffset(2)).isEqualTo(uiRow1.getHeight() + uiRow2.getHeight());
    }

    @Test
    public void testGetRowOffsetWithObjectAndExplicitRowHeights() {
        final List<Double> allRowHeights = new ArrayList<>(Arrays.asList(uiRow1.getHeight(), uiRow2.getHeight(), uiRow3.getHeight()));
        assertThat(helper.getRowOffset(uiRow1, allRowHeights)).isEqualTo(0.0);
        assertThat(helper.getRowOffset(uiRow2, allRowHeights)).isEqualTo(uiRow1.getHeight());
        assertThat(helper.getRowOffset(uiRow3, allRowHeights)).isEqualTo(uiRow1.getHeight() + uiRow2.getHeight());
    }

    @Test
    public void testGetRowOffsetWithIndexAndExplicitRowHeights() {
        final List<Double> allRowHeights = new ArrayList<>(Arrays.asList(uiRow1.getHeight(), uiRow2.getHeight(), uiRow3.getHeight()));
        assertThat(helper.getRowOffset(0, allRowHeights)).isEqualTo(0.0);
        assertThat(helper.getRowOffset(1, allRowHeights)).isEqualTo(uiRow1.getHeight());
        assertThat(helper.getRowOffset(2, allRowHeights)).isEqualTo(uiRow1.getHeight() + uiRow2.getHeight());
    }

    @Test
    public void testGetWidth() {
        assertThat(helper.getWidth(uiModel.getColumns())).isEqualTo(uiColumn1.getWidth() + uiColumn2.getWidth());
    }

    @Test
    public void testGetWidthWithHiddenColumn() {
        uiColumn1.setVisible(false);

        assertThat(helper.getWidth(uiModel.getColumns())).isEqualTo(uiColumn2.getWidth());
    }

    @Test
    public void testGetRenderingInformation_BoundsLeft() {
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(-(uiColumn1.getWidth() + uiColumn2.getWidth()) - 1, 0));

        assertThat(helper.getRenderingInformation()).isNull();
    }

    @Test
    public void testGetRenderingInformation_BoundsRight() {
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(BOUNDS_WIDTH + 1, 0));

        assertThat(helper.getRenderingInformation()).isNull();
    }

    @Test
    public void testGetRenderingInformation_BoundsTop() {
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0, -(HEADER_HEIGHT + uiRow1.getHeight() + uiRow2.getHeight() + uiRow3.getHeight()) - 1));

        assertThat(helper.getRenderingInformation()).isNull();
    }

    @Test
    public void testGetRenderingInformation_BoundsBottom() {
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0, BOUNDS_HEIGHT + 1));

        assertThat(helper.getRenderingInformation()).isNull();
    }

    @Test
    public void testGetRenderingInformation_FixedHeaderSelected_NoFloatingColumns() {
        when(gridWidget.isSelected()).thenReturn(true);
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0, 0));

        final BaseGridRendererHelper.RenderingInformation renderingInformation = helper.getRenderingInformation();
        assertRenderingInformation(renderingInformation,
                                   true,
                                   false,
                                   uiModel.getColumns(),
                                   0,
                                   uiModel.getRowCount() - 1,
                                   Arrays.asList(uiRow1.getHeight(), uiRow2.getHeight(), uiRow3.getHeight()),
                                   Arrays.asList(0.0, uiRow1.getHeight(), uiRow1.getHeight() + uiRow2.getHeight()),
                                   uiModel.getHeaderRowCount(),
                                   HEADER_ROW_HEIGHT,
                                   HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount(),
                                   HEADER_HEIGHT - HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount());

        assertBlockInformation(renderingInformation.getBodyBlockInformation(),
                               uiModel.getColumns(),
                               0.0,
                               0.0,
                               HEADER_HEIGHT,
                               uiColumn1.getWidth() + uiColumn2.getWidth());
        assertBlockInformation(renderingInformation.getFloatingBlockInformation(),
                               Collections.emptyList(),
                               0.0,
                               0.0,
                               HEADER_HEIGHT,
                               0.0);
    }

    @Test
    public void testGetRenderingInformation_FixedHeaderNotSelected_NoFloatingColumns() {
        when(gridWidget.isSelected()).thenReturn(false);
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0, 0));

        final BaseGridRendererHelper.RenderingInformation renderingInformation = helper.getRenderingInformation();
        assertRenderingInformation(renderingInformation,
                                   true,
                                   false,
                                   uiModel.getColumns(),
                                   0,
                                   uiModel.getRowCount() - 1,
                                   Arrays.asList(uiRow1.getHeight(), uiRow2.getHeight(), uiRow3.getHeight()),
                                   Arrays.asList(0.0, uiRow1.getHeight(), uiRow1.getHeight() + uiRow2.getHeight()),
                                   uiModel.getHeaderRowCount(),
                                   HEADER_ROW_HEIGHT,
                                   HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount(),
                                   HEADER_HEIGHT - HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount());

        assertBlockInformation(renderingInformation.getBodyBlockInformation(),
                               uiModel.getColumns(),
                               0.0,
                               0.0,
                               HEADER_HEIGHT,
                               uiColumn1.getWidth() + uiColumn2.getWidth());
        assertBlockInformation(renderingInformation.getFloatingBlockInformation(),
                               Collections.emptyList(),
                               0.0,
                               0.0,
                               HEADER_HEIGHT,
                               0.0);
    }

    @Test
    public void testGetRenderingInformation_FloatingHeaderSelected_NoFloatingColumns() {
        final double gridWidgetLocationY = -uiRow1.getHeight() - 5;
        when(gridWidget.isSelected()).thenReturn(true);
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0, gridWidgetLocationY));

        final BaseGridRendererHelper.RenderingInformation renderingInformation = helper.getRenderingInformation();
        assertRenderingInformation(renderingInformation,
                                   false,
                                   true,
                                   uiModel.getColumns(),
                                   1,
                                   uiModel.getRowCount() - 1,
                                   Arrays.asList(uiRow1.getHeight(), uiRow2.getHeight(), uiRow3.getHeight()),
                                   Arrays.asList(uiRow1.getHeight(), uiRow1.getHeight() + uiRow2.getHeight()),
                                   uiModel.getHeaderRowCount(),
                                   HEADER_ROW_HEIGHT,
                                   HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount(),
                                   HEADER_HEIGHT - HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount());

        assertBlockInformation(renderingInformation.getBodyBlockInformation(),
                               uiModel.getColumns(),
                               0.0,
                               -gridWidgetLocationY,
                               HEADER_HEIGHT + uiRow1.getHeight(),
                               uiColumn1.getWidth() + uiColumn2.getWidth());
        assertBlockInformation(renderingInformation.getFloatingBlockInformation(),
                               Collections.emptyList(),
                               0.0,
                               -gridWidgetLocationY,
                               HEADER_HEIGHT + uiRow1.getHeight(),
                               0.0);
    }

    @Test
    public void testGetRenderingInformation_FloatingHeaderSelected_FloatingColumns() {
        final double gridWidgetLocationY = -uiRow1.getHeight() - 5;
        final double gridWidgetLocationX = -uiColumn1.getWidth() - 5;
        when(gridWidget.isSelected()).thenReturn(true);
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(gridWidgetLocationX, gridWidgetLocationY));

        final BaseGridRendererHelper.RenderingInformation renderingInformation = helper.getRenderingInformation();
        assertRenderingInformation(renderingInformation,
                                   false,
                                   true,
                                   Collections.singletonList(uiColumn2),
                                   1,
                                   uiModel.getRowCount() - 1,
                                   Arrays.asList(uiRow1.getHeight(), uiRow2.getHeight(), uiRow3.getHeight()),
                                   Arrays.asList(uiRow1.getHeight(), uiRow1.getHeight() + uiRow2.getHeight()),
                                   uiModel.getHeaderRowCount(),
                                   HEADER_ROW_HEIGHT,
                                   HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount(),
                                   HEADER_HEIGHT - HEADER_ROW_HEIGHT * uiModel.getHeaderRowCount());

        assertBlockInformation(renderingInformation.getBodyBlockInformation(),
                               Collections.singletonList(uiColumn2),
                               uiColumn1.getWidth(),
                               -gridWidgetLocationY,
                               HEADER_HEIGHT + uiRow1.getHeight(),
                               uiColumn2.getWidth());
        assertBlockInformation(renderingInformation.getFloatingBlockInformation(),
                               Collections.singletonList(uiColumn1),
                               -gridWidgetLocationX,
                               -gridWidgetLocationY,
                               HEADER_HEIGHT + uiRow1.getHeight(),
                               uiColumn1.getWidth());
    }

    @Test
    public void testGetColumnInformation_FixedHeaderSelected_NoFloatingColumns() {
        when(gridWidget.isSelected()).thenReturn(true);
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0, 0));

        final BaseGridRendererHelper.ColumnInformation columnInformation = helper.getColumnInformation(uiColumn1.getWidth() / 2);
        assertColumnInformation(columnInformation,
                                uiColumn1,
                                0,
                                0.0);
    }

    @Test
    public void testGetColumnInformation_FixedHeaderSelected_FloatingColumns() {
        final double gridWidgetLocationX = -5;
        when(gridWidget.isSelected()).thenReturn(true);
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, BOUNDS_WIDTH, BOUNDS_HEIGHT));
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(gridWidgetLocationX, 0));

        final BaseGridRendererHelper.ColumnInformation columnInformation = helper.getColumnInformation(uiColumn1.getWidth() / 2);
        assertColumnInformation(columnInformation,
                                uiColumn1,
                                0,
                                -gridWidgetLocationX);
    }

    private void assertRenderingInformation(final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                            final boolean expectedIsFixedHeader,
                                            final boolean expectedIsFloatingHeader,
                                            final List<GridColumn<?>> expectedAllColumns,
                                            final int expectedMinVisibleRowIndex,
                                            final int expectedMaxVisibleRowIndex,
                                            final List<Double> expectedAllRowHeights,
                                            final List<Double> expectedVisibleRowOffsets,
                                            final int expectedHeaderRowCount,
                                            final double expectedHeaderRowHeight,
                                            final double expectedHeaderRowsHeight,
                                            final double expectedHeaderRowsYOffset) {
        assertThat(renderingInformation).isNotNull();
        assertThat(renderingInformation.isFixedHeader()).isEqualTo(expectedIsFixedHeader);
        assertThat(renderingInformation.isFloatingHeader()).isEqualTo(expectedIsFloatingHeader);
        assertThat(renderingInformation.getAllColumns()).containsSequence(expectedAllColumns);
        assertThat(renderingInformation.getMinVisibleRowIndex()).isEqualTo(expectedMinVisibleRowIndex);
        assertThat(renderingInformation.getMaxVisibleRowIndex()).isEqualTo(expectedMaxVisibleRowIndex);
        assertThat(renderingInformation.getAllRowHeights()).containsSequence(expectedAllRowHeights);
        assertThat(renderingInformation.getVisibleRowOffsets()).containsSequence(expectedVisibleRowOffsets);
        assertThat(renderingInformation.getHeaderRowCount()).isEqualTo(expectedHeaderRowCount);
        assertThat(renderingInformation.getHeaderRowHeight()).isEqualTo(expectedHeaderRowHeight);
        assertThat(renderingInformation.getHeaderRowsHeight()).isEqualTo(expectedHeaderRowsHeight);
        assertThat(renderingInformation.getHeaderRowsYOffset()).isEqualTo(expectedHeaderRowsYOffset);
    }

    private void assertBlockInformation(final BaseGridRendererHelper.RenderingBlockInformation blockInformation,
                                        final List<GridColumn<?>> expectedBlockColumns,
                                        final double expectedBlockOffsetX,
                                        final double expectedBlockHeaderYOffset,
                                        final double expectedBlockBodyYOffset,
                                        final double expectedBlockWidth) {
        assertThat(blockInformation).isNotNull();
        assertThat(blockInformation.getColumns()).containsSequence(expectedBlockColumns);
        assertThat(blockInformation.getX()).isEqualTo(expectedBlockOffsetX);
        assertThat(blockInformation.getHeaderY()).isEqualTo(expectedBlockHeaderYOffset);
        assertThat(blockInformation.getBodyY()).isEqualTo(expectedBlockBodyYOffset);
        assertThat(blockInformation.getWidth()).isEqualTo(expectedBlockWidth);
    }

    private void assertColumnInformation(final BaseGridRendererHelper.ColumnInformation columnInformation,
                                         final GridColumn<?> expectedColumn,
                                         final int expectedUiColumnIndex,
                                         final double expectedOffsetX) {
        assertThat(columnInformation).isNotNull();
        assertThat(columnInformation.getColumn()).isEqualTo(expectedColumn);
        assertThat(columnInformation.getUiColumnIndex()).isEqualTo(expectedUiColumnIndex);
        assertThat(columnInformation.getOffsetX()).isEqualTo(expectedOffsetX);
    }
}

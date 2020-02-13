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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultGridWidgetCollapsedCellMouseEventHandlerTest extends BaseGridWidgetMouseClickHandlerTest {

    @Mock
    private GridCell gridCell;

    @Mock
    private GridCell nextGridCell;

    @Mock
    private GridRow gridRow;

    @Mock
    private GridColumn gridColumn;

    private DefaultGridWidgetCollapsedCellMouseEventHandler handler;

    @Before
    public void setup() {
        super.setup();

        final DefaultGridWidgetCollapsedCellMouseEventHandler wrapped = new DefaultGridWidgetCollapsedCellMouseEventHandler(renderer);
        handler = spy(wrapped);

        doNothing().when(handler).collapseRows(any(GridWidget.class),
                                               anyInt(),
                                               anyInt(),
                                               anyInt());
        doNothing().when(handler).expandRows(any(GridWidget.class),
                                             anyInt(),
                                             anyInt(),
                                             anyInt());
    }

    @Test
    public void testNullCell() {
        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(0),
                                            event)).isFalse();

        verify(handler, never()).collapseRows(any(GridWidget.class),
                                              anyInt(),
                                              anyInt(),
                                              anyInt());
        verify(handler, never()).expandRows(any(GridWidget.class),
                                            anyInt(),
                                            anyInt(),
                                            anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNonMergedCell() {
        when(uiModel.getCell(eq(0), eq(0))).thenReturn(gridCell);
        when(gridCell.getMergedCellCount()).thenReturn(1);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(0),
                                            event)).isFalse();

        verify(handler, never()).collapseRows(any(GridWidget.class),
                                              anyInt(),
                                              anyInt(),
                                              anyInt());
        verify(handler, never()).expandRows(any(GridWidget.class),
                                            anyInt(),
                                            anyInt(),
                                            anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMergedCellNotOnHotSpot() {
        when(uiModel.getCell(eq(0), eq(0))).thenReturn(gridCell);
        when(uiModel.getCell(eq(1), eq(0))).thenReturn(nextGridCell);
        when(uiModel.getColumns()).thenReturn(Collections.singletonList(gridColumn));
        when(uiModel.getRow(eq(0))).thenReturn(gridRow);
        when(gridCell.getMergedCellCount()).thenReturn(2);
        when(nextGridCell.isCollapsed()).thenReturn(false);
        when(gridWidget.onGroupingToggle(anyDouble(),
                                         anyDouble(),
                                         anyDouble(),
                                         anyDouble())).thenReturn(false);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(0),
                                            event)).isFalse();

        verify(handler, never()).collapseRows(any(GridWidget.class),
                                              anyInt(),
                                              anyInt(),
                                              anyInt());
        verify(handler, never()).expandRows(any(GridWidget.class),
                                            anyInt(),
                                            anyInt(),
                                            anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMergedCellCollapseOnHotSpot() {
        when(uiModel.getCell(eq(0), eq(0))).thenReturn(gridCell);
        when(uiModel.getCell(eq(1), eq(0))).thenReturn(nextGridCell);
        when(uiModel.getColumns()).thenReturn(Collections.singletonList(gridColumn));
        when(uiModel.getRow(eq(0))).thenReturn(gridRow);
        when(gridCell.getMergedCellCount()).thenReturn(2);
        when(nextGridCell.isCollapsed()).thenReturn(false);
        when(gridWidget.onGroupingToggle(anyDouble(),
                                         anyDouble(),
                                         anyDouble(),
                                         anyDouble())).thenReturn(true);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(0),
                                            event)).isTrue();

        verify(handler).collapseRows(eq(gridWidget),
                                     eq(0),
                                     eq(0),
                                     eq(2));
        verify(handler, never()).expandRows(any(GridWidget.class),
                                            anyInt(),
                                            anyInt(),
                                            anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMergedCellExpandOnHotSpot() {
        when(uiModel.getCell(eq(0), eq(0))).thenReturn(gridCell);
        when(uiModel.getCell(eq(1), eq(0))).thenReturn(nextGridCell);
        when(uiModel.getColumns()).thenReturn(Collections.singletonList(gridColumn));
        when(uiModel.getRow(eq(0))).thenReturn(gridRow);
        when(gridCell.getMergedCellCount()).thenReturn(2);
        when(nextGridCell.isCollapsed()).thenReturn(true);
        when(gridWidget.onGroupingToggle(anyDouble(),
                                         anyDouble(),
                                         anyDouble(),
                                         anyDouble())).thenReturn(true);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(0),
                                            event)).isTrue();

        verify(handler, never()).collapseRows(any(GridWidget.class),
                                              anyInt(),
                                              anyInt(),
                                              anyInt());
        verify(handler).expandRows(eq(gridWidget),
                                   eq(0),
                                   eq(0),
                                   eq(2));
    }

    @Test
    public void checkOnNodeMouseEventDuringDragOperation() {
        doReturn(true).when(handler).isDNDOperationInProgress(eq(gridWidget));

        assertFalse(handler.onNodeMouseEvent(gridWidget,
                                             relativeLocation,
                                             Optional.empty(),
                                             Optional.empty(),
                                             Optional.of(0),
                                             Optional.of(1),
                                             event));
    }
}

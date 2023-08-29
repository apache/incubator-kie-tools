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

package org.kie.workbench.common.dmn.client.widgets.grid.handlers;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DelegatingGridWidgetCellSelectorMouseEventHandlerTest {

    private static final int PARENT_ROW_INDEX = 0;

    private static final int PARENT_COLUMN_INDEX = 1;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridCellTuple parent;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private CellSelectionManager parentCellSelectionManager;

    @Mock
    private LiteralExpressionGrid gridWidget;

    @Mock
    private Point2D relativeLocation;

    @Mock
    private AbstractNodeHumanInputEvent event;

    @Mock
    private Layer layer;

    private Optional<Integer> uiHeaderRowIndex;

    private Optional<Integer> uiHeaderColumnIndex;

    private Optional<Integer> uiRowIndex;

    private Optional<Integer> uiColumnIndex;

    private DelegatingGridWidgetCellSelectorMouseEventHandler handler;

    private void setupGrid(final Supplier<GridCellTuple> parentSupplier,
                           final Supplier<Integer> nestingSupplier) {
        this.handler = spy(new DelegatingGridWidgetCellSelectorMouseEventHandler(selectionManager,
                                                                                 parentSupplier,
                                                                                 nestingSupplier));

        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(parent.getRowIndex()).thenReturn(PARENT_ROW_INDEX);
        when(parent.getColumnIndex()).thenReturn(PARENT_COLUMN_INDEX);
        when(parentGridWidget.getCellSelectionManager()).thenReturn(parentCellSelectionManager);
        when(parentGridWidget.getLayer()).thenReturn(layer);
    }

    @Test
    public void testOnNodeMouseEventWhenNotNested() {
        setupGrid(() -> null, () -> 0);

        uiHeaderRowIndex = Optional.of(0);
        uiHeaderColumnIndex = Optional.of(1);
        uiRowIndex = Optional.of(2);
        uiColumnIndex = Optional.of(3);

        handler.onNodeMouseEvent(gridWidget,
                                 relativeLocation,
                                 uiHeaderRowIndex,
                                 uiHeaderColumnIndex,
                                 uiRowIndex,
                                 uiColumnIndex,
                                 event);

        verify(handler).doSuperOnNodeMouseEvent(eq(gridWidget),
                                                eq(relativeLocation),
                                                eq(uiHeaderRowIndex),
                                                eq(uiHeaderColumnIndex),
                                                eq(uiRowIndex),
                                                eq(uiColumnIndex),
                                                eq(event));
    }

    @Test
    public void testOnNodeMouseEventWhenNestedOnHeader() {
        setupGrid(() -> parent, () -> 1);

        uiHeaderRowIndex = Optional.of(0);
        uiHeaderColumnIndex = Optional.of(1);
        uiRowIndex = Optional.empty();
        uiColumnIndex = Optional.empty();

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            uiHeaderRowIndex,
                                            uiHeaderColumnIndex,
                                            uiRowIndex,
                                            uiColumnIndex,
                                            event)).isFalse();
    }

    @Test
    public void testOnNodeMouseEventWhenNestedOnBodyWithoutSelectionChange() {
        when(event.isShiftKeyDown()).thenReturn(false);
        when(event.isCtrlKeyDown()).thenReturn(true);
        when(parentCellSelectionManager.selectCell(anyInt(), anyInt(), anyBoolean(), anyBoolean())).thenReturn(false);

        setupGrid(() -> parent, () -> 1);

        uiHeaderRowIndex = Optional.empty();
        uiHeaderColumnIndex = Optional.empty();
        uiRowIndex = Optional.of(0);
        uiColumnIndex = Optional.of(1);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            uiHeaderRowIndex,
                                            uiHeaderColumnIndex,
                                            uiRowIndex,
                                            uiColumnIndex,
                                            event)).isFalse();

        verify(parentCellSelectionManager).selectCell(eq(0),
                                                      eq(1),
                                                      eq(false),
                                                      eq(true));
        verify(gridWidget).doAfterSelectionChange(eq(0), eq(1));
    }

    @Test
    public void testOnNodeMouseEventWhenNestedOnBodyWithSelectionChange() {
        when(event.isShiftKeyDown()).thenReturn(false);
        when(event.isCtrlKeyDown()).thenReturn(true);
        when(parentGridWidget.isSelected()).thenReturn(false);
        when(parentCellSelectionManager.selectCell(anyInt(), anyInt(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupGrid(() -> parent, () -> 1);

        uiHeaderRowIndex = Optional.empty();
        uiHeaderColumnIndex = Optional.empty();
        uiRowIndex = Optional.of(0);
        uiColumnIndex = Optional.of(1);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            uiHeaderRowIndex,
                                            uiHeaderColumnIndex,
                                            uiRowIndex,
                                            uiColumnIndex,
                                            event)).isTrue();

        verify(parentCellSelectionManager).selectCell(eq(0),
                                                      eq(1),
                                                      eq(false),
                                                      eq(true));
        verify(layer).batch();
        verify(gridWidget).doAfterSelectionChange(eq(0), eq(1));
        verify(selectionManager).select(eq(parentGridWidget));
    }
}

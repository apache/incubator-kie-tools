/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.grid;

import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NodeMouseEventHandlerTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private DefaultGridLayer gridLayer;

    @Mock
    private GridWidgetDnDHandlersState state;

    @Mock
    private Point2D relativeLocation;

    @Mock
    private AbstractNodeMouseEvent event;

    private NodeMouseEventHandler handler;

    @Before
    public void setup() {
        this.handler = (final GridWidget gridWidget,
                        final Point2D relativeLocation,
                        final Optional<Integer> uiHeaderRowIndex,
                        final Optional<Integer> uiHeaderColumnIndex,
                        final Optional<Integer> uiRowIndex,
                        final Optional<Integer> uiColumnIndex,
                        final AbstractNodeMouseEvent event) -> false;
        when(gridWidget.getLayer()).thenReturn(gridLayer);
        when(gridLayer.getGridWidgetHandlersState()).thenReturn(state);
    }

    @Test
    public void testHandleHeaderCell() {
        assertFalse(handler.handleHeaderCell(gridWidget, relativeLocation, 0, 0, event));
    }

    @Test
    public void testHandleBodyCell() {
        assertFalse(handler.handleBodyCell(gridWidget, relativeLocation, 0, 0, event));
    }

    @Test
    public void testIsDNDOperationInProgress() {
        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.NONE);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_RESIZE);
        assertTrue(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_MOVE_INITIATED);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.COLUMN_MOVE);
        assertTrue(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE_PENDING);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE_INITIATED);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.ROW_MOVE);
        assertTrue(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.GRID_MOVE_PENDING);
        assertFalse(handler.isDNDOperationInProgress(gridWidget));

        when(state.getOperation()).thenReturn(GridWidgetHandlersOperation.GRID_MOVE);
        assertTrue(handler.isDNDOperationInProgress(gridWidget));
    }
}

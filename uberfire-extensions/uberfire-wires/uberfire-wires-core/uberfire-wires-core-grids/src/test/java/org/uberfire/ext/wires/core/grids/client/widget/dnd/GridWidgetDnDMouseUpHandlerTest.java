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
package org.uberfire.ext.wires.core.grids.client.widget.dnd;

import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridWidgetDnDMouseUpHandlerTest {

    @Mock
    private GridLayer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private DivElement element;

    @Mock
    private Style style;

    @Mock
    private NodeMouseUpEvent event;

    private GridWidgetDnDHandlersState state;

    private GridWidgetDnDMouseUpHandler handler;

    @Before
    public void setup() {
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);

        final GridWidgetDnDHandlersState wrappedState = new GridWidgetDnDHandlersState();
        this.state = spy(wrappedState);

        final GridWidgetDnDMouseUpHandler wrapped = new GridWidgetDnDMouseUpHandler(layer,
                                                                                    state);
        this.handler = spy(wrapped);

        doAnswer(i -> {
            ((Command) i.getArguments()[0]).execute();
            return null;
        }).when(handler).scheduleDeferred(any(Command.class));
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsNone() {
        state.setOperation(GridWidgetHandlersOperation.NONE);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsColumnMovePending() {
        state.setOperation(GridWidgetHandlersOperation.COLUMN_MOVE_PENDING);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsColumnResizePending() {
        state.setOperation(GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsColumnResize() {
        state.setOperation(GridWidgetHandlersOperation.COLUMN_RESIZE);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsColumnMove() {
        state.setOperation(GridWidgetHandlersOperation.COLUMN_MOVE);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
        verify(layer,
               times(1)).remove(any(IPrimitive.class));
        verify(layer,
               times(1)).batch();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsRowMove() {
        state.setOperation(GridWidgetHandlersOperation.ROW_MOVE);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
        verify(layer,
               times(1)).remove(any(IPrimitive.class));
        verify(layer,
               times(1)).batch();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsColumnMoveInitiated() {
        state.setOperation(GridWidgetHandlersOperation.COLUMN_MOVE_INITIATED);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
        verify(layer,
               times(1)).remove(any(IPrimitive.class));
        verify(layer,
               times(1)).batch();
    }

    @Test
    public void stateIsResetOnMouseUpWhenStateIsRowMoveInitiated() {
        state.setOperation(GridWidgetHandlersOperation.ROW_MOVE_INITIATED);

        handler.onNodeMouseUp(event);

        verify(state,
               times(1)).reset();
        verify(layer,
               times(1)).remove(any(IPrimitive.class));
        verify(layer,
               times(1)).batch();
    }
}

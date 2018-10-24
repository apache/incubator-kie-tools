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

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GridCellSelectorMouseClickHandlerTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Viewport viewport;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private NodeMouseClickEvent event;

    private GridCellSelectorMouseClickHandler handler;

    @Before
    public void setup() {
        when(gridWidget.getViewport()).thenReturn(viewport);

        final GridCellSelectorMouseClickHandler wrapped = new GridCellSelectorMouseClickHandler(gridWidget,
                                                                                                selectionManager,
                                                                                                renderer);
        handler = spy(wrapped);
    }

    @Test
    public void skipInvisibleGrid() {
        when(gridWidget.isVisible()).thenReturn(false);

        handler.onNodeMouseClick(event);

        verify(handler,
               never()).handleHeaderCellClick(any(NodeMouseClickEvent.class));
        verify(handler,
               never()).handleBodyCellClick(any(NodeMouseClickEvent.class));
    }

    @Test
    public void basicCheckSelectionIsDelegated() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(200);

        handler.onNodeMouseClick(event);

        verify(handler,
               times(1)).handleHeaderCellClick(any(NodeMouseClickEvent.class));
        verify(gridWidget,
               times(1)).selectHeaderCell(any(Point2D.class),
                                          eq(false),
                                          eq(false));

        verify(handler,
               times(1)).handleBodyCellClick(any(NodeMouseClickEvent.class));
        verify(gridWidget,
               times(1)).selectCell(any(Point2D.class),
                                    eq(false),
                                    eq(false));
    }
}

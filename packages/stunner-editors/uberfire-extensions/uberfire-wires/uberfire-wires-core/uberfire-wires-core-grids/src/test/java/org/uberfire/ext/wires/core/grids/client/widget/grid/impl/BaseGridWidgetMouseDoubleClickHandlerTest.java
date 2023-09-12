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

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseGridWidgetMouseDoubleClickHandlerTest {

    @Mock
    protected Group header;

    @Mock
    protected Viewport viewport;

    @Mock
    protected DefaultGridLayer layer;

    @Mock
    protected GridData uiModel;

    @Mock
    protected GridWidget gridWidget;

    @Mock
    protected GridRenderer renderer;

    @Mock
    protected BaseGridRendererHelper helper;

    @Mock
    protected GridColumn<String> uiColumn;

    @Mock
    protected Point2D relativeLocation;

    @Mock
    private NodeMouseEventHandler eventHandler;

    @Mock
    private GridWidgetDnDHandlersState state;

    protected NodeMouseDoubleClickEvent event;

    @Mock
    protected MouseEvent doubleClickEvent;

    private BaseGridWidgetMouseDoubleClickHandler mouseDoubleClickHandler;

    @Before
    public void setup() {
        this.event = new NodeMouseDoubleClickEvent(mock(HTMLDivElement.class));
        this.event.override(null, doubleClickEvent, null, 0, 0, null);

        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getRenderer()).thenReturn(renderer);
        when(gridWidget.getRendererHelper()).thenReturn(helper);
        when(gridWidget.getLayer()).thenReturn(layer);
        when(gridWidget.getHeader()).thenReturn(header);
        when(gridWidget.getComputedLocation()).thenReturn(new Point2D(0.0, 0.0));
        when(renderer.getHeaderHeight()).thenReturn(64.0);
        when(renderer.getHeaderRowHeight()).thenReturn(32.0);
        when(uiModel.getHeaderRowCount()).thenReturn(2);
        when(uiModel.getColumnCount()).thenReturn(1);
        when(uiModel.getColumns()).thenReturn(new ArrayList<GridColumn<?>>() {{
            add(uiColumn);
        }});

        when(state.getOperation()).thenReturn(GridWidgetDnDHandlersState.GridWidgetHandlersOperation.NONE);
        when(layer.getGridWidgetHandlersState()).thenReturn(state);

        final BaseGridRendererHelper.RenderingInformation ri = BaseGridWidgetRenderingTestUtils.makeRenderingInformation(uiModel, Collections.emptyList());
        when(helper.getRenderingInformation()).thenReturn(ri);

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation(uiColumn, 0, 0);
        when(helper.getColumnInformation(anyDouble())).thenReturn(ci);

        this.mouseDoubleClickHandler = new BaseGridWidgetMouseDoubleClickHandler(gridWidget, Collections.singletonList(eventHandler));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void visibleGrid() {
        when(gridWidget.isVisible()).thenReturn(true);

        mouseDoubleClickHandler.onNodeMouseDoubleClick(event);

        verify(eventHandler).onNodeMouseEvent(eq(gridWidget),
                                              any(Point2D.class),
                                              any(Optional.class),
                                              any(Optional.class),
                                              any(Optional.class),
                                              any(Optional.class),
                                              eq(event));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skipInvisibleGrid() {
        when(gridWidget.isVisible()).thenReturn(false);

        mouseDoubleClickHandler.onNodeMouseDoubleClick(event);

        verify(eventHandler,
               never()).onNodeMouseEvent(any(GridWidget.class),
                                         any(Point2D.class),
                                         any(Optional.class),
                                         any(Optional.class),
                                         any(Optional.class),
                                         any(Optional.class),
                                         any(AbstractNodeHumanInputEvent.class));
    }
}

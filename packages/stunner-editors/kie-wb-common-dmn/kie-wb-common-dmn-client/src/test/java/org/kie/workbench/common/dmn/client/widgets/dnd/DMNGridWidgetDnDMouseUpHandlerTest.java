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
package org.kie.workbench.common.dmn.client.widgets.dnd;

import java.util.Collections;

import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridWidgetDnDMouseUpHandlerTest {

    private static final double INITIAL_WIDTH = 250.0;

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

    @Mock
    private BaseGrid activeGridWidget;

    @Mock
    private DMNGridColumn activeGridColumn;

    private GridWidgetDnDHandlersState state;

    private DMNGridWidgetDnDMouseUpHandler handler;

    @Before
    public void setup() {
        this.state = new GridWidgetDnDHandlersState();
        this.handler = new DMNGridWidgetDnDMouseUpHandler(layer, state);

        when(layer.getViewport()).thenReturn(viewport);
        when(element.getStyle()).thenReturn(style);
    }

    @Test
    public void testColumnResize() {
        state.setOperation(GridWidgetHandlersOperation.COLUMN_RESIZE);
        state.setActiveGridWidget(activeGridWidget);
        state.setActiveGridColumns(Collections.singletonList(activeGridColumn));
        state.setEventInitialColumnWidth(INITIAL_WIDTH);

        handler.onNodeMouseUp(event);

        verify(activeGridWidget).registerColumnResizeCompleted(activeGridColumn,
                                                               INITIAL_WIDTH);
    }
}

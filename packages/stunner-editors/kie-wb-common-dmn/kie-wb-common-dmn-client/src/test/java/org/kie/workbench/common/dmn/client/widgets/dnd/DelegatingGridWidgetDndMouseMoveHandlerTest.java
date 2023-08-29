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

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DelegatingGridWidgetDndMouseMoveHandlerTest {

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GridWidgetDnDHandlersState state;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private Viewport viewport;

    private DelegatingGridWidgetDndMouseMoveHandler handler;

    @Before
    public void setup() {
        this.handler = new DelegatingGridWidgetDndMouseMoveHandler(gridLayer,
                                                                   state);
    }

    @Test
    public void testDelegationToGridWidget() {
        final GridWidget view = mock(GridWidget.class);
        doReturn(rendererHelper).when(view).getRendererHelper();

        handler.findMovableRows(view, rendererHelper.getRenderingInformation(), 0, 0);

        verify(state, never()).reset();
    }

    @Test
    public void testDelegationToHasRowDragRestrictionsWhenPermitted() {
        final ContextGrid view = mock(ContextGrid.class);
        doReturn(rendererHelper).when(view).getRendererHelper();
        doReturn(true).when(view).isRowDragPermitted(eq(state));

        handler.findMovableRows(view, rendererHelper.getRenderingInformation(), 0, 0);

        verify(state, never()).reset();
    }

    @Test
    public void testDelegationToHasRowDragRestrictionsWhenNotPermitted() {
        final ContextGrid view = mock(ContextGrid.class);
        doReturn(rendererHelper).when(view).getRendererHelper();
        doReturn(false).when(view).isRowDragPermitted(eq(state));
        doReturn(Style.Cursor.DEFAULT).when(state).getCursor();
        doReturn(viewport).when(gridLayer).getViewport();
        CSSStyleDeclaration style = spy(new CSSStyleDeclaration());
        HTMLDivElement element = new HTMLDivElement();
        element.style = style;
        when(viewport.getElement()).thenReturn(element);

        handler.findMovableRows(view, rendererHelper.getRenderingInformation(), 0, 0);

        verify(state).reset();
        assertEquals(Style.Cursor.DEFAULT.getCssName(), style.cursor);
    }
}

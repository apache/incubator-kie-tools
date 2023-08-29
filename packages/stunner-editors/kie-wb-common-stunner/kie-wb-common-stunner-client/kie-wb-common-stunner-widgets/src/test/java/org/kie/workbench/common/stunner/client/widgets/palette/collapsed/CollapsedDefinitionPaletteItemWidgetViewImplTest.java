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


package org.kie.workbench.common.stunner.client.widgets.palette.collapsed;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedDefaultPaletteItem;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollapsedDefinitionPaletteItemWidgetViewImplTest {

    private static final double GLYPH_WIDTH = 35.0;

    private static final double GLYPH_HEIGHT = 45.0;

    private static final String GLYPH_TOOLTIP = "tooltip";

    private static final int CLIENT_X = 1;

    private static final int CLIENT_Y = 2;

    private static final int X = 3;

    private static final int Y = 1;

    @Mock
    private Button icon;

    @Mock
    private DOMGlyphRenderers domGlyphRenderers;

    @Mock
    private Glyph glyph;

    @Mock
    private org.jboss.errai.common.client.api.IsElement glyphElement;

    @Mock
    private HTMLElement glyphHtmlElement;

    @Mock
    private CollapsedDefinitionPaletteItemWidgetView.Presenter presenter;

    @Mock
    private CollapsedDefaultPaletteItem paletteItem;

    @Mock
    private MouseDownEvent mouseDownEvent;

    private CollapsedDefinitionPaletteItemWidgetViewImpl view;

    @Before
    public void setup() {
        this.view = new CollapsedDefinitionPaletteItemWidgetViewImpl(icon,
                                                                     domGlyphRenderers);
        this.view.init(presenter);

        when(domGlyphRenderers.render(eq(glyph), eq(GLYPH_WIDTH), eq(GLYPH_HEIGHT))).thenReturn(glyphElement);
        when(glyphElement.getElement()).thenReturn(glyphHtmlElement);
        when(presenter.getItem()).thenReturn(paletteItem);
        when(mouseDownEvent.getClientX()).thenReturn(CLIENT_X);
        when(mouseDownEvent.getClientY()).thenReturn(CLIENT_Y);
        when(mouseDownEvent.getX()).thenReturn(X);
        when(mouseDownEvent.getY()).thenReturn(Y);
    }

    @Test
    public void testRenderEmptyTitle() {
        this.view.render(glyph, GLYPH_WIDTH, GLYPH_HEIGHT);

        verify(icon).appendChild(eq(glyphHtmlElement));

        verify(icon).setTitle(eq(""));
    }

    @Test
    public void testRenderNonEmptyTitle() {
        when(paletteItem.getTooltip()).thenReturn(GLYPH_TOOLTIP);

        this.view.render(glyph, GLYPH_WIDTH, GLYPH_HEIGHT);

        verify(icon).appendChild(eq(glyphHtmlElement));

        verify(icon).setTitle(eq(GLYPH_TOOLTIP));
    }

    @Test
    public void testMouseDownEvent() {
        this.view.onMouseDown(mouseDownEvent);

        verify(presenter).onMouseDown(eq(CLIENT_X), eq(CLIENT_Y), eq(X), eq(Y));
    }
}

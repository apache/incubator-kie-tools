/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.widgets.palette;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler.Callback;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler.Item;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BS3PaletteWidgetViewImplTest {

    private static final String ITEM_ID = "item-id";
    private static final double X = 100.0;
    private static final double Y = 200.0;
    private static final double WIDTH = 32.0;
    private static final double HEIGHT = 64.0;

    @Mock
    private BS3PaletteWidget presenter;

    @Mock
    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    @Mock
    private Glyph glyph;

    @Captor
    private ArgumentCaptor<Item> dragItemCaptor;

    @Captor
    private ArgumentCaptor<Callback> dragProxyCallbackCaptor;

    private BS3PaletteWidgetViewImpl view;

    @Before
    public void setup() {
        this.view = new BS3PaletteWidgetViewImpl();
        this.view.setShapeGlyphDragHandler(shapeGlyphDragHandler);
        this.view.init(presenter);

        when(presenter.getShapeDragProxyGlyph(anyString())).thenReturn(glyph);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowDragProxy() {
        view.showDragProxy(ITEM_ID, X, Y, WIDTH, HEIGHT);

        verify(presenter).getShapeDragProxyGlyph(ITEM_ID);
        verify(shapeGlyphDragHandler).show(dragItemCaptor.capture(),
                                           eq((int) X),
                                           eq((int) Y),
                                           dragProxyCallbackCaptor.capture());

        final Item dragItem = dragItemCaptor.getValue();
        assertEquals(glyph,
                     dragItem.getShape());
        assertEquals(WIDTH,
                     dragItem.getWidth(),
                     0.0);
        assertEquals(HEIGHT,
                     dragItem.getHeight(),
                     0.0);

        final Callback dragProxyCallback = dragProxyCallbackCaptor.getValue();
        dragProxyCallback.onStart((int) X, (int) Y);
        verify(presenter).onDragStart(ITEM_ID, (int) X, (int) Y);

        dragProxyCallback.onMove((int) X, (int) Y);
        verify(presenter).onDragProxyMove(ITEM_ID, (int) X, (int) Y);

        dragProxyCallback.onComplete((int) X, (int) Y);
        verify(presenter).onDragProxyComplete(ITEM_ID, (int) X, (int) Y);
    }
}

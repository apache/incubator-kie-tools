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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeGlyphDragHandlerImplTest {

    private ShapeGlyphDragHandlerImpl shapeGlyphDragHandler;

    @Mock
    private LienzoGlyphRenderers glyphLienzoGlyphRenderer;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Glyph glyph;

    @Mock
    private ShapeGlyphDragHandler.Item item;

    @Before
    public void setUp() throws Exception {
        shapeGlyphDragHandler = new ShapeGlyphDragHandlerImpl(glyphLienzoGlyphRenderer);
        when(item.getHeight()).thenReturn(0);
        when(item.getWidth()).thenReturn(0);
        when(item.getShape()).thenReturn(glyph);
        when(glyphLienzoGlyphRenderer.render(glyph, 0, 0)).thenReturn
                (new Group());
    }

    @Test
    public void testProxyFor() throws Exception {
        DragProxy<AbstractCanvas, ShapeGlyphDragHandler.Item, DragProxyCallback> instance  = shapeGlyphDragHandler
                .proxyFor(canvas);
        assertTrue(instance == shapeGlyphDragHandler);
    }

    @Test
    public void testShow() throws Exception {

        DragProxyCallback callback = mock(DragProxyCallback.class);
        shapeGlyphDragHandler.show(item, 0, 0, callback);

        //asserting handlers registrations
        assertEquals(shapeGlyphDragHandler.handlerRegistrations.size(), 3);
        assertNotNull(shapeGlyphDragHandler.dragProxyPanel);

        verify(glyphLienzoGlyphRenderer).render(item.getShape(),
                                        item.getWidth(),
                                        item.getHeight());
    }

    @Test
    public void testClear() throws Exception {
        testShow();
        shapeGlyphDragHandler.clear();
        assertTrue(shapeGlyphDragHandler.handlerRegistrations.isEmpty());
        assertNull(shapeGlyphDragHandler.dragProxyPanel);
    }

    @Test
    public void testDestroy() throws Exception {
        testShow();
        shapeGlyphDragHandler.destroy();
        assertTrue(shapeGlyphDragHandler.handlerRegistrations.isEmpty());
        assertNull(shapeGlyphDragHandler.dragProxyPanel);
    }
}
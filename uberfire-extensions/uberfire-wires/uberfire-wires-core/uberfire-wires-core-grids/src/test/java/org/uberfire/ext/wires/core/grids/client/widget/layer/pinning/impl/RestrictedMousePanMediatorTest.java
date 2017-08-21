/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class RestrictedMousePanMediatorTest {

    private RestrictedMousePanMediator mediator;

    @Before
    public void setUp() {
        mediator = spy(new RestrictedMousePanMediator());
    }

    @Test
    public void testGetLayerViewport() {

        final GridLayer layer = mock(GridLayer.class);
        final Viewport expectedViewport = mock(Viewport.class);

        doReturn(expectedViewport).when(layer).getViewport();
        doReturn(layer).when(mediator).getGridLayer();

        final Viewport actualViewport = mediator.getLayerViewport();

        assertEquals(expectedViewport,
                     actualViewport);
    }

    @Test
    public void testSetCursor() {

        final Viewport viewport = mock(Viewport.class);
        final DivElement divElement = mock(DivElement.class);
        final Style.Cursor cursor = mock(Style.Cursor.class);
        final Style style = mock(Style.class);

        doReturn(style).when(divElement).getStyle();
        doReturn(divElement).when(viewport).getElement();
        doReturn(viewport).when(mediator).getLayerViewport();

        mediator.setCursor(cursor);

        verify(style).setCursor(cursor);
    }
}

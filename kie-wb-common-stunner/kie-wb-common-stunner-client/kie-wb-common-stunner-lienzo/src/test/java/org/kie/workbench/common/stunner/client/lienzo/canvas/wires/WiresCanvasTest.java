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

package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCanvasTest {

    private static final String UUID = "uuid";

    @Mock
    private EventSourceMock<CanvasClearEvent> canvasClearEvent;

    @Mock
    private EventSourceMock<CanvasShapeAddedEvent> canvasShapeAddedEvent;

    @Mock
    private EventSourceMock<CanvasShapeRemovedEvent> canvasShapeRemovedEvent;

    @Mock
    private EventSourceMock<CanvasDrawnEvent> canvasDrawnEvent;

    @Mock
    private EventSourceMock<CanvasFocusedEvent> canvasFocusedEvent;

    @Mock
    private LienzoLayer stunnerLayer;

    @Mock
    private com.ait.lienzo.client.core.shape.Layer lienzoLayer;

    @Mock
    private com.ait.lienzo.client.core.shape.Layer layer;

    @Mock
    private WiresCanvas.View view;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private com.ait.lienzo.client.core.shape.Shape lienzoShape;

    @Mock
    private Shape stunnerShape;

    private WiresUtils.UserData userData;

    private WiresCanvas canvas;

    @Before
    public void setup() {
        this.userData = new WiresUtils.UserData();
        this.userData.setUuid(UUID);

        when(view.getWiresManager()).thenReturn(wiresManager);
        when(stunnerLayer.getLienzoLayer()).thenReturn(lienzoLayer);
        when(lienzoLayer.getLayer()).thenReturn(layer);
        when(layer.findShapeAtPoint(anyInt(),
                                    anyInt())).thenReturn(lienzoShape);
        when(lienzoShape.getUserData()).thenReturn(userData);

        final WiresCanvas wrapped = new WiresCanvas(canvasClearEvent,
                                                    canvasShapeAddedEvent,
                                                    canvasShapeRemovedEvent,
                                                    canvasDrawnEvent,
                                                    canvasFocusedEvent,
                                                    stunnerLayer,
                                                    view) {
            @Override
            public void addControl(final IsWidget controlView) {
                //Not tested
            }

            @Override
            public void deleteControl(final IsWidget controlView) {
                //Not tested
            }

            @Override
            public Canvas initialize(CanvasSettings settings) {
                return this;
            }

            @Override
            public Shape getShape(final String uuid) {
                if (UUID.equals(uuid)) {
                    return stunnerShape;
                }
                return null;
            }
        };

        this.canvas = spy(wrapped);
    }

    @Test
    public void getShapeAtWithDefinedCoordinates() {
        final Optional<Shape> shape = canvas.getShapeAt(0,
                                                        0);
        assertTrue(shape.isPresent());

        assertEquals(stunnerShape,
                     shape.get());
    }

    @Test
    public void getShapeAtWithUndefinedCoordinates() {
        assertFalse(canvas.getShapeAt(-1,
                                      -1).isPresent());
    }
}

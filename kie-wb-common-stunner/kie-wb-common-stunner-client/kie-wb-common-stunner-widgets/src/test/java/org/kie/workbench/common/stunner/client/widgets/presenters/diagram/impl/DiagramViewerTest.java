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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramViewerTest extends AbstractCanvasHandlerViewerTest {

    @Mock
    ZoomControl<AbstractCanvas> zoomControl;

    @Mock
    SelectionControl<AbstractCanvasHandler, Element> selectionControl;

    @Mock
    WidgetWrapperView view;

    @Mock
    DiagramViewer.DiagramViewerCallback<Diagram> callback;

    private DiagramViewerImpl<Diagram, AbstractCanvasHandler, ClientSession> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        this.tested =
                new DiagramViewerImpl<>(canvas,
                                        canvasHandler,
                                        view,
                                        zoomControl,
                                        selectionControl);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(diagram,
                    callback);
        assertEquals(diagram,
                     tested.getInstance());
        verify(canvas,
               times(1)).initialize(100,
                                    100);
        verify(canvasHandler,
               times(1)).handle(eq(canvas));
        verify(canvasHandler,
               times(1)).draw(eq(diagram),
                              any(ParameterizedCommand.class));
        verify(callback,
               times(1)).afterCanvasInitialized();
        verify(zoomControl,
               times(1)).enable(eq(canvas));
        verify(selectionControl,
               times(1)).enable(eq(canvasHandler));
        verify(view,
               times(1)).setWidget(eq(canvasViewWidget));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScale() {
        tested.open(diagram,
                    callback);
        tested.scale(50,
                     50);
        assertEquals(diagram,
                     tested.getInstance());
        verify(zoomControl,
               times(1)).scale(eq(0.5d),
                               eq(0.5d));
        verify(lienzoPanel,
               times(1)).setPixelSize(50,
                                      50);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.open(diagram,
                    callback);
        tested.clear();
        assertNull(tested.getInstance());
        verify(zoomControl,
               times(1)).disable();
        verify(selectionControl,
               times(1)).disable();
        verify(canvasHandler,
               times(1)).clear();
        verify(view,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.open(diagram,
                    callback);
        tested.destroy();
        assertNull(tested.getInstance());
        verify(zoomControl,
               times(1)).disable();
        verify(selectionControl,
               times(1)).disable();
        verify(canvasHandler,
               times(1)).destroy();
        verify(view,
               times(1)).clear();
    }
}

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


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import java.util.Iterator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasElementListener;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasShapeListener;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DiagramViewerTest extends AbstractCanvasHandlerViewerTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private MediatorsControl<AbstractCanvas> mediatorsControlInstance;
    private ManagedInstance<MediatorsControl<AbstractCanvas>> mediatorsControl;

    @Mock
    private SelectionControl<AbstractCanvasHandler, Element> selectionControlInstance;
    private ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControl;

    private ManagedInstance<AbstractCanvas> canvases;
    private ManagedInstance<CanvasPanel> canvasPanels;
    private ManagedInstance<AbstractCanvasHandler> canvasHandlers;

    @Mock
    ScrollableLienzoPanel canvasPanel;

    @Mock
    WidgetWrapperView view;

    @Mock
    DiagramViewer.DiagramViewerCallback<Diagram> callback;

    @Mock
    CanvasSettings canvasSettings;

    @Mock
    StunnerPreferencesRegistries preferencesRegistries;

    @Mock
    StunnerPreferences stunnerPreferences;

    private DefaultDiagramViewer tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        when(canvasView.getLienzoPanel()).thenReturn(canvasPanel);
        canvases = spy(new ManagedInstanceStub<>(canvas));
        canvasPanels = spy(new ManagedInstanceStub<>(canvasPanel));
        canvasHandlers = spy(new ManagedInstanceStub<>(canvasHandler));
        mediatorsControl = spy(new ManagedInstanceStub<>(mediatorsControlInstance));
        selectionControl = spy(new ManagedInstanceStub<>(selectionControlInstance));
        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(preferencesRegistries.get(DEFINITION_SET_ID, StunnerPreferences.class)).thenReturn(stunnerPreferences);
        this.tested =
                new DefaultDiagramViewer(definitionUtils,
                                         canvases,
                                         canvasPanels,
                                         canvasHandlers,
                                         mediatorsControl,
                                         selectionControl,
                                         view,
                                         preferencesRegistries);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(diagram,
                    callback);
        assertEquals(diagram,
                     tested.getInstance());
        verify(canvasHandler,
               times(1)).handle(eq(canvas));
        verify(canvasHandler,
               times(1)).draw(eq(diagram),
                              any(ParameterizedCommand.class));
        verify(callback,
               times(1)).afterCanvasInitialized();
        verify(mediatorsControlInstance,
               times(1)).init(eq(canvas));
        verify(selectionControlInstance,
               times(1)).init(eq(canvasHandler));
        verify(view,
               times(1)).setWidget(eq(canvasViewWidget));
        ArgumentCaptor<CanvasShapeListener> shapeListenerArgumentCaptor = ArgumentCaptor.forClass(CanvasShapeListener.class);
        ArgumentCaptor<CanvasElementListener> elementListenerArgumentCaptor = ArgumentCaptor.forClass(CanvasElementListener.class);
        verify(canvas, times(1)).addRegistrationListener(shapeListenerArgumentCaptor.capture());
        verify(canvasHandler, times(1)).addRegistrationListener(elementListenerArgumentCaptor.capture());
        DefaultCanvasShapeListener shapeListener = (DefaultCanvasShapeListener) shapeListenerArgumentCaptor.getValue();
        Iterator<CanvasControl<AbstractCanvas>> canvasControls = shapeListener.getCanvasControls().iterator();
        assertTrue(canvasControls.next() instanceof MediatorsControl);
        assertFalse(canvasControls.hasNext());
        DefaultCanvasElementListener elementListener = (DefaultCanvasElementListener) elementListenerArgumentCaptor.getValue();
        Iterator<CanvasControl<AbstractCanvasHandler>> canvasHandlerControls1 = elementListener.getCanvasControls().iterator();
        assertTrue(canvasHandlerControls1.next() instanceof SelectionControl);
        assertFalse(canvasHandlerControls1.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScale() {
        when(canvas.getWidthPx()).thenReturn(100);
        when(canvas.getHeightPx()).thenReturn(100);
        tested.open(diagram,
                    callback);
        tested.scale(50,
                     50);
        assertEquals(diagram,
                     tested.getInstance());
        verify(mediatorsControlInstance,
               times(1)).scale(eq(0.5d),
                               eq(0.5d));
        verify(canvasView,
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
        verify(mediatorsControl,
               times(1)).destroyAll();
        verify(selectionControl,
               times(1)).destroyAll();
        verify(canvases,
               times(1)).destroyAll();
        verify(canvasHandlers,
               times(1)).destroyAll();
        verify(view,
               times(1)).clear();
    }

    @Override
    protected CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }
}

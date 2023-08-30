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


package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LineSpliceAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SessionEditorTest extends AbstractCanvasHandlerViewerTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    @Mock
    EditorSession session;
    @Mock
    SessionViewer.SessionViewerCallback<Diagram> callback;
    @Mock
    ScrollableLienzoPanel canvasPanel;
    @Mock
    ScrollablePanel canvasPanelView;
    @Mock
    MediatorsControl<AbstractCanvas> mediatorsControl;
    @Mock
    SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    @Mock
    ConnectionAcceptorControl<AbstractCanvasHandler> connectionAcceptorControl;
    @Mock
    ContainmentAcceptorControl<AbstractCanvasHandler> containmentAcceptorControl;
    @Mock
    DockingAcceptorControl<AbstractCanvasHandler> dockingAcceptorControl;
    @Mock
    LineSpliceAcceptorControl<AbstractCanvasHandler> lineSpliceAcceptorControl;
    @Mock
    WidgetWrapperView view;
    @Mock
    StunnerPreferencesRegistries preferencesRegistries;
    @Mock
    StunnerPreferences stunnerPreferences;

    private SessionEditorImpl<EditorSession> tested;

    @Before
    public void setup() throws Exception {
        super.init();
        when(canvasPanel.getView()).thenReturn(canvasPanelView);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(session.getCanvas()).thenReturn(canvas);
        when(session.getMediatorsControl()).thenReturn(mediatorsControl);
        when(session.getSelectionControl()).thenReturn(selectionControl);
        when(session.getConnectionAcceptorControl()).thenReturn(connectionAcceptorControl);
        when(session.getContainmentAcceptorControl()).thenReturn(containmentAcceptorControl);
        when(session.getDockingAcceptorControl()).thenReturn(dockingAcceptorControl);
        when(session.getLineSpliceAcceptorControl()).thenReturn(lineSpliceAcceptorControl);
        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(preferencesRegistries.get(DEFINITION_SET_ID, StunnerPreferences.class)).thenReturn(stunnerPreferences);
        this.tested = new SessionEditorImpl<>(view, canvasPanel, preferencesRegistries);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open(session,
                    callback);
        assertEquals(session,
                     tested.getInstance());
        assertEquals(canvasHandler,
                     tested.getSessionHandler());
        assertEquals(diagram,
                     tested.getHandler().getDiagram());
        assertEquals(mediatorsControl,
                     tested.getDiagramEditor().getMediatorsControl());
        assertEquals(selectionControl,
                     tested.getDiagramEditor().getSelectionControl());
        assertEquals(connectionAcceptorControl,
                     tested.getDiagramEditor().getConnectionAcceptorControl());
        assertEquals(containmentAcceptorControl,
                     tested.getDiagramEditor().getContainmentAcceptorControl());
        assertEquals(dockingAcceptorControl,
                     tested.getDiagramEditor().getDockingAcceptorControl());
        assertEquals(lineSpliceAcceptorControl,
                     tested.getDiagramEditor().getLineSpliceAcceptorControl());
        verify(canvasHandler,
               times(1)).draw(eq(diagram),
                              any(ParameterizedCommand.class));
        verify(view,
               times(1)).setWidget(any(Widget.class));
        verify(canvasPanelView, times(1)).onResize();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.open(session,
                    callback);
        tested.clear();
        verify(canvasHandler,
               times(1)).clear();
        verify(view,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.open(session,
                    callback);
        tested.destroy();
        assertNull(tested.getInstance());
        verify(canvasHandler,
               never()).destroy();
        verify(view,
               times(1)).clear();
        assertNull(tested.getDiagram());
    }

    @Override
    protected CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }
}

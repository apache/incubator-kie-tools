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


package org.kie.workbench.common.stunner.client.widgets.editor;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.api.JsStunnerEditor;
import org.kie.workbench.common.stunner.core.client.api.JsWindow;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.widgets.client.errorpage.ErrorPage;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StunnerEditorTest {

    @Mock
    private SessionEditorPresenter<EditorSession> sessionEditorPresenter;
    @Mock
    private ManagedInstance<SessionEditorPresenter<EditorSession>> sessionEditorPresenters;

    @Mock
    private SessionViewerPresenter<ViewerSession> sessionViewerPresenter;
    @Mock
    private ManagedInstance<SessionViewerPresenter<ViewerSession>> sessionViewerPresenters;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ErrorPage errorPage;

    @Mock
    private EditorSession editorSession;
    @Mock
    private ViewerSession viewerSession;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AlertsControl<AbstractCanvas> alertsControl;
    @Mock
    private LienzoCanvas canvas;
    @Mock
    private WiresCanvasView canvasView;
    @Mock
    private LienzoPanel panel;
    @Mock
    private LienzoBoundsPanel panelView;
    @Mock
    private Layer layer;

    private DiagramImpl diagram;

    private StunnerEditor tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionEditorPresenters.get()).thenReturn(sessionEditorPresenter);
        when(sessionViewerPresenters.get()).thenReturn(sessionViewerPresenter);
        when(sessionEditorPresenter.getInstance()).thenReturn(editorSession);
        when(sessionViewerPresenter.getInstance()).thenReturn(viewerSession);
        when(sessionEditorPresenter.getHandler()).thenReturn(canvasHandler);
        when(sessionViewerPresenter.getHandler()).thenReturn(canvasHandler);
        Metadata metadata = new MetadataImpl.MetadataImplBuilder("testSet").build();
        diagram = new DiagramImpl("testDiagram", metadata);
        when(editorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(panel);
        when(panel.getView()).thenReturn(panelView);
        when(panelView.getLayer()).thenReturn(layer);
        when(editorSession.getAlertsControl()).thenReturn(alertsControl);
        when(viewerSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(viewerSession.getAlertsControl()).thenReturn(alertsControl);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        tested = spy(new StunnerEditor(sessionEditorPresenters,
                sessionViewerPresenters,
                translationService,
                errorPage));
        doNothing().when(tested).setupRootContainer();
        doNothing().when(tested).clearRootAndDrawError();
        JsWindow.setEditor(new JsStunnerEditor());

    }

    @Test
    @SuppressWarnings("all")
    public void testOpenSuccess() {
        tested.setReadOnly(false);
        openSuccess();
        assertEquals(editorSession, tested.getSession());
        verify(sessionEditorPresenters).get();
        verify(sessionViewerPresenters, never()).get();
        assertFalse(tested.isReadOnly());
    }

    @Test
    public void testOpenSuccessReadOnly() {
        tested.setReadOnly(true);
        openSuccess();
        assertEquals(viewerSession, tested.getSession());
        verify(sessionViewerPresenters).get();
        verify(sessionEditorPresenters, never()).get();
        assertTrue(tested.isReadOnly());
    }

    @Test
    public void testClose() {
        openSuccess();
        tested.close();
        tested.close(); // Close twice to check it behaves properly as well.
        verify(sessionEditorPresenter, times(1)).destroy();
        verify(sessionEditorPresenters, times(1)).destroyAll();
        verify(sessionViewerPresenters, times(1)).destroyAll();
        assertNull(tested.getPresenter());
    }

    @Test
    public void testDestroy() {
        openSuccess();
        tested.destroy();
        assertNull(tested.getPresenter());
    }

    @Test
    @SuppressWarnings("all")
    public void testHandleParsingError() {
        Consumer<Throwable> exceptionConsumer = mock(Consumer.class);
        tested.setExceptionProcessor(exceptionConsumer);
        Consumer<DiagramParsingException> parsingExceptionConsumer = mock(Consumer.class);
        tested.setParsingExceptionProcessor(parsingExceptionConsumer);
        DiagramParsingException dpe = new DiagramParsingException(mock(Metadata.class), "testXml");
        ClientRuntimeError error = new ClientRuntimeError(dpe);
        tested.handleError(error);
        verify(parsingExceptionConsumer, times(1)).accept(eq(dpe));
        verify(exceptionConsumer, never()).accept(any());
    }

    @Test
    @SuppressWarnings("all")
    public void testHandleError() {
        Consumer<Throwable> exceptionConsumer = mock(Consumer.class);
        tested.setExceptionProcessor(exceptionConsumer);
        Consumer<DiagramParsingException> parsingExceptionConsumer = mock(Consumer.class);
        tested.setParsingExceptionProcessor(parsingExceptionConsumer);
        Throwable e = new Throwable("someErrorMessage");
        ClientRuntimeError error = new ClientRuntimeError(e);
        tested.handleError(error);
        verify(parsingExceptionConsumer, never()).accept(any());
        verify(exceptionConsumer, times(1)).accept(eq(e));
    }

    @SuppressWarnings("all")
    private void openSuccess() {
        doAnswer(invocation -> {
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).afterCanvasInitialized();
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(sessionViewerPresenter).open(eq(diagram), any(SessionPresenter.SessionPresenterCallback.class));
        doAnswer(invocation -> {
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).afterCanvasInitialized();
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(sessionEditorPresenter).open(eq(diagram), any(SessionPresenter.SessionPresenterCallback.class));
        SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);
        tested.open(diagram, callback);
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onError(any());
        assertEquals(canvasHandler, tested.getCanvasHandler());
        assertEquals(diagram, tested.getDiagram());
        assertEquals(diagram.hashCode(), tested.getCurrentContentHash());
    }

    @Test
    public void testGetSessionWhenPresenterIsNull() {
        assertNull(tested.getPresenter());
        assertNull(tested.getSession());
    }

    @Test
    public void testGetSessionWhenPresenterDisplayerIsNull() {
        doReturn(null).when(sessionEditorPresenter).getDisplayer();
        doCallRealMethod().when(sessionEditorPresenter).getInstance();
        assertNull(sessionEditorPresenter.getInstance());
    }

    @Test
    public void testSetCanvasBackgroundColor() {
        tested.setReadOnly(true);
        openSuccess();
        tested.setCanvasBackgroundColor();

        verify(canvas, times(1)).setBackgroundColor(any());
    }

    @Test
    public void testSetCanvasBackgroundColorNoSession() {
        tested.setCanvasBackgroundColor();

        verify(canvas, times(0)).setBackgroundColor(any());
    }

    @Test
    public void testSetScrollbarColors() {
        ScrollablePanel scrollablePanel = mock(ScrollablePanel.class);
        when(panel.getView()).thenReturn(scrollablePanel);

        tested.setReadOnly(true);
        openSuccess();
        tested.setScrollbarColors();

        verify(scrollablePanel, times(1)).setScrollbarColors(any(), any());
    }

    @Test
    public void testSetScrollbarColorsNoSession() {
        ScrollablePanel scrollablePanel = mock(ScrollablePanel.class);
        lenient().when(panel.getView()).thenReturn(scrollablePanel);

        tested.setScrollbarColors();

        verify(scrollablePanel, times(0)).setScrollbarColors(any(), any());
    }
}

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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.widgets.client.errorpage.ErrorPage;
import org.mockito.Mock;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StunnerEditorTest {

    @Mock
    private SessionEditorPresenter<EditorSession> sessionEditorPresenter;
    private ManagedInstance<SessionEditorPresenter<EditorSession>> sessionEditorPresenters;

    @Mock
    private SessionViewerPresenter<ViewerSession> sessionViewerPresenter;
    private ManagedInstance<SessionViewerPresenter<ViewerSession>> sessionViewerPresenters;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private StunnerEditorView view;

    @Mock
    private ErrorPage errorPage;

    @Mock
    private SessionPresenter.View sessionPresenterView;

    @Mock
    private EditorSession editorSession;
    @Mock
    private ViewerSession viewerSession;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    private DiagramImpl diagram;

    private StunnerEditor tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        sessionEditorPresenters = spy(new ManagedInstanceStub<>(sessionEditorPresenter));
        sessionViewerPresenters = spy(new ManagedInstanceStub<>(sessionViewerPresenter));
        when(sessionEditorPresenter.getView()).thenReturn(sessionPresenterView);
        when(sessionViewerPresenter.getView()).thenReturn(sessionPresenterView);
        when(sessionEditorPresenter.getInstance()).thenReturn(editorSession);
        when(sessionViewerPresenter.getInstance()).thenReturn(viewerSession);
        when(sessionEditorPresenter.getHandler()).thenReturn(canvasHandler);
        when(sessionViewerPresenter.getHandler()).thenReturn(canvasHandler);
        Metadata metadata = new MetadataImpl.MetadataImplBuilder("testSet").build();
        diagram = new DiagramImpl("testDiagram", mock(Graph.class), metadata);
        when(editorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(viewerSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        tested = new StunnerEditor(sessionEditorPresenters,
                                   sessionViewerPresenters,
                                   translationService,
                                   view,
                                   errorPage);
    }

    @Test
    @SuppressWarnings("all")
    public void testOpenSuccess() {
        doAnswer(invocation -> {
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(sessionEditorPresenter).open(eq(diagram), any(SessionPresenter.SessionPresenterCallback.class));
        tested.setReadOnly(false);
        openSuccess();
        assertEquals(editorSession, tested.getSession());
        verify(sessionEditorPresenters).get();
        verify(sessionViewerPresenters, never()).get();
        verify(sessionEditorPresenter).withPalette(eq(true));
        verify(sessionEditorPresenter).withToolbar(eq(false));
        assertFalse(tested.isReadOnly());
    }

    @Test
    public void testOpenSuccessReadOnly() {
        tested.setReadOnly(true);
        openSuccess();
        assertEquals(viewerSession, tested.getSession());
        verify(sessionViewerPresenters).get();
        verify(sessionEditorPresenters, never()).get();
        verify(sessionViewerPresenter).withPalette(eq(false));
        verify(sessionViewerPresenter).withToolbar(eq(false));
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
        verify(view).clear();
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
    public void testHandleError() {
        Consumer<Throwable> exceptionConsumer = mock(Consumer.class);
        tested.setExceptionProcessor(exceptionConsumer);
        Consumer<DiagramParsingException> parsingExceptionConsumer = mock(Consumer.class);
        tested.setParsingExceptionProcessor(parsingExceptionConsumer);
        Throwable e = new Throwable("someErrorMessage");
        ClientRuntimeError error = new ClientRuntimeError(e);
        tested.handleError(error);
        verify(parsingExceptionConsumer, never()).accept(any());
        verify(view, never()).setWidget(errorPage);
        verify(exceptionConsumer, times(1)).accept(e);
    }

    @Test
    public void testHandleParseExceptionError() {
        Consumer<Throwable> exceptionConsumer = mock(Consumer.class);
        tested.setExceptionProcessor(exceptionConsumer);
        Consumer<DiagramParsingException> parsingExceptionConsumer = mock(Consumer.class);
        tested.setParsingExceptionProcessor(parsingExceptionConsumer);
        DiagramParsingException e = new DiagramParsingException();
        String errorMessage = "error-message";
        ClientRuntimeError error = new ClientRuntimeError(errorMessage, e);
        tested.handleError(error);
        verify(parsingExceptionConsumer, times(1)).accept(e);
        verify(view, times(1)).setWidget(errorPage);
        verify(errorPage, times(1)).setErrorContent(errorMessage);
        verify(exceptionConsumer, never()).accept(any());
    }

    @SuppressWarnings("all")
    private void openSuccess() {
        doAnswer(invocation -> {
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(sessionViewerPresenter).open(eq(diagram), any(SessionPresenter.SessionPresenterCallback.class));
        doAnswer(invocation -> {
            ((SessionPresenter.SessionPresenterCallback) invocation.getArguments()[1]).onSuccess();
            return null;
        }).when(sessionEditorPresenter).open(eq(diagram), any(SessionPresenter.SessionPresenterCallback.class));
        Consumer<Integer> hashConsumer = mock(Consumer.class);
        tested.setOnResetContentHashProcessor(hashConsumer);
        SessionPresenter.SessionPresenterCallback callback = mock(SessionPresenter.SessionPresenterCallback.class);
        tested.open(diagram, callback);
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onError(any());
        assertEquals(canvasHandler, tested.getCanvasHandler());
        assertEquals(diagram, tested.getDiagram());
        assertEquals(diagram.hashCode(), tested.getCurrentContentHash());
    }
}

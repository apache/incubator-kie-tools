/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.screens.editor;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionDiagramEditorScreenTest {

    @Mock
    private DecisionNavigatorDock decisionNavigatorDock;

    @Mock
    private SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory;

    @Mock
    private ScreenPanelView screenPanelView;

    @Mock
    private SessionPresenter<AbstractClientFullSession, ?, Diagram> presenter;

    @Mock
    private ExpressionEditorView.Presenter expressionEditor;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSessionFactory<ClientFullSession> sessionFactory;

    @Captor
    private ArgumentCaptor<Consumer<ClientFullSession>> clientFullSessionConsumer;

    private SessionDiagramEditorScreen editor;

    @Before
    public void setup() {

        doReturn(presenter).when(sessionPresenterFactory).newPresenterEditor();
        doReturn(presenter).when(presenter).withToolbar(anyBoolean());
        doReturn(presenter).when(presenter).withPalette(anyBoolean());
        doReturn(presenter).when(presenter).displayNotifications(any());
        doNothing().when(presenter).open(any(), any(), any());

        editor = spy(new SessionDiagramEditorScreen(null, null, null, sessionManager, null, sessionPresenterFactory, null, null, screenPanelView, null, expressionEditor, decisionNavigatorDock));
    }

    @Test
    public void testInit() {
        editor.init();

        verify(decisionNavigatorDock).init(AuthoringPerspective.PERSPECTIVE_ID);
    }

    @Test
    public void testOpenDiagram() {

        final Diagram diagram = mock(Diagram.class);
        final Command callback = mock(Command.class);
        final Metadata metadata = mock(Metadata.class);
        final AbstractClientFullSession session = mock(AbstractClientFullSession.class);

        when(diagram.getMetadata()).thenReturn(metadata);
        when(sessionManager.getSessionFactory(metadata, ClientFullSession.class)).thenReturn(sessionFactory);

        editor.openDiagram(diagram, callback);

        verify(sessionFactory).newSession(eq(metadata), clientFullSessionConsumer.capture());

        clientFullSessionConsumer.getValue().accept(session);

        verify(editor).openDock(session);
    }

    @Test
    public void testOnClose() {

        doNothing().when(editor).destroyDock();
        doNothing().when(editor).destroySession();

        editor.onClose();

        verify(editor).destroyDock();
        verify(editor).destroySession();
    }

    @Test
    public void testOpenDock() {

        final AbstractClientFullSession session = mock(AbstractClientFullSession.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);

        editor.openDock(session);

        verify(decisionNavigatorDock).open();
        verify(decisionNavigatorDock).setupContent(canvasHandler);
    }

    @Test
    public void testDestroyDock() {

        editor.destroyDock();

        verify(decisionNavigatorDock).close();
        verify(decisionNavigatorDock).resetContent();
    }
}

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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A generic session's presenter instance for authoring purposes.
 * <p>
 * It provides support for an editor Toolbar and a BS3 Palette widget.
 * <p>
 * It aggregates a custom session viewer type which provides binds the editors's diagram instance and the
 * different editors' controls with the diagram and controls for the given session.
 *
 * @see <a>org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorImpl</a>
 */
@Dependent
public class SessionEditorPresenter<S extends EditorSession>
        extends AbstractSessionPresenter<Diagram, AbstractCanvasHandler, S, SessionDiagramEditor<S>>
        implements SessionDiagramPresenter<S> {

    private final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent;
    private final SessionEditorImpl<S> editor;
    private final SessionCardinalityStateHandler cardinalityStateHandler;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionEditorPresenter(final SessionManager sessionManager,
                                  final SessionEditorImpl<S> editor,
                                  final SessionCardinalityStateHandler cardinalityStateHandler,
                                  final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                                  final NotificationsObserver notificationsObserver,
                                  final Event<SessionFocusedEvent> sessionFocusedEvent,
                                  final Event<SessionLostFocusEvent> sessionLostFocusEvent,
                                  final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent,
                                  final View view) {
        super(sessionManager,
              view,
              notificationsObserver,
              sessionFocusedEvent,
              sessionLostFocusEvent,
              canvasLostFocusEventEvent);
        this.sessionDiagramOpenedEvent = sessionDiagramOpenedEvent;
        this.editor = editor;
        this.cardinalityStateHandler = cardinalityStateHandler;
    }

    @PostConstruct
    public void init() {
        editor.setDiagramSupplier(this::getDiagram);
    }

    @Override
    protected void onSessionOpened(final S session) {
        super.onSessionOpened(session);
        cardinalityStateHandler.bind(session);
        sessionDiagramOpenedEvent.fire(new SessionDiagramOpenedEvent(session));
    }


    void commandUndoExecutedFired(@Observes final CanvasCommandUndoneEvent event) {
        refreshOnEvent(event);
    }

    private void refreshOnEvent(final AbstractCanvasHandlerEvent event) {
        if (isSameContext(event)) {
            editor.getCanvasPanel().refresh();
        }
    }

    void onRequestSessionRefreshEvent(final @Observes RequestSessionRefreshEvent event) {
        getSession().ifPresent(session -> {
            if (session.getSessionUUID().equals(event.getSessionUUID())) {
                refresh();
            }
        });
    }

    @Override
    public void refresh() {
        super.refresh();
        getSession().ifPresent(SessionEditorPresenter::clearSelection);
    }

    @Override
    public SessionDiagramEditor<S> getDisplayer() {
        return editor;
    }

    @Override
    protected Class<? extends AbstractSession> getSessionType() {
        return EditorSession.class;
    }

    private boolean isSameContext(final AbstractCanvasHandlerEvent event) {
        final AbstractCanvasHandler sessionHandlerContext = (AbstractCanvasHandler) event.getCanvasHandler();
        return null != getHandler() &&
                getHandler().equals(sessionHandlerContext);
    }

    private static void clearSelection(final EditorSession session) {
        if (null != session.getSelectionControl()) {
            session.getSelectionControl().clearSelection();
        }
    }
}

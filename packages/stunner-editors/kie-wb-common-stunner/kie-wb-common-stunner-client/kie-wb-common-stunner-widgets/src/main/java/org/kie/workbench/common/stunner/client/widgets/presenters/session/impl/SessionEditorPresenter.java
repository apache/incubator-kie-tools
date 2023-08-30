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

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

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
    private final ManagedInstance<EditorToolbar> toolbars;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionEditorPresenter(final DefinitionUtils definitionUtils,
                                  final SessionManager sessionManager,
                                  final SessionEditorImpl<S> editor,
                                  final SessionCardinalityStateHandler cardinalityStateHandler,
                                  final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                                  final @Any ManagedInstance<EditorToolbar> toolbars,
                                  final DefaultPaletteFactory<AbstractCanvasHandler> paletteWidgetFactory,
                                  final NotificationsObserver notificationsObserver,
                                  final Event<SessionFocusedEvent> sessionFocusedEvent,
                                  final Event<SessionLostFocusEvent> sessionLostFocusEvent,
                                  final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent,
                                  final View view) {
        super(definitionUtils,
              sessionManager,
              view,
              paletteWidgetFactory,
              notificationsObserver,
              sessionFocusedEvent,
              sessionLostFocusEvent,
              canvasLostFocusEventEvent);
        this.sessionDiagramOpenedEvent = sessionDiagramOpenedEvent;
        this.editor = editor;
        this.cardinalityStateHandler = cardinalityStateHandler;
        this.toolbars = toolbars;
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

    void onScreenMaximizedEvent(@Observes ScreenMaximizedEvent event) {
        getPalette().onScreenMaximized(event);
    }

    void onScreenMinimizedEvent(@Observes ScreenMinimizedEvent event) {
        getPalette().onScreenMinimized(event);
    }

    void commandExecutedFired(@Observes final CanvasCommandExecutedEvent event) {
        refreshOnEvent(event);
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
    @SuppressWarnings("unchecked")
    protected Toolbar<S> newToolbar(final Annotation qualifier) {
        return (Toolbar<S>) InstanceUtils.lookup(toolbars,
                                                 qualifier);
    }

    @Override
    protected void destroyToolbarInstace(final Toolbar<S> toolbar) {
        toolbars.destroy((EditorToolbar) toolbar);
        toolbars.destroyAll();
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

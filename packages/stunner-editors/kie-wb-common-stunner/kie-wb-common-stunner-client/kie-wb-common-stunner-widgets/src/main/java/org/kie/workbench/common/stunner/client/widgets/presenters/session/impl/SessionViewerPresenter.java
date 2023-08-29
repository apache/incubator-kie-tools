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
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbar;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * A generic session's presenter instance for read-only purposes.
 * <p>
 * It provides a viewer Toolbar instance type, but does not provide a Palette to ensure view only mode.
 * <p>
 * It aggregates a custom session viewer type which provides binds the editors's diagram instance and the
 * different editors' controls with the diagram and controls for the given session.
 *
 * @see <a>org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerImpl</a>
 */
@Dependent
public class SessionViewerPresenter<S extends ViewerSession>
        extends AbstractSessionPresenter<Diagram, AbstractCanvasHandler, S, SessionDiagramViewer<S>>
        implements SessionDiagramPresenter<S> {

    private final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent;
    private final SessionViewerImpl<S> viewer;
    private final ManagedInstance<ViewerToolbar> toolbars;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionViewerPresenter(final DefinitionUtils definitionUtils,
                                  final SessionManager sessionManager,
                                  final SessionViewerImpl<S> viewer,
                                  final @Any ManagedInstance<ViewerToolbar> toolbars,
                                  final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                                  final NotificationsObserver notificationsObserver,
                                  final Event<SessionFocusedEvent> sessionFocusedEvent,
                                  final Event<SessionLostFocusEvent> sessionLostFocusEvent,
                                  final Event<CanvasLostFocusEvent> canvasLostFocusEventEvent,
                                  final View view) {
        super(definitionUtils,
              sessionManager,
              view,
              null,
              notificationsObserver,
              sessionFocusedEvent,
              sessionLostFocusEvent,
              canvasLostFocusEventEvent);
        this.viewer = viewer;
        this.toolbars = toolbars;
        this.sessionDiagramOpenedEvent = sessionDiagramOpenedEvent;
    }

    @PostConstruct
    public void init() {
        viewer.setDiagramSupplier(this::getDiagram);
    }

    public SessionViewer<S, AbstractCanvasHandler, Diagram> getViewer() {
        return viewer;
    }

    @Override
    public SessionDiagramViewer<S> getDisplayer() {
        return viewer;
    }

    @Override
    protected Class<? extends AbstractSession> getSessionType() {
        return ViewerSession.class;
    }

    @Override
    protected void onSessionOpened(final S session) {
        super.onSessionOpened(session);
        sessionDiagramOpenedEvent.fire(new SessionDiagramOpenedEvent(session));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Toolbar<S> newToolbar(final Annotation qualifier) {
        return (Toolbar<S>) toolbars.get();
    }

    @Override
    protected void destroyToolbarInstace(final Toolbar<S> toolbar) {
        toolbars.destroy((ViewerToolbar) toolbar);
        toolbars.destroyAll();
    }
}

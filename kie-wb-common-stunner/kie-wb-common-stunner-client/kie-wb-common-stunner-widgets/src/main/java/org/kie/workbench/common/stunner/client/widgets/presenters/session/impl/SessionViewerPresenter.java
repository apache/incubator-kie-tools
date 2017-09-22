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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.util.Optional;

import javax.enterprise.event.Event;

import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A generic session's presenter instance for read-only purposes.
 * <p>
 * It provides a viewer Toolbar instance type, but does not provide a Palette to ensure view only mode.
 * <p>
 * It aggregates a custom session viewer type which provides binds the editors's diagram instance and the
 * different editors' controls with the diagram and controls for the given session.
 * @see <a>org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerImpl</a>
 */
public class SessionViewerPresenter<S extends AbstractClientReadOnlySession, H extends AbstractCanvasHandler>
        extends AbstractSessionPresenter<Diagram, H, S, SessionViewer<S, H, Diagram>>
        implements SessionDiagramPresenter<S, H> {

    private final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent;
    private final SessionDiagramViewer<S, H> viewer;

    @SuppressWarnings("unchecked")
    SessionViewerPresenter(final SessionManager sessionManager,
                           final CanvasCommandManager<H> commandManager,
                           final ViewerToolbarFactory toolbarFactory,
                           final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                           final WidgetWrapperView diagramViewerView,
                           final NotificationsObserver notificationsObserver,
                           final View view) {
        super(sessionManager,
              view,
              Optional.of((ToolbarFactory<S>) toolbarFactory),
              Optional.empty(),
              notificationsObserver);
        this.viewer = new CustomSessionViewer(commandManager,
                                              diagramViewerView);
        this.sessionDiagramOpenedEvent = sessionDiagramOpenedEvent;
    }

    public SessionViewer<S, H, Diagram> getViewer() {
        return viewer;
    }

    /**
     * A session viewer which diagram is the instance to be presented.
     * Consider the default session editor (SessionEditorImpl) works for already loaded sessions,
     * it expects the diagram instance to be loaded and available from the canvas handler.
     * This is not the same case for the session presenter, which just presents a diagram instance, either
     * loaded from backend or recently created, and it loads the given diagram into the given session.
     */
    private class CustomSessionViewer extends SessionViewerImpl<S, H> {

        private CustomSessionViewer(final CanvasCommandManager<H> canvasCommandManager,
                                    final WidgetWrapperView view) {
            super(canvasCommandManager,
                  view);
        }

        @Override
        protected Diagram getDiagram() {
            return SessionViewerPresenter.this.getDiagram();
        }
    }

    @Override
    public SessionViewer<S, H, Diagram> getDisplayer() {
        return viewer;
    }

    @Override
    protected void onSessionOpened(final S session) {
        super.onSessionOpened(session);
        sessionDiagramOpenedEvent.fire(new SessionDiagramOpenedEvent(session));
    }
}

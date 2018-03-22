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
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionEditor;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenResizeEventObserver;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * A generic session's presenter instance for authoring purposes.
 * <p>
 * It provides support for an editor Toolbar and a BS3 Palette widget.
 * <p>
 * It aggregates a custom session viewer type which provides binds the editors's diagram instance and the
 * different editors' controls with the diagram and controls for the given session.
 * @see <a>org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorImpl</a>
 */
public class SessionEditorPresenter<S extends AbstractClientFullSession, H extends AbstractCanvasHandler>
        extends AbstractSessionPresenter<Diagram, H, S, SessionEditor<S, H, Diagram>>
        implements SessionDiagramPresenter<S, H> {

    private final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent;
    private final SessionEditor<S, H, Diagram> editor;

    @SuppressWarnings("unchecked")
    SessionEditorPresenter(final SessionManager sessionManager,
                           final CanvasCommandManager<H> commandManager,
                           final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                           final EditorToolbarFactory toolbarFactory,
                           final DefaultPaletteFactory<H> paletteWidgetFactory,
                           final WidgetWrapperView diagramEditorView,
                           final NotificationsObserver notificationsObserver,
                           final View view,
                           final ScreenResizeEventObserver screenResizeEventObserver) {
        super(sessionManager,
              view,
              Optional.of((ToolbarFactory<S>) toolbarFactory),
              Optional.of(paletteWidgetFactory),
              notificationsObserver);
        this.sessionDiagramOpenedEvent = sessionDiagramOpenedEvent;
        this.editor = new CustomSessionEditor(commandManager,
                                              diagramEditorView);

        //Registering event observers
        screenResizeEventObserver.registerEventCallback(ScreenMaximizedEvent.class, event -> onScreenMaximized(event));
        screenResizeEventObserver.registerEventCallback(ScreenMinimizedEvent.class, event -> onScreenMinimized(event));
    }

    private void onScreenMaximized(ScreenMaximizedEvent event) {
        getPalette().onScreenMaximized(event);
    }

    private void onScreenMinimized(ScreenMinimizedEvent event) {
        getPalette().onScreenMinimized(event);
    }

    @Override
    protected void onSessionOpened(final S session) {
        super.onSessionOpened(session);
        sessionDiagramOpenedEvent.fire(new SessionDiagramOpenedEvent(session));
    }

    @Override
    public SessionEditor<S, H, Diagram> getDisplayer() {
        return editor;
    }

    /**
     * A session editor which diagram is the instance to be presented.
     * Consider the default session editor (SessionEditorImpl) works for already loaded sessions,
     * it expects the diagram instance to be loaded and available from the canvas handler.
     * This is not the same case for the session presenter, which just presents a diagram instance, either
     * loaded from backend or recently created, and it loads the given diagram into the given session.
     */
    private class CustomSessionEditor extends SessionEditorImpl<S, H> {

        private CustomSessionEditor(final CanvasCommandManager<H> canvasCommandManager,
                                    final WidgetWrapperView view) {
            super(canvasCommandManager,
                  view);
        }

        @Override
        protected Diagram getDiagram() {
            return SessionEditorPresenter.this.getDiagram();
        }
    }
}

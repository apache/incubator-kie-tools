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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenResizeEventObserver;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

@ApplicationScoped
public class SessionPresenterFactoryImpl implements SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> {

    private final SessionManager sessionManager;
    private final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManagerInstances;
    private final ManagedInstance<ViewerToolbarFactory> viewerToolbarFactoryInstances;
    private final ManagedInstance<EditorToolbarFactory> editorToolbarFactoryInstances;
    private final ManagedInstance<SessionDiagramPreview<AbstractClientSession>> sessionPreviewInstances;
    private final ManagedInstance<WidgetWrapperView> diagramViewerViewInstances;
    private final ManagedInstance<SessionPresenter.View> viewInstances;
    private final ManagedInstance<NotificationsObserver> notificationsObserverInstances;
    private final DefaultPaletteFactory<AbstractCanvasHandler> paletteWidgetFactory;
    private final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEventInstances;
    private final ScreenResizeEventObserver screenResizeEventObserver;

    protected SessionPresenterFactoryImpl() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public SessionPresenterFactoryImpl(final SessionManager sessionManager,
                                       final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> commandManagerInstances,
                                       final ManagedInstance<ViewerToolbarFactory> viewerToolbarFactoryInstances,
                                       final ManagedInstance<EditorToolbarFactory> editorToolbarFactoryInstances,
                                       final ManagedInstance<SessionDiagramPreview<AbstractClientSession>> sessionPreviewInstances,
                                       final ManagedInstance<WidgetWrapperView> diagramViewerViewInstances,
                                       final ManagedInstance<SessionPresenter.View> viewInstances,
                                       final ManagedInstance<NotificationsObserver> notificationsObserverInstances,
                                       final DefaultPaletteFactory<AbstractCanvasHandler> paletteWidgetFactory,
                                       final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEventInstances,
                                       final ScreenResizeEventObserver screenResizeEventObserver) {
        this.sessionManager = sessionManager;
        this.commandManagerInstances = commandManagerInstances;
        this.viewerToolbarFactoryInstances = viewerToolbarFactoryInstances;
        this.editorToolbarFactoryInstances = editorToolbarFactoryInstances;
        this.sessionPreviewInstances = sessionPreviewInstances;
        this.diagramViewerViewInstances = diagramViewerViewInstances;
        this.paletteWidgetFactory = paletteWidgetFactory;
        this.notificationsObserverInstances = notificationsObserverInstances;
        this.viewInstances = viewInstances;
        this.sessionDiagramOpenedEventInstances = sessionDiagramOpenedEventInstances;
        this.screenResizeEventObserver = screenResizeEventObserver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SessionPreview<AbstractClientSession, Diagram> newPreview() {
        return sessionPreviewInstances.get();
    }

    @Override
    public SessionViewer<AbstractClientReadOnlySession, ?, Diagram> newViewer() {
        return new SessionViewerImpl<>(commandManagerInstances.get(),
                                       diagramViewerViewInstances.get());
    }

    @Override
    public SessionEditor<AbstractClientFullSession, ?, Diagram> newEditor() {
        return new SessionEditorImpl<>(commandManagerInstances.get(),
                                       diagramViewerViewInstances.get());
    }

    @Override
    public SessionPresenter<AbstractClientReadOnlySession, ?, Diagram> newPresenterViewer() {
        return new SessionViewerPresenter<>(sessionManager,
                                            commandManagerInstances.get(),
                                            viewerToolbarFactoryInstances.get(),
                                            sessionDiagramOpenedEventInstances,
                                            diagramViewerViewInstances.get(),
                                            notificationsObserverInstances.get(),
                                            viewInstances.get());
    }

    @Override
    public SessionPresenter<AbstractClientFullSession, ?, Diagram> newPresenterEditor() {
        return new SessionEditorPresenter<>(sessionManager,
                                            commandManagerInstances.get(),
                                            sessionDiagramOpenedEventInstances,
                                            editorToolbarFactoryInstances.get(),
                                            paletteWidgetFactory,
                                            diagramViewerViewInstances.get(),
                                            notificationsObserverInstances.get(),
                                            viewInstances.get(),
                                            screenResizeEventObserver);
    }
}

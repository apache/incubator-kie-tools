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

package org.kie.workbench.common.stunner.cm.client.session.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbarFactory;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenResizeEventObserver;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;

@CaseManagementEditor
@ApplicationScoped
public class SessionPresenterFactoryImpl extends org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterFactoryImpl {

    // CDI proxy.
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
                                       final @CaseManagementEditor ManagedInstance<EditorToolbarFactory> editorToolbarFactoryInstances,
                                       final ManagedInstance<SessionDiagramPreview<AbstractClientSession>> sessionPreviewInstances,
                                       final ManagedInstance<WidgetWrapperView> diagramViewerViewInstances,
                                       final ManagedInstance<SessionPresenter.View> viewInstances,
                                       final ManagedInstance<NotificationsObserver> notificationsObserverInstances,
                                       final BS3PaletteFactory paletteWidgetFactory,
                                       final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEventInstances,
                                       final ScreenResizeEventObserver screenResizeEventObserver) {
        super(sessionManager,
              commandManagerInstances,
              viewerToolbarFactoryInstances,
              editorToolbarFactoryInstances,
              sessionPreviewInstances,
              diagramViewerViewInstances,
              viewInstances,
              notificationsObserverInstances,
              paletteWidgetFactory,
              sessionDiagramOpenedEventInstances,
              screenResizeEventObserver
        );
    }
}
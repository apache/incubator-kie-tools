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
package org.kie.workbench.common.stunner.project.client.screens;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * The screen for the project context (includes the kie workbenches) which is included in a docked area
 * and displays a preview and and a diagram element's explorer (using a tree visual hierarchy) for the one being edited.
 * TODO: I18n.
 */
@Dependent
@WorkbenchScreen(identifier = ProjectDiagramExplorerScreen.SCREEN_ID)
public class ProjectDiagramExplorerScreen {

    public static final String SCREEN_ID = "ProjectDiagramExplorerScreen";
    public static final String TITLE = "Explore";
    public static final int PREVIEW_WIDTH = 350;
    public static final int PREVIEW_HEIGHT = 175;
    private static Logger LOGGER = Logger.getLogger(ProjectDiagramExplorerScreen.class.getName());
    private final SessionManager clientSessionManager;
    private final ManagedInstance<TreeExplorer> treeExplorers;
    private final ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final ErrorPopupPresenter errorPopupPresenter;
    private final View view;

    private PlaceRequest placeRequest;
    private String title = TITLE;
    private TreeExplorer explorerWidget;
    private SessionDiagramPreview<AbstractSession> previewWidget;

    protected ProjectDiagramExplorerScreen() {
        this(null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ProjectDiagramExplorerScreen(final SessionManager clientSessionManager,
                                        final @Any ManagedInstance<TreeExplorer> treeExplorers,
                                        final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                        final @Any @Default ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final View view) {
        this.clientSessionManager = clientSessionManager;
        this.treeExplorers = treeExplorers;
        this.changeTitleNotificationEvent = changeTitleNotificationEvent;
        this.sessionPreviews = sessionPreviews;
        this.errorPopupPresenter = errorPopupPresenter;
        this.view = view;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
    }

    @OnOpen
    public void onOpen() {
        final ClientSession current = clientSessionManager.getCurrentSession();
        if (null != current) {
            show(current);
        }
    }

    @OnClose
    public void onClose() {
        close();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "projectDiagramExplorerScreenContext";
    }

    public void show(final ClientSession session) {
        // Do not show sessions not already initialized with some diagram instance.
        if (null != session.getCanvasHandler().getDiagram()) {
            showPreview(session);
            showExplorer(session);
            updateTitle(session);
        }
    }

    public void close() {
        closeTreeExplorer();
        closePreview();
    }

    private void closeTreeExplorer() {
        view.clearExplorerWidget();
        if (null != explorerWidget) {
            treeExplorers.destroy(explorerWidget);
            explorerWidget = null;
        }
    }

    private void closePreview() {
        view.clearPreviewWidget();
        if (null != previewWidget) {
            previewWidget.destroy();
            sessionPreviews.destroy(previewWidget);
            previewWidget = null;
        }
    }

    @SuppressWarnings("unchecked")
    void onCanvasSessionOpened(@Observes SessionOpenedEvent sessionOpenedEvent) {
        checkNotNull("sessionOpenedEvent",
                     sessionOpenedEvent);
        show(sessionOpenedEvent.getSession());
    }

    void onCanvasSessionDestroyed(@Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent",
                     sessionDestroyedEvent);
        close();
    }

    void onSessionDiagramOpenedEvent(@Observes SessionDiagramOpenedEvent sessionDiagramOpenedEvent) {
        checkNotNull("sessionDiagramOpenedEvent",
                     sessionDiagramOpenedEvent);
        show(sessionDiagramOpenedEvent.getSession());
    }

    private void showExplorer(final ClientSession session) {
        if (null != explorerWidget) {
            closeTreeExplorer();
        }
        explorerWidget = treeExplorers.get();
        explorerWidget.show(session.getCanvasHandler());
        view.setExplorerWidget(explorerWidget);
    }

    private void showPreview(final ClientSession session) {
        if (null != session && session instanceof AbstractSession) {
            if (null != previewWidget) {
                closePreview();
            }
            previewWidget = sessionPreviews.get();
            previewWidget.open((AbstractSession) session,
                               PREVIEW_WIDTH,
                               PREVIEW_HEIGHT,
                               new SessionViewer.SessionViewerCallback<Diagram>() {
                                   @Override
                                   public void afterCanvasInitialized() {

                                   }

                                   @Override
                                   public void onSuccess() {
                                       view.setPreviewWidget(previewWidget.getView());
                                       updateTitle();
                                   }

                                   @Override
                                   public void onError(final ClientRuntimeError error) {
                                       showError(error);
                                   }
                               });
        }
    }

    private void updateTitle() {
        final ClientSession session = clientSessionManager.getCurrentSession();
        updateTitle(session);
    }

    private void updateTitle(final ClientSession session) {
        String title = TITLE;
        if (null != session.getCanvasHandler() && null != session.getCanvasHandler().getDiagram()) {
            final Diagram<?, ?> diagram = session.getCanvasHandler().getDiagram();
            title = diagram.getMetadata().getTitle();
        }
        doUpdateTitle(title);
    }

    private void doUpdateTitle(final String title) {
        // Change screen title.
        ProjectDiagramExplorerScreen.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }

    private void showError(final ClientRuntimeError error) {
        final String s = error.toString();
        errorPopupPresenter.showMessage(s);
        LOGGER.log(Level.SEVERE,
                   s);
    }

    public interface View extends IsWidget {

        View setPreviewWidget(final IsWidget widget);

        View clearPreviewWidget();

        View setExplorerWidget(final IsWidget widget);

        View clearExplorerWidget();

        View clear();
    }
}

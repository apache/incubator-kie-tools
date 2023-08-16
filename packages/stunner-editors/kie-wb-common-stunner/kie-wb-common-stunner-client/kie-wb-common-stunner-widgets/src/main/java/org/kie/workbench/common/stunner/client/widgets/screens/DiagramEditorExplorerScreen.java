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

package org.kie.workbench.common.stunner.client.widgets.screens;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.canvas.StunnerBoundsProviderFactory;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * The screen for the project context (includes the kie workbenches) which is included in a docked area
 * and displays a preview and and a diagram element's explorer (using a tree visual hierarchy) for the one being edited.
 * TODO: I18n.
 */
@Dependent
@Named(DiagramEditorExplorerScreen.SCREEN_ID)
public class DiagramEditorExplorerScreen extends AbstractActivity {

    private static final Logger LOGGER = Logger.getLogger(DiagramEditorExplorerScreen.class.getName());

    public static final String SCREEN_ID = "ProjectDiagramExplorerScreen";
    public static final String TITLE = "Explore";
    public static final int PREVIEW_WIDTH = 420;
    public static final int PREVIEW_HEIGHT = StunnerBoundsProviderFactory.computeHeight(PREVIEW_WIDTH);

    private final SessionManager clientSessionManager;
    private final ManagedInstance<TreeExplorer> treeExplorers;
    private final ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;
    private final Event<ScreenPreMaximizedStateEvent> screenStateEvent;
    private final View view;

    private PlaceRequest placeRequest;
    private TreeExplorer explorerWidget;
    private SessionDiagramPreview<AbstractSession> previewWidget;

    protected DiagramEditorExplorerScreen() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DiagramEditorExplorerScreen(final SessionManager clientSessionManager,
                                       final @Any ManagedInstance<TreeExplorer> treeExplorers,
                                       final @Any @Default ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews,
                                       final View view,
                                       final Event<ScreenPreMaximizedStateEvent> screenStateEvent) {
        this.clientSessionManager = clientSessionManager;
        this.treeExplorers = treeExplorers;
        this.sessionPreviews = sessionPreviews;
        this.view = view;
        this.screenStateEvent = screenStateEvent;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.DOCK;
    }

    @Override
    public String getIdentifier() {
        return SCREEN_ID;
    }

    @Override
    public void onStartup(final PlaceRequest placeRequest) {
        super.onStartup(placeRequest);

        this.placeRequest = placeRequest;
    }

    @Override
    public void onOpen() {
        super.onOpen();

        final ClientSession current = clientSessionManager.getCurrentSession();
        if (null != current) {
            show(current);
        }
    }

    @Override
    public void onClose() {
        super.onClose();

        close();
    }

    @Override
    public IsWidget getWidget() {
        return view;
    }

    public void show(final ClientSession session) {
        // Do not show sessions not already initialized with some diagram instance.
        if (null != session.getCanvasHandler().getDiagram()) {
            showPreview(session);
            showExplorer(session);
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
        checkNotNull("sessionOpenedEvent", sessionOpenedEvent);
        show(sessionOpenedEvent.getSession());
    }

    void onCanvasSessionDestroyed(@Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent", sessionDestroyedEvent);
        close();
    }

    void onSessionDiagramOpenedEvent(@Observes SessionDiagramOpenedEvent sessionDiagramOpenedEvent) {
        checkNotNull("sessionDiagramOpenedEvent", sessionDiagramOpenedEvent);
        show(sessionDiagramOpenedEvent.getSession());
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
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
        if (session instanceof AbstractSession) {
            if (null != previewWidget) {
                closePreview();
            }
            previewWidget = sessionPreviews.get();
            previewWidget.open((AbstractSession) session,
                               new SessionViewer.SessionViewerCallback<Diagram>() {
                                   @Override
                                   public void afterCanvasInitialized() {

                                   }

                                   @Override
                                   public void onSuccess() {
                                       previewWidget.scale(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                                       view.setPreviewWidget(previewWidget.getView());
                                   }

                                   @Override
                                   public void onError(final ClientRuntimeError error) {
                                       showError(error);
                                   }
                               });
        }
    }

    private void showError(final ClientRuntimeError error) {
        final String s = error.toString();
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

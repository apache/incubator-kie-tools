/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.docks.preview;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.client.widgets.canvas.StunnerBoundsProviderFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@WorkbenchScreen(identifier = PreviewDiagramScreen.SCREEN_ID)
public class PreviewDiagramScreen {

    private static Logger LOGGER = Logger.getLogger(PreviewDiagramScreen.class.getName());

    private static final int PREVIEW_WIDTH = 420;

    private static final int PREVIEW_HEIGHT = StunnerBoundsProviderFactory.computeHeight(PREVIEW_WIDTH);

    public static final String SCREEN_ID = "DMNProjectDiagramExplorerScreen";

    private final SessionManager clientSessionManager;
    private final ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;
    private final View view;

    private SessionDiagramPreview<AbstractSession> previewWidget;

    protected PreviewDiagramScreen() {
        this(null,
             null,
             null);
    }

    @Inject
    public PreviewDiagramScreen(final SessionManager clientSessionManager,
                                final @Any @DMNEditor ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews,
                                final View view) {
        this.clientSessionManager = clientSessionManager;
        this.sessionPreviews = sessionPreviews;
        this.view = view;
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest placeRequest) {
        //Nothing required for this dock screen.
    }

    @OnOpen
    @SuppressWarnings("unused")
    public void onOpen() {
        final ClientSession current = clientSessionManager.getCurrentSession();
        if (Objects.nonNull(current)) {
            showPreview(current);
        }
    }

    @OnClose
    @SuppressWarnings("unused")
    public void onClose() {
        closePreview();
    }

    @WorkbenchPartTitle
    @SuppressWarnings("unused")
    public String getTitle() {
        //The WorkbenchPanel used for Docks do not have a title.
        return "Unused";
    }

    @WorkbenchPartView
    @SuppressWarnings("unused")
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchContextId
    @SuppressWarnings("unused")
    public String getMyContextRef() {
        return "DMNProjectDiagramExplorerScreenContext";
    }

    void closePreview() {
        view.clearPreviewWidget();
        if (null != previewWidget) {
            previewWidget.destroy();
            sessionPreviews.destroy(previewWidget);
            previewWidget = null;
        }
    }

    void onCanvasSessionOpened(final @Observes SessionOpenedEvent sessionOpenedEvent) {
        checkNotNull("sessionOpenedEvent", sessionOpenedEvent);
        showPreview(sessionOpenedEvent.getSession());
    }

    void onCanvasSessionDestroyed(final @Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent", sessionDestroyedEvent);
        closePreview();
    }

    void onSessionDiagramOpenedEvent(final @Observes SessionDiagramOpenedEvent sessionDiagramOpenedEvent) {
        checkNotNull("sessionDiagramOpenedEvent", sessionDiagramOpenedEvent);
        showPreview(sessionDiagramOpenedEvent.getSession());
    }

    void showPreview(final ClientSession session) {
        if (Objects.isNull(session)) {
            return;
        }
        if (session instanceof AbstractSession) {
            if (Objects.nonNull(previewWidget)) {
                closePreview();
            }
            previewWidget = sessionPreviews.get();
            previewWidget.open((AbstractSession) session,
                               PREVIEW_WIDTH,
                               PREVIEW_HEIGHT,
                               new SessionViewer.SessionViewerCallback<Diagram>() {
                                   @Override
                                   public void afterCanvasInitialized() {
                                       //Nothing to do.
                                   }

                                   @Override
                                   public void onSuccess() {
                                       view.setPreviewWidget(previewWidget.getView());
                                   }

                                   @Override
                                   public void onError(final ClientRuntimeError error) {
                                       LOGGER.log(Level.SEVERE, error.getMessage());
                                   }
                               });
        }
    }

    public interface View extends IsWidget {

        void setPreviewWidget(final IsWidget widget);

        void clearPreviewWidget();
    }
}

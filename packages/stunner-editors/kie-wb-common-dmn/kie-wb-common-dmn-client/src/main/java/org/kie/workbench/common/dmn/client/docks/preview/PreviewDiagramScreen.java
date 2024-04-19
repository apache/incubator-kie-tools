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
package org.kie.workbench.common.dmn.client.docks.preview;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

@Dependent
@Named(PreviewDiagramScreen.SCREEN_ID)
public class PreviewDiagramScreen extends AbstractActivity {

    private static final Logger LOGGER = Logger.getLogger(PreviewDiagramScreen.class.getName());

    public static final String SCREEN_ID = "DMNProjectDiagramExplorerScreen";

    private final SessionManager clientSessionManager;
    private final ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;
    private final View view;
    private final DMNDiagramsSession session;

    private SessionDiagramPreview<AbstractSession> previewWidget;

    protected PreviewDiagramScreen() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public PreviewDiagramScreen(final SessionManager clientSessionManager,
                                final @Any @DMNEditor ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews,
                                final View view,
                                final DMNDiagramsSession session) {
        this.clientSessionManager = clientSessionManager;
        this.sessionPreviews = sessionPreviews;
        this.view = view;
        this.session = session;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.DOCK;
    }

    @Override
    public String getIdentifier() {
        return PreviewDiagramScreen.SCREEN_ID;
    }

    @Override
    public void onOpen() {
        super.onOpen();

        final ClientSession current = clientSessionManager.getCurrentSession();
        if (Objects.nonNull(current)) {
            showPreview(current);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        closePreview();
    }

    @Override
    public IsWidget getWidget() {
        return view;
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

        if (isSameDiagramSession(sessionOpenedEvent.getSession())) {
            showPreview(sessionOpenedEvent.getSession());
        }
    }

    void onCanvasSessionDestroyed(final @Observes SessionDestroyedEvent sessionDestroyedEvent) {
        checkNotNull("sessionDestroyedEvent", sessionDestroyedEvent);

        if (isSameDiagramSession(sessionDestroyedEvent.getMetadata())) {
            closePreview();
        }
    }

    void onSessionDiagramOpenedEvent(final @Observes SessionDiagramOpenedEvent sessionDiagramOpenedEvent) {
        checkNotNull("sessionDiagramOpenedEvent", sessionDiagramOpenedEvent);

        if (isSameDiagramSession(sessionDiagramOpenedEvent.getSession())) {
            showPreview(sessionDiagramOpenedEvent.getSession());
        }
    }

    private boolean isSameDiagramSession(final ClientSession session) {
        final CanvasHandler canvasHandler = session.getCanvasHandler();
        final Diagram diagram = canvasHandler.getDiagram();
        return Optional
                .ofNullable(diagram)
                .map((Function<Diagram, Metadata>) Diagram::getMetadata)
                .map(this::isSameDiagramSession)
                .orElse(false);
    }

    private boolean isSameDiagramSession(final Metadata metadata) {
        return Objects.equals(session.getCurrentSessionKey(), session.getSessionKey(metadata));
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
                                       LOGGER.log(Level.SEVERE, error.getErrorMessage());
                                   }
                               });
        }
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public interface View extends IsWidget {

        void setPreviewWidget(final IsWidget widget);

        void clearPreviewWidget();
    }
}

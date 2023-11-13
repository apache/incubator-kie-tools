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

package org.kie.workbench.common.stunner.forms.client.screens;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * The screen for the project context (includes the kie workbenches) which is included in
 * a docked area and displays the properties for the selected element on the canvas.
 * TODO: I18n.
 */
@Dependent
@Named(DiagramEditorPropertiesScreen.SCREEN_ID)
public class DiagramEditorPropertiesScreen extends AbstractActivity {

    private static Logger LOGGER = Logger.getLogger(DiagramEditorPropertiesScreen.class.getName());
    public static final String SCREEN_ID = "DiagramEditorPropertiesScreen";

    private final FormPropertiesWidget formPropertiesWidget;
    private final SessionManager clientSessionManager;
    private final DiagramEditorPropertiesScreenView view;
    private final Event<ScreenPreMaximizedStateEvent> screenStateEvent;

    private PlaceRequest placeRequest;
    private ClientSession session;
    private boolean open = false;

    protected DiagramEditorPropertiesScreen() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DiagramEditorPropertiesScreen(final FormPropertiesWidget formPropertiesWidget,
                                         final SessionManager clientSessionManager,
                                         final DiagramEditorPropertiesScreenView view,
                                         final Event<ScreenPreMaximizedStateEvent> screenStateEvent) {
        this.formPropertiesWidget = formPropertiesWidget;
        this.clientSessionManager = clientSessionManager;
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

    @PostConstruct
    public void init() {
        view.setWidget(ElementWrapperWidget.getWidget(formPropertiesWidget.getElement()));
    }

    @Override
    public void onStartup(final PlaceRequest placeRequest) {
        super.onStartup(placeRequest);

        this.placeRequest = placeRequest;
    }

    @Override
    public void onOpen() {
        super.onOpen();

        log(Level.FINE,
            "Opening DiagramEditorPropertiesScreen.");
        open = true;
        final ClientSession current = clientSessionManager.getCurrentSession();
        handleSession(current);
    }

    @Override
    public void onClose() {
        super.onClose();

        log(Level.FINE,
            "Closing DiagramEditorPropertiesScreen.");
        open = false;
        destroy();
    }

    @SuppressWarnings("unchecked")
    private void handleSession(final ClientSession session) {
        boolean done = false;
        view.showLoading();
        if (null != session) {
            this.session = session;
            // Show the loading view.
            view.showLoading();
            // Open the forms properties widget for the current session.

            formPropertiesWidget
                    .bind(session)
                    .show(view::hideLoading);

            done = true;
        }
        if (!done) {
            formPropertiesWidget.unbind();
            view.hideLoading();
            this.session = null;
        }
    }

    private void destroy() {
        formPropertiesWidget.destroy();
        session = null;
    }

    @Override
    public IsWidget getWidget() {
        return ElementWrapperWidget.getWidget(formPropertiesWidget.getElement());
    }

    void onSessionOpened(final @Observes SessionDiagramOpenedEvent event) {
        if (open && !event.getSession().equals(session)) {
            log(Level.FINE,
                "DiagramEditorPropertiesScreen -> Current Session Changed.");
            handleSession(clientSessionManager.getCurrentSession());
        }
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}

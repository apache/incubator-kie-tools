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
package org.kie.workbench.common.stunner.kogito.client.screens;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenView;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

/**
 * The screen for the project context (includes the kie workbenches) which is included in
 * a docked area and displays the properties for the selected element on the canvas.
 * TODO: I18n.
 */
@Dependent
@WorkbenchScreen(identifier = DiagramEditorPropertiesScreen.SCREEN_ID)
public class DiagramEditorPropertiesScreen {

    private static Logger LOGGER = Logger.getLogger(DiagramEditorPropertiesScreen.class.getName());
    public static final String SCREEN_ID = "DiagramEditorPropertiesScreen";

    private final FormPropertiesWidget formPropertiesWidget;
    private final SessionManager clientSessionManager;
    private final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    private final DiagramEditorScreenView view;
    private final Event<ScreenPreMaximizedStateEvent> screenStateEvent;

    private PlaceRequest placeRequest;
    private ClientSession session;
    private String title = "Properties";
    private boolean open = false;

    protected DiagramEditorPropertiesScreen() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DiagramEditorPropertiesScreen(final FormPropertiesWidget formPropertiesWidget,
                                         final SessionManager clientSessionManager,
                                         final Event<ChangeTitleWidgetEvent> changeTitleNotification,
                                         final DiagramEditorScreenView view,
                                         final Event<ScreenPreMaximizedStateEvent> screenStateEvent) {
        this.formPropertiesWidget = formPropertiesWidget;
        this.clientSessionManager = clientSessionManager;
        this.changeTitleNotificationEvent = changeTitleNotification;
        this.view = view;
        this.screenStateEvent = screenStateEvent;
    }

    @PostConstruct
    public void init() {
        view.setWidget(ElementWrapperWidget.getWidget(formPropertiesWidget.getElement()));
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
    }

    @OnOpen
    public void onOpen() {
        log(Level.FINE,
            "Opening DiagramEditorPropertiesScreen.");
        open = true;
        final ClientSession current = clientSessionManager.getCurrentSession();
        handleSession(current);
    }

    @OnClose
    public void onClose() {
        log(Level.FINE,
            "Closing DiagramEditorPropertiesScreen.");
        open = false;
        destroy();
    }

    protected void onPlaceMaximizedEvent(@Observes PlaceMaximizedEvent event) {
        screenStateEvent.fire(new ScreenPreMaximizedStateEvent(false));
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

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        // TODO: return view.asWidget() - See DiagramEditorScreenViewImpl TODO;
        return ElementWrapperWidget.getWidget(formPropertiesWidget.getElement());
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "projectDiagramPropertiesScreenContext";
    }

    void onFormPropertiesOpened(final @Observes FormPropertiesOpened propertiesOpened) {
        if (null != session && session.equals(propertiesOpened.getSession())) {
            updateTitle(propertiesOpened.getName());
        }
    }

    void onSessionOpened(final @Observes SessionDiagramOpenedEvent event) {
        if (open && !event.getSession().equals(session)) {
            log(Level.FINE,
                "DiagramEditorPropertiesScreen -> Current Session Changed.");
            handleSession(clientSessionManager.getCurrentSession());
        }
    }

    private void updateTitle(final String title) {
        // Change screen title.
        DiagramEditorPropertiesScreen.this.title = title;
        changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                     this.title));
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}

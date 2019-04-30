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
package org.kie.workbench.common.stunner.standalone.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

/**
 * This screen wraps the FormsProperties widget.
 * It is session aware - It shows the properties view for the current session's selection element.
 */
@Dependent
@WorkbenchScreen(identifier = SessionPropertiesScreen.SCREEN_ID)
public class SessionPropertiesScreen extends AbstractSessionScreen {

    public static final String SCREEN_ID = "SessionPropertiesScreen";

    @Inject
    private ManagedInstance<FormPropertiesWidget> formPropertiesWidgets;

    @Inject
    private SessionScreenView view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    private PlaceRequest placeRequest;
    private String title = "Properties";
    private FormPropertiesWidget widget;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    public void onClose() {
        close();
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
        return "sessionPropertiesScreenContext";
    }

    @Override
    protected void doOpenDiagram() {
        widget = formPropertiesWidgets.get();
        view.showScreenView(ElementWrapperWidget.getWidget(widget.getElement()));
        widget.bind(getSession())
                .show();
    }

    @Override
    protected void doCloseSession() {
        view.clear();
        if (null != widget) {
            widget.destroy();
            widget = null;
        }
        formPropertiesWidgets.destroyAll();
    }

    @Override
    protected void doUpdateTitle(String title) {
        updateTitle(title);
    }

    void onFormPropertiesOpened(final @Observes FormPropertiesOpened propertiesOpened) {
        if (null != getSession() && getSession().equals(propertiesOpened.getSession())) {
            updateTitle(propertiesOpened.getName());
        }
    }

    private void updateTitle(final String title) {
        // Change screen title.
        SessionPropertiesScreen.this.title = title;
        if (null != title && title.trim().length() > 0) {
            changeTitleNotificationEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                                         title));
        }
    }
}

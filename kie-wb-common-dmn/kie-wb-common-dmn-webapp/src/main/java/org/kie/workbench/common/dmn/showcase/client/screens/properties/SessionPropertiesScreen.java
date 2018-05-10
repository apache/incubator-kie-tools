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
package org.kie.workbench.common.dmn.showcase.client.screens.properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.showcase.client.screens.BaseSessionScreen;
import org.kie.workbench.common.dmn.showcase.client.screens.SessionScreenView;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = SessionPropertiesScreen.SCREEN_ID)
public class SessionPropertiesScreen extends BaseSessionScreen {

    public static final String SCREEN_ID = "SessionPropertiesScreen";
    public static final String TITLE = "Properties";

    private SessionScreenView view;
    private ManagedInstance<FormPropertiesWidget> formPropertiesWidgets;
    private FormPropertiesWidget widget;

    public SessionPropertiesScreen() {
        //CDI proxy
    }

    @Inject
    public SessionPropertiesScreen(final SessionScreenView view,
                                   final ManagedInstance<FormPropertiesWidget> formPropertiesWidgets) {
        this.view = view;
        this.formPropertiesWidgets = formPropertiesWidgets;
    }

    @PostConstruct
    public void init() {
        view.showEmptySession();
    }

    @OnStartup
    @SuppressWarnings("unused")
    public void onStartup(final PlaceRequest placeRequest) {
        //Nothing to do, move on...
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return TITLE;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @Override
    protected void doOpenSession() {
    }

    @Override
    protected void doOpenDiagram() {
        widget = formPropertiesWidgets.get();
        view.showScreenView(ElementWrapperWidget.getWidget(widget.getElement()));
        widget.bind(getSession()).show();
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
}

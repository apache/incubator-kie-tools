/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Window;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.ext.security.management.client.widgets.management.editor.role.workflow.RoleEditorWorkflow;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = RoleEditorScreen.SCREEN_ID)
public class RoleEditorScreen {

    public static final String SCREEN_ID = "RoleEditorScreen";
    public static final String ROLE_NAME = "roleName";

    @Inject
    PlaceManager placeManager;

    @Inject
    Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    ErrorPopupPresenter errorPopupPresenter;

    @Inject
    BaseScreen baseScreen;

    @Inject
    ClientUserSystemManager clientUserSystemManager;

    @Inject
    RoleEditorWorkflow roleEditorWorkflow;

    String roleName;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        roleName = placeRequest.getParameter(ROLE_NAME,
                                             null);
        show();
    }

    @OnMayClose
    public boolean onMayClose() {
        return !roleEditorWorkflow.isDirty() ||
                Window.confirm(UsersManagementWidgetsConstants.INSTANCE.roleIsDirty());
    }

    @OnClose
    public void onClose() {
        roleEditorWorkflow.clear();
        this.roleName = null;
    }

    void show() {
        baseScreen.init(roleEditorWorkflow);
        roleEditorWorkflow.show(roleName);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return UsersManagementWorkbenchConstants.INSTANCE.showRole(roleName);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return baseScreen;
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "roleEditorContext";
    }
}

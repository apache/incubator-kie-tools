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

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.workflow.GroupCreationWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.workflow.GroupViewerWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteGroupEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = GroupEditorScreen.SCREEN_ID )
public class GroupEditorScreen {

    public static final String SCREEN_ID = "GroupEditorScreen";
    public static final String GROUP_NAME = "groupName";
    public static final String ADD_GROUP = "addGroup";

    @Inject
    PlaceManager placeManager;
    
    @Inject
    ErrorPopupPresenter errorPopupPresenter;

    @Inject
    BaseScreen baseScreen;
    
    @Inject
    GroupViewerWorkflow groupViewerWorkflow;

    @Inject
    GroupCreationWorkflow groupCreationWorkflow;
    
    @Inject
    ClientUserSystemManager clientUserSystemManager;

    private String title = UsersManagementWorkbenchConstants.INSTANCE.groupEditor();
    private PlaceRequest placeRequest;
    String groupName;
    
    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        final String addGroup = placeRequest.getParameter(ADD_GROUP, "false");
        final String name = placeRequest.getParameter(GROUP_NAME, null);
        if (Boolean.valueOf(addGroup)) {
            create();
        } else {
            show(name);
        }
    }
    
    @OnOpen
    public void onOpen() {

    }

    @OnClose
    public void onClose() {
        groupViewerWorkflow.clear();
        groupCreationWorkflow.clear();
        this.groupName = null;
    }

    void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }

    void show(final String name) {
        this.groupName = name;
        title = new SafeHtmlBuilder().appendEscaped(UsersManagementWorkbenchConstants.INSTANCE.showGroup())
                .appendEscaped(" ").appendEscaped(name).toSafeHtml().asString();
        baseScreen.init(groupViewerWorkflow);
        groupViewerWorkflow.show(name);
    }

    void create() {
        this.groupName = null;
        title = UsersManagementWorkbenchConstants.INSTANCE.createNewGroup();
        baseScreen.init(groupCreationWorkflow);
        groupCreationWorkflow.create();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return baseScreen;
    }
    
    @WorkbenchContextId
    public String getMyContextRef() {
        return "groupEditorContext";
    }

    void onGroupDeleted(@Observes final DeleteGroupEvent deleteGroupEvent) {
        final String deletedGroup = deleteGroupEvent.getName();
        if (groupName != null && groupName.equals(deletedGroup)) {
            closeEditor();
        }
    }

    private void closeEditor() {
        placeManager.closePlace(placeRequest);
    }
}

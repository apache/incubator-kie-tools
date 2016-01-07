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

package org.uberfire.ext.security.management.client.screens.home;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
@WorkbenchScreen(identifier = GroupsManagementHomeScreen.SCREEN_ID )
public class GroupsManagementHomeScreen {

    public static final String SCREEN_ID = "GroupsManagementHomeScreen";
    
    @Inject
    ClientUserSystemManager clientUserSystemManager;

    @Inject
    BaseScreen baseScreen;
    
    @Inject
    EntitiesManagementHome view;
    
    @PostConstruct
    public void init() {
        baseScreen.init(view);

    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest) {
        show();
    }

    @OnOpen
    public void onOpen() {
        
    }

    @OnClose
    public void onClose() {
        
    }

    public void show() {
        final List<String> homeEnabledItems = new ArrayList<String>(4);
        if (clientUserSystemManager.isGroupCapabilityEnabled(Capability.CAN_ADD_GROUP)) {
            homeEnabledItems.add(UsersManagementWorkbenchConstants.INSTANCE.home_createGroup());
        }
        homeEnabledItems.add(UsersManagementWorkbenchConstants.INSTANCE.home_listSearchGroups());
        if (clientUserSystemManager.isGroupCapabilityEnabled(Capability.CAN_READ_GROUP)) {
            homeEnabledItems.add(UsersManagementWorkbenchConstants.INSTANCE.home_clickOnGroupInListToRead());
        }
        if (clientUserSystemManager.isGroupCapabilityEnabled(Capability.CAN_DELETE_GROUP)) {
            homeEnabledItems.add(UsersManagementWorkbenchConstants.INSTANCE.home_deleteGroup());
        }
        view.show(UsersManagementWorkbenchConstants.INSTANCE.groupEditorWelcomeText(),homeEnabledItems);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return UsersManagementWorkbenchConstants.INSTANCE.groupsManagementHome();
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return baseScreen.asWidget();
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "groupsManagementHomeContext";
    }

}

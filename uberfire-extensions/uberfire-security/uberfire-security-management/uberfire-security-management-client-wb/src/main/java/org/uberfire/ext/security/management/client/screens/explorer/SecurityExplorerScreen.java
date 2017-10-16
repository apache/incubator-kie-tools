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

package org.uberfire.ext.security.management.client.screens.explorer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.client.ClientSecurityExceptionMessageResolver;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.editor.GroupEditorScreen;
import org.uberfire.ext.security.management.client.screens.editor.RoleEditorScreen;
import org.uberfire.ext.security.management.client.screens.editor.UserEditorScreen;
import org.uberfire.ext.security.management.client.widgets.management.events.NewGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.NewUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.GroupsExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.RolesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@WorkbenchScreen(identifier = SecurityExplorerScreen.SCREEN_ID)
public class SecurityExplorerScreen {

    public static final String SCREEN_ID = "SecurityExplorerScreen";

    public static final String ACTIVE_TAB = "activeTab";

    public static final String ROLES_TAB = "RolesTab";

    public static final String GROUPS_TAB = "GroupsTab";

    public static final String USERS_TAB = "UsersTab";

    private final View view;
    private final RolesExplorer rolesExplorer;
    private final GroupsExplorer groupsExplorer;
    private final UsersExplorer usersExplorer;
    private final ErrorPopupPresenter errorPopupPresenter;
    private final PlaceManager placeManager;
    private final ClientUserSystemManager userSystemManager;
    private final ClientSecurityExceptionMessageResolver exceptionMessageResolver;

    @Inject
    public SecurityExplorerScreen(final View view,
                                  final RolesExplorer rolesExplorer,
                                  final GroupsExplorer groupsExplorer,
                                  final UsersExplorer usersExplorer,
                                  final ErrorPopupPresenter errorPopupPresenter,
                                  final PlaceManager placeManager,
                                  final ClientUserSystemManager userSystemManager,
                                  final ClientSecurityExceptionMessageResolver exceptionMessageResolver) {
        this.view = view;
        this.rolesExplorer = rolesExplorer;
        this.groupsExplorer = groupsExplorer;
        this.usersExplorer = usersExplorer;
        this.errorPopupPresenter = errorPopupPresenter;
        this.placeManager = placeManager;
        this.userSystemManager = userSystemManager;
        this.exceptionMessageResolver = exceptionMessageResolver;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return UsersManagementWorkbenchConstants.INSTANCE.securityExplorer();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @PostConstruct
    public void init() {
        view.init(this,
                  rolesExplorer,
                  groupsExplorer,
                  usersExplorer);
        rolesExplorer.show();

        view.rolesEnabled(true);
        view.groupsEnabled(false);
        view.usersEnabled(false);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        final String activeTab = placeRequest.getParameter(ACTIVE_TAB,
                                                           ROLES_TAB);

        userSystemManager.waitForInitialization(() -> {
            if (userSystemManager.isActive()) {
                groupsExplorer.show();
                usersExplorer.show();
                view.groupsEnabled(true);
                view.usersEnabled(true);

                if (activeTab.equals(USERS_TAB)) {
                    view.rolesActive(false);
                    view.groupsActive(false);
                    view.usersActive(true);
                } else if (activeTab.equals(GROUPS_TAB)) {
                    view.rolesActive(false);
                    view.groupsActive(true);
                    view.usersActive(false);
                }
            }
        });
    }

    @OnClose
    public void onClose() {
        rolesExplorer.clear();
        groupsExplorer.clear();
        usersExplorer.clear();
    }

    void onRoleRead(@Observes final ReadRoleEvent readRoleEvent) {
        checkNotNull("event",
                     readRoleEvent);
        final String name = readRoleEvent.getName();
        final Map<String, String> params = new HashMap(1);
        params.put(RoleEditorScreen.ROLE_NAME,
                   name);
        placeManager.goTo(new DefaultPlaceRequest(RoleEditorScreen.SCREEN_ID,
                                                  params));
    }

    // Event processing

    void onGroupRead(@Observes final ReadGroupEvent readGroupEvent) {
        final String name = readGroupEvent.getName();
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(GroupEditorScreen.GROUP_NAME,
                   name);
        placeManager.goTo(new DefaultPlaceRequest(GroupEditorScreen.SCREEN_ID,
                                                  params));
    }

    void onUserRead(@Observes final ReadUserEvent readUserEvent) {
        checkNotNull("event",
                     readUserEvent);
        final String id = readUserEvent.getIdentifier();
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(UserEditorScreen.USER_ID,
                   id);
        placeManager.goTo(new DefaultPlaceRequest(UserEditorScreen.SCREEN_ID,
                                                  params));
    }

    void onGroupCreate(@Observes final NewGroupEvent newGroupEvent) {
        checkNotNull("event",
                     newGroupEvent);
        final Map<String, String> params = new HashMap(1);
        params.put(GroupEditorScreen.ADD_GROUP,
                   "true");
        placeManager.goTo(new DefaultPlaceRequest(GroupEditorScreen.SCREEN_ID,
                                                  params));
    }

    void onUserCreate(@Observes final NewUserEvent newUserEvent) {
        checkNotNull("event",
                     newUserEvent);
        final Map<String, String> params = new HashMap(1);
        params.put(UserEditorScreen.ADD_USER,
                   "true");
        placeManager.goTo(new DefaultPlaceRequest(UserEditorScreen.SCREEN_ID,
                                                  params));
    }

    void onErrorEvent(@Observes final OnErrorEvent event) {
        checkNotNull("event",
                     event);
        exceptionMessageResolver
                .consumeExceptionMessage(event.getException(),
                                         errorPopupPresenter::showMessage);
    }

    public interface View extends UberView<SecurityExplorerScreen> {

        void init(SecurityExplorerScreen presenter,
                  IsWidget rolesExplorer,
                  IsWidget groupsExplorer,
                  IsWidget usersExplorer);

        void rolesEnabled(boolean enabled);

        void groupsEnabled(boolean enabled);

        void usersEnabled(boolean enabled);

        void rolesActive(boolean active);

        void groupsActive(boolean active);

        void usersActive(boolean active);
    }
}

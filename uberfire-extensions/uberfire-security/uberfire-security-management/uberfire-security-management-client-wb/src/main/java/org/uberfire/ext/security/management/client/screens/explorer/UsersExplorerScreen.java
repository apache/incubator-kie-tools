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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.ext.security.management.client.screens.editor.UserEditorScreen;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@WorkbenchScreen(identifier = UsersExplorerScreen.SCREEN_ID )
public class UsersExplorerScreen {

    public static final String SCREEN_ID = "UsersExplorerScreen";

    @Inject
    BaseScreen baseScreen;
    
    @Inject
    ErrorPopupPresenter errorPopupPresenter;
    
    @Inject
    UsersExplorer usersExplorer;

    @Inject
    PlaceManager placeManager;
    
    @Inject
    ClientUserSystemManager clientUserSystemManager;

    Menus menu = null;
    
    @PostConstruct
    public void init() {
        this.menu = makeMenuBar();
        baseScreen.init(usersExplorer);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        show();
    }
    
    @OnOpen
    public void onOpen() {

    }

    @OnClose
    public void onClose() {
        usersExplorer.clear();
    }

    public void show() {
        final boolean canAddUser = clientUserSystemManager.isUserCapabilityEnabled(Capability.CAN_ADD_USER);
        menu.getItems().get(0).setEnabled(canAddUser);
        usersExplorer.show();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu(UsersManagementWorkbenchConstants.INSTANCE.createNewUser())
                .respondsWith(getNewCommand())
                .endMenu()
                .build();    
    }

    private Command getNewCommand() {
        return new Command() {
            public void execute() {
                newUser();
            }
        };
    }

    void newUser() {
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(UserEditorScreen.ADD_USER, "true");
        placeManager.goTo(new DefaultPlaceRequest(UserEditorScreen.SCREEN_ID, params));
    }
    
    void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }

    void onUserRead(@Observes final ReadUserEvent readUserEvent) {
        checkNotNull("event", readUserEvent);
        final String id = readUserEvent.getIdentifier();
        goToUserEditorScreen(id);
    }

    void onErrorEvent(@Observes final OnErrorEvent onErrorEvent) {
        checkNotNull("event", onErrorEvent);
        final Throwable cause = onErrorEvent.getCause();
        final String message = onErrorEvent.getMessage();
        final String m = message != null ? message : cause.getMessage();
        errorPopupPresenter.showMessage(m);
    }

    private void goToUserEditorScreen(String id) {
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put(UserEditorScreen.USER_ID, id);
        placeManager.goTo(new DefaultPlaceRequest(UserEditorScreen.SCREEN_ID, params));
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return UsersManagementWorkbenchConstants.INSTANCE.usersExplorer();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return baseScreen;
    }
    
    @WorkbenchContextId
    public String getMyContextRef() {
        return "usersExplorerContext";
    }
    
}

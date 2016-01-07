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
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWorkbenchConstants;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.workflow.UserCreationWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.workflow.UserEditorWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.events.ContextualEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnShowEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = UserEditorScreen.SCREEN_ID )
public class UserEditorScreen {

    public static final String SCREEN_ID = "UserEditorScreen";
    public static final String USER_ID = "userId";
    public static final String ADD_USER = "addUser";

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
    UserEditorWorkflow userEditorWorkflow;

    @Inject
    UserCreationWorkflow userCreationWorkflow;

    private String title = UsersManagementWorkbenchConstants.INSTANCE.userEditor();
    private PlaceRequest placeRequest;
    String userId;

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        final String addUser = placeRequest.getParameter(ADD_USER, "false");
        final String userId = placeRequest.getParameter(USER_ID, null);
        if (Boolean.valueOf(addUser)) {
            create();
        } else {
            show(userId);
        }
    }
    
    @OnOpen
    public void onOpen() {

    }

    @OnClose
    public void onClose() {
        userEditorWorkflow.clear();
        userCreationWorkflow.clear();
        this.userId = null;
    }

    void show(final String id) {
        baseScreen.init(userEditorWorkflow);
        userEditorWorkflow.show(id);
    }

    void create() {
        title = UsersManagementWorkbenchConstants.INSTANCE.createNewUser();
        baseScreen.init(userCreationWorkflow);
        userCreationWorkflow.create();
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
        return "userEditorContext";
    }

    void onEditUserEvent(@Observes final OnEditEvent onEditEvent) {
        if (checkEventContext(onEditEvent, userEditorWorkflow.getUserEditor())) {
            try {
                User user = (User) onEditEvent.getInstance();
                this.userId = user.getIdentifier();
                changeTitleNotification.fire(new ChangeTitleWidgetEvent(placeRequest,
                        new SafeHtmlBuilder().appendEscaped(UsersManagementWorkbenchConstants.INSTANCE.editUser())
                                .appendEscaped(" ").appendEscaped(user.getIdentifier()).toSafeHtml().asString()));
            } catch (ClassCastException e) { }
        }
    }

    void onShowUserEvent(@Observes final OnShowEvent onShowEvent) {
        if (checkEventContext(onShowEvent, userEditorWorkflow.getUserEditor())) {
            try {
                User user = (User) onShowEvent.getInstance();
                this.userId = user.getIdentifier();
                final String title = new SafeHtmlBuilder().appendEscaped(UsersManagementWorkbenchConstants.INSTANCE.showUser())
                        .appendEscaped(" ").appendEscaped(user.getIdentifier()).toSafeHtml().asString();
                changeTitleNotification.fire(new ChangeTitleWidgetEvent(placeRequest, title));
            } catch (ClassCastException e) { }
        }
    }

    void onUserDeleted(@Observes final DeleteUserEvent deleteUserEvent) {
        final String deletedId = deleteUserEvent.getIdentifier();
        if (userId != null && userId.equals(deletedId)) {
            closeEditor();
        }
    }

    private boolean checkEventContext(final ContextualEvent contextualEvent, final Object context) {
        return contextualEvent != null && contextualEvent.getContext() != null && contextualEvent.getContext().equals(context);
    }

    void closeEditor() {
        placeManager.closePlace(placeRequest);
    }

    void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }
    
}

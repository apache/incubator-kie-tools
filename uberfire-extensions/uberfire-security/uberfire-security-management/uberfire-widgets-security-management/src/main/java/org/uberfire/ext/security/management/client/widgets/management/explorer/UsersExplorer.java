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

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.NewUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;

/**
 * <p>Presenter class for users explorer widget.</p>
 */
@Dependent
public class UsersExplorer extends AbstractEntityExplorer<User> {

    Event<ReadUserEvent> readUserEvent;
    Event<NewUserEvent> newUserEvent;

    @Inject
    public UsersExplorer(final ClientUserSystemManager userSystemManager,
                         final Event<OnErrorEvent> errorEvent,
                         final LoadingBox loadingBox,
                         final EntitiesList<User> entitiesList,
                         final EntitiesExplorerView view,
                         final Event<ReadUserEvent> readUserEvent,
                         final Event<NewUserEvent> newUserEvent) {
        super(userSystemManager,
              errorEvent,
              loadingBox,
              entitiesList,
              view);
        this.readUserEvent = readUserEvent;
        this.newUserEvent = newUserEvent;
    }

    @Override
    protected String getEmptyText() {
        return UsersManagementWidgetsConstants.INSTANCE.noUsers();
    }

    @Override
    protected String getEntityType() {
        return UsersManagementWidgetsConstants.INSTANCE.users();
    }

    @Override
    protected String getTitle() {
        return UsersManagementWidgetsConstants.INSTANCE.user();
    }

    @Override
    protected String getEntityId(final User entity) {
        return entity.getIdentifier();
    }

    @Override
    protected String getEntityName(final User entity) {
        return entity.getIdentifier();
    }

    @Override
    protected boolean canSearch() {
        return userSystemManager.isUserCapabilityEnabled(Capability.CAN_SEARCH_USERS);
    }

    @Override
    protected boolean canCreate() {
        return userSystemManager.isUserCapabilityEnabled(Capability.CAN_ADD_USER);
    }

    @Override
    protected boolean canRead() {
        return userSystemManager.isUserCapabilityEnabled(Capability.CAN_READ_USER);
    }

    @Override
    protected void fireReadEvent(final String identifier) {
        readUserEvent.fire(new ReadUserEvent(identifier));
    }

    @Override
    protected void showCreate() {
        newUserEvent.fire(new NewUserEvent());
    }

    @Override
    protected void showSearch() {
        showLoadingView();

        // Call backend service.
        userSystemManager.users(new RemoteCallback<AbstractEntityManager.SearchResponse<User>>() {
                                    @Override
                                    public void callback(final AbstractEntityManager.SearchResponse<User> response) {

                                        // Configure the entities list view.
                                        final EntitiesList.Callback<User> callback = createCallback();
                                        entitiesList.show(response,
                                                          callback);

                                        // Show the explorer view.
                                        view.show(context,
                                                  viewCallback);

                                        hideLoadingView();
                                    }
                                },
                                errorCallback).search(new SearchRequestImpl(searchPattern,
                                                                            currentPage,
                                                                            PAGE_SIZE));
    }

    void onUserDeleted(@Observes final DeleteUserEvent deleteUserEvent) {
        attemptToShowSearch();
    }

    void onUserCreated(@Observes final CreateUserEvent createUserEvent) {
        attemptToShowSearch();
    }

    void onUserSaved(@Observes final SaveUserEvent saveUserEvent) {
        attemptToShowSearch();
    }

}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.*;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Presenter class for users explorer widget.</p>
 */
@Dependent
public class UsersExplorer implements IsWidget {

    public static final String SEARCH_PATTERN_ALL = "";
    private final static int PAGE_SIZE = 15;

    ClientUserSystemManager userSystemManager;
    Event<OnErrorEvent> errorEvent;
    LoadingBox loadingBox;
    EntitiesList<User> entitiesList;
    Event<ReadUserEvent> readUserEvent;
    public EntitiesExplorerView view;

    String searchPattern = SEARCH_PATTERN_ALL;
    int currentPage = 1;
    ExplorerViewContext context;
    Set<String> selectedUsers;

    @Inject
    public UsersExplorer(final ClientUserSystemManager userSystemManager, 
                         final Event<OnErrorEvent> errorEvent, 
                         final LoadingBox loadingBox, 
                         final EntitiesList<User> entitiesList, 
                         final Event<ReadUserEvent> readUserEvent, 
                         final EntitiesExplorerView view) {
        
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.loadingBox = loadingBox;
        this.entitiesList = entitiesList;
        this.readUserEvent = readUserEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        entitiesList.setPageSize(PAGE_SIZE);
        entitiesList.setEmptyEntitiesText(UsersManagementWidgetsConstants.INSTANCE.noUsers());
        view.configure(UsersManagementWidgetsConstants.INSTANCE.user(), entitiesList.view);
    }

    public void show() {
        show(null);
    }
    
    public void show(final ExplorerViewContext context) {
        // Configure the view context.
        this.context = context;
        if (this.context == null) this.context = new ExplorerViewContext();
        this.context.setParent(createParentContext());
        this.selectedUsers = this.context.getSelectedEntities();

        final boolean canSearch = userSystemManager.isUserCapabilityEnabled(Capability.CAN_SEARCH_USERS);
        if (canSearch) showSearch();
        else view.showMessage(LabelType.WARNING, UsersManagementWidgetsConstants.INSTANCE.doesNotHavePrivileges());
        
    }

    public Set<String> getSelectedUsers() {
        return nullSafe(selectedUsers);
    }
    
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    private Set<String> nullSafe(final Set<String> set) {
        return set != null ? Collections.unmodifiableSet(set) : null;
    }

    private void showSearch() {
        showLoadingView();

        // Call backend service.
        userSystemManager.users(new RemoteCallback<AbstractEntityManager.SearchResponse<User>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<User> response) {
                
                // Configure the entities list view.
                entitiesList.show(response, new EntitiesList.Callback<User>() {

                    @Override
                    public String getEntityType() {
                        return UsersManagementWidgetsConstants.INSTANCE.users();
                    }
                    
                    @Override
                    public boolean canRead() {
                        return UsersExplorer.this.context.canRead();
                    }

                    @Override
                    public boolean canRemove() {
                        return UsersExplorer.this.context.canDelete();
                    }

                    @Override
                    public boolean canSelect() {
                        return UsersExplorer.this.context.canSelect();
                    }

                    @Override
                    public boolean isSelected(final String id) {
                        return selectedUsers != null && UsersExplorer.this.selectedUsers.contains(id);
                    }

                    @Override
                    public String getIdentifier(final User entity) {
                        return entity.getIdentifier();
                    }

                    @Override
                    public String getTitle(final User entity) {
                        return entity.getIdentifier();
                    }

                    @Override
                    public void onReadEntity(final String identifier) {
                        UsersExplorer.this.readUserEvent.fire(new ReadUserEvent(identifier));
                    }

                    @Override
                    public void onRemoveEntity(final String identifier) {
                        // Not available from explorer widget.
                    }

                    @Override
                    public void onSelectEntity(String identifier, boolean isSelected) {
                        if (isSelected) {
                            if (selectedUsers == null) selectedUsers = new HashSet<String>(1);
                            UsersExplorer.this.selectedUsers.add(identifier);
                        } else if (selectedUsers != null) {
                            UsersExplorer.this.selectedUsers.remove(identifier);
                        }
                    }

                    @Override
                    public void onChangePage(final int currentPage, final int goToPage) {
                        UsersExplorer.this.currentPage = goToPage;
                        UsersExplorer.this.showSearch();
                    }
                });
                
                // Show the explorer view.
                view.show(context, viewCallback);

                hideLoadingView();

            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(final Message message, final Throwable throwable) {
                
                if (throwable != null) showError("[ERROR] Users Mngt Service - Throwable: " + throwable.getMessage());
                else showError("[ERROR] Users Mngt Service - Message: " + message.getSubject());
                return false;

            }
        }).search(new SearchRequestImpl(searchPattern, currentPage, PAGE_SIZE));
    }
    
    private EntitiesExplorerView.ViewContext createParentContext() {
        return new EntitiesExplorerView.ViewContext() {

            final boolean canSearch = userSystemManager.isUserCapabilityEnabled(Capability.CAN_SEARCH_USERS);
            final boolean canAdd = userSystemManager.isUserCapabilityEnabled(Capability.CAN_ADD_USER);
            // final boolean canDelete = userSystemManager.isUserCapabilityEnabled(Capability.CAN_DELETE_USER);
            final boolean canRead = userSystemManager.isUserCapabilityEnabled(Capability.CAN_READ_USER);
            final boolean canSelect = false;

            @Override
            public boolean canSearch() {
                return canSearch;
            }

            @Override
            public boolean canCreate() {
                return canAdd;
            }

            @Override
            public boolean canRead() {
                return canRead;
            }

            @Override
            public boolean canDelete() {
                // By default, the exlorer widget does not allow to delete.
                return false;
            }

            @Override
            public boolean canSelect() {
                return canSelect;
            }

            @Override
            public Set<String> getSelectedEntities() {
                return selectedUsers;
            }

        };
    }
        
    private EntitiesExplorerView.ViewCallback viewCallback = new EntitiesExplorerView.ViewCallback() {
        
        @Override
        public void onRefresh() {
            currentPage = 1;
            showSearch();
        }

        @Override
        public void onSearch(final String pattern) {
            UsersExplorer.this.searchPattern = pattern != null ? pattern : SEARCH_PATTERN_ALL;
            UsersExplorer.this.currentPage = 1;
            if (pattern == null || pattern.trim().length() == 0) view.clearSearch();
            showSearch();
        }
    };
    
    protected void showLoadingView() {
        loadingBox.show();
    }

    void hideLoadingView() {
        loadingBox.hide();
    }

    protected void showError(final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : UsersManagementWidgetsConstants.INSTANCE.genericError();
        showError(msg);
    }
    
    protected void showError(final String message) {
        hideLoadingView();
        errorEvent.fire(new OnErrorEvent(UsersExplorer.this, message));
    }

    public void clear() {
        context = null;
        searchPattern = SEARCH_PATTERN_ALL;
        currentPage = 1;
        selectedUsers = null;
        view.clear();
    }

    
    void onUserDeleted(@Observes final DeleteUserEvent deleteUserEvent) {
        showSearch();
    }

    void onUserCreated(@Observes final CreateUserEvent createUserEvent) {
        showSearch();
    }

    void onUserSaved(@Observes final SaveUserEvent saveUserEvent) {
        showSearch();
    }
    
}

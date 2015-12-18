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
import org.jboss.errai.security.shared.api.Group;
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
 * <p>Presenter class for groups explorer widget.</p>
 */
@Dependent
public class GroupsExplorer implements IsWidget {

    private final static int PAGE_SIZE = 15;
    private static final String SEARCH_PATTERN_ALL = "";
    
    ClientUserSystemManager userSystemManager;
    Event<OnErrorEvent> errorEvent;
    LoadingBox loadingBox;
    EntitiesList<Group> entitiesList;
    public EntitiesExplorerView view;
    private Event<ReadGroupEvent> readGroupEvent;

    int pageSize = PAGE_SIZE;
    String searchPattern = SEARCH_PATTERN_ALL;
    int currentPage = 1;
    ExplorerViewContext context;
    Set<String> selectedGroups;

    @Inject
    public GroupsExplorer(final ClientUserSystemManager userSystemManager, 
                          final Event<OnErrorEvent> errorEvent,
                          final LoadingBox loadingBox, 
                          final EntitiesList<Group> entitiesList, 
                          final EntitiesExplorerView view, 
                          final Event<ReadGroupEvent> readGroupEvent) {
        
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.loadingBox = loadingBox;
        this.entitiesList = entitiesList;
        this.view = view;
        this.readGroupEvent = readGroupEvent;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @PostConstruct
    public void init() {
        entitiesList.setPageSize(pageSize);
        entitiesList.setEmptyEntitiesText(UsersManagementWidgetsConstants.INSTANCE.noGroups());
        view.configure(UsersManagementWidgetsConstants.INSTANCE.group(), entitiesList.view);
    }
    
    public void show() {
        show(null);
    }


    public void show(final ExplorerViewContext context) {
        // Configure the view context.
        this.context = context;
        if (this.context == null) this.context = new ExplorerViewContext();
        this.context.setParent(createParentContext());
        this.selectedGroups = this.context.getSelectedEntities();

        final boolean canSearch = userSystemManager.isGroupCapabilityEnabled(Capability.CAN_SEARCH_GROUPS);
        if (canSearch) showSearch();
        else view.showMessage(LabelType.WARNING, UsersManagementWidgetsConstants.INSTANCE.doesNotHavePrivileges());
        
    }

    public Set<String> getSelectedGroups() {
        return nullSafe(selectedGroups);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    private final ErrorCallback<Message> errorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message, final Throwable throwable) {
            showError(throwable);
            return false;
        }
    };
    
    private void showSearch() {
        showLoadingView();

        // Call backend service.
        userSystemManager.groups(new RemoteCallback<AbstractEntityManager.SearchResponse<Group>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<Group> response) {
                if (response != null) {

                    entitiesList.show(response, new EntitiesList.Callback<Group>() {
                        
                        @Override
                        public String getEntityType() {
                            return UsersManagementWidgetsConstants.INSTANCE.groups();
                        }

                        @Override
                        public boolean canRead() {
                            return GroupsExplorer.this.context.canRead();
                        }

                        @Override
                        public boolean canRemove() {
                            return GroupsExplorer.this.context.canDelete();
                        }

                        @Override
                        public boolean canSelect() {
                            return GroupsExplorer.this.context.canSelect();
                        }

                        @Override
                        public boolean isSelected(final String id) {
                            return selectedGroups != null && GroupsExplorer.this.selectedGroups.contains(id);
                        }

                        @Override
                        public String getIdentifier(final Group entity) {
                            return entity.getName();
                        }

                        @Override
                        public String getTitle(final Group entity) {
                            return entity.getName();
                        }

                        @Override
                        public void onReadEntity(final String identifier) {
                            GroupsExplorer.this.readGroupEvent.fire(new ReadGroupEvent(identifier));
                        }

                        @Override
                        public void onRemoveEntity(final String identifier) {
                            // Not available from explorer widget.
                        }

                        @Override
                        public void onSelectEntity(String identifier, boolean isSelected) {
                            if (isSelected) {
                                if (selectedGroups == null) selectedGroups = new HashSet<String>(1);
                                GroupsExplorer.this.selectedGroups.add(identifier);
                            } else if (selectedGroups != null) {
                                GroupsExplorer.this.selectedGroups.remove(identifier);
                            }
                        }

                        @Override
                        public void onChangePage(final int currentPage, final int goToPage) {
                            GroupsExplorer.this.currentPage = goToPage;
                            GroupsExplorer.this.showSearch();
                        }
                    });
                    
                    // Show the explorer's view.
                    view.show(context, viewCallback);

                    hideLoadingView();
                }
            }
        }, errorCallback).search(new SearchRequestImpl(searchPattern, currentPage, pageSize));
    }
    
    
    private EntitiesExplorerView.ViewContext createParentContext() {
        final boolean canSearch = userSystemManager.isGroupCapabilityEnabled(Capability.CAN_SEARCH_GROUPS);
        final boolean canAdd = userSystemManager.isGroupCapabilityEnabled(Capability.CAN_ADD_GROUP);
        //final boolean canDelete = userSystemManager.isGroupCapabilityEnabled(Capability.CAN_DELETE_GROUP);
        final boolean canRead = userSystemManager.isGroupCapabilityEnabled(Capability.CAN_READ_GROUP);
        final boolean canSelect = false;
    
        return new EntitiesExplorerView.ViewContext() {
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
                return selectedGroups;
            }

        };
    }
    
    private EntitiesExplorerView.ViewCallback viewCallback = new EntitiesExplorerView.ViewCallback() {
        @Override
        public void onSearch(final String pattern) {
            GroupsExplorer.this.searchPattern = pattern != null ? pattern : SEARCH_PATTERN_ALL;
            GroupsExplorer.this.currentPage = 1;
            if (pattern == null || pattern.trim().length() == 0) view.clearSearch();
            showSearch();
        }

        @Override
        public void onRefresh()
        {
            currentPage = 1;
            showSearch();
        }

    };

    protected void showLoadingView() {
        loadingBox.show();
    }

    private Set<String> nullSafe(final Set<String> set) {
        return set != null ? Collections.unmodifiableSet(set) : null;
    }
    
    protected void hideLoadingView() {
        loadingBox.hide();
    }

    protected void showError(final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : UsersManagementWidgetsConstants.INSTANCE.genericError();
        showError(msg);
    }
    
    protected void showError(final String message) {
        hideLoadingView();
        errorEvent.fire(new OnErrorEvent(GroupsExplorer.this, message));
    }

    public void clear() {
        context = null;
        searchPattern = SEARCH_PATTERN_ALL;
        currentPage = 1;
        view.clear();
    }

    void onGroupDeleted(@Observes final DeleteGroupEvent deleteGroupEvent) {
        showSearch();
    }

    void onAddUsersToGroup(@Observes final AddUsersToGroupEvent addUsersToGroupEvent) {
        showSearch();
    }

    void onGroupCreated(@Observes final CreateGroupEvent createGroupEvent) {
        showSearch();
    }

}

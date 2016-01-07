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

import com.google.gwt.user.client.ui.Widget;
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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * <p>Presenter class for groups explorer widget.</p>
 * <p>By default, the <code>admin</code> identifier is constrained.</p>
 */
@Dependent
public class GroupsExplorer extends AbstractEntityExplorer<Group> {

   
    private Event<ReadGroupEvent> readGroupEvent;

    @Inject
    public GroupsExplorer(final ClientUserSystemManager userSystemManager, 
                          final Event<OnErrorEvent> errorEvent, 
                          final LoadingBox loadingBox, 
                          final EntitiesList<Group> entitiesList, 
                          final EntitiesExplorerView view,
                          final Event<ReadGroupEvent> readGroupEvent) {
        super(userSystemManager, errorEvent, loadingBox, entitiesList, view);
        this.readGroupEvent = readGroupEvent;
    }


    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    protected String getEmptyText() {
        return UsersManagementWidgetsConstants.INSTANCE.noGroups();
    }

    @Override
    protected String getEntityType() {
        return UsersManagementWidgetsConstants.INSTANCE.groups();
    }

    @Override
    protected String getTitle() {
        return UsersManagementWidgetsConstants.INSTANCE.group();
    }

    @Override
    protected String getEntityId(final Group entity) {
        return entity.getName();
    }

    @Override
    protected String getEntityName(final Group entity) {
        return entity.getName();
    }

    @Override
    protected boolean canSearch() {
        return userSystemManager.isGroupCapabilityEnabled(Capability.CAN_SEARCH_GROUPS);
    }

    @Override
    protected boolean canCreate() {
        return userSystemManager.isGroupCapabilityEnabled(Capability.CAN_ADD_GROUP);
    }

    @Override
    protected boolean canRead() {
        return userSystemManager.isGroupCapabilityEnabled(Capability.CAN_READ_GROUP);
    }

    @Override
    protected void fireReadEvent(final String identifier) {
        GroupsExplorer.this.readGroupEvent.fire(new ReadGroupEvent(identifier));
    }

    protected void showSearch() {
        showLoadingView();

        // Call backend service.
        userSystemManager.groups(new RemoteCallback<AbstractEntityManager.SearchResponse<Group>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<Group> response) {
                if (response != null) {

                    final EntitiesList.Callback<Group> callback = createCallback();

                    entitiesList.show(response, callback);

                    // Show the explorer's view.
                    view.show(context, viewCallback);

                    hideLoadingView();
                }
            }
        }, errorCallback).search(new SearchRequestImpl(searchPattern, currentPage, pageSize,
                context != null ? context.getConstrainedEntities() : null));
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

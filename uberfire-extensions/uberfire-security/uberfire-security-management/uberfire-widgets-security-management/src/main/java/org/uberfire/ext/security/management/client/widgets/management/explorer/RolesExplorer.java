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

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * <p>Presenter class for roles explorer widget.</p>
 * <p>By default, the <code>admin</code> identifier is constrained.</p>
 */
@Dependent
public class RolesExplorer extends AbstractEntityExplorer<Role> {

    @Inject
    public RolesExplorer(final ClientUserSystemManager userSystemManager, 
                         final Event<OnErrorEvent> errorEvent, 
                         final LoadingBox loadingBox, 
                         final EntitiesList<Role> entitiesList, 
                         final EntitiesExplorerView view) {
        super(userSystemManager, errorEvent, loadingBox, entitiesList, view);
    }

    @Override
    protected String getEmptyText() {
        return UsersManagementWidgetsConstants.INSTANCE.noRoles();
    }

    @Override
    protected String getEntityType() {
        return UsersManagementWidgetsConstants.INSTANCE.roles();
    }

    @Override
    protected String getTitle() {
        return UsersManagementWidgetsConstants.INSTANCE.role();
    }

    @Override
    protected String getEntityId(final Role entity) {
        return entity.getName();
    }

    @Override
    protected String getEntityName(final Role entity) {
        return entity.getName();
    }

    @Override
    protected boolean canSearch() {
        return true;
    }

    @Override
    protected boolean canCreate() {
        return false;
    }

    @Override
    protected boolean canRead() {
        return false;
    }

    protected void showSearch() {
        showLoadingView();

        // Call backend service.
        userSystemManager.roles(new RemoteCallback<AbstractEntityManager.SearchResponse<Role>>() {
            @Override
            public void callback(final AbstractEntityManager.SearchResponse<Role> response) {
                if (response != null) {

                    final EntitiesList.Callback<Role> callback = createCallback();

                    entitiesList.show(response, callback);

                    // Show the explorer's view.
                    view.show(context, viewCallback);

                    hideLoadingView();
                }
            }
        }, errorCallback).search(new SearchRequestImpl(searchPattern, currentPage, pageSize,
                context != null ? context.getConstrainedEntities() : null));
    }
}

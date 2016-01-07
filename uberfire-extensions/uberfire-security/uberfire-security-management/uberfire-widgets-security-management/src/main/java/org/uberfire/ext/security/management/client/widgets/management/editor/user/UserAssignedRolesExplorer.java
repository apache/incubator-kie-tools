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

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.events.OnRemoveUserRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.RolesList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned roles explorer widget.</p>
 * <p>It's considered an Editor due to it allows removing assigned user's roles.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class UserAssignedRolesExplorer extends UserAssignedEntitiesExplorer<Role> implements org.uberfire.ext.security.management.client.editor.user.UserAssignedRolesExplorer {

    Event<OnRemoveUserRoleEvent> removeUserRoleEvent;

    @Inject
    public UserAssignedRolesExplorer(final ClientUserSystemManager userSystemManager,
                                     final ConfirmBox confirmBox,
                                     final RolesList rolesList,
                                     final AssignedEntitiesExplorer view,
                                     final Event<OnRemoveUserRoleEvent> removeUserRoleEvent) {
        super(userSystemManager, confirmBox, rolesList, view);
        this.removeUserRoleEvent = removeUserRoleEvent;
    }


    @Override
    protected String getEmptyText() {
        return UsersManagementWidgetsConstants.INSTANCE.userHasNoRoles();
    }

    @Override
    protected String getEntityType() {
        return UsersManagementWidgetsConstants.INSTANCE.rolesAssigned();
    }

    @Override
    protected String getTitle() {
        return null;
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
    protected String getEnsureRemoveText() {
        return UsersManagementWidgetsConstants.INSTANCE.ensureRemoveRoleFromUser();
    }

    @Override
    protected boolean canAssignEntities() {
        final boolean canAssignRoles = userSystemManager.isUserCapabilityEnabled(Capability.CAN_ASSIGN_ROLES);
        return isEditMode && canAssignRoles;
    }

    @Override
    protected void open(final User user) {
        final Set<Role> uRoles = user.getRoles();
        for (final Role _role : uRoles) {
            this.entities.add(_role);
        }
        super.open(user);
    }

    @Override
    protected void doShow() {
        entitiesList.show(this.entities, getCallback());
    }

    @Override
    protected void removeEntity(String name) {
        // Remove the role from the local cache.
        Iterator<Role> roles = this.entities.iterator();
        while (roles.hasNext()) {
            final Role g = roles.next();
            if (g.getName().equals(name)) {
                roles.remove();
            }
        }

        // Reload view.
        doShow();

        // Fire the event for any others components listening.
        removeUserRoleEvent.fire(new OnRemoveUserRoleEvent(UserAssignedRolesExplorer.this, name));
    }

}
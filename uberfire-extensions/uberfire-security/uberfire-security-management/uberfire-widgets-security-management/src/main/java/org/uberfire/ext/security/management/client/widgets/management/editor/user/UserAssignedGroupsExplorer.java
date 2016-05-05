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

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.events.OnRemoveUserGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.GroupsList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned groups explorer widget.</p>
 * <p>It's considered an Editor due to it allows removing assigned user's groups.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class UserAssignedGroupsExplorer extends UserAssignedEntitiesExplorer<Group> implements IsWidget, org.uberfire.ext.security.management.client.editor.user.UserAssignedGroupsExplorer {

    Event<OnRemoveUserGroupEvent> removeUserGroupEventEvent;

    @Inject
    public UserAssignedGroupsExplorer(final ClientUserSystemManager userSystemManager, 
                                      final ConfirmBox confirmBox, 
                                      final GroupsList groupList, 
                                      final AssignedEntitiesExplorer view,
                                      final Event<OnRemoveUserGroupEvent> removeUserGroupEventEvent) {
        super(userSystemManager, confirmBox, groupList, view);
        this.removeUserGroupEventEvent = removeUserGroupEventEvent;
    }


    @Override
    protected String getEmptyText() {
        return UsersManagementWidgetsConstants.INSTANCE.userHasNoGroups();
    }

    @Override
    protected String getEntityType() {
        return UsersManagementWidgetsConstants.INSTANCE.groupsAssigned();
    }

    @Override
    protected String getTitle() {
        return null;
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
    protected String getEnsureRemoveText() {
        return UsersManagementWidgetsConstants.INSTANCE.ensureRemoveGroupFromUser();
    }

    @Override
    protected boolean canAssignEntities() {
        final boolean canAssignGroups = userSystemManager.isUserCapabilityEnabled(Capability.CAN_ASSIGN_GROUPS);
        return isEditMode && canAssignGroups;
    }

    @Override
    protected void open(final User user) {
        final Set<Group> userGroups = user.getGroups();
        for (final Group group : userGroups) {
            if (!userSystemManager.getConstrainedGroups().contains(group.getName())) {
                this.entities.add(group);
            }
        }
        super.open(user);
    }

    @Override
    protected void doShow() {
        entitiesList.show(this.entities, getCallback());
    }

    @Override
    protected void removeEntity(String name) {
        // Remove the group from the local cache.
        Iterator<Group> groups = this.entities.iterator();
        while (groups.hasNext()) {
            final Group g = groups.next();
            if (g.getName().equals(name)) {
                groups.remove();
            }
        }

        // Reload view.
        doShow();

        // Fire the event for any others components listening.
        removeUserGroupEventEvent.fire(new OnRemoveUserGroupEvent(UserAssignedGroupsExplorer.this, name));
    }

}
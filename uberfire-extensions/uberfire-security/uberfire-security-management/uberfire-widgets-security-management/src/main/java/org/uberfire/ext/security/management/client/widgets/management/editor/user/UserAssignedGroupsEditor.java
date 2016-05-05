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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesModalEditor;
import org.uberfire.ext.security.management.client.widgets.management.events.OnUpdateUserGroupsEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.GroupsExplorer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned groups editor.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class UserAssignedGroupsEditor extends UserAssignedEntitiesEditor<Group> implements org.uberfire.ext.security.management.client.editor.user.UserAssignedGroupsEditor {

    Event<OnUpdateUserGroupsEvent> updateUserGroupsEventEvent;

    @Inject
    public UserAssignedGroupsEditor(final ClientUserSystemManager userSystemManager, 
                                    final GroupsExplorer groupsExplorer, 
                                    final @AssignedEntitiesModalEditor AssignedEntitiesEditor<UserAssignedGroupsEditor> view,
                                    final Event<OnUpdateUserGroupsEvent> updateUserGroupsEventEvent) {
        super(userSystemManager, groupsExplorer, view);
        this.updateUserGroupsEventEvent = updateUserGroupsEventEvent;
    }


    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    protected String getCancelText() {
        return UsersManagementWidgetsConstants.INSTANCE.cancel();
    }

    @Override
    protected String getAddText() {
        return UsersManagementWidgetsConstants.INSTANCE.addToSelectedGroups();
    }

    @Override
    protected String getTitle() {
        return UsersManagementWidgetsConstants.INSTANCE.groupSelectionFor();
    }

    @Override
    protected String getEntityIdentifier(final Group entity) {
        return entity.getName();
    }

    protected void open(final User user) {
        assert user != null;
        this.entities.addAll(user.getGroups());
        entitiesExplorer.show(getViewContext());
        super.open(user);
    }

    @Override
    protected void onSave(Set<String> selectedEntities) {
        super.onSave(selectedEntities);
        if (selectedEntities != null && !selectedEntities.isEmpty()) {
            for (final String name : selectedEntities) {
                entities.add(userSystemManager.createGroup(name));
            }
        }

        // Delegate the recently updated assigned groups for the user.
        updateUserGroupsEventEvent.fire(new OnUpdateUserGroupsEvent(UserAssignedGroupsEditor.this, selectedEntities));
    }
    
}

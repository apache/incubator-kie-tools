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
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesModalEditor;
import org.uberfire.ext.security.management.client.widgets.management.events.OnUpdateUserRolesEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.RolesExplorer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned roles editor.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class UserAssignedRolesEditor extends UserAssignedEntitiesEditor<Role> implements org.uberfire.ext.security.management.client.editor.user.UserAssignedRolesEditor {

    Event<OnUpdateUserRolesEvent> updateUserRolesEvent;

    @Inject
    public UserAssignedRolesEditor(final ClientUserSystemManager userSystemManager,
                                   final RolesExplorer rolesExplorer,
                                   final @AssignedEntitiesModalEditor AssignedEntitiesEditor<UserAssignedRolesEditor> view,
                                   final Event<OnUpdateUserRolesEvent> updateUserRolesEvent) {
        super(userSystemManager, rolesExplorer, view);
        this.updateUserRolesEvent = updateUserRolesEvent;
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
        return UsersManagementWidgetsConstants.INSTANCE.addToSelectedRoles();
    }

    @Override
    protected String getTitle() {
        return UsersManagementWidgetsConstants.INSTANCE.roleSelectionFor();
    }

    @Override
    protected String getEntityIdentifier(final Role entity) {
        return entity.getName();
    }

    protected void open(final User user) {
        assert user != null;
        this.entities.addAll(user.getRoles());
        entitiesExplorer.show(getViewContext());
        super.open(user);
    }

    @Override
    protected void onSave(Set<String> selectedEntities) {
        super.onSave(selectedEntities);
        if (selectedEntities != null && !selectedEntities.isEmpty()) {
            for (final String name : selectedEntities) {
                entities.add(userSystemManager.createRole(name));
            }
        }

        // Delegate the recently updated assigned roles for the user.
        updateUserRolesEvent.fire(new OnUpdateUserRolesEvent(UserAssignedRolesEditor.this, selectedEntities));
    }
    
}

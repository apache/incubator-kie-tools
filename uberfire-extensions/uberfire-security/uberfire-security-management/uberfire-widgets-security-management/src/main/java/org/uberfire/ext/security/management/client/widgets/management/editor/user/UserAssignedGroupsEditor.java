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

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesModalEditor;
import org.uberfire.ext.security.management.client.widgets.management.events.OnUpdateUserGroupsEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.ExplorerViewContext;
import org.uberfire.ext.security.management.client.widgets.management.explorer.GroupsExplorer;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned groups editor.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class UserAssignedGroupsEditor implements IsWidget, org.uberfire.ext.security.management.client.editor.user.UserAssignedGroupsEditor {

    ClientUserSystemManager userSystemManager;
    Event<OnUpdateUserGroupsEvent> updateUserGroupsEventEvent;
    GroupsExplorer groupsExplorer;
    public AssignedEntitiesEditor<UserAssignedGroupsEditor> view;

    final Set<Group> groups = new LinkedHashSet<Group>();
    boolean isEditMode;
    
    @Inject
    public UserAssignedGroupsEditor(final ClientUserSystemManager userSystemManager, 
                                    final Event<OnUpdateUserGroupsEvent> updateUserGroupsEventEvent, 
                                    final GroupsExplorer groupsExplorer,
                                    @AssignedEntitiesModalEditor final AssignedEntitiesEditor<UserAssignedGroupsEditor> view) {
        this.userSystemManager = userSystemManager;
        this.updateUserGroupsEventEvent = updateUserGroupsEventEvent;
        this.groupsExplorer = groupsExplorer;
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */
    
    @PostConstruct
    public void init() {
        view.init(this);
        view.configure(groupsExplorer.view);
        view.configureClose(UsersManagementWidgetsConstants.INSTANCE.cancel(), closeEditorCallback);
        view.configureSave(UsersManagementWidgetsConstants.INSTANCE.addToSelectedGroups(), saveEditorCallback);
        groupsExplorer.setPageSize(10);
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */
    
    @Override
    public void show(final User user) {
        clear();
        this.isEditMode = false;
        open(user);
    }
    
    @Override
    public void edit(final User user) {
        clear();
        this.isEditMode = true;
        open(user);
    }

    @Override
    public void flush() {
        assert isEditMode;
        // No additional flush logic to perform here.
    }

    @Override
    public Set<Group> getValue() {
        return groups;
    }

    @Override
    public void setViolations(Set<ConstraintViolation<User>> constraintViolations) {
        //  Currently no violations expected.
    }

    public void hide() {
        view.hide();
    }

    public void clear() {
        groupsExplorer.clear();
        groups.clear();
    }
    
    
    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */
    
    protected void open(final User user) {
        assert user != null;
        this.groups.addAll(user.getGroups());
        
        groupsExplorer.show(new ExplorerViewContext() {


            @Override
            public boolean canCreate() {
                return false;
            }

            @Override
            public boolean canRead() {
                return false;
            }

            @Override
            public boolean canDelete() {
                return false;
            }

            @Override
            public boolean canSelect() {
                return true;
            }

            @Override
            public Set<String> getSelectedEntities() {
                if (groups != null && !groups.isEmpty()) {
                    final Set<String> result = new HashSet<String>(groups.size());
                    for (final Group group : groups) {
                        result.add(group.getName());
                    }
                    return result;
                }
                return null;
            }
        });
        view.show(UsersManagementWidgetsConstants.INSTANCE.groupSelectionFor() + " " + user.getIdentifier());
    }

    final Command closeEditorCallback = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    final Command saveEditorCallback = new Command() {
        @Override
        public void execute() {
            hide();

            final Set<String> selectedGroups = groupsExplorer.getSelectedGroups();
            groups.clear();
            if (selectedGroups != null && !selectedGroups.isEmpty()) {
                for (final String name : selectedGroups) {
                    groups.add(userSystemManager.createGroup(name));
                }
            }
            groupsExplorer.clear();

            // Delegate the recently updated assigned groups for the user.
            updateUserGroupsEventEvent.fire(new OnUpdateUserGroupsEvent(UserAssignedGroupsEditor.this, selectedGroups));
        }
    };

}

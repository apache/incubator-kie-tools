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
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.events.OnRemoveUserGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.management.list.GroupsList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned groups explorer widget.</p>
 * <p>It's considered an Editor due to it allows removing assigned user's groups.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class UserAssignedGroupsExplorer implements IsWidget, org.uberfire.ext.security.management.client.editor.user.UserAssignedGroupsExplorer {

    private final static int PAGE_SIZE = 5;

    ClientUserSystemManager userSystemManager;
    Event<OnRemoveUserGroupEvent> removeUserGroupEventEvent;
    ConfirmBox confirmBox;
    GroupsList groupList;
    public AssignedEntitiesExplorer view;

    @Inject
    public UserAssignedGroupsExplorer(final ClientUserSystemManager userSystemManager,
                                      final Event<OnRemoveUserGroupEvent> removeUserGroupEventEvent,
                                      final ConfirmBox confirmBox,
                                      final GroupsList groupList,
                                      final AssignedEntitiesExplorer view) {
        this.userSystemManager = userSystemManager;
        this.removeUserGroupEventEvent = removeUserGroupEventEvent;
        this.confirmBox = confirmBox;
        this.groupList = groupList;
        this.view = view;
    }

    final Set<Group> groups = new LinkedHashSet<Group>(); 
    boolean isEditMode;

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */

    @PostConstruct
    public void init() {
        groupList.setPageSize(PAGE_SIZE);
        groupList.setEmptyEntitiesText(UsersManagementWidgetsConstants.INSTANCE.userHasNoGroups());
        view.configure(UsersManagementWidgetsConstants.INSTANCE.memberOfGroups(), groupList.view);
    }

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

    public void clear() {
        view.clear();
        groupList.clear();
        isEditMode = false;
        groups.clear();
    }
    
    
    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */
    
    protected void open(final User user) {
        assert user != null;
        
        final Set<Group> groups = user.getGroups();
        this.groups.addAll(groups);
        
        showGroups();
    }

    protected void showGroups() {
        groupList.show(this.groups, new EntitiesList.Callback<Group>() {
            @Override
            public String getEntityType() {
                return UsersManagementWidgetsConstants.INSTANCE.groupsAssigned();
            }

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public boolean canRemove() {
                return canAssignGroups();
            }

            @Override
            public boolean canSelect() {
                return false;
            }

            @Override
            public boolean isSelected(final String identifier) {
                return false;
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
                // Not allowed.
            }

            @Override
            public void onRemoveEntity(final String identifier) {
                if (identifier != null) {
                    confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UsersManagementWidgetsConstants.INSTANCE.ensureRemoveGroupFromUser(),
                            new Command() {
                                @Override
                                public void execute() {
                                    removeGroup(identifier);
                                }
                            });

                }
            }

            @Override
            public void onSelectEntity(final String identifier, final boolean isSelected) {
                // Entity selection not available for the explorer widget.
            }

            @Override
            public void onChangePage(final int currentPage, final int goToPage) {
                // Do nothing by default, let the groupList paginate.
            }
        });
    }
    
    boolean canAssignGroups() {
        final boolean canAssignGroups = userSystemManager.isUserCapabilityEnabled(Capability.CAN_ASSIGN_GROUPS);
        return isEditMode && canAssignGroups;
    }
    
    void removeGroup(final String name) {
        
        // Remove the group from the local cache.
        Iterator<Group> groups = this.groups.iterator();
        while (groups.hasNext()) {
            final Group g = groups.next();
            if (g.getName().equals(name)) {
                groups.remove();
            }
        }
        
        // Reload view.
        showGroups();
        
        // Fire the event for any others components listening.
        removeUserGroupEventEvent.fire(new OnRemoveUserGroupEvent(UserAssignedGroupsExplorer.this, name));
        
    }
}
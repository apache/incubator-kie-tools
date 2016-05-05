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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.RemoveUserGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.management.list.GroupsList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Set;

/**
 * <p>Presenter class for user's assigned groups explorer widget.</p>
 */
@Dependent
public class UserGroupsExplorer implements IsWidget {

    public interface View extends UberView<UserGroupsExplorer> {

        /**
         * Configure the view with the list view instance.
         * @param entitiesList The entities list view instance.
         * @return The view implementation.
         */
        View configure(final EntitiesList.View entitiesList);

        /**
         * Clears the view..
         * @return The view instance.
         */
        View clear();

    }
    
    protected GroupsList groupList;
    protected View view;
    ConfirmBox confirmBox;
    private Event<RemoveUserGroupEvent> removeUserGroupEventEvent;

    private final static int PAGE_SIZE = 5;
    boolean canRemove;

    @Inject
    public UserGroupsExplorer(GroupsList groupList, View view, ConfirmBox confirmBox, Event<RemoveUserGroupEvent> removeUserGroupEventEvent) {
        this.groupList = groupList;
        this.view = view;
        this.confirmBox = confirmBox;
        this.removeUserGroupEventEvent = removeUserGroupEventEvent;
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
        groupList.setPageSize(PAGE_SIZE);
        groupList.setEmptyEntitiesText(UsersManagementWidgetsConstants.INSTANCE.userHasNoGroups());
        groupList.setEntityTitleSize(HeadingSize.H5);
        view.configure(groupList.view);
    }

    public void show(final Set<Group> groups, final boolean canRemove) {
        // Clear current view.
        clear();

        // Remove assigned group feature.
        this.canRemove = canRemove;

        groupList.show(groups, new EntitiesList.Callback<Group>() {

            @Override
            public String getEntityType() {
                return UsersManagementWidgetsConstants.INSTANCE.groupsAssigned();
            }
            
            @Override
            public boolean canRead() {
                return false;
            }

            @Override
            public boolean canRemove() {
                return UserGroupsExplorer.this.canRemove;
            }

            @Override
            public boolean canSelect() {
                return false;
            }

            @Override
            public boolean isSelected(final String id) {
                return groups != null && groups.contains(id);
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
                                    // Delegate the recently created attribute addition to the entity.
                                    removeUserGroupEventEvent.fire(new RemoveUserGroupEvent(UserGroupsExplorer.this, identifier));
                                }
                            });
                }
            }

            @Override
            public void onSelectEntity(String identifier, boolean isSelected) {
                // Entity selection not available for the explorer widget.
            }

            @Override
            public void onChangePage(int currentPage, int goToPage) {
                // Do nothing by default, let the groupList paginate.
            }
        });

    }
    
    public void clear() {
        view.clear();
        canRemove = false;
    }
    
}
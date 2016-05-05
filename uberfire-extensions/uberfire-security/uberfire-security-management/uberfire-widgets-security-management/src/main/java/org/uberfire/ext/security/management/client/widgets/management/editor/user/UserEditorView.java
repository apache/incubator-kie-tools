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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.*;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;

public class UserEditorView extends Composite implements UserEditor.View {

    interface UserEditorViewBinder
            extends
            UiBinder<Widget, UserEditorView> {

    }

    private static UserEditorViewBinder uiBinder = GWT.create(UserEditorViewBinder.class);

    @UiField
    FlowPanel mainPanel;

    @UiField
    Row userTitleRow;

    @UiField
    Heading userTitle;

    @UiField
    Button editButton;

    @UiField
    Button deleteButton;

    @UiField
    Row userAttributesRow;
    
    @UiField(provided = true)
    UserAttributesEditor.View userAttributesEditorView;

    @UiField
    TabListItem groupsTabItem;
    
    @UiField
    TabListItem rolesTabItem;
    
    @UiField
    TabContent tabContent;
    
    @UiField
    TabPane groupsTabPane;

    @UiField
    TabPane rolesTabPane;
    
    @UiField
    Button addToGroupsButton;
    
    @UiField(provided = true)
    AssignedEntitiesExplorer userAssignedGroupsExplorerView;

    @UiField
    Button changePasswordButton;

    @UiField(provided = true)
    AssignedEntitiesEditor userAssignedGroupsEditorView;

    @UiField(provided = true)
    AssignedEntitiesEditor userAssignedRolesEditorView;
    
    @UiField
    Button addToRolesButton;
    
    @UiField(provided = true)
    AssignedEntitiesExplorer userAssignedRolesExplorerView;
    
    private UserEditor presenter;
    
    @Override
    public void init(final UserEditor presenter) {
        this.presenter = presenter;    
    }

    @Override
    public UserEditor.View initWidgets(final UserAttributesEditor.View userAttributesEditorView,
                                       final AssignedEntitiesExplorer userAssignedGroupsExplorerView,
                                       final AssignedEntitiesEditor userAssignedGroupsEditorView,
                                       final AssignedEntitiesExplorer userAssignedRolesExplorerView,
                                       final AssignedEntitiesEditor userAssignedRolesEditorView) {

        this.userAttributesEditorView = userAttributesEditorView;
        this.userAssignedGroupsExplorerView = userAssignedGroupsExplorerView;
        this.userAssignedGroupsEditorView = userAssignedGroupsEditorView;
        this.userAssignedRolesExplorerView = userAssignedRolesExplorerView;
        this.userAssignedRolesEditorView = userAssignedRolesEditorView;
        
        // Bind this view and initialize the widget.
        initWidget( uiBinder.createAndBindUi( this ) );

        // Tab panel configuration.
        groupsTabItem.setDataTargetWidget(groupsTabPane);
        rolesTabItem.setDataTargetWidget(rolesTabPane);
        
        return this;
    }

    @Override
    public UserEditor.View setUsername(final String username) {
        userTitle.setText(username);
        userTitle.setTitle(username);
        return this;
    }

    @Override
    public UserEditor.View setEditButtonVisible(final boolean isVisible) {
        editButton.setVisible(isVisible);
        return this;
    }

    @Override
    public UserEditor.View setDeleteButtonVisible(final boolean isVisible) {
        deleteButton.setVisible(isVisible);
        return this;
    }

    @Override
    public UserEditor.View setChangePasswordButtonVisible(final boolean isVisible) {
        changePasswordButton.setVisible(isVisible);
        return this;
    }

    @Override
    public UserEditor.View setAddToGroupsButtonVisible(final boolean isVisible) {
        addToGroupsButton.setVisible(isVisible);
        return this;
    }

    @Override
    public UserEditor.View setAddToRolesButtonVisible(final boolean isVisible) {
        addToRolesButton.setVisible(isVisible);
        return this;
    }

    @Override
    public UserEditor.View setAttributesEditorVisible(boolean isVisible) {
        userAttributesRow.setVisible(isVisible);
        return this;
    }

    @UiHandler( "editButton" )
    public void onEditButtonClick( final ClickEvent event ) {
        presenter.onEdit();
    }

    @UiHandler( "deleteButton" )
    public void onDeleteButtonClick( final ClickEvent event ) {
        presenter.onDelete();
    }

    @UiHandler( "changePasswordButton" )
    public void onChangePasswordButtonClick( final ClickEvent event ) {
        presenter.onChangePassword();
    }

    @UiHandler( "addToGroupsButton" )
    public void onAddToGroupsButtonClick(final ClickEvent event ) {
        presenter.onAssignGroups();
    }

    @UiHandler( "addToRolesButton" )
    public void onAddToRolessButtonClick(final ClickEvent event ) {
        presenter.onAssignRoles();
    }
    
}

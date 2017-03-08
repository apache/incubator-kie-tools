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

package org.uberfire.ext.security.management.client.widgets.management.editor.role;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLSettings;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLViewer;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnShowEvent;
import org.uberfire.security.authz.PermissionCollection;

/**
 * <p>The user editor presenter.</p>
 * <p>User's groups are edited using the UserAssignedGroupsExplorer editor component. So the UserAssignedGroupsEditor works with a dummy user instance.</p>
 * @since 0.8.0
 */
@Dependent
public class RoleEditor implements IsWidget,
                                   org.uberfire.ext.security.management.client.editor.role.RoleEditor {

    public View view;
    ClientUserSystemManager userSystemManager;
    ACLSettings aclSettings;
    ACLViewer aclViewer;
    ACLEditor aclEditor;
    Event<OnEditEvent> onEditEvent;
    Event<OnShowEvent> onShowEvent;
    Role role;
    boolean isEditMode;
    @Inject
    public RoleEditor(final ClientUserSystemManager userSystemManager,
                      final ACLSettings aclSettings,
                      final ACLViewer aclViewer,
                      final ACLEditor aclEditor,
                      final Event<OnEditEvent> onEditEvent,
                      final Event<OnShowEvent> onShowEvent,
                      final View view) {

        this.userSystemManager = userSystemManager;
        this.aclSettings = aclSettings;
        this.aclViewer = aclViewer;
        this.aclEditor = aclEditor;
        this.onEditEvent = onEditEvent;
        this.onShowEvent = onShowEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setACLSettings(aclSettings);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*  ******************************************************************************************************
                                     PUBLIC PRESENTER API 
         ****************************************************************************************************** */

    @Override
    public String name() {
        return role.getName();
    }

    @Override
    public PerspectiveActivity homePerspective() {
        return aclSettings.getHomePerspective();
    }

    @Override
    public int rolePriority() {
        return aclSettings.getPriority();
    }

    @Override
    public PermissionCollection permissions() {
        return aclEditor.getPermissions();
    }

    public ACLSettings getAclSettings() {
        return aclSettings;
    }

    public ACLEditor getAclEditor() {
        return aclEditor;
    }

    @Override
    public void show(final Role role) {
        clear();
        this.isEditMode = false;
        open(role);
        onShowEvent.fire(new OnShowEvent(RoleEditor.this,
                                         role));
    }

    @Override
    public void edit(final Role role) {
        clear();
        this.isEditMode = true;
        open(role);
    }

    @Override
    public void flush() {
        assert role != null;
        assert isEditMode;
        // No additional flush logic to perform here.
    }

    @Override
    public Role getValue() {
        return role;
    }

    @Override
    public void setViolations(final Set<ConstraintViolation<Role>> violations) {
        //  Currently no violations expected.
    }

    public void clear() {
        isEditMode = false;
        role = null;
    }

    void onEdit() {
        onEditEvent.fire(new OnEditEvent(RoleEditor.this,
                                         role));
    }
    
    /*  ******************************************************************************************************
                                 VIEW CALLBACKS 
     ****************************************************************************************************** */

    protected void open(final Role role) {
        assert role != null;
        this.role = role;

        // Role name
        final String name = role.getName();
        view.setRolename(name);

        // Edit mode
        view.setEditButtonVisible(!isEditMode);

        // ACL Editor/Viewer
        if (isEditMode) {
            aclSettings.edit(role);
            aclEditor.edit(role);
            view.editACL(aclEditor);
        } else {
            aclSettings.show(role);
            aclViewer.show(role);
            view.showACL(aclViewer);
        }
    }

     /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */

    public interface View extends UberView<RoleEditor> {

        View setRolename(String username);

        View setEditButtonVisible(boolean isVisible);

        View setACLSettings(IsWidget aclSettings);

        View showACL(IsWidget aclViewer);

        View editACL(IsWidget aclEditor);
    }
}

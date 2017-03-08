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

package org.uberfire.ext.security.management.client.editor.role;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.Driver;
import org.uberfire.security.authz.PermissionCollection;

/**
 * <p>It links the user editors hierarchy with the instance edited by flushing the editor's values into the User model.</p>
 * <p>It contains all logic for editing a User instance by using Editors hierarchy instances, so
 * the editors's concrete widget/component implementation is isolated from the edition logic.</p>
 * @since 0.8.0
 */
@Dependent
public class RoleEditorDriver implements Driver<Role, RoleEditor> {

    ClientUserSystemManager userSystemManager;

    RoleEditor roleEditor;
    Role role;
    boolean isFlushed = false;
    boolean isEditMode = false;

    @Inject
    public RoleEditorDriver(final ClientUserSystemManager userSystemManager) {
        this.userSystemManager = userSystemManager;
    }

    public void show(final Role role,
                     final RoleEditor roleEditor) {
        this.isFlushed = false;
        this.isEditMode = false;
        this.role = role;
        this.roleEditor = roleEditor;
        roleEditor.show(role);
    }

    public void edit(final Role role,
                     final RoleEditor roleEditor) {
        this.isFlushed = false;
        this.isEditMode = true;
        this.role = role;
        this.roleEditor = roleEditor;
        roleEditor.edit(role);
    }

    public boolean flush() {
        assert this.isEditMode;
        this.isFlushed = true;

        roleEditor.flush();

        // Obtain the editor's values
        final String name = roleEditor.name();

        // Create a new resulting instance
        role = new RoleImpl(name);

        // Validate the instance and set delegate violations, if any, to the editors hierarchy.
        Set<ConstraintViolation<Role>> violations = userSystemManager.rolesValidator().validate(role);
        roleEditor.setViolations(violations);
        return violations == null || violations.isEmpty();
    }

    @Override
    public Role getValue() {
        assert this.isFlushed;
        return role;
    }

    public PerspectiveActivity getHomePerspective() {
        return roleEditor.homePerspective();
    }

    public int getRolePriority() {
        return roleEditor.rolePriority();
    }

    public PermissionCollection getPermissions() {
        return roleEditor.permissions();
    }
}

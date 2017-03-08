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

package org.uberfire.ext.security.management.client.editor.group;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.Driver;
import org.uberfire.security.authz.PermissionCollection;

@Dependent
public class GroupEditorDriver implements Driver<Group, GroupEditor> {

    ClientUserSystemManager userSystemManager;

    GroupEditor groupEditor;
    Group group;
    boolean isFlushed = false;
    boolean isEditMode = false;

    @Inject
    public GroupEditorDriver(final ClientUserSystemManager userSystemManager) {
        this.userSystemManager = userSystemManager;
    }

    public void show(final Group group,
                     final GroupEditor groupEditor) {
        this.isFlushed = false;
        this.isEditMode = false;
        this.group = group;
        this.groupEditor = groupEditor;
        groupEditor.show(group);
    }

    public void edit(final Group group,
                     final GroupEditor groupEditor) {
        this.isFlushed = false;
        this.isEditMode = true;
        this.group = group;
        this.groupEditor = groupEditor;
        groupEditor.edit(group);
    }

    public boolean flush() {
        assert this.isEditMode;
        this.isFlushed = true;

        groupEditor.flush();

        // Obtain the editor's values
        final String name = groupEditor.name();

        // Create a new resulting instance
        group = new GroupImpl(name);

        // Validate the instance and set delegate violations, if any, to the editors hierarchy.
        Set<ConstraintViolation<Group>> violations = userSystemManager.groupsValidator().validate(group);
        groupEditor.setViolations(violations);
        return violations == null || violations.isEmpty();
    }

    @Override
    public Group getValue() {
        assert this.isFlushed;
        return group;
    }

    public PermissionCollection getPermissions() {
        return groupEditor.permissions();
    }

    public PerspectiveActivity getHomePerspective() {
        return groupEditor.homePerspective();
    }

    public int getGroupPriority() {
        return groupEditor.groupPriority();
    }
}

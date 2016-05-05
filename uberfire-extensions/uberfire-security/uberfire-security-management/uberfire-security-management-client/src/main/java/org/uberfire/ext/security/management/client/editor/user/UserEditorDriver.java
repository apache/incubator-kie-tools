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

package org.uberfire.ext.security.management.client.editor.user;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.Driver;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <p>It links the user editors hierarchy with the instance edited by flushing the editor's values into the User model.</p>
 * <p>It contains all logic for editing a User instance by using Editors hierarchy instances, so 
 * the editors's concrete widget/component implementation is isolated from the edition logic.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class UserEditorDriver implements Driver<User, UserEditor> {

    ClientUserSystemManager userSystemManager;
    
    UserEditor userEditor;
    User user;
    boolean isFlushed = false;
    boolean isEditMode = false;
    
    @Inject
    public UserEditorDriver(final ClientUserSystemManager userSystemManager) {
        this.userSystemManager = userSystemManager;
    }

    public User createNewUser(final String identifier) {
        User user = userSystemManager.createUser(identifier);
        final Collection<UserManager.UserAttribute> attrs = userSystemManager.getUserSupportedAttributes();
        if (attrs != null) {
            for (UserManager.UserAttribute attribute : attrs) {
                final String name = attribute.getName();
                final boolean isMandatory = attribute.isMandatory();
                final boolean isEditable = attribute.isEditable();
                if (isMandatory && isEditable && name != null) {
                    final String defaultValue = attribute.getDefaultValue();
                    user.setProperty(name, defaultValue);
                }
            }
        }
        return user;
    }
    
    public void show(final User user, final UserEditor userEditor) {
        this.isFlushed = false;
        this.isEditMode = false;
        this.user = user;
        this.userEditor = userEditor;
        // Root viewer..
        userEditor.show(user);
        // Sub-viewers.
        userEditor.attributesEditor().show(user);
        userEditor.groupsExplorer().show(user);
        userEditor.rolesExplorer().show(user);
    }

    public void edit(final User user, final UserEditor userEditor) {
        this.isFlushed = false;
        this.isEditMode = true;
        this.user = user;
        this.userEditor = userEditor;
        // Root editor edition.
        userEditor.edit(user);
        // Sub-editors edition.
        userEditor.attributesEditor().edit(user);
        userEditor.groupsExplorer().edit(user);
        userEditor.rolesExplorer().edit(user);
    }

    public boolean flush() {
        assert this.isEditMode;
        this.isFlushed = true;
        
        // Flush editor and sub-editors.
        userEditor.flush();
        userEditor.attributesEditor().flush();
        userEditor.groupsExplorer().flush();
        userEditor.rolesExplorer().flush();

        // Obtain the editor's values.
        final String id = userEditor.identifier();
        final Map<String, String> properties = userEditor.attributesEditor().getValue();
        final Set<Group> groups = userEditor.groupsExplorer().getValue();
        final Set<Role> roles = userEditor.rolesExplorer().getValue();
        
        // Create a new resulting instance (as groups & roles are unmodifiable collections in the default UserImpl).
        user = new UserImpl(id, roles, groups, properties);
        
        // Validate the instance and set delegate violations, if any, to the editors hierarchy.
        Set<ConstraintViolation<User>> violations = userSystemManager.usersValidator().validate(user);
        userEditor.setViolations(violations);
        return violations == null || violations.isEmpty();
    }

    @Override
    public User getValue() {
        assert this.isFlushed;
        return user;
    }

}

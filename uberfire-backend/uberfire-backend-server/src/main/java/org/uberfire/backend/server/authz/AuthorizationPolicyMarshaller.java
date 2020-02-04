/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.backend.server.authz;

import java.util.Map;
import java.util.Properties;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

import static org.uberfire.backend.server.authz.AuthorizationPolicyMarshaller.ReadMode.DEFAULT_EXCLUDED;
import static org.uberfire.backend.server.authz.AuthorizationPolicyMarshaller.ReadMode.DEFAULT_ONLY;
import static org.uberfire.backend.server.authz.AuthorizationPolicyMarshaller.ReadMode.EVERYTHING;

/**
 * Class used to convert an {@link AuthorizationPolicy} instance into/from a set of key/value pairs.
 * <p>
 * <p>The format of the key/value pairs is:</p><a name="entriesFormat"></a>
 * <p>
 * <pre>"classifier.identifier.setting.extra=value"</pre>
 *
 * Where:
 * <ul>
 * <li>classifier = <i>role</i>|<i>group</i></li>
 * <li>identifier = An existing role or group identifier (depending on the classifier type)</li>
 * <li>setting    = <i>home</i>|<i>priority</i>|<i>permission</i></li>
 * <li>extra      = Extra setting information. Mandatory for instance to define the permission's name</li>
 * <li>value      = The setting value (depends on the setting selected). Value expected per setting type:
 * <ul>
 * <li>home: An existing perspective identifier to redirect after login</li>
 * <li>priority: An integer indicating how priority is this role|group compared to others. Used for conflict resolution.</li>
 * <li>permission: A name representing a specific feature or capability over a given resource.</li>
 * </ul></li>
 * </ul>
 *
 * <p>For example:
 *
 * <pre>
 * #Role "admin"
 * role.admin.home=Home
 * role.admin.priority=10
 * role.admin.permission.perspective.read=true
 * role.admin.permission.perspective.read.Dashboard=false
 *
 * # Role "user"
 * role.user.home=Dashboard
 * role.user.priority=0
 * role.user.permission.perspective.read=false
 * role.user.permission.perspective.read.Home=true
 * role.user.permission.perspective.read.Dashboard=true
 * </pre>
 */
public class AuthorizationPolicyMarshaller {

    private static final String DEFAULT = "default";
    private static final String ROLE = "role";
    private static final String GROUP = "group";
    private static final String PERMISSION = "permission";
    private static final String PRIORITY = "priority";
    private static final String HOME = "home";
    private static final String DESCRIPTION = "description";

    /**
     * It reads all the entries from the collection of property files passed as a parameter. For every entry
     * a call to the proper {@link AuthorizationPolicyBuilder} method is executed.
     * <p>
     * <p>The valid format for the entries is specified in the <a href="#entriesFormat">class description</a>.</p>
     * @param builder The {@link AuthorizationPolicyBuilder} used to register every processed entry.
     * @param input The property objects containing the authz policy entries
     */
    public void read(AuthorizationPolicyBuilder builder,
                     Map... input) {
        for (Map m : input) {
            // Process the global/default settings first in order to make sure the rest overwrite them and not viceversa
            m.forEach((x, y) -> read(builder,
                                     x.toString(),
                                     y.toString(),
                                     DEFAULT_ONLY));
        }
        for (Map m : input) {
            // Process the rest of the settings
            m.forEach((x, y) -> read(builder,
                                     x.toString(),
                                     y.toString(),
                                     DEFAULT_EXCLUDED));
        }
    }

    /**
     * It reads key/value pair passed as a parameter and it calls to the right
     * {@link AuthorizationPolicyBuilder} method .
     * <p>
     * <p>The valid format for an key/value pair is specified in the <a href="#entriesFormat">class description</a>.</p>
     * @param builder The {@link AuthorizationPolicyBuilder} used to register the entry.
     * @param key The key to read
     * @param value The value to read
     */
    public void read(AuthorizationPolicyBuilder builder,
                     String key,
                     String value) {
        this.read(builder,
                  key,
                  value,
                  EVERYTHING);
    }

    /**
     * It reads key/value pair passed as a parameter and it calls to the right
     * {@link AuthorizationPolicyBuilder} method .
     * <p>
     * <p>The valid format for an key/value pair is specified in the <a href="#entriesFormat">class description</a>.</p>
     * @param builder The {@link AuthorizationPolicyBuilder} used to register the entry.
     * @param key The key to read
     * @param value The value to read
     * @param readMode The {@link ReadMode} determines if the specified key shall be included or excluded
     */
    public void read(AuthorizationPolicyBuilder builder,
                     String key,
                     String value,
                     ReadMode readMode) {
        Key keyObj = parse(key);

        if (isReadable(keyObj,
                       readMode)) {
            read(builder,
                 keyObj,
                 value);
        }
    }

    /**
     * Check if a key object can be read according the given read mode.
     */
    private boolean isReadable(Key keyObj,
                               ReadMode readMode) {
        if (keyObj.isDefault() && DEFAULT_EXCLUDED.equals(readMode)) {
            return false;
        }
        if (!keyObj.isDefault() && DEFAULT_ONLY.equals(readMode)) {
            return false;
        }
        return true;
    }

    private void read(AuthorizationPolicyBuilder builder,
                      Key keyObj,
                      String value) {

        if (!keyObj.isDefault()) {
            if (keyObj.isRole()) {
                builder.role(keyObj.getRole());
            } else if (keyObj.isGroup()) {
                builder.group(keyObj.getGroup());
            } else {
                throw new IllegalArgumentException("Key must start either with 'role' or 'group': " + keyObj);
            }
        }

        String attr = keyObj.getAttributeType();
        switch (attr) {

            case DESCRIPTION:
                builder.description(value);
                break;

            case HOME:
                builder.home(value);
                break;

            case PRIORITY:
                builder.priority(Integer.parseInt(value));
                break;

            case PERMISSION:
                String permission = keyObj.getAttributeId();
                if (permission.length() == 0) {
                    throw new IllegalArgumentException("Permission is incomplete: " + keyObj);
                }
                boolean granted = Boolean.parseBoolean(value);
                builder.permission(permission,
                                   granted);
                break;

            default:
                throw new IllegalArgumentException("Unknown key: " + keyObj);
        }
    }

    /**
     * Dumps the {@link AuthorizationPolicy} instance passed as a parameter into the output {@link Properties} object
     * specified.
     * <p>
     * <p>The format for an key/value pair is specified in the <a href="#entriesFormat">class description</a>.</p>
     * @param policy The {@link AuthorizationPolicy} to serialize
     * @param out The {@link Properties} instance used as output
     */
    public void write(AuthorizationPolicy policy,
                      Map out) {
        write(policy.getHomePerspective(),
              out);
        write(policy.getPermissions(),
              out);

        for (Role subject : policy.getRoles()) {
            write(subject,
                  policy.getHomePerspective(subject),
                  out);
            write(subject,
                  policy.getPriority(subject),
                  out);
            write(subject,
                  policy.getPermissions(subject),
                  out);
        }
        for (Group subject : policy.getGroups()) {
            write(subject,
                  policy.getHomePerspective(subject),
                  out);
            write(subject,
                  policy.getPriority(subject),
                  out);
            write(subject,
                  policy.getPermissions(subject),
                  out);
        }
    }

    public void write(String homePerspectiveId,
                      Map out) {
        String key = DEFAULT + "." + HOME;
        out.remove(key);
        if (homePerspectiveId != null) {
            out.put(key,
                    homePerspectiveId);
        }
    }

    public void write(PermissionCollection permissions,
                      Map out) {
        for (Permission p : permissions.collection()) {
            boolean granted = p.getResult() != null && p.getResult().equals(AuthorizationResult.ACCESS_GRANTED);
            String key = DEFAULT + "." + PERMISSION + "." + p.getName();
            out.put(key,
                    Boolean.toString(granted));
        }
    }

    public void write(Role role,
                      String homePerspectiveId,
                      Map out) {
        String key = ROLE + "." + role.getName() + "." + HOME;
        out.remove(key);
        if (homePerspectiveId != null) {
            out.put(key,
                    homePerspectiveId);
        }
    }

    public void write(Role role,
                      int priority,
                      Map out) {
        String key = ROLE + "." + role.getName() + "." + PRIORITY;
        out.put(key,
                Integer.toString(priority));
    }

    public void write(Role role,
                      PermissionCollection permissions,
                      Map out) {
        for (Permission p : permissions.collection()) {
            boolean granted = p.getResult() != null && p.getResult().equals(AuthorizationResult.ACCESS_GRANTED);
            String key = ROLE + "." + role.getName() + "." + PERMISSION + "." + p.getName();
            out.put(key,
                    Boolean.toString(granted));
        }
    }

    public void write(Group group,
                      String homePerspectiveId,
                      Map out) {
        String key = GROUP + "." + group.getName() + "." + HOME;
        out.remove(key);
        if (homePerspectiveId != null) {
            out.put(key,
                    homePerspectiveId);
        }
    }

    public void write(Group group,
                      int priority,
                      Map out) {
        String key = GROUP + "." + group.getName() + "." + PRIORITY;
        out.put(key,
                Integer.toString(priority));
    }

    public void write(Group group,
                      PermissionCollection permissions,
                      Map out) {
        for (Permission p : permissions.collection()) {
            boolean granted = p.getResult() != null && p.getResult().equals(AuthorizationResult.ACCESS_GRANTED);
            String key = GROUP + "." + group.getName() + "." + PERMISSION + "." + p.getName();
            out.put(key,
                    Boolean.toString(granted));
        }
    }

    public void remove(Group group, AuthorizationPolicy policy,
                       Map out) {

        write(policy, out);

        for (Group subject : policy.getGroups()) {
            if (group.getName().equals(subject.getName())) {
                remove(subject,
                       out);
                remove(subject,
                       policy.getPermissions(subject),
                       out);
            }
        }
    }

    private void remove(Group group,
                       Map out) {
        String homePerspectiveKey = GROUP + "." + group.getName() + "." + HOME;
        String priorityKey = GROUP + "." + group.getName() + "." + PRIORITY;
        out.remove(priorityKey);
        out.remove(homePerspectiveKey);
    }

    private void remove(Group group,
                       PermissionCollection permissions,
                       Map out) {
        for (Permission p : permissions.collection()) {
            String key = GROUP + "." + group.getName() + "." + PERMISSION + "." + p.getName();
            out.remove(key);
        }
    }

    public Key parse(String key) {
        int _idx = 0;
        String _key = key.endsWith(".*") ? key.substring(0,
                                                         key.length() - 2) : key;
        String[] tokens = _key.split("\\.");
        Key result = new Key(key);

        // Type
        if (_idx < tokens.length) {
            result.setType(tokens[_idx++]);
        }
        // Role / Group
        if (_idx < tokens.length) {
            if (result.isRole()) {
                result.setRole(tokens[_idx++]);
            } else if (result.isGroup()) {
                result.setGroup(tokens[_idx++]);
            }
        }
        // Attribute type
        if (_idx < tokens.length) {
            result.setAttributeType(tokens[_idx++]);
        }
        // Attribute id.
        if (_idx < tokens.length) {
            StringBuilder attrIdStr = new StringBuilder();
            for (int i = _idx; i < tokens.length; i++) {
                if (i > _idx) {
                    attrIdStr.append(".");
                }
                attrIdStr.append(tokens[i]);
            }
            result.setAttributeId(attrIdStr.toString());
        }
        // Validate & return
        result.validate();
        return result;
    }

    /**
     * Different ways to specify what are the target entries to read when calling the
     * {@link #read(AuthorizationPolicyBuilder, String, String, ReadMode)} method.
     */
    public enum ReadMode {

        /**
         * Read only those entries classified as "default"
         */
        DEFAULT_ONLY,

        /**
         * Read everything but the entries classified as "default"
         */
        DEFAULT_EXCLUDED,

        /**
         * Read everything
         */
        EVERYTHING
    }

    public class Key {

        String key = null;
        String type = null;
        String roleGroup = null;
        String attributeType = null;
        String attributeId = null;

        public Key(String key) {
            this.key = key;
        }

        public boolean isDefault() {
            return type != null && DEFAULT.equals(type);
        }

        public boolean isRole() {
            return type != null && ROLE.equals(type);
        }

        public boolean isGroup() {
            return type != null && GROUP.equals(type);
        }

        public String getRole() {
            return isRole() ? roleGroup : null;
        }

        public void setRole(String role) {
            this.roleGroup = role;
        }

        public String getGroup() {
            return isGroup() ? roleGroup : null;
        }

        public void setGroup(String group) {
            this.roleGroup = group;
        }

        public String getAttributeType() {
            return attributeType;
        }

        public void setAttributeType(String attributeType) {
            this.attributeType = attributeType;
        }

        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = attributeId;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return key;
        }

        public void validate() throws IllegalStateException {
            if (type == null || (!DEFAULT.equals(type) && !ROLE.equals(type) && !GROUP.equals(type))) {
                throw new IllegalArgumentException("Key must start with [default|role|group]");
            }
            if (isRole() && (roleGroup == null || roleGroup.length() == 0)) {
                throw new IllegalArgumentException("Role value is empty");
            }
            if (isGroup() && (roleGroup == null || roleGroup.length() == 0)) {
                throw new IllegalArgumentException("Group value is empty");
            }
            if (attributeType == null || attributeType.length() == 0) {
                throw new IllegalArgumentException("Empty attribute type not allowed: " + attributeType);
            }
        }
    }
}

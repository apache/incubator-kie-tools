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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

/**
 * Class used to convert an {@link AuthorizationPolicy} instance into/from a set of key/value pairs.
 *
 * <p>The format of the key/value pairs is:</p><a name="entriesFormat"></a>
 *
 * <pre>"classifier.identifier.setting.extra=value"</pre>
 *
 * Where:
 * <ul>
 *     <li>classifier = <i>role</i>|<i>group</i></li>
 *     <li>identifier = An existing role or group identifier (depending on the classifier type)</li>
 *     <li>setting    = <i>home</i>|<i>priority</i>|<i>permission</i></li>
 *     <li>extra      = Extra setting information. Mandatory for instance to define the permission's name</li>
 *     <li>value      = The setting value (depends on the setting selected). Value expected per setting type:
 *     <ul>
 *         <li>home: An existing perspective identifier to redirect after login</li>
 *         <li>priority: An integer indicating how priority is this role|group compared to others. Used for conflict resolution.</li>
 *         <li>permission: A name representing a specific feature or capability over a given resource.</li>
 *     </ul></li>
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

    private static final String ROLE = "role";
    private static final String GROUP = "group";
    private static final String PERMISSION = "permission";
    private static final String PRIORITY = "priority";
    private static final String HOME = "home";
    private static final String DESCRIPTION = "description";

    /**
     * It reads all the entries from the collection of property files passed as a parameter. For every entry
     * a call to the proper {@link AuthorizationPolicyBuilder} method is executed.
     *
     * <p>The valid format for the entries is specified in the <a href="#entriesFormat">class description</a>.</p>
     *
     * @param builder The {@link AuthorizationPolicyBuilder} used to register every processed entry.
     * @param input The property objects containing the authz policy entries
     */
    public void read(AuthorizationPolicyBuilder builder, Map... input) {
        for (Map m : input) {
            m.forEach((x,y) -> read(builder, x.toString(), y.toString()));
        }
    }

    /**
     * It reads key/value pair passed as a parameter and it calls to the right
     * {@link AuthorizationPolicyBuilder} method .
     *
     * <p>The valid format for an key/value pair is specified in the <a href="#entriesFormat">class description</a>.</p>
     *
     * @param builder The {@link AuthorizationPolicyBuilder} used to register the entry.
     * @param key The key to read
     * @param value The value to read
     */
    public void read(AuthorizationPolicyBuilder builder, String key, String value) {
        List<String> tokens = split(key);

        // Role or group setting
        String type = tokens.get(0);
        String typeId = tokens.get(1);
        switch (type) {
            case ROLE:
                builder.role(typeId);
                break;

            case GROUP:
                builder.group(typeId);
                break;

            default:
                throw new IllegalArgumentException("Key must start either with 'role' or 'group': " + key);
        }

        // Attribute/value
        String attr = tokens.get(2);
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
                String permission = tokens.get(3);
                if (permission.length() == 0) {
                    throw new IllegalArgumentException("Permission is incomplete: " + key);
                }
                boolean granted = Boolean.parseBoolean(value);
                builder.permission(permission, granted);
                break;

            default:
                throw new IllegalArgumentException("Unknown key: " + key);
        }
    }

    /**
     * Dumps the {@link AuthorizationPolicy} instance passed as a parameter into the output {@link Properties} object
     * specified.
     *
     * <p>The format for an key/value pair is specified in the <a href="#entriesFormat">class description</a>.</p>
     *
     * @param policy The {@link AuthorizationPolicy} to serialize
     * @param out The {@link Properties} instance used as output
     */
    public void write(AuthorizationPolicy policy, Map out) {
        for (Role subject : policy.getRoles()) {
            write(subject, policy.getHomePerspective(subject), out);
            write(subject, policy.getPriority(subject), out);
            write(subject, policy.getPermissions(subject), out);
        }
        for (Group subject : policy.getGroups()) {
            write(subject, policy.getHomePerspective(subject), out);
            write(subject, policy.getPriority(subject), out);
            write(subject, policy.getPermissions(subject), out);
        }
    }

    public void write(Role role, String homePerspectiveId, Map out) {
        String key = ROLE + "." + role.getName()  + "." + HOME;
        out.remove(key);
        if (homePerspectiveId != null) {
            out.put(key, homePerspectiveId);
        }
    }

    public void write(Role role, int priority, Map out) {
        String key = ROLE + "." + role.getName() + "." + PRIORITY;
        out.put(key, Integer.toString(priority));
    }

    public void write(Role role, PermissionCollection permissions, Map out) {
        for (Permission p : permissions.collection()) {
            boolean granted = p.getResult() != null && p.getResult().equals(AuthorizationResult.ACCESS_GRANTED);
            String key = ROLE + "." + role.getName() + "." + PERMISSION + "." + p.getName();
            out.put(key, Boolean.toString(granted));
        }
    }

    public void write(Group group, String homePerspectiveId, Map out) {
        String key = GROUP + "." + group.getName() + "." + HOME;
        out.remove(key);
        if (homePerspectiveId != null) {
            out.put(key, homePerspectiveId);
        }
    }

    public void write(Group group, int priority, Map out) {
        String key = GROUP + "." + group.getName() + "." + PRIORITY;
        out.put(key, Integer.toString(priority));
    }

    public void write(Group group, PermissionCollection permissions, Map out) {
        for (Permission p : permissions.collection()) {
            boolean granted = p.getResult() != null && p.getResult().equals(AuthorizationResult.ACCESS_GRANTED);
            String key = GROUP + "." + group.getName() + "." + PERMISSION + "." + p.getName();
            out.put(key, Boolean.toString(granted));
        }
    }

    public List<String> split(String key) {
        String _key = key.endsWith(".*") ? key.substring(0, key.length()-2) : key;
        List<String> result = new ArrayList<>();
        String[] tokens = _key.split("\\.");
        for (String token : tokens) {
            if (token.length() == 0) {
                throw new IllegalArgumentException("Empty token not allowed: " + key);
            }
            if (result.size() < 4) {
                result.add(token);
            } else {
                result.set(3, result.get(3) + "." + token);
            }
        }
        if (result.size() < 3) {
            throw new IllegalArgumentException("Incomplete key: " + key);
        }
        return result;
    }
}

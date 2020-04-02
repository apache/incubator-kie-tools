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
package org.uberfire.security.impl.authz;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;

import static org.uberfire.security.authz.AuthorizationResult.ACCESS_ABSTAIN;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_DENIED;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

/**
 * An implementation where the permission's name is formatted using dots. For instance:
 * <p>
 * <ul>
 * <li><b>resource.view</b> => View all resources</li>
 * <li><b>resource.view.r1</b> => View only r1</li>
 * </ul>
 * The {@code implies(Permission other)} implementation is based on the simple fact that one permission implies
 * another just if its name starts with the another's name. This very simple mechanism can be applied to most of
 * the resources that require authorization control, like for instance, workbench perspectives, a file system, etc.
 */
@Portable
public class DotNamedPermission implements Permission,
                                           Comparable<Permission> {

    private String name;
    private AuthorizationResult result;
    private boolean _immutable;

    public DotNamedPermission() {
    }

    public DotNamedPermission(String name) {
        this(name,
             ACCESS_ABSTAIN);
    }

    public DotNamedPermission(String name,
                              Boolean granted) {
        this.name = name;
        result = granted == null ? ACCESS_ABSTAIN : (granted ? ACCESS_GRANTED : ACCESS_DENIED);
    }

    public DotNamedPermission(String name,
                              AuthorizationResult result) {
        this.name = name;
        this.result = result;
    }

    protected void _enableImmutability() {
        _immutable = true;
    }

    protected void _checkImmutability() {
        if (_immutable) {
            throw new IllegalStateException("The permission is non mutable: " + this);
        }
    }

    public <T extends DotNamedPermission> T nonMutable() {
        _enableImmutability();
        return (T) this;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        _checkImmutability();
        this.name = name;
    }

    @Override
    public AuthorizationResult getResult() {
        return result;
    }

    public void setResult(AuthorizationResult result) {
        _checkImmutability();
        this.result = result;
    }

    @Override
    public boolean implies(Permission other) {
        return impliesName(other) && impliesResult(other);
    }

    @Override
    public boolean impliesName(Permission other) {
        if (equalsName(other)) {
            return true;
        }
        if (name == null) {
            return false;
        }
        String otherName = other.getName();
        return otherName != null && otherName.startsWith(name + ".");
    }

    @Override
    public boolean impliesResult(Permission other) {
        if (result == null || ACCESS_ABSTAIN.equals(result)) {
            return other.getResult() == null || ACCESS_ABSTAIN.equals(other.getResult());
        }
        boolean otherDenied = other.getResult() != null && ACCESS_DENIED.equals(other.getResult());
        return ACCESS_DENIED.equals(result) == otherDenied;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Permission)) {
            return false;
        }
        Permission other = (Permission) obj;
        return equalsName(other) && equalsResult(other);
    }

    @Override
    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    public boolean equalsName(Permission other) {
        if (name != null && !name.equals(other.getName())) {
            return false;
        }
        if (name == null && other.getName() != null) {
            return false;
        }
        return true;
    }

    public boolean equalsResult(Permission other) {
        if (result == null && other.getResult() != null) {
            return false;
        }
        if (result != null && !result.equals(other.getResult())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Permission o) {
        if (name == null) {
            return o.getName() == null ? 0 : -1;
        }
        return name.compareTo(o.getName());
    }

    @Override
    public Permission clone() {
        return new DotNamedPermission(name,
                                      result);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(name).append(" ").append(result);
        return out.toString();
    }
}

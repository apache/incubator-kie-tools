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
package org.dashbuilder.navigation.impl;

import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavItemVisitor;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NavItemImpl implements NavItem {

    String id = null;
    String name = null;
    String description = null;
    NavGroup parent = null;
    boolean modifiable = true;
    String context = null;
    Type type = Type.ITEM;

    public NavItemImpl() {
    }

    public NavItemImpl(String id, String name, String description, NavGroup parent, boolean modifiable, String context) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.modifiable = modifiable;
        this.context = context;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public NavGroup getParent() {
        return parent;
    }

    @Override
    public void setParent(NavGroup parent) {
        this.parent = parent;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    @Override
    public void accept(NavItemVisitor visitor) {
        visitor.visitItem(this);
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object obj) {
        if (id == null || obj == null || !(obj instanceof NavItem)) {
            return false;
        }
        NavItem other = (NavItem) obj;
        return id.equals(other.getId());
    }

    @Override
    public NavItem cloneItem() {
        NavItemImpl clone = new NavItemImpl();
        clone.id = this.id;
        clone.parent = this.parent;
        clone.name = this.name;
        clone.description = this.description;
        clone.modifiable = this.modifiable;
        clone.context = this.context;
        return clone;
    }

    public String toString() {
        return toString("ITEM");
    }

    public String toString(String type) {
        StringBuilder out = new StringBuilder();
        out.append(type).append("=").append(id).append("\n");
        out.append("NAME=").append(name).append("\n");
        out.append("DESCRIPTION=").append(description).append("\n");
        out.append("DELETABLE=").append(modifiable).append("\n");
        out.append("PARENT=").append(parent).append("\n");
        out.append("CONTEXT=").append(context).append("\n");
        return out.toString();
    }
}

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

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavItemVisitor;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NavGroupImpl extends NavItemImpl implements NavGroup {

    List<NavItem> children = new ArrayList<>();

    public NavGroupImpl() {
        type = Type.GROUP;
    }

    @Override
    public List<NavItem> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<NavItem> children) {
        this.children = children;
    }

    @Override
    public void accept(NavItemVisitor visitor) {
        visitor.visitGroup(this);
        for (NavItem child : children) {
            child.accept(visitor);
        }
    }

    @Override
    public NavItem cloneItem() {
        NavGroupImpl clone = new NavGroupImpl();
        clone.id = this.id;
        clone.name = this.name;
        clone.parent = this.parent;
        clone.description = this.description;
        clone.modifiable = this.modifiable;
        clone.context = this.context;
        for (NavItem child : children) {
            NavItem childClone = child.cloneItem();
            clone.children.add(childClone);
            childClone.setParent(clone);
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(super.toString("GROUP"));
        out.append("CHILDREN=[");
        children.forEach(i -> out.append(i.getId()).append(" "));
        out.append("]\n");
        return out.toString();
    }
}

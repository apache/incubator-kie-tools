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

package org.uberfire.workbench.model.menu.impl;

import java.util.ArrayList;
import java.util.List;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;

import static org.uberfire.plugin.PluginUtil.ensureIterable;

@JsType
public class DefaultMenuGroup implements MenuGroup {

    private final List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();
    private final List menuItems;
    private final String contributionPoint;
    private final String caption;
    private final MenuPosition position;
    private final int order;
    private boolean isEnabled = true;
    private List<ResourceActionRef> resourceActionRefs;
    private List<String> permissionNames;

    @JsIgnore
    public DefaultMenuGroup(List<MenuItem> menuItems,
                            List<ResourceActionRef> resourceActionRefs,
                            List<String> permissionNames,
                            String contributionPoint,
                            String caption,
                            MenuPosition position,
                            int order) {
        this.menuItems = menuItems;
        this.resourceActionRefs = resourceActionRefs;
        this.permissionNames = permissionNames;
        this.contributionPoint = contributionPoint;
        this.caption = caption;
        this.position = position;
        this.order = order;
    }

    @JsIgnore
    @Override
    public List<MenuItem> getItems() {
        return menuItems;
    }

    @Override
    public String getContributionPoint() {
        return contributionPoint;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public MenuPosition getPosition() {
        return position;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.isEnabled = enabled;
        notifyListeners(enabled);
    }

    @JsIgnore
    @Override
    public void addEnabledStateChangeListener(final EnabledStateChangeListener listener) {
        enabledStateChangeListeners.add(listener);
    }

    @Override
    public void accept(MenuVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (MenuItem child : ensureIterable(getItems())) {
                child.accept(visitor);
            }
            visitor.visitLeave(this);
        }
    }

    private void notifyListeners(final boolean enabled) {
        for (final EnabledStateChangeListener listener : enabledStateChangeListeners) {
            listener.enabledStateChanged(enabled);
        }
    }

    @Override
    public String getIdentifier() {
        if (contributionPoint != null) {
            return getClass().getName() + "#" + contributionPoint + "#" + caption;
        }
        return getClass().getName() + "#" + caption;
    }

    @JsIgnore
    @Override
    public List<ResourceActionRef> getResourceActions() {
        return resourceActionRefs;
    }

    @JsIgnore
    @Override
    public List<String> getPermissions() {
        return permissionNames;
    }

    @JsIgnore
    @Override
    public List<Resource> getDependencies() {
        return menuItems;
    }
}
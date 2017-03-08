/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.workbench.model.menu;

import java.util.Collections;
import java.util.List;

import jsinterop.annotations.JsType;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.security.authz.RuntimeFeatureResource;

/**
 * Meta-data for a Workbench MenuItem including permissions. The default is that
 * all users have permission to access a MenuItem and that it is enabled.
 */
@JsType
public interface MenuItem extends RuntimeFeatureResource,
                                  HasEnabledStateChangeListeners {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getContributionPoint();

    String getCaption();

    MenuPosition getPosition();

    int getOrder();

    /**
     * Get the list of {@link ResourceActionRef} actions this menu item is
     * restricted to.
     * <p>
     * <p>
     * The menu item will be available provided all the given actions are
     * authorized within the current context.
     * </p>
     */
    default List<ResourceActionRef> getResourceActions() {
        return Collections.emptyList();
    }

    /**
     * Get the list of permission names this menu item is restricted to.
     * <p>
     * <p>
     * The menu item will be available provided all the given permissions are
     * authorized within the current context.
     * </p>
     */
    default List<String> getPermissions() {
        return Collections.emptyList();
    }

    /**
     * Causes the given {@link MenuVisitor} to visit this menu item and its
     * children.
     */
    void accept(MenuVisitor visitor);
}

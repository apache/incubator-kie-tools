/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.navigation.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * A widget that shows a tile representing a {@link NavItem}
 */
@Dependent
public class NavItemTileWidget implements IsElement {

    public interface View extends UberElement<NavItemTileWidget> {

        enum ItemType {
            GROUP,
            PERSPECTIVE,
            RUNTIME_PERSPECTIVE;
        }

        void show(String name, String descr, ItemType type);
    }

    View view;
    PerspectivePluginManager perspectivePluginManager;
    Command onClick = null;

    @Inject
    public NavItemTileWidget(View view, PerspectivePluginManager perspectivePluginManager) {
        this.view = view;
        this.perspectivePluginManager = perspectivePluginManager;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setOnClick(Command onClick) {
        this.onClick = onClick;
    }

    public void show(NavItem navItem) {
        String name = navItem.getName();
        String descr = navItem.getDescription();

        if (navItem instanceof NavGroup) {
            view.show(name, descr, View.ItemType.GROUP);
        }
        else {
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            String resourceId = navCtx.getResourceId();
            if (resourceId != null && ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {
                if (perspectivePluginManager.isRuntimePerspective(resourceId)) {
                    view.show(name, descr, View.ItemType.RUNTIME_PERSPECTIVE);
                } else {
                    view.show(name, descr, View.ItemType.PERSPECTIVE);
                }
            }
        }
    }

    // View callbacks

    void onClick() {
        if (onClick != null) {
            onClick.execute();
        }
    }
}
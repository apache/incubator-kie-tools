/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

@Dependent
public class NavTreeWidget extends TargetDivNavWidget {

    public interface View extends TargetDivNavWidget.View<NavTreeWidget> {

        void setLevel(int level);

        void addRuntimePerspective(String id, String name, String description, Command onClicked);

        void addPerspective(String id, String name, String description, Command onClicked);
    }

    View view;
    SyncBeanManager beanManager;

    @Inject
    public NavTreeWidget(View view,
                         SyncBeanManager beanManager,
                         PerspectivePluginManager pluginManager,
                         PlaceManager placeManager,
                         NavigationManager navigationManager) {
        super(view, pluginManager, placeManager, navigationManager);
        this.view = view;
        this.beanManager = beanManager;
    }

    @Override
    protected NavWidget lookupNavGroupWidget() {
        return beanManager.lookupBean(NavTreeWidget.class).newInstance();
    }

    @Override
    public void show(List<NavItem> itemList) {
        view.setLevel(getLevel());
        super.show(itemList);
    }

    @Override
    protected void showItem(NavItem navItem) {
        NavWorkbenchCtx ctx = NavWorkbenchCtx.get(navItem);
        if (pluginManager.isRuntimePerspective(ctx.getResourceId())) {
            view.addRuntimePerspective(navItem.getId(), navItem.getName(), navItem.getDescription(), () -> {
                onItemClicked(navItem);
            });
        } else {
            view.addPerspective(navItem.getId(), navItem.getName(), navItem.getDescription(), () -> {
                onItemClicked(navItem);
            });
        }
    }
}
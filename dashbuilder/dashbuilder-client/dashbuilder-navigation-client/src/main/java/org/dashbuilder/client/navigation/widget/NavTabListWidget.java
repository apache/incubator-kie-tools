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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavItem;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class NavTabListWidget extends TargetDivNavWidget {

    public interface View extends TargetDivNavWidget.View<NavTabListWidget> {

        void clearChildrenTabs();

        void showChildrenTabs(IsWidget tabListWidget);

        void showAsSubmenu(boolean enabled);
    }

    View view;
    SyncBeanManager beanManager;

    @Inject
    public NavTabListWidget(View view,
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
        return beanManager.lookupBean(NavTabListWidget.class).newInstance();
    }

    @Override
    public boolean setSelectedItem(String id) {
        boolean selected = super.setSelectedItem(id);
        if (selected && activeNavSubgroup != null) {
            view.setSelectedItem(activeNavSubgroup.getNavGroup().getId());
            view.showChildrenTabs(activeNavSubgroup);
        }
        return selected;
    }

    @Override
    public void show(List<NavItem> itemList) {
        view.showAsSubmenu(getLevel() > 0);
        super.show(itemList);
    }

    @Override
    public void onItemClicked(NavItem navItem) {
        view.clearChildrenTabs();
        super.onItemClicked(navItem);
    }

    // View callbacks

    void onGroupTabClicked(String id) {
        TargetDivNavWidget navGroupWidget = (TargetDivNavWidget) super.getSubgroupNavWidget(id);
        if (navGroupWidget != null) {
            super.onItemClicked(navGroupWidget.getNavGroup());
            view.showChildrenTabs(navGroupWidget);
            navGroupWidget.gotoDefaultItem();
        }
    }
}
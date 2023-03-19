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

package org.dashbuilder.client.navigation.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@Dependent
public class NavDropDownWidget extends BaseNavWidget {

    public interface View extends NavWidgetView<NavDropDownWidget> {

        void setDropDownName(String name);

        void showAsSubmenu(boolean enabled);

        void setActive(boolean active);
    }

    View view;
    SyncBeanManager beanManager;

    @Inject
    public NavDropDownWidget(View view, SyncBeanManager beanManager, NavigationManager navigationManager) {
        super(view, navigationManager);
        this.view = view;
        this.beanManager = beanManager;
    }

    @Override
    public NavWidget lookupNavGroupWidget() {
        return beanManager.lookupBean(NavDropDownWidget.class).newInstance();
    }

    @Override
    public void show(NavGroup navGroup) {
        if (navGroup == null) {
            view.errorNavGroupNotFound();
        } else {
            view.setDropDownName(navGroup.getName());
            view.showAsSubmenu(getLevel() > 1);
            super.show(navGroup);
        }
    }

    @Override
    public void onItemClicked(NavItem navItem) {
        super.onItemClicked(navItem);
        setActive(true);
    }

    @Override
    public void onSubGroupItemClicked(NavWidget subGroup) {
        super.onSubGroupItemClicked(subGroup);
        setActive(true);
    }

    @Override
    public boolean setSelectedItem(String id) {
        boolean enabled = super.setSelectedItem(id);
        setActive(enabled);
        return enabled;
    }

    @Override
    public void clearSelectedItem() {
        super.clearSelectedItem();
        setActive(false);
    }

    private void setActive(boolean enabled) {
        // Disable for subgroups, as it is not working well
        view.setActive(enabled && getLevel() == 1);
    }
}

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
package org.dashbuilder.client.navbar;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import org.dashbuilder.client.navigation.NavBarHelper;
import org.dashbuilder.client.navigation.NavTreeDefinitions;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Templated
@ApplicationScoped
public class AppHeader implements Header {

    private AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    @DataField
    HTMLDivElement header;

    @Inject
    private WorkbenchMegaMenuPresenter menuBarPresenter;

    @Inject
    UserMenu userMenu;

    @Inject
    private NavBarHelper navBarHelper;

    User user;
    Command onItemSelectedCommand;
    Command onLogoutCommand;
    NavTree navTree;
    String currentPerspectiveId;

    private Menus userMenus;

    public AppHeader() {}

    @Inject
    public AppHeader(User user) {
        this.user = user;
    }

    @PostConstruct
    private void init() {
        header.appendChild(Js.cast(menuBarPresenter.getView().getElement()));

        userMenu.clear();
        userMenu.addMenus(MenuFactory.newTopLevelMenu(i18n.logOut())
                                     .respondsWith(this::onLogoutClicked)
                                     .endMenu()
                                     .build());
        userMenus = MenuFactory.newTopLevelCustomMenu(userMenu).endMenu().build();
    }

    @Override
    public String getId() {
        return "AppHeader";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public void setOnLogoutCommand(Command command) {
        this.onLogoutCommand = command;
        menuBarPresenter.addMenus(userMenus);
    }

    public void onLogoutClicked() {
        if (onLogoutCommand != null) {
            onLogoutCommand.execute();
        }
    }

    public void setupMenu(NavTree navTree) {
        Menus menus = navBarHelper.buildMenusFromNavTree(navTree).build();
        menuBarPresenter.clear();
        menuBarPresenter.addMenus(menus);
        menuBarPresenter.addMenus(userMenus);
    }

}
/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.dashbuilder.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Templated
@ApplicationScoped
public class AppNavBar implements Header {

    @Inject
    @DataField
    HTMLDivElement header;

    @Inject
    WorkbenchMegaMenuPresenter menuBarPresenter;

    @Inject
    GoToDashboardMenuBuilder goToDashboardMenu;

    @Inject
    DashboardListMenuBuilder dashboardsListMenu;

    private boolean isDashboardListEnabled = false;

    private boolean isGoToDashboardMenuEnabled = false;

    @AfterInitialization
    public void setup() {
        setupMenus();
    }

    public void setupMenus(final Menus menus) {
        setupMenus();
        menuBarPresenter.addMenus(menus);
    }

    @Override
    public String getId() {
        return "AppNavBar";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    public void setDashboardListEnabled(boolean isDashboardListEnabled) {
        this.isDashboardListEnabled = isDashboardListEnabled;
    }

    public void setExternalMenuEnabled(boolean isExternalMenuEnabled) {
        this.isGoToDashboardMenuEnabled = isExternalMenuEnabled;
    }

    public void setupMenus() {
        menuBarPresenter.clear();
        menuBarPresenter.clearContextMenu();
        if (isGoToDashboardMenuEnabled) {
            menuBarPresenter.addMenus(MenuFactory.newTopLevelCustomMenu(goToDashboardMenu).endMenu().build());
        }
        if (isDashboardListEnabled) {
            menuBarPresenter.addMenus(MenuFactory.newTopLevelCustomMenu(dashboardsListMenu).endMenu().build());
        }

        header.innerHTML = "";
        header.appendChild(Js.cast(menuBarPresenter.getView().getElement()));
    }

    public void setDisplayMainMenu(boolean display) {
        HTMLElement menuElement = Js.cast(
                                          DomGlobal.document.querySelector("#mega-menu").querySelector("li.dropdown.uf-yamm--fw"));
        menuElement.style.display = display ? "block" : "none";
    }
}
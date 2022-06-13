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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.HeaderPanel;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.client.RuntimeClientLoader;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.WorkbenchLayout;
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
    WorkbenchLayout wbLayout;

    @Inject
    WorkbenchMegaMenuPresenter menuBarPresenter;

    @Inject
    GoToDashboardMenuBuilder goToDashboardMenu;

    @Inject
    DashboardListMenuBuilder dashboardsListMenu;

    @Inject
    RuntimeClientLoader loader;

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
        if (loader.isHideNavBar()) {
            return;
        }
        menuBarPresenter.clear();
        menuBarPresenter.clearContextMenu();
        if (isGoToDashboardMenuEnabled && !loader.isEditor()) {
            menuBarPresenter.addMenus(MenuFactory.newTopLevelCustomMenu(goToDashboardMenu).endMenu().build());
        }
        if (isDashboardListEnabled) {
            menuBarPresenter.addMenus(MenuFactory.newTopLevelCustomMenu(dashboardsListMenu).endMenu().build());
        }
        header.innerHTML = "";
        header.appendChild(Js.cast(menuBarPresenter.getView().getElement()));
    }

    public void setDisplayMainMenu(boolean display) {
        var menu = DomGlobal.document.querySelector("#mega-menu");
        if (menu != null) {
            HTMLElement menuElement = Js.cast(menu.querySelector("li.dropdown.uf-yamm--fw"));
            menuElement.style.display = display ? "block" : "none";
        }
    }

    public void setHide(boolean hide) {
        var _hide = loader.isHideNavBar() || hide;
        var header = (HeaderPanel) wbLayout.getRoot();
        var headerParent =
                header.getHeaderWidget()
                        .asWidget()
                        .getElement()
                        .getParentElement();
        headerParent.getStyle()
                .setDisplay(_hide ? Display.NONE : Display.BLOCK);
        // workaround for header still showing a white space
        headerParent.getStyle()
                .setProperty("min-height", _hide ? "0px" : "20px");
    }

}

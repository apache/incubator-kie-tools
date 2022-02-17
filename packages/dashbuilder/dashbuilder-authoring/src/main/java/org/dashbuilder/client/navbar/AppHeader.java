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
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.workbench.model.menu.Menus;

@Templated
@ApplicationScoped
public class AppHeader implements Header {

    @Inject
    @DataField
    HTMLDivElement header;

    @Inject
    private WorkbenchMegaMenuPresenter menuBarPresenter;

    @Inject
    private NavBarHelper navBarHelper;

    public AppHeader() {}

    @PostConstruct
    private void init() {
        header.appendChild(Js.cast(menuBarPresenter.getView().getElement()));
    }

    @Override
    public String getId() {
        return "AppHeader";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public void setupMenu(NavTree navTree) {
        Menus menus = navBarHelper.buildMenusFromNavTree(navTree).build();
        menuBarPresenter.clear();
        menuBarPresenter.addMenus(menus);
    }

}
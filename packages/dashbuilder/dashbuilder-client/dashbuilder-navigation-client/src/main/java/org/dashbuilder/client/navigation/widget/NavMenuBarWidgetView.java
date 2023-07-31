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
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import jsinterop.base.Js;
import org.dashbuilder.patternfly.alert.Alert;
import org.dashbuilder.patternfly.menu.MenuItem;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class NavMenuBarWidgetView extends TargetDivNavWidgetView<NavMenuBarWidget>
                                  implements NavMenuBarWidget.View {

    @Inject
    @DataField
    HTMLDivElement mainDiv;

    @Inject
    @DataField
    HTMLUListElement navBar;

    @Inject
    @DataField
    @Named("nav")
    HTMLElement nav;

    @Inject
    Elemental2DomUtil domUtil;

    @Inject
    protected SyncBeanManager beanManager;

    NavMenuBarWidget presenter;

    @Inject
    public NavMenuBarWidgetView(Alert alertBox) {
        super(alertBox);
    }

    @Override
    public void init(NavMenuBarWidget presenter) {
        this.presenter = presenter;
        super.navWidget = Js.cast(navBar);
        setNavHeaderVisible(true);
    }

    @Override
    public void addDivider() {
        // Useless in a menu bar
    }

    @Override
    public void clearItems() {
        super.clearItems();
        domUtil.removeAllElementChildren(mainDiv);
        mainDiv.appendChild(navBar.parentElement);
    }

    @Override
    public void error(String message) {
        domUtil.removeAllElementChildren(mainDiv);
        alertBox.setMessage(message);
        mainDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public void addItem(String id, String name, String description, Command onItemSelected) {
        var menuItem = beanManager.lookupBean(MenuItem.class).newInstance();
        menuItem.setText(name);
        if (navBar.childElementCount == 0) { 
            selectItem(navBar, menuItem.getElement());
        }
        menuItem.setOnSelect(() -> {
            selectItem(navBar, menuItem.getElement());
            onItemSelected.execute();
        });
        navBar.appendChild(menuItem.getElement());
        super.itemMap.put(id, Js.cast(menuItem.getElement()));
    }

    @Override
    public void setNavHeaderVisible(boolean visible) {
        // TODO: Is this still necessary?        
        //nav.setClassName(visible ? "navbar navbar-default navbar-pf" : "");
    }

}

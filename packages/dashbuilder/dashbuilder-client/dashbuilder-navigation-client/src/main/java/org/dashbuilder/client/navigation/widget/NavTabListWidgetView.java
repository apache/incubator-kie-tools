/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.    private static final String SELECTED_CLASS = "pf-m-current";
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import jsinterop.base.Js;
import org.dashbuilder.patternfly.alert.Alert;
import org.dashbuilder.patternfly.tab.Tab;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class NavTabListWidgetView extends TargetDivNavWidgetView<NavTabListWidget>
                                  implements NavTabListWidget.View {

    @Inject
    @DataField
    HTMLDivElement mainDiv;

    @Inject
    @DataField
    HTMLDivElement tabsDiv;

    @Inject
    @DataField
    HTMLUListElement tabList;

    @Inject
    @DataField
    HTMLDivElement childrenDiv;

    @Inject
    Elemental2DomUtil domUtil;

    @Inject
    protected SyncBeanManager beanManager;

    Map<String, Tab> tabsCache;

    NavTabListWidget presenter;

    @Inject
    public NavTabListWidgetView(Alert alertBox) {
        super(alertBox);
        tabsCache = new HashMap<>();
    }

    @Override
    public void init(NavTabListWidget presenter) {
        this.presenter = presenter;
        super.navWidget = Js.cast(tabList);
    }

    @Override
    public void addDivider() {
        // Useless in a tab list
    }

    @Override
    public void addGroupItem(String id, String name, String description, HTMLElement el) {
        this.addItem(id, name, description, () -> presenter.onGroupTabClicked(id));
    }

    @Override
    public void addItem(String id, String name, String description, Command onItemSelected) {
        var tab = produceTab(id);
        var element = tab.getElement();
        if (tabList.childElementCount == 0) {
            selectItem(tabList, tab.getElement());
        }
        tab.setTitle(name);
        tab.setOnSelect(() -> {
            selectItem(tabList, tab.getElement());
            onItemSelected.execute();
        });

        tabList.appendChild(element);

        super.itemMap.put("id", Js.cast(element));
    }

    private Tab produceTab(String id) {
        return tabsCache.compute(id, (key, value) -> {
            if (value != null) {
                IOC.getBeanManager().destroyBean(value);
            }
            return beanManager.lookupBean(Tab.class).newInstance();
        });
    }

    @Override
    public void showAsSubmenu(boolean enabled) {
        tabsDiv.classList.add("pf-m-secondary");
    }

    @Override
    public void clearChildrenTabs() {
        domUtil.removeAllElementChildren(childrenDiv);
    }

    @Override
    public void showChildrenTabs(HTMLElement tabListWidget) {
        domUtil.removeAllElementChildren(childrenDiv);
        childrenDiv.appendChild(tabListWidget);
        if (presenter.getLevel() == 0) {
            childrenDiv.style.setProperty("margin-left", "15px");
        }
    }

    @Override
    public void clearItems() {
        super.clearItems();
        domUtil.removeAllElementChildren(mainDiv);
        mainDiv.appendChild(tabsDiv);
    }

    @Override
    public void error(String message) {
        domUtil.removeAllElementChildren(mainDiv);
        alertBox.setMessage(message);
        mainDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public HTMLElement getElement() {
        return mainDiv;
    }

    @PreDestroy
    void destroy() {
        tabsCache.values().forEach(IOC.getBeanManager()::destroyBean);
    }

}

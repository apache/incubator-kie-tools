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

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.common.client.widgets.AlertBox;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NavTabListWidgetView extends TargetDivNavWidgetView<NavTabListWidget>
    implements NavTabListWidget.View {

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    Div tabsDiv;

    @Inject
    @DataField
    UnorderedList tabList;

    @Inject
    @DataField
    Div childrenDiv;

    NavTabListWidget presenter;

    @Inject
    public NavTabListWidgetView(AlertBox alertBox) {
        super(alertBox);
    }

    @Override
    public void init(NavTabListWidget presenter) {
        this.presenter = presenter;
        super.navWidget = tabList;
    }

    @Override
    public void addDivider() {
        // Useless in a tab list
    }

    @Override
    public void addGroupItem(String id, String name, String description, IsWidget widget) {
        this.addItem(id, name, description, () -> presenter.onGroupTabClicked(id));
    }

    @Override
    public void showAsSubmenu(boolean enabled) {
        tabList.setClassName("nav nav-tabs" + (enabled ? " nav-tabs-pf" : ""));
    }

    @Override
    public void clearChildrenTabs() {
        DOMUtil.removeAllChildren(childrenDiv);
    }

    @Override
    public void showChildrenTabs(IsWidget tabListWidget) {
        DOMUtil.removeAllChildren(childrenDiv);
        super.appendWidgetToElement(childrenDiv, tabListWidget);
        if (presenter.getLevel() == 0) {
            childrenDiv.getStyle().setProperty("margin-left", "15px");
        }
    }

    @Override
    public void clearItems() {
        super.clearItems();
        DOMUtil.removeAllChildren(mainDiv);
        mainDiv.appendChild(tabsDiv);
    }

    @Override
    public void error(String message) {
        DOMUtil.removeAllChildren(mainDiv);
        alertBox.setMessage(message);
        mainDiv.appendChild(alertBox.getElement());
    }
}

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

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class NavItemTileWidgetView implements NavItemTileWidget.View {

    private static final String RUNTIME_ICON = "fas fa-tachometer-alt";

    private static final String PERSPECTIVE_ICON = "pficon-screen";

    private static final String GROUP_ICON = "fas fa-folder-open";

    @Inject
    @DataField
    HTMLDivElement mainDiv;

    @Inject
    @DataField
    HTMLDivElement iconDiv;

    @Inject
    @DataField
    @Named("span")
    HTMLElement iconSpan;

    @Inject
    @DataField
    HTMLDivElement textSpan;

    @Override
    public void init(NavItemTileWidget presenter) {
        mainDiv.onclick = e -> {
            presenter.onClick();
            return null;
        };
    }

    @Override
    public void show(String name, String descr, ItemType type) {
        textSpan.textContent = name;
        mainDiv.style.setProperty("title", descr);

        if (ItemType.GROUP == type) {
            iconSpan.className = GROUP_ICON;
            mainDiv.title = NavigationConstants.INSTANCE.openNavItem(name);
        } else if (ItemType.PERSPECTIVE == type) {
            iconSpan.className = PERSPECTIVE_ICON;
            mainDiv.title = NavigationConstants.INSTANCE.gotoNavItem(name);
        } else if (ItemType.RUNTIME_PERSPECTIVE == type) {
            iconSpan.className = RUNTIME_ICON;
            mainDiv.title = NavigationConstants.INSTANCE.showNavItem(name);
        }
    }

    @Override
    public HTMLElement getElement() {
        return mainDiv;
    }
}

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

import com.google.gwt.event.dom.client.ClickEvent;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class NavItemTileWidgetView implements NavItemTileWidget.View, IsElement {

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    Div iconDiv;

    @Inject
    @DataField
    Span iconSpan;

    @Inject
    @DataField
    Span textSpan;

    NavItemTileWidget presenter;

    @Override
    public void init(NavItemTileWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(String name, String descr, ItemType type) {
        textSpan.setTextContent(name);
        mainDiv.getStyle().setProperty("title", descr);

        if (ItemType.GROUP == type) {
            mainDiv.setClassName("uf-navitem-tile-body uf-navitem-tile-group");
            iconSpan.setClassName("pficon-folder-open");
            mainDiv.setTitle(NavigationConstants.INSTANCE.openNavItem(name));
        }
        else if (ItemType.PERSPECTIVE == type) {
            mainDiv.setClassName("uf-navitem-tile-body uf-navitem-tile-perspective");
            iconSpan.setClassName("pficon-screen");
            mainDiv.setTitle(NavigationConstants.INSTANCE.gotoNavItem(name));
        }
        else if (ItemType.RUNTIME_PERSPECTIVE == type) {
            mainDiv.setClassName("uf-navitem-tile-body uf-navitem-tile-runtime-perspective");
            iconSpan.setClassName("pficon-virtual-machine");
            mainDiv.setTitle(NavigationConstants.INSTANCE.showNavItem(name));
        }
    }

    @EventHandler("mainDiv")
    public void onMainDivClick(ClickEvent event) {
        presenter.onClick();
    }
}

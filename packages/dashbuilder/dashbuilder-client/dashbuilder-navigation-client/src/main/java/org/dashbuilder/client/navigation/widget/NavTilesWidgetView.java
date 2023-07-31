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
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOListElement;
import jsinterop.base.Js;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.patternfly.alert.Alert;
import org.dashbuilder.patternfly.alert.AlertType;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class NavTilesWidgetView extends BaseNavWidgetView<NavTilesWidget>
                                implements NavTilesWidget.View {

    private static final String CURRENT_ITEM_CLASS = "pf-m-current";

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    Div contentDiv;

    @Inject
    @DataField
    HTMLDivElement tilesDiv;

    @Inject
    @DataField
    HTMLOListElement breadcrumb;

    @Inject
    Elemental2DomUtil domUtil;

    NavTilesWidget presenter;
    Alert alertBox;

    @Inject
    public NavTilesWidgetView(Alert alertBox) {
        this.alertBox = alertBox;
        alertBox.setType(AlertType.WARNING);
        alertBox.getElement().style.setProperty("width", "96%");
    }

    @Override
    public void init(NavTilesWidget presenter) {
        this.presenter = presenter;
        super.navWidget = Js.cast(tilesDiv);
    }

    @Override
    public void addDivider() {
        // Useless in tile navigator
    }

    @Override
    public void addTileWidget(HTMLElement tileWidget) {
        DOMUtil.removeAllChildren(mainDiv);
        mainDiv.appendChild(contentDiv);
        tilesDiv.appendChild(Js.cast(tileWidget));
    }

    @Override
    public void showTileContent(IsWidget tileContent) {
        domUtil.removeAllElementChildren(tilesDiv);
        super.appendWidgetToElement(Js.cast(tilesDiv), tileContent);
    }

    @Override
    public void errorNavItemsEmpty() {
        DOMUtil.removeAllChildren(mainDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navGroupEmptyError());
        mainDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public void errorNavGroupNotFound() {
        DOMUtil.removeAllChildren(mainDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navGroupNotFound());
        mainDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public void infiniteRecursionError(String cause) {
        domUtil.removeAllElementChildren(tilesDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navTilesDragComponentInfiniteRecursion() + cause);
        tilesDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public void clearBreadcrumb() {
        domUtil.removeAllElementChildren(breadcrumb);
    }

    @Override
    public void addBreadcrumbItem(String navItemName) {
        addBreadcrumbItem(navItemName, null);
    }

    @Override
    public void addBreadcrumbItem(String navItemName, Command onClicked) {
        var li = DomGlobal.document.createElement("li");
        var divider = createBreadcrumbDivider();
        li.appendChild(divider);

        var anchor = (HTMLAnchorElement) DomGlobal.document.createElement("a");
        anchor.textContent = navItemName;
        anchor.href = "#";
        anchor.className = "pf-v5-c-breadcrumb__link";
        li.appendChild(anchor);
        li.className = "pf-v5-c-breadcrumb__item";

        anchor.onclick = e -> {
            onClicked.execute();
            return null;
        };
        // add here the divider
        if (onClicked == null) {
            breadcrumb.querySelectorAll("li > a").forEach((v, i, list) -> {
                v.classList.remove(CURRENT_ITEM_CLASS);
                return null; 
            });
            anchor.classList.add(CURRENT_ITEM_CLASS);
        }
        breadcrumb.appendChild(li);        
    }

    Element createBreadcrumbDivider() {
        var span = DomGlobal.document.createElement("span");
        var i = DomGlobal.document.createElement("i");
        span.className = "pf-v5-c-breadcrumb__item-divider";
        i.className = "fas fa-angle-right";
        span.appendChild(i);
        return span;
    }

}

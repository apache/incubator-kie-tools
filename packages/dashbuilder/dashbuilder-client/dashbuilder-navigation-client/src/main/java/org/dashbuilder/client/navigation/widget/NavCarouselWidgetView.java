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

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.patternfly.alert.Alert;
import org.dashbuilder.patternfly.alert.AlertType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NavCarouselWidgetView extends BaseNavWidgetView<NavCarouselWidget>
                                   implements NavCarouselWidget.View {

    @Inject
    @DataField
    HTMLDivElement mainDiv;

    @Inject
    @DataField
    HTMLDivElement carouselDiv;

    @Inject
    @DataField
    HTMLDivElement slidesDiv;

    NavCarouselWidget presenter;
    Alert alertBox;

    @Inject
    public NavCarouselWidgetView(Alert alertBox) {
        this.alertBox = alertBox;
        alertBox.setType(AlertType.WARNING);
        alertBox.getElement().style.setProperty("width", "96%");
    }

    @Override
    public void init(NavCarouselWidget presenter) {
        this.presenter = presenter;
        super.navWidget = slidesDiv;
    }

    @Override
    public void addDivider() {
        // Useless in a tab list
    }

    @Override
    public void addContentSlide(HTMLElement widget) {
        domUtil.removeAllElementChildren(mainDiv);
        mainDiv.appendChild(carouselDiv);

        var div = DomGlobal.document.createElement("div");
        div.className = (slidesDiv.childElementCount == 0 ? "item active" : "item");
        div.appendChild(Js.cast(widget));
        slidesDiv.appendChild(div);
    }

    @Override
    public void errorNavGroupNotFound() {
        domUtil.removeAllElementChildren(mainDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navGroupNotFound());
        mainDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public void errorNavItemsEmpty() {
        domUtil.removeAllElementChildren(mainDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navCarouselDragComponentEmptyError());
        mainDiv.appendChild(Js.cast(alertBox.getElement()));
    }

    @Override
    public void infiniteRecursionError(String cause) {
        var div = DomGlobal.document.createElement("div");
        div.className = (slidesDiv.childElementCount == 0 ? "item active" : "item");
        alertBox.setMessage(NavigationConstants.INSTANCE.navCarouselDragComponentInfiniteRecursion() + " " + cause);
        div.appendChild(Js.cast(alertBox.getElement()));
        slidesDiv.appendChild(div);
    }

    @Override
    public HTMLElement getElement() {
        return Js.cast(mainDiv);
    }
}

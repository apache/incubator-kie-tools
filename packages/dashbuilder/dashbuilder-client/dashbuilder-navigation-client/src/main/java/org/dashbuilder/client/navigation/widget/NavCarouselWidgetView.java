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
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.common.client.widgets.AlertBox;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NavCarouselWidgetView extends BaseNavWidgetView<NavCarouselWidget>
    implements NavCarouselWidget.View {

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    Div carouselDiv;

    @Inject
    @DataField
    Div slidesDiv;

    NavCarouselWidget presenter;
    AlertBox alertBox;

    @Inject
    public NavCarouselWidgetView(AlertBox alertBox) {
        this.alertBox = alertBox;
        alertBox.setLevel(AlertBox.Level.WARNING);
        alertBox.setCloseEnabled(false);
        alertBox.getElement().getStyle().setProperty("width", "96%");
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
    public void addContentSlide(IsWidget widget) {
        DOMUtil.removeAllChildren(mainDiv);
        mainDiv.appendChild(carouselDiv);

        Div div = (Div) Window.getDocument().createElement("div");
        div.setClassName(slidesDiv.getChildNodes().getLength() == 0 ? "item active" : "item");
        super.appendWidgetToElement(div, widget);
        slidesDiv.appendChild(div);
    }

    @Override
    public void errorNavGroupNotFound() {
        DOMUtil.removeAllChildren(mainDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navGroupNotFound());
        mainDiv.appendChild(alertBox.getElement());
    }

    @Override
    public void errorNavItemsEmpty() {
        DOMUtil.removeAllChildren(mainDiv);
        alertBox.setMessage(NavigationConstants.INSTANCE.navCarouselDragComponentEmptyError());
        mainDiv.appendChild(alertBox.getElement());
    }

    @Override
    public void infiniteRecursionError(String cause) {
        Div div = (Div) Window.getDocument().createElement("div");
        div.setClassName(slidesDiv.getChildNodes().getLength() == 0 ? "item active" : "item");
        alertBox.setMessage(NavigationConstants.INSTANCE.navCarouselDragComponentInfiniteRecursion() + " " + cause);
        div.appendChild(alertBox.getElement());
        slidesDiv.appendChild(div);
    }
}

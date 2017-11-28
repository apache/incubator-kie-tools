/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.cms.widget;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.common.client.widgets.AlertBox;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public class PerspectiveWidgetView implements PerspectiveWidget.View, IsWidget {

    FlowPanel mainPanel = new FlowPanel();
    PerspectiveWidget presenter;
    AlertBox alertBox;

    @Inject
    public PerspectiveWidgetView(AlertBox alertBox) {
        this.alertBox = alertBox;
        alertBox.setLevel(AlertBox.Level.WARNING);
        alertBox.setCloseEnabled(false);
        CSSStyleDeclaration style = alertBox.getElement().getStyle();
        style.setProperty("width", "30%");
        style.setProperty("margin", "10px");
    }

    @Override
    public void init(PerspectiveWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }

    @Override
    public void showContent(IsWidget widget) {
        mainPanel.clear();
        mainPanel.add(widget);
    }

    @Override
    public void notFoundError() {
        alertBox.setMessage(ContentManagerConstants.INSTANCE.perspectiveDragNotFoundError());
        mainPanel.clear();
        mainPanel.add(ElementWrapperWidget.getWidget(alertBox.getElement()));
    }

    @Override
    public void infiniteRecursionError() {
        alertBox.setMessage(ContentManagerConstants.INSTANCE.perspectiveInfiniteRecursionError());
        mainPanel.clear();
        mainPanel.add(ElementWrapperWidget.getWidget(alertBox.getElement()));
    }
}

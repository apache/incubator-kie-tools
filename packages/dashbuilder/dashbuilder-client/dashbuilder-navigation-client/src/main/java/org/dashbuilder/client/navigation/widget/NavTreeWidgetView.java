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
package org.dashbuilder.client.navigation.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.common.client.widgets.AlertBox;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class NavTreeWidgetView extends TargetDivNavWidgetView<NavTreeWidget>
    implements NavTreeWidget.View {

    @Inject
    @DataField
    Div mainDiv;

    NavTreeWidget presenter;

    @Inject
    public NavTreeWidgetView(AlertBox alertBox) {
        super(alertBox);
    }

    @Override
    public void init(NavTreeWidget presenter) {
        this.presenter = presenter;
        super.navWidget = mainDiv;
    }

    @Override
    public void addDivider() {
        // Useless in a tree
    }

    @Override
    public void setLevel(int level) {
        int margin = level*10;
        mainDiv.getStyle().setProperty("margin-left", margin + "px");
        if (level == 0) {
            mainDiv.getStyle().setProperty("padding", "10px");
        }
    }

    @Override
    public void addRuntimePerspective(String id, String name, String description, Command onClicked) {
        this.addItem("pficon-virtual-machine", id, name, description, onClicked);
    }

    @Override
    public void addPerspective(String id, String name, String description, Command onClicked) {
        this.addItem("pficon-screen", id, name, description, onClicked);
    }

    @Override
    public void addGroupItem(String id, String name, String description, IsWidget widget) {
        this.addItem("pficon-folder-open",id, name, description, null);
        super.addGroupItem(id, name, description, widget);
    }

    @Override
    protected void setSelectedEnabled(boolean enabled) {
        String cname = selectedItem.getClassName();
        if (!cname.equals("uf-navtree-widget-non-clickable")) {
            if (enabled) {
                selectedItem.setClassName("uf-navtree-widget-clicked");
            } else {
                selectedItem.setClassName("uf-navtree-widget-non-clicked");
            }
        }
    }

    protected void addItem(String iconClass, String id, String name, String description, Command onClicked) {
        Element nameEl = onClicked != null ? Document.get().createAnchorElement() : Document.get().createSpanElement();
        nameEl.setInnerText(name);
        nameEl.setClassName(onClicked != null ? "uf-navtree-widget-non-clicked" : "uf-navtree-widget-non-clickable");
        if (description != null && !description.equals(name)) {
            nameEl.setTitle(description);
        }

        SpanElement iconSpan = Document.get().createSpanElement();
        iconSpan.setClassName("uf-navtree-widget-icon " + iconClass);

        DivElement div = Document.get().createDivElement();
        div.appendChild(iconSpan);
        div.appendChild(nameEl);

        navWidget.appendChild((Node) div);
        itemMap.put(id, nameEl);

        if (onClicked != null) {
            Event.sinkEvents(nameEl, Event.ONCLICK);
            Event.setEventListener(nameEl, event -> {
                if (Event.ONCLICK == event.getTypeInt()) {
                    onClicked.execute();
                }
            });
        }
    }
}

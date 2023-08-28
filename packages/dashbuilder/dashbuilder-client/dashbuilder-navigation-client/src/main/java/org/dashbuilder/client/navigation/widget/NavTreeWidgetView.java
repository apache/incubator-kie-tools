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

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.patternfly.alert.Alert;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class NavTreeWidgetView extends TargetDivNavWidgetView<NavTreeWidget>
                               implements NavTreeWidget.View {

    private static final String RUNTIME_ICON = "fas fa-tachometer-alt";

    private static final String PERSPECTIVE_ICON = "pficon-screen";

    private static final String GROUP_ICON = "fas fa-folder-open";

    @Inject
    @DataField
    HTMLDivElement mainDiv;

    NavTreeWidget presenter;

    @Inject
    public NavTreeWidgetView(Alert alertBox) {
        super(alertBox);
    }

    @Override
    public void init(NavTreeWidget presenter) {
        this.presenter = presenter;
        super.navWidget = Js.cast(mainDiv);
    }

    @Override
    public void addDivider() {
        // Useless in a tree
    }

    @Override
    public void setLevel(int level) {
        int margin = level * 10;
        mainDiv.style.setProperty("margin-left", margin + "px");
        if (level == 0) {
            mainDiv.style.setProperty("padding", "10px");
        }
    }

    @Override
    public void addRuntimePerspective(String id, String name, String description, Command onClicked) {
        this.addItem(RUNTIME_ICON, id, name, description, onClicked);
    }

    @Override
    public void addPerspective(String id, String name, String description, Command onClicked) {
        this.addItem(PERSPECTIVE_ICON, id, name, description, onClicked);
    }

    @Override
    public void addGroupItem(String id, String name, String description, HTMLElement widget) {
        this.addItem(GROUP_ICON, id, name, description, null);
        super.addGroupItem(id, name, description, widget);
    }

    @Override
    protected void setSelectedEnabled(boolean enabled) {
        String cname = selectedItem.className.toString();
        if (!cname.equals("uf-navtree-widget-non-clickable")) {
            if (enabled) {
                selectedItem.className = "uf-navtree-widget-clicked";
            } else {
                selectedItem.className = "uf-navtree-widget-non-clicked";
            }
        }
    }

    protected void addItem(String iconClass, String id, String name, String description, Command onClicked) {
        var elName = onClicked != null ? "a" : "span";
        var nameEl = (HTMLElement) DomGlobal.document.createElement(elName);
        nameEl.textContent = (name);
        nameEl.className = onClicked != null ? "uf-navtree-widget-non-clicked" : "uf-navtree-widget-non-clickable";
        if (description != null && !description.equals(name)) {
            nameEl.title = (description);
        }

        var iconSpan = DomGlobal.document.createElement("span");
        iconSpan.className = ("uf-navtree-widget-icon " + iconClass);

        var div = DomGlobal.document.createElement("div");
        div.appendChild(iconSpan);
        div.appendChild(nameEl);

        navWidget.appendChild(div);
        itemMap.put(id, nameEl);

        if (onClicked != null) {
            nameEl.onclick = e -> {                
                onClicked.execute();
                return null;
            };
        }
    }

    @Override
    public HTMLElement getElement() {
        return mainDiv;
    }
}

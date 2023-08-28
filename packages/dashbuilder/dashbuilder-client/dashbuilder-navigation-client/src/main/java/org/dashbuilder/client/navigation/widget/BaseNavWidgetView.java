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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.uberfire.mvp.Command;

public abstract class BaseNavWidgetView<T> implements NavWidgetView<T> {

    protected Node navWidget = null;
    protected Element selectedItem = null;
    protected Map<String, Element> itemMap = new HashMap<>();
    protected Set<IsWidget> widgetSet = new HashSet<>();
    @Inject
    Elemental2DomUtil domUtil;

    protected void appendWidgetToElement(HTMLElement element, IsWidget widget) {
        domUtil.appendWidgetToElement(element, widget.asWidget());
        widgetSet.add(widget);
    }

    @Override
    public void clearItems() {
        domUtil.removeAllElementChildren(navWidget);

        widgetSet.forEach(w -> w.asWidget().getElement().removeFromParent());
    }

    @Override
    public void addGroupItem(String id, String name, String description, elemental2.dom.HTMLElement el) {
        navWidget.appendChild((Node) el);
    }

    @Override
    public void addItem(String id, String name, String description, Command onItemSelected) {
        var anchor = (HTMLAnchorElement) DomGlobal.document.createElement("a");
        anchor.textContent = name;
        if (description != null && !description.equals(name)) {
            anchor.title = description;
        }

        var li = (HTMLLIElement) DomGlobal.document.createElement("li");
        li.style.cursor = "pointer";
        li.appendChild(anchor);
        navWidget.appendChild(li);
        itemMap.put(id, li);

        anchor.onclick = e -> {
            onItemSelected.execute();
            return null;
        };

    }

    @Override
    public void setSelectedItem(String id) {
        Element el = itemMap.get(id);
        if (el != null) {
            clearSelectedItem();
            selectedItem = el;
            setSelectedEnabled(true);
        }
    }

    @Override
    public void clearSelectedItem() {
        if (selectedItem != null) {
            setSelectedEnabled(false);
            selectedItem = null;
        }
    }

    protected void setSelectedEnabled(boolean enabled) {
        if (enabled) {
            selectedItem.classList.add("active");
        } else {
            selectedItem.classList.remove("active");
        }
    }

}

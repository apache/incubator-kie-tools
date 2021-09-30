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

package org.uberfire.ext.layout.editor.client.widgets;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;

@Dependent
@Templated
public class LayoutDragComponentGroupView implements UberElement<LayoutDragComponentGroupPresenter>,
                                                     LayoutDragComponentGroupPresenter.View,
                                                     IsElement {

    @Inject
    @DataField("group-title")
    Anchor groupTitle;

    @Inject
    @DataField
    Div components;

    @Inject
    @DataField
    Div collapse;

    @Inject
    ManagedInstance<LayoutDragComponentWidget> layoutDragComponentWidgets;

    private LayoutDragComponentGroupPresenter presenter;

    private Map<String, LayoutDragComponentWidget> componentsMap = new HashMap<>();

    @Override
    public void init(LayoutDragComponentGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTitle(String id,
                         String name) {
        groupTitle.setTextContent(name);
        collapse.setId(id);
        groupTitle.setAttribute("href",
                                "#" + id);
    }

    @Override
    public void addComponents(Map<String, LayoutDragComponent> dragComponents) {
        for (String key : dragComponents.keySet()) {
            LayoutDragComponentWidget widget = createDragComponentWidget(dragComponents.get(key));
            componentsMap.put(key,
                              widget);
            components.appendChild(widget.getElement());
        }
    }

    @Override
    public void addComponent(String componentId,
                             LayoutDragComponent component) {
        LayoutDragComponentWidget widget = createDragComponentWidget(component);
        componentsMap.put(componentId,
                          widget);
        components.appendChild(widget.getElement());
    }

    @Override
    public void removeComponent(String componentId) {
        if (hasComponent(componentId)) {
            LayoutDragComponentWidget layoutDragComponentWidget = componentsMap.get(componentId);
            components.removeChild(layoutDragComponentWidget.getElement());
            componentsMap.remove(componentId);
        }
    }

    @Override
    public boolean hasComponent(String componentId) {
        return componentsMap.containsKey(componentId);
    }

    @Override
    public void setComponentVisible(String componentId, boolean visible) {
        LayoutDragComponentWidget widget = componentsMap.get(componentId);
        if (widget != null) {
            widget.getElement().setHidden(!visible);
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        groupTitle.setClassName(expanded ? "" : "collapsed");
        groupTitle.setAttribute("aria-expanded", Boolean.toString(expanded));
        collapse.setClassName("panel-collapse collapse"  + (expanded ? " in" : ""));
    }

    private LayoutDragComponentWidget createDragComponentWidget(LayoutDragComponent dragComponent) {
        LayoutDragComponentWidget widget = layoutDragComponentWidgets.get();
        widget.init(dragComponent);
        return widget;
    }
}
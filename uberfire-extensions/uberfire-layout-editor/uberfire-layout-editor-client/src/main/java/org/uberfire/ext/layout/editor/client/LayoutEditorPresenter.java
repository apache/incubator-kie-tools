/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class LayoutEditorPresenter {

    private final View view;
    private LayoutTemplate.Style pageStyle = LayoutTemplate.Style.FLUID;
    protected Map<String, LayoutDragComponentGroupPresenter> layoutDragComponentGroups = new HashMap<>();
    ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance;
    @Inject
    private Container container;
    @Inject
    private Event<NotificationEvent> ufNotification;

    @Inject
    public LayoutEditorPresenter(final View view,
                                 Container container,
                                 ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance) {
        this.view = view;
        this.container = container;
        this.layoutDragComponentGroupInstance = layoutDragComponentGroupInstance;
        view.init(this);
    }

    @PostConstruct
    public void initNew() {
        view.setupContainer(container.getView());
    }

    public void clear() {
        List<String> groupNames = new ArrayList<>(layoutDragComponentGroups.keySet());
        groupNames.forEach(groupName -> removeDraggableGroup(groupName));
        container.reset();
    }

    public UberElement<LayoutEditorPresenter> getView() {
        return view;
    }

    public LayoutTemplate getLayout() {
        return container.toLayoutTemplate();
    }

    public void loadLayout(LayoutTemplate layoutTemplate,
                           String emptyTitleText,
                           String emptySubTitleText) {
        container.load(layoutTemplate,
                       emptyTitleText,
                       emptySubTitleText);
    }

    public void loadEmptyLayout(String layoutName,
                                String emptyTitleText,
                                String emptySubTitleText) {
        container.loadEmptyLayout(layoutName,
                                  pageStyle,
                                  emptyTitleText,
                                  emptySubTitleText);
    }

    public void addLayoutProperty(String key,
                                  String value) {
        container.addProperty(key,
                              value);
    }

    public String getLayoutProperty(String key) {
        return container.getProperty(key);
    }

    public void addDraggableComponentGroup(LayoutDragComponentGroup group) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = createLayoutDragComponentPresenter(
                group);
        view.addDraggableComponentGroup(layoutDragComponentGroupPresenter.getView());
    }

    private LayoutDragComponentGroupPresenter createLayoutDragComponentPresenter(LayoutDragComponentGroup group) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroupInstance.get();
        layoutDragComponentGroups.put(group.getName(),
                                      layoutDragComponentGroupPresenter);
        layoutDragComponentGroupPresenter.init(group);
        return layoutDragComponentGroupPresenter;
    }

    public void addDraggableComponentToGroup(String groupName,
                                             String componentId,
                                             LayoutDragComponent component) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroups
                .get(groupName);
        layoutDragComponentGroupPresenter.add(componentId,
                                              component);
    }

    public void removeDraggableGroup(String groupName) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroups
                .remove(groupName);
        if (layoutDragComponentGroupPresenter != null) {
            view.removeDraggableComponentGroup(layoutDragComponentGroupPresenter.getView());
        }
    }

    public void removeDraggableComponentFromGroup(String groupName,
                                                  String componentId) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroups
                .get(groupName);
        if (layoutDragComponentGroupPresenter != null) {
            layoutDragComponentGroupPresenter.removeDraggableComponentFromGroup(componentId);
        }
    }

    public void setPageStyle(LayoutTemplate.Style pageStyle) {
        this.pageStyle = pageStyle;
    }

    public interface View extends UberElement<LayoutEditorPresenter> {

        void setupContainer(UberElement<Container> container);

        void addDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> group);

        void removeDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> id);
    }
}

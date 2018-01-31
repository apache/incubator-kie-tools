/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LayoutComponentPalettePresenter implements LayoutDragComponentPalette {

    public interface View extends UberElement<LayoutComponentPalettePresenter> {

        void addDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> group);

        void removeDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> id);
    }

    private View view;
    protected Map<String, LayoutDragComponentGroupPresenter> layoutDragComponentGroups = new HashMap<>();
    private ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance;

    public LayoutComponentPalettePresenter() {
    }

    @Inject
    public LayoutComponentPalettePresenter(final View view,
                                 ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance) {
        this.view = view;
        this.layoutDragComponentGroupInstance = layoutDragComponentGroupInstance;
        view.init(this);
    }

    public UberElement<LayoutComponentPalettePresenter> getView() {
        return view;
    }

    public Map<String, LayoutDragComponentGroupPresenter> getLayoutDragComponentGroups() {
        return layoutDragComponentGroups;
    }

    @Override
    public void clear() {
        List<String> groupNames = new ArrayList<>(layoutDragComponentGroups.keySet());
        groupNames.forEach(this::removeDraggableGroup);
    }

    @Override
    public void addDraggableGroup(LayoutDragComponentGroup group) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = createComponentGroupPresenter(
                group);
        view.addDraggableComponentGroup(layoutDragComponentGroupPresenter.getView());
    }

    private LayoutDragComponentGroupPresenter createComponentGroupPresenter(LayoutDragComponentGroup group) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroupInstance.get();
        layoutDragComponentGroups.put(group.getName(),
                                      layoutDragComponentGroupPresenter);
        layoutDragComponentGroupPresenter.init(group);
        return layoutDragComponentGroupPresenter;
    }

    @Override
    public void removeDraggableGroup(String groupName) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroups
                .remove(groupName);
        if (layoutDragComponentGroupPresenter != null) {
            view.removeDraggableComponentGroup(layoutDragComponentGroupPresenter.getView());
        }
    }

    @Override
    public boolean hasDraggableGroup(String groupName) {
        return layoutDragComponentGroups.containsKey(groupName);
    }

    @Override
    public void addDraggableComponent(String groupName,
                                      String componentId,
                                      LayoutDragComponent component) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroups
                .get(groupName);
        layoutDragComponentGroupPresenter.addComponent(componentId,
                component);
    }

    @Override
    public void removeDraggableComponent(String groupName,
                                         String componentId) {
        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroups
                .get(groupName);
        if (layoutDragComponentGroupPresenter != null) {
            layoutDragComponentGroupPresenter.removeComponent(componentId);
        }
    }

    @Override
    public boolean hasDraggableComponent(String groupName,
                                         String componentId) {
        return hasDraggableGroup(groupName) && layoutDragComponentGroups
                .get(groupName).hasComponent(componentId);
    }
}

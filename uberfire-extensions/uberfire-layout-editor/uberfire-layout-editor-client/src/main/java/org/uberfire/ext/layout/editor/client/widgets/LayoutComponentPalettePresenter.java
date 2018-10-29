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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Factory;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;

@ApplicationScoped
public class LayoutComponentPalettePresenter implements LayoutDragComponentPalette {

    public interface View extends UberElement<LayoutComponentPalettePresenter> {

        void addDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> group);

        void removeDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> id);
    }

    private View view;
    private ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance;
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    private Map<String, LayoutDragComponentGroupPresenter> layoutDragComponentGroups = new HashMap<>();

    public LayoutComponentPalettePresenter() {
    }

    @Inject
    public LayoutComponentPalettePresenter(View view, ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance, ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService) {
        this.view = view;
        this.layoutDragComponentGroupInstance = layoutDragComponentGroupInstance;
        this.experimentalFeaturesRegistryService = experimentalFeaturesRegistryService;
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
    public void addDraggableGroups(Collection<LayoutComponentPaletteGroupProvider> groupProviders) {
        PortablePreconditions.checkNotNull("groupDefinitions", groupProviders);

        groupProviders.stream()
                .filter(this::isEnabled)
                .forEach(this::addDraggableGroup);
    }

    @Override
    public void addDraggableGroup(LayoutComponentPaletteGroupProvider groupProvider) {
        PortablePreconditions.checkNotNull("groupProvider", groupProvider);

        LayoutDragComponentGroup group = groupProvider.getComponentGroup();

        Map<String, LayoutDragComponent> components = group.getComponents();

        List<String> disabledGroups =  group.getComponents().entrySet().stream()
                .filter(entry -> !isEnabled(entry.getValue()))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        disabledGroups.stream()
                .forEach(components::remove);

        LayoutDragComponentGroupPresenter layoutDragComponentGroupPresenter = layoutDragComponentGroupInstance.get();
        layoutDragComponentGroups.put(group.getName(), layoutDragComponentGroupPresenter);
        layoutDragComponentGroupPresenter.init(group);
        view.addDraggableComponentGroup(layoutDragComponentGroupPresenter.getView());
    }

    private boolean isEnabled(Object object) {
        object = Factory.maybeUnwrapProxy(object);

        return experimentalFeaturesRegistryService.isFeatureEnabled(object.getClass().getName());
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
        return hasDraggableGroup(groupName) && layoutDragComponentGroups.get(groupName).hasComponent(componentId);
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.editor.external;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.editor.resources.i18n.Constants;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ComponentService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.plugin.client.perspective.editor.events.PerspectiveEditorFocusEvent;

@ApplicationScoped
public class ComponentGroupProducer {

    private static final Constants i18n = Constants.INSTANCE;

    private SyncBeanManager beanManager;
    private LayoutDragComponentPalette layoutDragComponentPalette;
    private Caller<ComponentService> componentService;

    @Inject
    public ComponentGroupProducer(Caller<ComponentService> externalComponentService,
                                  SyncBeanManager beanManager,
                                  LayoutDragComponentPalette layoutDragComponentPalette) {
        this.componentService = externalComponentService;
        this.beanManager = beanManager;
        this.layoutDragComponentPalette = layoutDragComponentPalette;
    }

    public void onEditorFocus(@Observes PerspectiveEditorFocusEvent event) {
        loadComponents();
    }

    public void loadComponents() {

        componentService.call((List<ExternalComponent> components) -> {
            addProvidedComponents(components.stream()
                                            .filter(c -> c.isProvided())
                                            .collect(Collectors.toList()));
            addExternalComponents(components.stream()
                                            .filter(c -> !c.isProvided())
                                            .collect(Collectors.toList()));
        }).listAllComponents();

    }

    public void addExternalComponents(List<ExternalComponent> components) {
        String groupId = i18n.externalComponentsGroupName();
        if (!components.isEmpty()) {
            checkGroup(groupId);
        }
        components.forEach(comp -> {
            layoutDragComponentPalette.addDraggableComponent(groupId,
                                                             comp.getId(),
                                                             produceDragComponent(comp));
        });

    }

    public void addProvidedComponents(List<ExternalComponent> components) {
        components.stream().forEach(component -> {
            String groupId = component.getCategory() != null ? component.getCategory() : i18n.internalComponentsGroupName();
            checkGroup(groupId);
            layoutDragComponentPalette.addDraggableComponent(groupId, component.getId(), produceDragComponent(component));
        });

    }

    private void checkGroup(String groupId) {
        if (!layoutDragComponentPalette.hasDraggableGroup(groupId)) {
            layoutDragComponentPalette.addDraggableGroup(new LayoutComponentPaletteGroupProvider() {

                @Override
                public String getName() {
                    return groupId;
                }

                @Override
                public LayoutDragComponentGroup getComponentGroup() {
                    return new LayoutDragComponentGroup(groupId);
                }
            });
        }

    }

    ExternalComponentDragDef produceDragComponent(ExternalComponent comp) {
        ExternalComponentDragDef dragComp;
        if (comp.isNoData()) {
            dragComp = beanManager.lookupBean(ExternalDragComponent.class).getInstance();
        } else {
            dragComp = beanManager.lookupBean(ExternalDisplayerDragComponent.class).getInstance();
        }
        dragComp.setDragInfo(comp.getName(), comp.getIcon());
        dragComp.setComponentId(comp.getId());
        return dragComp;
    }

}
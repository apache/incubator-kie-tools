/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.renderer.RendererSettings;
import org.dashbuilder.renderer.service.RendererSettingsService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * This class holds a registry of all the RendererLibrary implementations available.
 */
@EntryPoint
@ApplicationScoped
public class RendererManager {

    private SyncBeanManager beanManager;
    private List<RendererLibrary> renderersList;
    private Map<DisplayerType, RendererLibrary> renderersDefault = new EnumMap<>(DisplayerType.class);
    private Map<DisplayerType, List<RendererLibrary>> renderersByType = new EnumMap<>(DisplayerType.class);
    private Map<DisplayerSubType, List<RendererLibrary>> renderersBySubType = new EnumMap<>(DisplayerSubType.class);
    
    CommonConstants i18n = CommonConstants.INSTANCE;
    
    Caller<RendererSettingsService> rendererSettingsService;

    public RendererManager() {
    }

    @Inject
    public RendererManager(SyncBeanManager beanManager, Caller<RendererSettingsService> rendererSettingsService) {
        this.beanManager = beanManager;
        this.rendererSettingsService = rendererSettingsService;
    }

    @PostConstruct
    protected void init() {
        rendererSettingsService.call((RendererSettings settings) -> lookupRenderers(settings))
                               .getSettings();
    }

    protected void lookupRenderers(RendererSettings settings) {
        String defaultUUID = settings.getDefaultRenderer();
        boolean onlyOffline = settings.isOffline();
        renderersList = new ArrayList<>();
        Collection<SyncBeanDef<RendererLibrary>> beanDefs = beanManager.lookupBeans(RendererLibrary.class);
        
        if (onlyOffline) {
            beanDefs = beanDefs.stream().filter(bd -> bd.getInstance().isOffline()).collect(Collectors.toList());
        }
        
        if (defaultUUID != null && ! defaultUUID.isEmpty()) {
            beanDefs.stream()
                    .map(SyncBeanDef::getInstance)
                    .filter(render -> render.getUUID().equals(defaultUUID))
                    .findFirst().ifPresent(rend -> 
                        rend.getSupportedTypes().forEach(c -> renderersDefault.put(c, rend))
            );
        }
        for (SyncBeanDef<RendererLibrary> beanDef : beanDefs) {

            RendererLibrary lib = beanDef.getInstance();
            renderersList.add(lib);

            for (DisplayerType displayerType : DisplayerType.values()) {
                if (lib.isDefault(displayerType)) {
                    renderersDefault.putIfAbsent(displayerType, lib);
                }
            }
            List<DisplayerType> types = lib.getSupportedTypes();
            if (types != null && !types.isEmpty()) {

                for (DisplayerType type : types) {
                    List<RendererLibrary> set = renderersByType.get(type);
                    if (set == null) {
                        set = new ArrayList<>();
                        renderersByType.put(type, set);
                    }
                    set.add(lib);

                    List<DisplayerSubType> subTypes = lib.getSupportedSubtypes(type);
                    if (subTypes != null && !subTypes.isEmpty()) {
                        for (DisplayerSubType subType : subTypes) {
                            List<RendererLibrary> subset = renderersBySubType.get(subType);
                            if (subset == null) {
                                subset = new ArrayList<>();
                                renderersBySubType.put(subType, subset);
                            }
                            subset.add(lib);
                        }
                    }
                }
            }
        }
    }

    public List<RendererLibrary> getRenderers() {
        return renderersList;
    }

    public RendererLibrary getDefaultRenderer(DisplayerType displayerType) {
        return renderersDefault.get(displayerType);
    }

    public void setDefaultRenderer(DisplayerType displayerType, String rendererName) {
        renderersDefault.put(displayerType, getRendererByUUID(rendererName));
    }

    public List<RendererLibrary> getRenderersForType(DisplayerType displayerType) {
        return  renderersByType.getOrDefault(displayerType, new ArrayList<>());
    }

    public List<RendererLibrary> getRenderersForType(DisplayerType type, DisplayerSubType subType) {
        List<RendererLibrary> types  = renderersByType.getOrDefault(type, Collections.emptyList());
        List<RendererLibrary> subTypes = renderersBySubType.getOrDefault(subType, Collections.emptyList());
        if (type == null) {
            return subType == null ? renderersList : subTypes;
        }
        else if (subType == null) {
            return types;
        }
        else {
            List<RendererLibrary> result = new ArrayList<RendererLibrary>(subTypes);
            Iterator<RendererLibrary> it = result.iterator();
            while (it.hasNext()) {
                RendererLibrary rl = it.next();
                if (!types.contains(rl)) {
                    it.remove();
                }
            }
            return result;
        }
    }

    public RendererLibrary getRendererByUUID(String renderer) {
        return getRendererByOrThrowError(renderer, lib -> lib.getUUID().equals(renderer));
    }

    private RendererLibrary _getRendererByUUID(String renderer) {
        for (RendererLibrary lib : renderersList) {
            if (lib.getUUID().equals(renderer)) {
                return lib;
            }
        }
        return null;
    }

    public RendererLibrary getRendererByName(String renderer) {
        return getRendererByOrThrowError(renderer, lib -> lib.getName().equals(renderer));
    }
    
    private RendererLibrary getRendererByOrThrowError(String renderer, Predicate<RendererLibrary> test) {
        return renderersList.stream()
                     .filter(test)
                     .findFirst()
                     .orElseThrow(() -> new RuntimeException(i18n.rendererliblocator_renderer_not_found(renderer)));
    }

    public RendererLibrary getRendererForType(DisplayerType displayerType) {
        return renderersDefault.getOrDefault(displayerType, renderersByType.get(displayerType).get(0));
    }

    public RendererLibrary getRendererForDisplayer(DisplayerSettings target) {

        // Get the renderer specified
        if (!StringUtils.isBlank(target.getRenderer())) {
            RendererLibrary targetRenderer = _getRendererByUUID(target.getRenderer());
            if (targetRenderer != null) return targetRenderer;
        }

        // Return always the renderer declared as default
        List<RendererLibrary> renderersSupported = getRenderersForType(target.getType(), target.getSubtype());
        RendererLibrary defaultRenderer = getDefaultRenderer(target.getType());
        for (RendererLibrary rendererLibrary : renderersSupported) {
            if (defaultRenderer != null && rendererLibrary.equals(defaultRenderer)) {
                return defaultRenderer;
            }
        }
        // If no default then return the first supported one
        if (!renderersSupported.isEmpty()) return renderersSupported.get(0);
        throw new RuntimeException(i18n.renderermanager_renderer_not_available(target.getType().name()));
    }
    
    public boolean isTypeSupported(DisplayerType type) {
        return !getRenderersForType(type).isEmpty();
    }
}

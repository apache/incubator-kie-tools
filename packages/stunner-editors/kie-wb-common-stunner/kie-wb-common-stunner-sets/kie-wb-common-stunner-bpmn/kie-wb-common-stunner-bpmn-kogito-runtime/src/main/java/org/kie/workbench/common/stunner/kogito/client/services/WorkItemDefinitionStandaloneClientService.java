/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.kogito.client.services.util.WidPresetResources;
import org.kie.workbench.common.stunner.kogito.client.services.util.WorkItemIconCache;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientParser.parse;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

@ApplicationScoped
public class WorkItemDefinitionStandaloneClientService implements WorkItemDefinitionClientService {

    private static Logger LOGGER = Logger.getLogger(WorkItemDefinitionStandaloneClientService.class.getName());

    private static final String RESOURCE_ALL_WID_PATTERN = "*.wid";
    private static final String RESOURCE_GLOBAL_DIRECTORY_WID_PATTERN = "/global/*.wid";
    private static final String MILESTONE_ICON = "defaultmilestoneicon.png";
    private static final String MILESTONE_NAME = "Milestone";

    private final Promises promises;
    private final WorkItemDefinitionCacheRegistry registry;
    private final ResourceContentService resourceContentService;
    private final WorkItemIconCache workItemIconCache;

    @Inject
    public WorkItemDefinitionStandaloneClientService(final Promises promises,
                                                     final WorkItemDefinitionCacheRegistry registry,
                                                     final ResourceContentService resourceContentService,
                                                     final WorkItemIconCache workItemIconCache) {

        this.promises = promises;
        this.registry = registry;
        this.resourceContentService = resourceContentService;
        this.workItemIconCache = workItemIconCache;
    }

    @PostConstruct
    public void init() {
    }

    @Produces
    @Default
    @Override
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public Promise<Collection<WorkItemDefinition>> call(final Metadata input) {
        return allWorkItemsLoader(input.getPath());
    }

    @PreDestroy
    public void destroy() {
        registry.clear();
    }


    private Promise<Collection<WorkItemDefinition>> allWorkItemsLoader(Path openedDiagramPath) {

        final int lastSlashIndex = openedDiagramPath != null ? openedDiagramPath.toURI().lastIndexOf('/') : -1;
        final String directoryPath = (lastSlashIndex != -1) 
            ? openedDiagramPath.toURI().substring(0, lastSlashIndex + 1) 
            : ""; 

        final String widPattern = "/" + ((openedDiagramPath == null || isEmpty(directoryPath)) ?  RESOURCE_ALL_WID_PATTERN : directoryPath + RESOURCE_ALL_WID_PATTERN);
        
        return promises.create((success, failure) -> {
            registry.clear();
            final List<WorkItemDefinition> loaded = new LinkedList<>();
            LOGGER.log(Level.FINE, "Searching WID using pattern: " + RESOURCE_GLOBAL_DIRECTORY_WID_PATTERN);
            resourceContentService
                    .list(RESOURCE_GLOBAL_DIRECTORY_WID_PATTERN, ResourceListOptions.traversal())
                    .then(paths1 -> {
                        LOGGER.log(Level.FINE, "Searching WID using pattern: " + widPattern);
                        resourceContentService
                                .list(widPattern, ResourceListOptions.traversal())
                                .then(paths2 -> {
                                    String[] paths = mergeTwoArrays(paths1, paths2);
                                    if (paths.length > 0) {
                                        promises.all(asList(paths),
                                                     path -> workItemsLoader(path, loaded))
                                                .then(wids -> {
                                                    Collection<WorkItemDefinition> widsWithPreset = addPresetWids(wids);
                                                    widsWithPreset.forEach(registry::register);
                                                    success.onInvoke(widsWithPreset);
                                                    return null;
                                                })
                                                .catch_(error -> {
                                                    failure.onInvoke(error);
                                                    return null;
                                                });
                                    } else {
                                        promises.all(presetWorkItemsLoader(loaded)).then(wids -> {
                                            wids.forEach(registry::register);
                                            success.onInvoke(wids);
                                            return null;
                                        }).catch_(error -> {
                                            failure.onInvoke(error);
                                            return null;
                                        });
                                    }
                                    return promises.resolve();
                                });
                        return promises.resolve();
                    });
        });
    }

    private String[] mergeTwoArrays(String[] paths1, String[] paths2) {
        List<String> both = new ArrayList<>(paths1.length + paths2.length);
        Collections.addAll(both, paths1);
        Collections.addAll(both, paths2);
        return both.toArray(new String[0]);
    }

    private Promise<Collection<WorkItemDefinition>> presetWorkItemsLoader(final Collection<WorkItemDefinition> preset) {
        final List<WorkItemDefinition> wids = parse(WidPresetResources.INSTANCE.asText().getText());
        return getPromises(wids, preset, "");
    }

    private Promise<Collection<WorkItemDefinition>> workItemsLoader(final String path,
                                                                    final Collection<WorkItemDefinition> loaded) {
        int lastDirIndex = path.lastIndexOf('/');
        final String directory = (lastDirIndex >= 0) ? path.substring(0, lastDirIndex) + "/" : "";
        if (nonEmpty(path)) {
            return resourceContentService
                    .get(path)
                    .then(value -> getPromises(parse(value), loaded, directory));
        }
        return promises.resolve(emptyList());
    }

    private Collection<WorkItemDefinition> addPresetWids(final Collection<WorkItemDefinition> wids) {
        if (wids.stream().noneMatch(wid -> wid.getName().equals(MILESTONE_NAME))) {
            List<WorkItemDefinition> presetWids = parse(getPresetAsText());
            presetWids.stream()
                    .filter(wid -> wid.getName().equals(MILESTONE_NAME))
                    .findFirst().ifPresent(wid -> {
                wid.getIconDefinition().setIconData(
                        getPresetIcon(wid.getIconDefinition().getUri())
                );
                wids.add(wid);
            });

            presetWids.addAll(wids);
            return presetWids;
        }

        return wids;
    }

    String getPresetAsText() {
        return WidPresetResources.INSTANCE.asText().getText();
    }

    String getMilestoneIconAsBase64() {
        return WidPresetResources.INSTANCE
                .getMillestoneImage()
                .getSafeUri()
                .asString();
    }

    private Promise<Collection<WorkItemDefinition>> getPromises(final List<WorkItemDefinition> wids, final Collection<WorkItemDefinition> loaded, final String path) {
        wids.forEach(w -> {
            LOGGER.log(Level.FINE, "WID Icon: " + path + w.getIconDefinition().getUri());
            w.getIconDefinition().setUri(path + w.getIconDefinition().getUri());
        });
        return promises.create((success, failure) -> {
            promises.all(wids, this::workItemIconLoader)
                    .then(wid -> {
                        loaded.addAll(wids);
                        success.onInvoke(loaded);
                        return promises.resolve();
                    })
                    .catch_(error -> {
                        failure.onInvoke(error);
                        return null;
                    });
        });
    }

    private Promise workItemIconLoader(final WorkItemDefinition wid) {
        final String iconUri = wid.getIconDefinition().getUri();
        if (nonEmpty(iconUri)) {
            return workItemIconCache.getIcon(iconUri).then(iconData -> {
                if (nonEmpty(iconData) && !isIconDataUri(iconData)) {
                    iconData = iconDataUri(iconUri, iconData);
                }

                if (nonEmpty(iconData)) {
                    wid.getIconDefinition().setIconData(iconData);
                }

                return promises.resolve();
            });
        }
        return promises.resolve();
    }

    protected String getPresetIcon(final String iconUri) {
        if (iconUri.equals(MILESTONE_ICON)) {
            return getMilestoneIconAsBase64();
        }
        return "";
    }

    protected static boolean isIconDataUri(String iconData) {
        return iconData.startsWith("data:");
    }

    protected static String iconDataUri(String iconUri, String iconData) {
        String[] iconUriParts = iconUri.split("\\.");
        if (iconUriParts.length > 1) {
            int fileTypeIndex = iconUriParts.length - 1;
            String fileType = iconUriParts[fileTypeIndex];
            return "data:image/" + fileType + ";base64, " + iconData;
        }
        return iconData;
    }
}

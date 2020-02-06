/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.promise.Promises;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientParser.parse;
import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

@ApplicationScoped
public class WorkItemDefinitionStandaloneClientService implements WorkItemDefinitionClientService {

    private static Logger LOGGER = Logger.getLogger(BaseCanvasHandler.class.getName());
    private static final String RESOURCE_ALL_WID_PATTERN = "**/*.wid";

    private final Promises promises;
    private final WorkItemDefinitionCacheRegistry registry;
    private final ResourceContentService resourceContentService;

    // Cache the promise, as by definition will be performed just once,
    // so the available work item definitions will be also just registered once, by app.
    private Promise<Collection<WorkItemDefinition>> loader;

    @Inject
    public WorkItemDefinitionStandaloneClientService(final Promises promises,
                                                     final WorkItemDefinitionCacheRegistry registry,
                                                     final ResourceContentService resourceContentService) {

        this.promises = promises;
        this.registry = registry;
        this.resourceContentService = resourceContentService;
    }

    @PostConstruct
    public void init() {
        loader = allWorkItemsLoader();
    }

    @Produces
    @Default
    @Override
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public Promise<Collection<WorkItemDefinition>> call(final Metadata input) {
        return loader;
    }

    @PreDestroy
    public void destroy() {
        registry.clear();
        loader = null;
    }

    private Promise<Collection<WorkItemDefinition>> allWorkItemsLoader() {
        log("Starting loading of all Work Items");
        return promises.create((success, failure) -> {
            log("Loading all Work Items");
            registry.clear();
            final List<WorkItemDefinition> loaded = new LinkedList<>();
            resourceContentService
                    .list(RESOURCE_ALL_WID_PATTERN)
                    .then(paths -> {
                        if (paths.length > 0) {
                            log("Work Items found at [" + paths + "]");
                            promises.all(asList(paths),
                                         path -> workItemsLoader(path, loaded))
                                    .then(wids -> {
                                        wids.forEach(registry::register);
                                        success.onInvoke(wids);
                                        return null;
                                    })
                                    .catch_(error -> {
                                        failure.onInvoke(error);
                                        return null;
                                    });
                        } else {
                            log("NO Work Items found at [" + paths + "]");
                            success.onInvoke(emptyList());
                        }
                        return promises.resolve();
                    })
                    .catch_(error -> {
                        failure.onInvoke(error);
                        return null;
                    });
        });
    }

    @SuppressWarnings("unchecked")
    private Promise<Collection<WorkItemDefinition>> workItemsLoader(final String path,
                                                                    final Collection<WorkItemDefinition> loaded) {
        log("Processing [" + path + "]");
        if (nonEmpty(path)) {
            return resourceContentService
                    .get(path)
                    .then(value -> {
                        log("Content for path = [" + value + "]");
                        log("Loading Work Items for path [" + path + "]");
                        final List<WorkItemDefinition> wids = parse(value);
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
                    });
        }
        return promises.resolve(emptyList());
    }

    private Promise workItemIconLoader(final WorkItemDefinition wid) {
        final String iconUri = wid.getIconDefinition().getUri();
        log("Loading icon for URI [" + iconUri + "]");
        if (nonEmpty(iconUri)) {
            return resourceContentService
                    .get(iconUri)
                    .then(iconData -> {
                        log("Content for icon = [" + iconData + "]");
                        if (nonEmpty(iconData)) {
                            wid.getIconDefinition().setIconData(iconData);
                        }
                        return promises.resolve();
                    });
        }
        return promises.resolve();
    }

    private static void log(String s) {
        LOGGER.fine(s);
    }
}

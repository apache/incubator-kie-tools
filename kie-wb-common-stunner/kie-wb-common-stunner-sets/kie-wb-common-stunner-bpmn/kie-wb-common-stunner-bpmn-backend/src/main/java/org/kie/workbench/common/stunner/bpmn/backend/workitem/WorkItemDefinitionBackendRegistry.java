/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionMetadataRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * The default Work Item Definition Registry for server side.
 * <p>
 * - It produces the @Default WorkItemDefinitionRegistry for a request context
 * - It performs calls to the Work Item Definition Service in order to populate a request's registry
 * - It destroy the registry, if any, when the request context destroys the instance
 */
@RequestScoped
@Typed(WorkItemDefinitionBackendRegistry.class)
public class WorkItemDefinitionBackendRegistry
        implements WorkItemDefinitionRegistry {

    private final WorkItemDefinitionCacheRegistry registry;
    private final WorkItemDefinitionService service;
    private final WorkItemDefinitionMetadataRegistry metadataRegistry;

    // CDI proxy.
    protected WorkItemDefinitionBackendRegistry() {
        this(null,
             null,
             null);
    }

    @Inject
    public WorkItemDefinitionBackendRegistry(final WorkItemDefinitionCacheRegistry registry,
                                             final WorkItemDefinitionService service,
                                             final WorkItemDefinitionMetadataRegistry metadataRegistry) {
        this.registry = registry;
        this.service = service;
        this.metadataRegistry = metadataRegistry;
    }

    @PostConstruct
    public void init() {
        metadataRegistry
                .setRegistrySupplier(() -> registry)
                .setWorkItemsByPathSupplier((path, collectionConsumer) ->
                                                    collectionConsumer.accept(service.search(path)));
    }

    public WorkItemDefinitionBackendRegistry load(final Metadata metadata) {
        metadataRegistry.load(metadata,
                              () -> {
                              });
        return this;
    }

    @Produces
    @Default
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public Collection<WorkItemDefinition> items() {
        return metadataRegistry.items();
    }

    @Override
    public WorkItemDefinition get(final String name) {
        return metadataRegistry.get(name);
    }

    @PreDestroy
    public void destroy() {
        registry.clear();
    }
}
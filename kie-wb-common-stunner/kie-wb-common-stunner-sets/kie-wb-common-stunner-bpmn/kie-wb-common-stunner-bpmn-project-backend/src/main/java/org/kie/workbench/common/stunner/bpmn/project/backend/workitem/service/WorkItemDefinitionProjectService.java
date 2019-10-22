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

package org.kie.workbench.common.stunner.bpmn.project.backend.workitem.service;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.deploy.WorkItemDefinitionDeployServices;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionVFSLookupService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * - It produces the work item definition registry for the current request
 * - It performs look-ups by calling the VFS WorkItemDefinitionService, in order to populate a request's registry
 * - If no work item definitions found for the module, it starts the deployment (see <code>DEFAULT_DEPLOY_PREDICATE</code>)
 */
@RequestScoped
@Service
public class WorkItemDefinitionProjectService
        implements WorkItemDefinitionLookupService {

    private final WorkItemDefinitionCacheRegistry registry;
    private final WorkItemDefinitionVFSLookupService vfsService;
    private final WorkItemDefinitionDeployServices deployServices;
    private final BiPredicate<Metadata, Collection<WorkItemDefinition>> deployPredicate;

    /**
     * Assuming that at least the default built-in work item definitions provided by the workbench should be
     * present, this predicate just checks if any asset has been found for the active module,
     * if no assets found, it means the deployment must be performed, otherwise, do not perform any deployment.
     */
    private static final BiPredicate<Metadata, Collection<WorkItemDefinition>> DEFAULT_DEPLOY_PREDICATE =
            ((metadata, workItemDefinitions) -> workItemDefinitions.isEmpty());

    // CDI proxy.
    @SuppressWarnings("all")
    protected WorkItemDefinitionProjectService() {
        this(null, null, null, null);
    }

    @Inject
    public WorkItemDefinitionProjectService(final WorkItemDefinitionCacheRegistry registry,
                                            final WorkItemDefinitionVFSLookupService vfsService,
                                            final WorkItemDefinitionDeployServices deployServices) {
        this(registry,
             vfsService,
             deployServices,
             DEFAULT_DEPLOY_PREDICATE);
    }

    WorkItemDefinitionProjectService(final WorkItemDefinitionCacheRegistry registry,
                                     final WorkItemDefinitionVFSLookupService vfsService,
                                     final WorkItemDefinitionDeployServices deployServices,
                                     final BiPredicate<Metadata, Collection<WorkItemDefinition>> deployPredicate) {
        this.registry = registry;
        this.vfsService = vfsService;
        this.deployServices = deployServices;
        this.deployPredicate = deployPredicate;
    }

    @Produces
    @Default
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public Collection<WorkItemDefinition> execute(final Metadata metadata) {
        return load(metadata).items();
    }

    @PreDestroy
    public void destroy() {
        registry.destroy();
    }

    private WorkItemDefinitionCacheRegistry load(final Metadata metadata) {
        Collection<WorkItemDefinition> items = search(metadata);
        if (deployPredicate.test(metadata, items)) {
            deployServices.deploy(metadata);
            items = search(metadata);
        }
        items.forEach(registry::register);
        return registry;
    }

    private List<WorkItemDefinition> search(final Metadata metadata) {
        return vfsService
                .search(metadata)
                .stream()
                .collect(Collectors.toList());
    }
}

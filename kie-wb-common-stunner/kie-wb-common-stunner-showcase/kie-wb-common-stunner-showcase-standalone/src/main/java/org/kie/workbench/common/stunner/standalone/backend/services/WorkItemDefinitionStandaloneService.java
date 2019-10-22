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

package org.kie.workbench.common.stunner.standalone.backend.services;

import java.util.Collection;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

@ApplicationScoped
@Service
public class WorkItemDefinitionStandaloneService
        implements WorkItemDefinitionLookupService {

    private final WorkItemDefinitionCacheRegistry registry;

    // CDI proxy.
    @SuppressWarnings("all")
    protected WorkItemDefinitionStandaloneService() {
        this(null);
    }

    @Inject
    public WorkItemDefinitionStandaloneService(final WorkItemDefinitionCacheRegistry registry) {
        this.registry = registry;
    }

    @Produces
    @Default
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public Collection<WorkItemDefinition> execute(final Metadata metadata) {
        return registry.items();
    }

    @PreDestroy
    public void destroy() {
        registry.destroy();
    }
}

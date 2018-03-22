/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

/**
 * This class contains the mappings for the different stencil identifiers that are different from
 * the patterns used in this tool.
 */
@Dependent
public class Bpmn2OryxIdMappings extends BaseOryxIdMappings {

    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;

    @Inject
    public Bpmn2OryxIdMappings(final DefinitionManager definitionManager,
                               final Instance<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        super(definitionManager);
        this.workItemDefinitionRegistry = workItemDefinitionRegistry::get;
    }

    public Bpmn2OryxIdMappings(final DefinitionManager definitionManager,
                               final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        super(definitionManager);
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
    }

    @Override
    public String getOryxDefinitionId(final Object def) {
        if (ServiceTask.isWorkItem(def)) {
            return ((ServiceTask) def).getName();
        }
        return super.getOryxDefinitionId(def);
    }

    @Override
    public Class<?> getDefinition(final String oryxId) {
        if (isWorkItem(oryxId)) {
            return ServiceTask.class;
        }
        return super.getDefinition(oryxId);
    }

    @Override
    public String getDefinitionId(final String oryxId) {
        if (isWorkItem(oryxId)) {
            return BindableAdapterUtils.getDynamicDefinitionId(ServiceTask.class,
                                                               oryxId);
        }
        return super.getDefinitionId(oryxId);
    }

    public boolean isWorkItem(final String oryxId) {
        return null != workItemDefinitionRegistry.get().get(oryxId);
    }

    @Override
    protected Class<? extends BPMNDiagram> getDiagramType() {
        return BPMNDiagramImpl.class;
    }
}

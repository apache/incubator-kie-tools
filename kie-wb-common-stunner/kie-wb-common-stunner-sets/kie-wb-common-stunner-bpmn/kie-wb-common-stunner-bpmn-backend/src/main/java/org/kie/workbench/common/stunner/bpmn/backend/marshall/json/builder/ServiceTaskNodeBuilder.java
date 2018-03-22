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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.util.function.Supplier;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ServiceTaskNodeBuilder extends NodeBuilderImpl {

    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;

    public ServiceTaskNodeBuilder(final String defId,
                                  final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        super(ServiceTask.class, defId);
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
    }

    @Override
    protected Node<View<BPMNDefinition>, Edge> doBuild(final BuilderContext context) {
        final Node<View<BPMNDefinition>, Edge> node = super.doBuild(context);
        final ServiceTask serviceTask = (ServiceTask) node.getContent().getDefinition();
        // Oryx handles the taskName property, use it for populating the service task.
        final String taskName = serviceTask.getExecutionSet().getTaskName().getValue();
        final WorkItemDefinition workItemDefinition = workItemDefinitionRegistry.get().get(taskName);
        ServiceTaskFactory.ServiceTaskBuilder.setProperties(workItemDefinition,
                                                            serviceTask);
        return node;
    }
}

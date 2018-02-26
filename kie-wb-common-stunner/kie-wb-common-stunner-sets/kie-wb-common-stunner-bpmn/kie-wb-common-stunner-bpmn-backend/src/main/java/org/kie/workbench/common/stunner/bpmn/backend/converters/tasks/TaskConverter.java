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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.NoneTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.EmptyTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TaskConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;
    private final BusinessRuleTaskConverter businessRuleTaskConverter;
    private final UserTaskConverter userTaskConverter;
    private final ScriptTaskConverter scriptTaskConverter;

    public TaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
        this.businessRuleTaskConverter = new BusinessRuleTaskConverter(factoryManager, propertyReaderFactory);
        this.userTaskConverter = new UserTaskConverter(factoryManager, propertyReaderFactory);
        this.scriptTaskConverter = new ScriptTaskConverter(factoryManager, propertyReaderFactory);
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.Task task) {
        return Match.ofNode(Task.class, BPMNViewDefinition.class)
                .when(org.eclipse.bpmn2.BusinessRuleTask.class, businessRuleTaskConverter::convert)
                .when(org.eclipse.bpmn2.ScriptTask.class, scriptTaskConverter::convert)
                .when(org.eclipse.bpmn2.UserTask.class, userTaskConverter::convert)
                .missing(org.eclipse.bpmn2.ServiceTask.class)
                .missing(org.eclipse.bpmn2.ManualTask.class)
                .orElse(t -> {
                    Node<View<NoneTask>, Edge> node = factoryManager.newNode(t.getId(), NoneTask.class);
                    NoneTaskPropertyReader p = propertyReaderFactory.of(task);

                    NoneTask definition = node.getContent().getDefinition();

                    definition.setGeneral(new TaskGeneralSet(
                            new Name(p.getName()),
                            new Documentation(p.getDocumentation())
                    ));

                    definition.setExecutionSet(new EmptyTaskExecutionSet());

                    definition.setSimulationSet(
                            p.getSimulationSet()
                    );

                    node.getContent().setBounds(p.getBounds());

                    definition.setDimensionsSet(p.getRectangleDimensionsSet());
                    definition.setBackgroundSet(p.getBackgroundSet());
                    definition.setFontSet(p.getFontSet());

                    return node;
                })
                .apply(task)
                .asSuccess().value();
    }
}

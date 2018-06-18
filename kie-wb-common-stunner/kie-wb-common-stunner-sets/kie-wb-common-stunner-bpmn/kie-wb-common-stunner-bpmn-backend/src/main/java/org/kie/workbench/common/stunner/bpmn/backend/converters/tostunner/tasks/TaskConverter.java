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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks;

import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BusinessRuleTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ScriptTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.TaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.EmptyTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Script;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TaskConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public TaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnNode convert(org.eclipse.bpmn2.Task task) {
        return Match.of(Task.class, BpmnNode.class)
                .when(org.eclipse.bpmn2.BusinessRuleTask.class, this::businessRuleTask)
                .when(org.eclipse.bpmn2.ScriptTask.class, this::scriptTask)
                .when(org.eclipse.bpmn2.UserTask.class, this::userTask)
                .missing(org.eclipse.bpmn2.ManualTask.class)
                .orElse(this::fallback)
                .apply(task).value();
    }

    private BpmnNode serviceTask(org.eclipse.bpmn2.Task task) {
        Node<View<ServiceTask>, Edge> node = factoryManager.newNode(task.getId(), ServiceTask.class);

        ServiceTask definition = node.getContent().getDefinition();
        ServiceTaskPropertyReader p = propertyReaderFactory.ofCustom(task);

        definition.setName(p.getServiceTaskName());
        definition.getTaskType().setRawType(p.getServiceTaskName());
        definition.setDescription(p.getServiceTaskDescription());
        definition.setCategory(p.getServiceTaskCategory());
        definition.setDefaultHandler(p.getServiceTaskDefaultHandler());

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new ServiceTaskExecutionSet(
                new TaskName(p.getTaskName()),
                new IsAsync(p.isAsync()),
                new AdHocAutostart(p.isAdHocAutoStart()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction())
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        return BpmnNode.of(node);
    }

    private BpmnNode businessRuleTask(org.eclipse.bpmn2.BusinessRuleTask task) {
        Node<View<BusinessRuleTask>, Edge> node = factoryManager.newNode(task.getId(), BusinessRuleTask.class);

        BusinessRuleTask definition = node.getContent().getDefinition();
        BusinessRuleTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new BusinessRuleTaskExecutionSet(
                new RuleFlowGroup(p.getRuleFlowGroup()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new IsAsync(p.isAsync()),
                new AdHocAutostart(p.isAdHocAutoStart())
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        return BpmnNode.of(node);
    }

    private BpmnNode scriptTask(org.eclipse.bpmn2.ScriptTask task) {
        Node<View<ScriptTask>, Edge> node = factoryManager.newNode(task.getId(), ScriptTask.class);

        ScriptTask definition = node.getContent().getDefinition();
        ScriptTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new ScriptTaskExecutionSet(
                new Script(p.getScript()),
                new IsAsync(p.isAsync())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        definition.setSimulationSet(p.getSimulationSet());

        return BpmnNode.of(node);
    }

    private BpmnNode userTask(org.eclipse.bpmn2.UserTask task) {
        Node<View<UserTask>, Edge> node = factoryManager.newNode(task.getId(), UserTask.class);

        UserTask definition = node.getContent().getDefinition();
        UserTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setSimulationSet(
                p.getSimulationSet()
        );

        definition.setExecutionSet(new UserTaskExecutionSet(
                new TaskName(p.getTaskName()),
                p.getActors(),
                new Groupid(p.getGroupid()),
                p.getAssignmentsInfo(),
                new IsAsync(p.isAsync()),
                new Skippable(p.isSkippable()),
                new Priority(p.getPriority()),
                new Subject(p.getSubject()),
                new Description(p.getDescription()),
                new CreatedBy(p.getCreatedBy()),
                new AdHocAutostart(p.isAdHocAutostart()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        return BpmnNode.of(node);
    }

    private BpmnNode fallback(Task task) {
        String taskName = CustomAttribute.serviceTaskName.of(task).get();
        if (taskName.isEmpty()) {
            return noneTask(task);
        } else if (taskName.equals("BusinessRuleTask")) {
            return noneTask(task);
        } else {
            return serviceTask(task);
        }
    }

    private BpmnNode noneTask(Task task) {
        Node<View<NoneTask>, Edge> node = factoryManager.newNode(task.getId(), NoneTask.class);
        TaskPropertyReader p = propertyReaderFactory.of(task);

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

        return BpmnNode.of(node);
    }
}

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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.tasks;

import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BusinessRuleTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.GenericServiceTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ScriptTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ServiceTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.UserTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.cast;

public class TaskConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public TaskConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseTask>, ?> node) {
        BaseTask def = node.getContent().getDefinition();
        if (def instanceof NoneTask) {
            return noneTask(cast(node));
        }
        if (def instanceof ScriptTask) {
            return scriptTask(cast(node));
        }
        if (def instanceof BusinessRuleTask) {
            return businessRuleTask(cast(node));
        }
        if (def instanceof BaseUserTask) {
            return userTask(cast(node));
        }
        if (def instanceof CustomTask) {
            return serviceTask(cast(node));
        }
        if (def instanceof GenericServiceTask) {
            return genericServiceTask(cast(node));
        }
        return ConverterUtils.notSupported(def);
    }

    private PropertyWriter genericServiceTask(Node<View<GenericServiceTask>, ?> n) {
        org.eclipse.bpmn2.ServiceTask task = bpmn2.createServiceTask();
        task.setId(n.getUUID());

        GenericServiceTask definition = n.getContent().getDefinition();
        GenericServiceTaskPropertyWriter p = propertyWriterFactory.of(task);

        TaskGeneralSet general = definition.getGeneral();
        GenericServiceTaskExecutionSet executionSet =
                definition.getExecutionSet();

        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setAbsoluteBounds(n);
        p.setSimulationSet(definition.getSimulationSet());
        p.setValue(executionSet.getGenericServiceTaskInfo().getValue());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());
        if (Boolean.TRUE.equals(executionSet.getIsMultipleInstance().getValue())) {
            p.setIsSequential(executionSet.getMultipleInstanceExecutionMode().isSequential());
            p.setCollectionInput(executionSet.getMultipleInstanceCollectionInput().getValue());
            p.setInput(executionSet.getMultipleInstanceDataInput().getValue());
            p.setCollectionOutput(executionSet.getMultipleInstanceCollectionOutput().getValue());
            p.setOutput(executionSet.getMultipleInstanceDataOutput().getValue());
            p.setCompletionCondition(executionSet.getMultipleInstanceCompletionCondition().getValue());
        }
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setSLADueDate(executionSet.getSlaDueDate().getValue());
        p.setAssignmentsInfo(executionSet.getAssignmentsinfo());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());
        return p;
    }

    private PropertyWriter serviceTask(Node<View<CustomTask>, ?> n) {
        Task task = bpmn2.createTask();
        task.setId(n.getUUID());

        CustomTask definition = n.getContent().getDefinition();
        ServiceTaskPropertyWriter p = propertyWriterFactory.of(task);

        p.setServiceTaskName(definition.getName());

        TaskGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAssignmentsInfo(
                definition.getDataIOSet().getAssignmentsinfo());

        CustomTaskExecutionSet executionSet =
                definition.getExecutionSet();

        p.setTaskName(executionSet.getTaskName().getValue());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());
        p.setSlaDueDate(executionSet.getSlaDueDate().getValue());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);
        return p;
    }

    public PropertyWriter userTask(Node<View<BaseUserTask>, ?> n) {
        org.eclipse.bpmn2.UserTask task = bpmn2.createUserTask();
        task.setId(n.getUUID());
        BaseUserTask definition = n.getContent().getDefinition();
        UserTaskPropertyWriter p = propertyWriterFactory.of(task);

        TaskGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setSimulationSet(definition.getSimulationSet());

        BaseUserTaskExecutionSet executionSet = definition.getExecutionSet();

        p.setTaskName(executionSet.getTaskName().getValue());
        p.setActors(executionSet.getActors());
        p.setAssignmentsInfo(executionSet.getAssignmentsinfo());
        p.setReassignments(executionSet.getReassignmentsInfo());
        p.setNotifications(executionSet.getNotificationsInfo());
        p.setSkippable(executionSet.getSkippable().getValue());
        p.setGroupId(executionSet.getGroupid().getValue());
        p.setSubject(executionSet.getSubject().getValue());
        p.setDescription(executionSet.getDescription().getValue());
        p.setPriority(StringUtils.replaceIllegalCharsAttribute(executionSet.getPriority().getValue()));
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setCreatedBy(executionSet.getCreatedBy().getValue());
        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());
        if (Boolean.TRUE.equals(executionSet.getIsMultipleInstance().getValue())) {
            p.setIsSequential(executionSet.getMultipleInstanceExecutionMode().isSequential());
            p.setCollectionInput(executionSet.getMultipleInstanceCollectionInput().getValue());
            p.setInput(executionSet.getMultipleInstanceDataInput().getValue());
            p.setCollectionOutput(executionSet.getMultipleInstanceCollectionOutput().getValue());
            p.setOutput(executionSet.getMultipleInstanceDataOutput().getValue());
            p.setCompletionCondition(executionSet.getMultipleInstanceCompletionCondition().getValue());
        }
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setContent(executionSet.getContent().getValue());
        p.setSLADueDate(executionSet.getSlaDueDate().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);

        return p;
    }

    private PropertyWriter businessRuleTask(Node<View<BusinessRuleTask>, ?> n) {
        org.eclipse.bpmn2.BusinessRuleTask task = bpmn2.createBusinessRuleTask();
        task.setId(n.getUUID());
        BusinessRuleTask definition = n.getContent().getDefinition();
        BusinessRuleTaskPropertyWriter p = propertyWriterFactory.of(task);

        TaskGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        BusinessRuleTaskExecutionSet executionSet =
                definition.getExecutionSet();

        p.setAsync(executionSet.getIsAsync().getValue());
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());
        p.setSlaDueDate(executionSet.getSlaDueDate().getValue());

        RuleLanguage ruleLanguage = executionSet.getRuleLanguage();
        p.setImplementation(ruleLanguage);

        if (ruleLanguage.getValue().equals(RuleLanguage.DRL)) {
            p.setRuleFlowGroup(executionSet.getRuleFlowGroup());
        } else if (ruleLanguage.getValue().equals(RuleLanguage.DMN)) {
            p.setFileName(executionSet.getFileName());
            p.setNamespace(executionSet.getNamespace());
            p.setDecisionName(executionSet.getDecisionName());
            p.setDmnModelName(executionSet.getDmnModelName());
        }

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);
        return p;
    }

    private PropertyWriter scriptTask(Node<View<ScriptTask>, ?> n) {
        org.eclipse.bpmn2.ScriptTask task = bpmn2.createScriptTask();
        task.setId(n.getUUID());
        ScriptTask definition = n.getContent().getDefinition();
        ScriptTaskPropertyWriter p = propertyWriterFactory.of(task);

        TaskGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        ScriptTaskExecutionSet executionSet = definition.getExecutionSet();

        p.setScript(executionSet.getScript().getValue());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);
        return p;
    }

    private PropertyWriter noneTask(Node<View<NoneTask>, ?> n) {
        Task task = bpmn2.createTask();
        task.setId(n.getUUID());
        NoneTask definition = n.getContent().getDefinition();
        ActivityPropertyWriter p = propertyWriterFactory.of(task);

        TaskGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);

        p.setSimulationSet(definition.getSimulationSet());
        return p;
    }
}

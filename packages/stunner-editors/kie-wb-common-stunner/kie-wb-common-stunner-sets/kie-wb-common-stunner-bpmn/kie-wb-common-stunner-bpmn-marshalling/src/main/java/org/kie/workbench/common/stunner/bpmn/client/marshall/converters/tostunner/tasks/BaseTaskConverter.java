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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.tasks;

import java.util.Optional;

import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.BPMNElementDecorators;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.AbstractConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.BusinessRuleTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.GenericServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ScriptTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ServiceTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.TaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DecisionName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DmnModelName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.EmptyTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.FileName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Namespace;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Script;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public abstract class BaseTaskConverter<U extends BaseUserTask<S>, S extends BaseUserTaskExecutionSet>
        extends AbstractConverter implements NodeConverter<Task> {

    protected final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public BaseTaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory,
                             Mode mode) {
        super(mode);
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    @Override
    public Result<BpmnNode> convert(Task task) {
        return Match.<Task, BpmnNode>of()
                .when(e -> e instanceof org.eclipse.bpmn2.BusinessRuleTask, this::businessRuleTask)
                .when(e -> e instanceof org.eclipse.bpmn2.ScriptTask, this::scriptTask)
                .when(e -> e instanceof org.eclipse.bpmn2.UserTask, this::userTask)
                .when(e -> e instanceof org.eclipse.bpmn2.ServiceTask, this::serviceTaskResolver)
                .when(e -> org.eclipse.bpmn2.impl.TaskImpl.class.equals(e.getClass()), this::defaultTaskResolver)
                .missing(e -> e instanceof ManualTask, ManualTask.class)
                .missing(e -> e instanceof CustomTask, SendTask.class)
                .missing(e -> e instanceof ReceiveTask, ReceiveTask.class)
                .orElse(this::defaultTaskResolver)
                .inputDecorator(BPMNElementDecorators.flowElementDecorator())
                .outputDecorator(BPMNElementDecorators.bpmnNodeDecorator())
                .mode(getMode())
                .apply(task);
    }

    private BpmnNode jbpmServiceTask(Task task) {
        final ServiceTaskPropertyReader serviceTaskPropertyReader = propertyReaderFactory.ofCustom(task);
        final Node<View<CustomTask>, Edge> node = factoryManager.newNode(task.getId(), CustomTask.class);
        final CustomTask definition = node.getContent().getDefinition();

        definition.setName(serviceTaskPropertyReader.getServiceTaskName());
        definition.getTaskType().setRawType(serviceTaskPropertyReader.getServiceTaskName());
        definition.setDescription(serviceTaskPropertyReader.getServiceTaskDescription());
        definition.setCategory(serviceTaskPropertyReader.getServiceTaskCategory());
        definition.setDefaultHandler(serviceTaskPropertyReader.getServiceTaskDefaultHandler());

        definition.setGeneral(new TaskGeneralSet(
                new Name(serviceTaskPropertyReader.getName()),
                new Documentation(serviceTaskPropertyReader.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                serviceTaskPropertyReader.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new CustomTaskExecutionSet(
                new TaskName(serviceTaskPropertyReader.getTaskName()),
                new IsAsync(serviceTaskPropertyReader.isAsync()),
                new AdHocAutostart(serviceTaskPropertyReader.isAdHocAutoStart()),
                new OnEntryAction(serviceTaskPropertyReader.getOnEntryAction()),
                new OnExitAction(serviceTaskPropertyReader.getOnExitAction()),
                new SLADueDate(serviceTaskPropertyReader.getSlaDueDate())
        ));

        definition.setSimulationSet(serviceTaskPropertyReader.getSimulationSet());

        node.getContent().setBounds(serviceTaskPropertyReader.getBounds());

        definition.setDimensionsSet(serviceTaskPropertyReader.getRectangleDimensionsSet());
        definition.setBackgroundSet(serviceTaskPropertyReader.getBackgroundSet());
        definition.setFontSet(serviceTaskPropertyReader.getFontSet());
        definition.setAdvancedData(new AdvancedData(serviceTaskPropertyReader.getMetaDataAttributes()));

        return BpmnNode.of(node, serviceTaskPropertyReader);
    }

    BpmnNode bpmnServiceTask(org.eclipse.bpmn2.ServiceTask task) {
        Node<View<GenericServiceTask>, Edge> node = factoryManager.newNode(task.getId(), GenericServiceTask.class);

        GenericServiceTask definition = node.getContent().getDefinition();
        GenericServiceTaskPropertyReader p = propertyReaderFactory.of(task);

        if (p == null) {
            throw new NullPointerException(task.getClass().getCanonicalName());
        }

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new GenericServiceTaskExecutionSet(
                new GenericServiceTaskInfo(p.getGenericServiceTask()),
                p.getAssignmentsInfo(),
                new AdHocAutostart(p.isAdHocAutostart()),
                new IsAsync(p.isAsync()),
                new IsMultipleInstance(p.isMultipleInstance()),
                new MultipleInstanceExecutionMode(p.isSequential()),
                new MultipleInstanceCollectionInput(p.getCollectionInput()),
                new MultipleInstanceDataInput(p.getDataInput()),
                new MultipleInstanceCollectionOutput(p.getCollectionOutput()),
                new MultipleInstanceDataOutput(p.getDataOutput()),
                new MultipleInstanceCompletionCondition(p.getCompletionCondition()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new SLADueDate(p.getSLADueDate())));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
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

        RuleLanguage ruleLanguage = new RuleLanguage(p.getImplementation());
        RuleFlowGroup ruleFlowGroup = null;
        FileName fileName = null;
        Namespace namespace = null;
        DecisionName decisionName = null;
        DmnModelName dmnModelName = null;

        if (ruleLanguage.getValue().equals(RuleLanguage.DRL)) {
            ruleFlowGroup = new RuleFlowGroup(p.getRuleFlowGroup());
            namespace = new Namespace();
            decisionName = new DecisionName();
            dmnModelName = new DmnModelName();
        } else if (ruleLanguage.getValue().equals(RuleLanguage.DMN)) {
            ruleFlowGroup = new RuleFlowGroup();
            fileName = new FileName(p.getFileName());
            namespace = new Namespace(p.getNamespace());
            decisionName = new DecisionName(p.getDecisionName());
            dmnModelName = new DmnModelName(p.getDmnModelName());
        }

        definition.setExecutionSet(new BusinessRuleTaskExecutionSet(
                new RuleLanguage(p.getImplementation()),
                ruleFlowGroup,
                fileName,
                namespace,
                decisionName,
                dmnModelName,
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new IsAsync(p.isAsync()),
                new AdHocAutostart(p.isAdHocAutoStart()),
                new SLADueDate(p.getSlaDueDate())
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
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
                new IsAsync(p.isAsync()),
                new AdHocAutostart(p.isAdHocAutoStart())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());

        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
    }

    private BpmnNode userTask(org.eclipse.bpmn2.UserTask task) {
        Node<View<U>, Edge> node = createNode(task.getId());

        U definition = node.getContent().getDefinition();
        UserTaskPropertyReader p = propertyReaderFactory.of(task);

        definition.setGeneral(new TaskGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setSimulationSet(
                p.getSimulationSet()
        );

        definition.setExecutionSet(createUserTaskExecutionSet(p));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
    }

    private BpmnNode defaultTaskResolver(Task task) {
        //in case serviceTaskName attribute is present handle as a Service Task, default is a None Task
        return Optional.ofNullable(CustomAttribute.serviceTaskName.of(task).get())
                .filter(ConverterUtils::nonEmpty)
                .map(name -> jbpmServiceTask(task))
                .orElseGet(() -> noneTask(task));
    }

    private BpmnNode serviceTaskResolver(final Task task) {
        org.eclipse.bpmn2.ServiceTask serviceTask = (org.eclipse.bpmn2.ServiceTask) task;
        if (StringUtils.nonEmpty(CustomAttribute.serviceImplementation.of(task).get())
                || StringUtils.nonEmpty(serviceTask.getImplementation())) {
            return bpmnServiceTask(serviceTask);
        } else {
            return jbpmServiceTask(task);
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
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
    }

    protected abstract Node<View<U>, Edge> createNode(String id);

    protected abstract S createUserTaskExecutionSet(UserTaskPropertyReader p);
}

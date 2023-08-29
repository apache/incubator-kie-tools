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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.processes;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.AdHocSubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.MultipleInstanceSubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.SubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseAdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.cast;

public class SubProcessConverter extends ProcessConverterDelegate {

    private final DefinitionsBuildingContext context;
    private final PropertyWriterFactory propertyWriterFactory;

    public SubProcessConverter(DefinitionsBuildingContext context,
                               PropertyWriterFactory propertyWriterFactory,
                               ConverterFactory converterFactory) {
        super(converterFactory);
        this.context = context;
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public Result<SubProcessPropertyWriter> convertSubProcess(Node<View<? extends BPMNViewDefinition>, ?> node) {
        final Result<SubProcessPropertyWriter> processRootResult;
        final BPMNViewDefinition def = node.getContent().getDefinition();
        if (def instanceof EmbeddedSubprocess) {
            processRootResult = Result.success(convertEmbeddedSubprocessNode(cast(node)));
        } else if (def instanceof EventSubprocess) {
            processRootResult = Result.success(convertEventSubprocessNode(cast(node)));
        } else if (def instanceof BaseAdHocSubprocess) {
            processRootResult = Result.success(convertAdHocSubprocessNode(cast(node)));
        } else if (def instanceof MultipleInstanceSubprocess) {
            processRootResult = Result.success(convertMultipleInstanceSubprocessNode(cast(node)));
        } else {
            return Result.ignored("unknown type");
        }

        DefinitionsBuildingContext subContext = context.withRootNode(node);
        SubProcessPropertyWriter processRoot = processRootResult.value();

        super.convertChildNodes(processRoot, subContext);
        super.convertEdges(processRoot, subContext);

        return processRootResult;
    }

    protected SubProcessPropertyWriter convertMultipleInstanceSubprocessNode(Node<View<MultipleInstanceSubprocess>, ?> n) {
        SubProcess process = bpmn2.createSubProcess();
        process.setId(n.getUUID());

        MultipleInstanceSubProcessPropertyWriter p = propertyWriterFactory.ofMultipleInstanceSubProcess(process);

        MultipleInstanceSubprocess definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        ProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        MultipleInstanceSubprocessTaskExecutionSet executionSet = definition.getExecutionSet();
        p.setIsSequential(executionSet.getMultipleInstanceExecutionMode().isSequential());
        p.setCollectionInput(executionSet.getMultipleInstanceCollectionInput().getValue());
        p.setInput(executionSet.getMultipleInstanceDataInput().getValue());
        p.setCollectionOutput(executionSet.getMultipleInstanceCollectionOutput().getValue());
        p.setOutput(executionSet.getMultipleInstanceDataOutput().getValue());
        p.setCompletionCondition(executionSet.getMultipleInstanceCompletionCondition().getValue());
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setSlaDueDate(executionSet.getSlaDueDate());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);

        return p;
    }

    protected SubProcessPropertyWriter convertAdHocSubprocessNode(Node<View<BaseAdHocSubprocess>, ?> n) {
        org.eclipse.bpmn2.AdHocSubProcess process = bpmn2.createAdHocSubProcess();
        process.setId(n.getUUID());

        AdHocSubProcessPropertyWriter p = propertyWriterFactory.of(process);
        BaseAdHocSubprocess definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();

        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        BaseProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        BaseAdHocSubprocessTaskExecutionSet executionSet = definition.getExecutionSet();
        p.setAdHocActivationCondition(executionSet.getAdHocActivationCondition());
        p.setAdHocCompletionCondition(executionSet.getAdHocCompletionCondition());
        p.setAdHocOrdering(executionSet.getAdHocOrdering());
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setSlaDueDate(executionSet.getSlaDueDate());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);

        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());

        return p;
    }

    protected SubProcessPropertyWriter convertEventSubprocessNode(Node<View<EventSubprocess>, ?> n) {
        SubProcess process = bpmn2.createSubProcess();
        process.setId(n.getUUID());

        SubProcessPropertyWriter p = propertyWriterFactory.of(process);

        EventSubprocess definition = n.getContent().getDefinition();
        process.setTriggeredByEvent(true);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        ProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        EventSubprocessExecutionSet executionSet = definition.getExecutionSet();
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setSlaDueDate(executionSet.getSlaDueDate());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);

        return p;
    }

    protected SubProcessPropertyWriter convertEmbeddedSubprocessNode(Node<View<EmbeddedSubprocess>, ?> n) {
        SubProcess process = bpmn2.createSubProcess();
        process.setId(n.getUUID());

        SubProcessPropertyWriter p = propertyWriterFactory.of(process);

        EmbeddedSubprocess definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        EmbeddedSubprocessExecutionSet executionSet = definition.getExecutionSet();

        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setSlaDueDate(executionSet.getSlaDueDate());

        ProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAbsoluteBounds(n);
        return p;
    }
}
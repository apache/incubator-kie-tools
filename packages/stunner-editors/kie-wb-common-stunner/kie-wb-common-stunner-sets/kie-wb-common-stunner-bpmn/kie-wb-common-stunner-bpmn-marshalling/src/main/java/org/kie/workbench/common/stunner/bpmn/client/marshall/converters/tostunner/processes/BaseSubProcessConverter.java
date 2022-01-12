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
package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.ResultComposer;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AdHocSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.MultipleInstanceSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.SubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseAdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class BaseSubProcessConverter<A extends BaseAdHocSubprocess<P, S>,
        P extends BaseProcessData, S extends BaseAdHocSubprocessTaskExecutionSet> {

    final ProcessConverterDelegate delegate;

    public BaseSubProcessConverter(TypedFactoryManager typedFactoryManager,
                                   PropertyReaderFactory propertyReaderFactory,
                                   DefinitionResolver definitionResolver,
                                   BaseConverterFactory converterFactory) {
        this.delegate = new ProcessConverterDelegate(typedFactoryManager,
                                                     propertyReaderFactory,
                                                     definitionResolver,
                                                     converterFactory);
    }

    public Result<BpmnNode> convertSubProcess(SubProcess subProcess) {
        BpmnNode subProcessRoot;
        if (subProcess instanceof org.eclipse.bpmn2.AdHocSubProcess) {
            subProcessRoot = convertAdHocSubProcess((org.eclipse.bpmn2.AdHocSubProcess) subProcess);
        } else if (subProcess.getLoopCharacteristics() != null) {
            subProcessRoot = convertMultInstanceSubprocessNode(subProcess);
        } else if (subProcess.isTriggeredByEvent()) {
            subProcessRoot = convertEventSubprocessNode(subProcess);
        } else {
            subProcessRoot = convertEmbeddedSubprocessNode(subProcess);
        }

        Result<Map<String, BpmnNode>> nodesResult = delegate.convertChildNodes(subProcessRoot,
                                                                               subProcess.getFlowElements(),
                                                                               subProcess.getLaneSets());
        Map<String, BpmnNode> nodes = nodesResult.value();

        Result<Boolean> edgesResult = delegate.convertEdges(subProcessRoot,
                                                            Stream.concat(subProcess.getFlowElements().stream(),
                                                                          subProcess.getArtifacts().stream()).collect(Collectors.toList()),
                                                            nodes);

        return ResultComposer.compose(subProcessRoot, nodesResult, edgesResult);
    }

    private BpmnNode convertMultInstanceSubprocessNode(SubProcess subProcess) {
        Node<View<MultipleInstanceSubprocess>, Edge> node = delegate.factoryManager.newNode(subProcess.getId(),
                                                                                            MultipleInstanceSubprocess.class);

        MultipleInstanceSubprocess definition = node.getContent().getDefinition();
        MultipleInstanceSubProcessPropertyReader p = delegate.propertyReaderFactory.ofMultipleInstance(subProcess);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()),
                                                 new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(
                new MultipleInstanceSubprocessTaskExecutionSet(new MultipleInstanceExecutionMode(p.isSequential()),
                                                               new MultipleInstanceCollectionInput(p.getCollectionInput()),
                                                               new MultipleInstanceCollectionOutput(p.getCollectionOutput()),
                                                               new MultipleInstanceDataInput(p.getDataInput()),
                                                               new MultipleInstanceDataOutput(p.getDataOutput()),
                                                               new MultipleInstanceCompletionCondition(p.getCompletionCondition()),
                                                               new OnEntryAction(p.getOnEntryAction()),
                                                               new OnExitAction(p.getOnExitAction()),
                                                               new IsMultipleInstance(true),
                                                               new IsAsync(p.isAsync()),
                                                               new SLADueDate(p.getSlaDueDate())
                ));

        definition.setProcessData(new ProcessData(new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode convertAdHocSubProcess(org.eclipse.bpmn2.AdHocSubProcess subProcess) {
        Node<View<A>, Edge> node = createNode(subProcess.getId());
        A definition = node.getContent().getDefinition();
        AdHocSubProcessPropertyReader p = delegate.propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()),
                                                 new Documentation(p.getDocumentation())
        ));

        definition.setProcessData(createProcessData(p.getProcessVariables()));

        definition.setExecutionSet(createAdHocSubprocessTaskExecutionSet(p));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
    }

    private BpmnNode convertEmbeddedSubprocessNode(SubProcess subProcess) {
        Node<View<EmbeddedSubprocess>, Edge> node = delegate.factoryManager.newNode(subProcess.getId(),
                                                                                    EmbeddedSubprocess.class);

        EmbeddedSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = delegate.propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()),
                                                 new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new EmbeddedSubprocessExecutionSet(new OnEntryAction(p.getOnEntryAction()),
                                                                      new OnExitAction(p.getOnExitAction()),
                                                                      new IsAsync(p.isAsync()),
                                                                      new SLADueDate(p.getSlaDueDate())
        ));

        definition.setProcessData(new ProcessData(new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return BpmnNode.of(node, p);
    }

    private BpmnNode convertEventSubprocessNode(SubProcess subProcess) {
        Node<View<EventSubprocess>, Edge> node = delegate.factoryManager.newNode(subProcess.getId(),
                                                                                 EventSubprocess.class);

        EventSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = delegate.propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()),
                                                 new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new EventSubprocessExecutionSet(new IsAsync(p.isAsync()),
                                                                   new SLADueDate(p.getSlaDueDate())));

        definition.setProcessData(new ProcessData(new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    protected abstract Node<View<A>, Edge> createNode(String id);

    protected abstract P createProcessData(String processVariables);

    protected abstract S createAdHocSubprocessTaskExecutionSet(AdHocSubProcessPropertyReader p);
}

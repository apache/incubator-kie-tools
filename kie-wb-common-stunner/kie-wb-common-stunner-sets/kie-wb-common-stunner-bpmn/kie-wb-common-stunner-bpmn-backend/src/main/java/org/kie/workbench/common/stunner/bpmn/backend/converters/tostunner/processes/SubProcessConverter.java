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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AdHocSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.MultipleInstanceSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MITrigger;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SubProcessConverter {

    private final ProcessConverterDelegate delegate;

    public SubProcessConverter(
            TypedFactoryManager typedFactoryManager,
            PropertyReaderFactory propertyReaderFactory,
            DefinitionResolver definitionResolver,
            ConverterFactory converterFactory) {

        this.delegate = new ProcessConverterDelegate(typedFactoryManager, propertyReaderFactory, definitionResolver, converterFactory);
    }

    public BpmnNode convertSubProcess(SubProcess subProcess) {
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

        Map<String, BpmnNode> nodes =
                delegate.convertChildNodes(
                        subProcessRoot,
                        subProcess.getFlowElements(),
                        subProcess.getLaneSets());

        delegate.convertEdges(
                subProcessRoot,
                Stream.concat(subProcess.getFlowElements().stream(),
                              subProcess.getArtifacts().stream()).collect(Collectors.toList()),
                nodes);

        return subProcessRoot;
    }

    private BpmnNode convertMultInstanceSubprocessNode(SubProcess subProcess) {
        Node<View<MultipleInstanceSubprocess>, Edge> node =
                delegate.factoryManager.newNode(subProcess.getId(), MultipleInstanceSubprocess.class);

        MultipleInstanceSubprocess definition = node.getContent().getDefinition();
        MultipleInstanceSubProcessPropertyReader p = delegate.propertyReaderFactory.ofMultipleInstance(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(p.getCollectionInput()),
                new MultipleInstanceCollectionOutput(p.getCollectionOutput()),
                new MultipleInstanceDataInput(p.getDataInput()),
                new MultipleInstanceDataOutput(p.getDataOutput()),
                new MultipleInstanceCompletionCondition(p.getCompletionCondition()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new MITrigger("true"),
                new IsAsync(p.isAsync())
        ));

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node);
    }

    private BpmnNode convertAdHocSubProcess(org.eclipse.bpmn2.AdHocSubProcess subProcess) {
        Node<View<AdHocSubprocess>, Edge> node =
                delegate.factoryManager.newNode(subProcess.getId(), AdHocSubprocess.class);
        AdHocSubprocess definition = node.getContent().getDefinition();
        AdHocSubProcessPropertyReader p = delegate.propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        definition.setExecutionSet(new AdHocSubprocessTaskExecutionSet(
                new AdHocCompletionCondition(p.getAdHocCompletionCondition()),
                new AdHocOrdering(p.getAdHocOrdering()),
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction())
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode convertEmbeddedSubprocessNode(SubProcess subProcess) {
        Node<View<EmbeddedSubprocess>, Edge> node =
                delegate.factoryManager.newNode(subProcess.getId(), EmbeddedSubprocess.class);

        EmbeddedSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = delegate.propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new EmbeddedSubprocessExecutionSet(
                new OnEntryAction(p.getOnEntryAction()),
                new OnExitAction(p.getOnExitAction()),
                new IsAsync(p.isAsync())
        ));

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode convertEventSubprocessNode(SubProcess subProcess) {
        Node<View<EventSubprocess>, Edge> node =
                delegate.factoryManager.newNode(subProcess.getId(), EventSubprocess.class);

        EventSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = delegate.propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new EventSubprocessExecutionSet(
                new IsAsync(p.isAsync())
        ));

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node);
    }
}
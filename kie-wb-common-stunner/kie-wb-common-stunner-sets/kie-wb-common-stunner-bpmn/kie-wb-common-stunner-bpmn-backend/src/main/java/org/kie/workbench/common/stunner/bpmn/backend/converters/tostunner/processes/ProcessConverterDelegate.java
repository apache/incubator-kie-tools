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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LaneSet;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;

/**
 * Creates converters for Processes and SubProcesses
 * <p>
 * Processes and SubProcesses are alike, but are not exactly compatible
 * type-wise. However, they may contain the same type of nodes.
 * ProcessConverterFactory returns instances of ProcessConverters
 * and SubprocessConverters.
 */
final class ProcessConverterDelegate {

    protected final TypedFactoryManager factoryManager;
    protected final PropertyReaderFactory propertyReaderFactory;
    protected final DefinitionResolver definitionResolver;
    private final ConverterFactory converterFactory;

    ProcessConverterDelegate(
            TypedFactoryManager typedFactoryManager,
            PropertyReaderFactory propertyReaderFactory,
            DefinitionResolver definitionResolver,
            ConverterFactory factory) {

        this.factoryManager = typedFactoryManager;
        this.definitionResolver = definitionResolver;
        this.propertyReaderFactory = propertyReaderFactory;
        this.converterFactory = factory;
    }

    Map<String, BpmnNode> convertChildNodes(
            BpmnNode firstNode,
            List<FlowElement> flowElements,
            List<LaneSet> laneSets) {

        Map<String, BpmnNode> freeFloatingNodes =
                convertFlowElements(flowElements);

        freeFloatingNodes.values()
                .forEach(n -> n.setParent(firstNode));

        convertLaneSets(laneSets, freeFloatingNodes, firstNode);

        return freeFloatingNodes;
    }

    void convertEdges(BpmnNode processRoot, List<BaseElement> flowElements, Map<String, BpmnNode> nodes) {
        flowElements.stream()
                .map(e -> converterFactory.edgeConverter().convertEdge(e, nodes))
                .filter(Result::isSuccess)
                .map(Result::value)
                .forEach(processRoot::addEdge);
    }

    private Map<String, BpmnNode> convertFlowElements(List<FlowElement> flowElements) {
        LinkedHashMap<String, BpmnNode> result = new LinkedHashMap<>();

        flowElements
                .stream()
                .map(converterFactory.flowElementConverter()::convertNode)
                .filter(Result::isSuccess)
                .map(Result::value)
                .forEach(n -> result.put(n.value().getUUID(), n));

        return result;
    }

    private void convertLaneSets(List<LaneSet> laneSets, Map<String, BpmnNode> freeFloatingNodes, BpmnNode firstDiagramNode) {
        laneSets.stream()
                .flatMap(laneSet -> laneSet.getLanes().stream())
                .forEach(lane -> {
                    BpmnNode laneNode = converterFactory.laneConverter().convert(lane);
                    laneNode.setParent(firstDiagramNode);

                    lane.getFlowNodeRefs().forEach(node -> {
                        freeFloatingNodes.get(node.getId()).setParent(laneNode);
                    });
                });
    }
}

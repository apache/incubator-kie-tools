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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.ResultComposer;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
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
    private final BaseConverterFactory converterFactory;

    ProcessConverterDelegate(
            TypedFactoryManager typedFactoryManager,
            PropertyReaderFactory propertyReaderFactory,
            DefinitionResolver definitionResolver,
            BaseConverterFactory factory) {

        this.factoryManager = typedFactoryManager;
        this.definitionResolver = definitionResolver;
        this.propertyReaderFactory = propertyReaderFactory;
        this.converterFactory = factory;
    }

    Result<Map<String, BpmnNode>> convertChildNodes(
            BpmnNode firstNode,
            List<FlowElement> flowElements,
            List<LaneSet> laneSets) {

        final Result<Map<String, BpmnNode>> flowElementsResult = convertFlowElements(flowElements);
        final Map<String, BpmnNode> freeFloatingNodes = flowElementsResult.value();

        freeFloatingNodes
                .values()
                .forEach(n -> n.setParent(firstNode));

        final Result<BpmnNode>[] laneSetsResult = convertLaneSets(laneSets, freeFloatingNodes, firstNode);

        final Map<String, BpmnNode> lanesMap = Stream.of(laneSetsResult)
                .filter(Objects::nonNull).map(v -> v.value())
                .collect(Collectors.toMap(n -> n.value().getUUID(), n -> n));
        freeFloatingNodes.putAll(lanesMap);

        return ResultComposer.compose(freeFloatingNodes, flowElementsResult, ResultComposer.compose(laneSets, laneSetsResult));
    }

    Result<Boolean> convertEdges(BpmnNode processRoot, List<BaseElement> flowElements, Map<String, BpmnNode> nodes) {

        List<Result<BpmnEdge>> results = flowElements.stream()
                .map(e -> converterFactory.edgeConverter().convertEdge(e, nodes))
                .filter(Result::isSuccess)
                .collect(Collectors.toList());

        boolean value = results.size() > 0 ?
                results.stream()
                        .map(Result::value)
                        .filter(Objects::nonNull)
                        .map(processRoot::addEdge)
                        .allMatch(Boolean.TRUE::equals)
                : false;

        return ResultComposer.compose(value, results);
    }

    private Result<Map<String, BpmnNode>> convertFlowElements(List<FlowElement> flowElements) {
        final List<Result<BpmnNode>> results = flowElements
                .stream()
                .map(converterFactory.flowElementConverter()::convertNode)
                .collect(Collectors.toList());

        final Map<String, BpmnNode> resultMap = results.stream()
                .map(Result::value)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(n -> n.value().getUUID(), n -> n));

        return ResultComposer.compose(resultMap, results);
    }

    private Result<BpmnNode>[] convertLaneSets(List<LaneSet> laneSets, Map<String, BpmnNode> freeFloatingNodes, BpmnNode firstDiagramNode) {
        final Result<BpmnNode>[] results = laneSets.stream()
                .map(laneSet -> convertLaneSet(laneSet, new ArrayList<>(), freeFloatingNodes, firstDiagramNode))
                .flatMap(Stream::of)
                .toArray(Result[]::new);
        return results;
    }

    private Result<BpmnNode>[] convertLane(Lane lane, List<Lane> parents, Map<String, BpmnNode> freeFloatingNodes, BpmnNode firstDiagramNode) {
        if (lane.getChildLaneSet() != null) {
            parents.add(lane);
            Result<BpmnNode>[] laneSetResult = convertLaneSet(lane.getChildLaneSet(), parents, freeFloatingNodes, firstDiagramNode);
            parents.removeIf(parent -> Objects.equals(parent.getId(), lane.getId()));
            //return ResultComposer.compose(null, laneSetResult);
            return laneSetResult;
        } else {
            Result<BpmnNode> laneResult;
            if (!parents.isEmpty() && lane != parents.get(0)) {
                laneResult = converterFactory.laneConverter().convert(lane, parents.get(0));
            } else {
                laneResult = converterFactory.laneConverter().convert(lane);
            }
            final Optional<BpmnNode> value = Optional.ofNullable(laneResult.value());
            value.ifPresent(laneNode -> laneNode.setParent(firstDiagramNode));
            value.ifPresent(laneNode -> lane.getFlowNodeRefs()
                    .forEach(node -> freeFloatingNodes.get(node.getId()).setParent(laneNode)));
            return new Result[]{laneResult};
        }
    }

    private Result<BpmnNode>[] convertLaneSet(LaneSet laneSet, List<Lane> parents, Map<String, BpmnNode> freeFloatingNodes,
                                              BpmnNode firstDiagramNode) {
        Result<BpmnNode>[] results = laneSet.getLanes()
                .stream()
                .map(lane -> convertLane(lane, parents, freeFloatingNodes, firstDiagramNode))
                .flatMap(Stream::of)
                .toArray(Result[]::new);

        //return ResultComposer.compose(laneSet, results);
        return results;
    }

    Result<BpmnNode> postConvert(BpmnNode processRoot) {
        return converterFactory.newProcessPostConverter().postConvert(processRoot, definitionResolver);
    }
}

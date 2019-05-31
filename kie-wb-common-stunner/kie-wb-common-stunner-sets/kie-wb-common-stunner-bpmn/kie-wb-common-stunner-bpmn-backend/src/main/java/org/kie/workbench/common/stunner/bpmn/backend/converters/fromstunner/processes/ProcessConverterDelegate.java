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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BoundaryEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.LanePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.SubProcessPropertyWriter;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

class ProcessConverterDelegate {

    private final ConverterFactory converterFactory;

    ProcessConverterDelegate(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    void convertChildNodes(
            ElementContainer p,
            DefinitionsBuildingContext context) {

        List<SubProcessPropertyWriter> subprocesses =
                context.nodes().map(converterFactory.subProcessConverter()::convertSubProcess)
                        .filter(Result::notIgnored)
                        .map(Result::value)
                        .collect(toList());

        // keep track of nested children to avoid adding them again to ancestors
        Set<String> processed = subprocesses.stream()
                .flatMap(sub -> sub.getChildElements().stream().map(BasePropertyWriter::getId))
                .collect(toSet());

        subprocesses.stream()
                .filter(e -> !processed.contains(e.getId())) // skip processed
                .forEach(p::addChildElement);

        context.nodes()
                .filter(e -> !processed.contains(e.getUUID())) // skip processed
                .map(converterFactory.viewDefinitionConverter()::toFlowElement)
                .filter(Result::notIgnored)
                .map(Result::value)
                .forEach(p::addChildElement);

        convertLanes(context, processed, p);
    }

    private void convertLanes(
            DefinitionsBuildingContext context,
            Set<String> processed, ElementContainer p) {
        LaneConverter laneConverter = converterFactory.laneConverter();
        List<LanePropertyWriter> convertedLanes = context.lanes()
                .map(laneConverter::toElement)
                .filter(Result::isSuccess)
                .map(Result::value)
                .peek(convertedLane -> {
                    // for each lane, we get the child nodes in the graph
                    context.withRootNode(convertedLane.getId()).childNodes()
                            .filter(n -> !processed.contains(n.getUUID()))
                            // then for each converted element, we re-set its parent to the converted lane
                            .forEach(n -> p.getChildElement(n.getUUID()).setParent(convertedLane));
                })
                .collect(toList());

        p.addLaneSet(convertedLanes);
    }

    void convertEdges(ElementContainer p, DefinitionsBuildingContext context) {
        context.dockEdges()
                .forEach(e -> {
                    ActivityPropertyWriter pSrc =
                            (ActivityPropertyWriter) p.getChildElement(e.getSourceNode().getUUID());
                    BoundaryEventPropertyWriter pTgt =
                            (BoundaryEventPropertyWriter) p.getChildElement(e.getTargetNode().getUUID());
                    // if it's null, then this edge is not related to this process. ignore.
                    if (pTgt != null) {
                        pTgt.setParentActivity(pSrc);
                    }
                });

        context.edges()
                .map(e -> converterFactory.edgeElementConverter().toFlowElement(e, p))
                .filter(Result::isSuccess)
                .map(Result::value)
                .forEach(p::addChildElement);
    }

    void postConvertChildNodes(ProcessPropertyWriter processWriter,
                               DefinitionsBuildingContext context) {
        final Map<String, BasePropertyWriter> propertyWriters = collectPropertyWriters(processWriter);
        context.nodes().forEach(node -> converterFactory.flowElementPostConverter().postConvert(processWriter,
                                                                                                propertyWriters.get(node.getUUID()),
                                                                                                node));
    }

    private Map<String, BasePropertyWriter> collectPropertyWriters(ElementContainer container) {

        final Map<String, BasePropertyWriter> result = container.getChildElements()
                .stream()
                .collect(Collectors.toMap(BasePropertyWriter::getId,
                                          p -> p));

        container.getChildElements()
                .stream()
                .filter(e -> e instanceof ElementContainer)
                .map(e -> (ElementContainer) e)
                .map(this::collectPropertyWriters)
                .collect(Collectors.toList())
                .forEach(result::putAll);

        return result;
    }
}
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

class TestDefinitionsWriter {

    private final DefinitionResolver definitionResolver;

    TestDefinitionsWriter() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        this.definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
    }

    DefinitionResolver getDefinitionResolver() {
        return definitionResolver;
    }

    FlowNode mockNode(String id, Bounds bounds) {
        Task node = bpmn2.createTask();
        node.setId(id);

        BPMNShape shape = di.createBPMNShape();
        shape.setBounds(bounds);
        shape.setBpmnElement(node);
        definitionResolver.getPlane().getPlaneElement().add(shape);

        return node;
    }

    SequenceFlow sequenceFlowOf(String id, FlowNode source, FlowNode target, List<Point> waypoints) {
        SequenceFlow sequenceFlow = bpmn2.createSequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setSourceRef(source);
        sequenceFlow.setTargetRef(target);

        BPMNEdge edge = di.createBPMNEdge();
        edge.setBpmnElement(sequenceFlow);
        definitionResolver.getPlane().getPlaneElement().add(edge);
        edge.getWaypoint().addAll(waypoints);

        return sequenceFlow;
    }
}

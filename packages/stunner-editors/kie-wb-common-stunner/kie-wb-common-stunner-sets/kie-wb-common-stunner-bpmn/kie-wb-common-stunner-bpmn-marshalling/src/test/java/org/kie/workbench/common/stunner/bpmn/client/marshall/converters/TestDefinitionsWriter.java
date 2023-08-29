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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;

public class TestDefinitionsWriter {

    private final DefinitionResolver definitionResolver;
    private BPMNPlane bpmnPlane;

    public TestDefinitionsWriter() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnPlane = di.createBPMNPlane();
        bpmnDiagram.setPlane(bpmnPlane);
        definitions.getDiagrams().add(bpmnDiagram);

        this.definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
    }

    public DefinitionResolver getDefinitionResolver() {
        return definitionResolver;
    }

    public FlowNode mockNode(String id, Bounds bounds) {
        Task node = bpmn2.createTask();
        node.setId(id);

        BPMNShape shape = di.createBPMNShape();
        shape.setBounds(bounds);
        shape.setBpmnElement(node);
        bpmnPlane.getPlaneElement().add(shape);

        return node;
    }

    public SequenceFlow sequenceFlowOf(String id, FlowNode source, FlowNode target, List<Point> waypoints) {
        SequenceFlow sequenceFlow = bpmn2.createSequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setSourceRef(source);
        sequenceFlow.setTargetRef(target);

        BPMNEdge edge = di.createBPMNEdge();
        edge.setBpmnElement(sequenceFlow);
        bpmnPlane.getPlaneElement().add(edge);
        edge.getWaypoint().addAll(waypoints);

        return sequenceFlow;
    }
}

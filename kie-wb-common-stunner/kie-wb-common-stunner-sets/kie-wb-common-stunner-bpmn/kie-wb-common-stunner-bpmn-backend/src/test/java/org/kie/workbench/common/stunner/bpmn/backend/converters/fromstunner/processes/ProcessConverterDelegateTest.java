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

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class ProcessConverterDelegateTest {

    @Test
    public void convertDockedEdges() {
        GraphNodeStoreImpl nodes = new GraphNodeStoreImpl();
        GraphImpl g = new GraphImpl("g", nodes);

        NodeImpl<Object> root = new NodeImpl<>("root");
        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl();
        root.setContent(new ViewImpl<>(bpmnDiagram, BoundsImpl.build()));
        g.addNode(root);

        NodeImpl<Object> n = new NodeImpl<>("n");
        EmbeddedSubprocess subProcessNode = new EmbeddedSubprocess();
        n.setContent(new ViewImpl<>(subProcessNode, BoundsImpl.build()));
        g.addNode(n);

        NodeImpl<Object> e = new NodeImpl<>("e");
        IntermediateErrorEventCatching intermediateErrorEventCatching = new IntermediateErrorEventCatching();
        e.setContent(new ViewImpl<>(intermediateErrorEventCatching, BoundsImpl.build()));
        g.addNode(e);

        EdgeImpl<Object> edge = new EdgeImpl<>("edge");
        Dock dock = new Dock();
        edge.setContent(dock);

        n.getOutEdges().add(edge);
        edge.setSourceNode(n);
        e.getInEdges().add(edge);
        edge.setTargetNode(e);

        DefinitionsBuildingContext ctx = new DefinitionsBuildingContext(g);
        PropertyWriterFactory pwFactory = new PropertyWriterFactory();
        ConverterFactory factory = new ConverterFactory(ctx, pwFactory);

        MyProcessConverter abstractProcessConverter =
                new MyProcessConverter(factory);

        ProcessPropertyWriter p = pwFactory.of(bpmn2.createProcess());

        assertThatCode(() -> {
            abstractProcessConverter.convertChildNodes(p, ctx);
            abstractProcessConverter.convertEdges(p, ctx);
        }).doesNotThrowAnyException();
    }

    static class MyProcessConverter extends ProcessConverterDelegate {

        public MyProcessConverter(ConverterFactory converterFactory) {
            super(converterFactory);
        }
    }
}
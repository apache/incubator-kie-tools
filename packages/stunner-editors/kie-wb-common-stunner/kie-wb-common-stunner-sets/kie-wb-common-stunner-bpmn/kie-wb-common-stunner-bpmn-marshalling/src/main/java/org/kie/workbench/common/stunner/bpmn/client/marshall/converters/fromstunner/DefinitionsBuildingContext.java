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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

//
// the implementation for this class is in the package-private
// abstract class below. We are using type parameters to fake type aliases.
// Scroll down for details
//
public class DefinitionsBuildingContext
        extends DefinitionsContextHelper<
        /*EdgeT = */
        Edge<ViewConnector<BPMNViewDefinition>,
                Node<? extends View<? extends BPMNViewDefinition>, ?>>,

        /*NodeT = */
        Node<View<? extends BPMNViewDefinition>,
                Edge<ViewConnector<BPMNViewDefinition>,
                        Node<? extends View<? extends BPMNViewDefinition>, ?>>>
        > {

    // constructor uses raw Graph for convenience

    public DefinitionsBuildingContext(
            Graph<DefinitionSet,
                    Node<View<? extends BPMNViewDefinition>,
                            Edge<ViewConnector<BPMNViewDefinition>,
                                    Node<? extends View<? extends BPMNViewDefinition>, ?>>>> graph) {
        super(graph);
    }

    public DefinitionsBuildingContext(
            Graph<DefinitionSet,
                    Node<View<? extends BPMNViewDefinition>,
                            Edge<ViewConnector<BPMNViewDefinition>,
                                    Node<? extends View<? extends BPMNViewDefinition>, ?>>>> graph,
            Class<?> nodeType) {
        super(graph, nodeType);
    }

    public DefinitionsBuildingContext(
            Node<?, ?> firstNode,
            Map<String, Node> nodes) {
        super(firstNode, nodes);
    }
}

//
// this is sort-of a hack: we don't have type aliases in Java
// so we use this abstract class to bind a type-parameter to this horribly long
// Node, Edge declarations (because Node, Edge are... mutually recursive... erm)
// so we declare EdgeT, NodeT to "extend" the type we want to alias
// then in the concrete instance we actually **bind** them to the exact type
//
abstract class DefinitionsContextHelper<
        EdgeT extends
                Edge<ViewConnector<BPMNViewDefinition>,
                        Node<? extends View<? extends BPMNViewDefinition>, ?>>,
        NodeT extends
                Node<View<? extends BPMNViewDefinition>, EdgeT>
        > {

    private final Map<String, ? extends NodeT> nodes;

    private final Node firstNode;

    public DefinitionsContextHelper(Graph<DefinitionSet, NodeT> graph) {
        this(graph, BPMNDiagramImpl.class);
    }

    public DefinitionsContextHelper(Graph<DefinitionSet, NodeT> graph,
                                    Class<?> nodeType) {
        this.firstNode =
                GraphUtils.getFirstNode((Graph) graph, nodeType);

        this.nodes =
                StreamSupport
                        .stream(graph.nodes().spliterator(), false)
                        .filter(n -> !firstNode.getUUID().equals(n.getUUID()))
                        .collect(Collectors.toMap(Node::getUUID, Function.identity()));
    }

    public DefinitionsContextHelper(
            Node<?, ?> firstNode,
            Map<String, ? extends Node> nodes) {
        this.firstNode = firstNode;
        this.nodes = (Map<String, ? extends NodeT>) nodes;
    }

    public Stream<? extends NodeT> nodes() {
        return nodes.values().stream();
    }

    public NodeT getNode(String id) {
        return nodes.get(id);
    }

    public Node firstNode() {
        return firstNode;
    }

    public Stream<EdgeT> edges() {
        return nodes()
                .flatMap(e -> Stream.concat(
                        e.getInEdges().stream(),
                        e.getOutEdges().stream()))
                .distinct()
                .filter(e -> (isViewConnector(e)));
    }

    public Stream<EdgeT> dockEdges() {
        return nodes()
                .flatMap(e -> Stream.concat(
                        e.getInEdges().stream(),
                        e.getOutEdges().stream()))
                .distinct()
                .filter(e -> (isDock(e)));
    }

    public Stream<EdgeT> childEdges() {
        return nodes()
                .flatMap(e -> Stream.concat(
                        e.getInEdges().stream(),
                        e.getOutEdges().stream()))
                .distinct()
                .filter(e -> (isChild(e)));
    }

    public Stream<NodeT> childNodes() {
        return childEdges().map(edgeT -> (NodeT) (edgeT.getTargetNode()));
    }

    public DefinitionsBuildingContext withRootNode(String nodeId) {
        return new DefinitionsBuildingContext(getNode(nodeId), findEdgesBydId(nodeId));
    }

    public DefinitionsBuildingContext withRootNode(Node<?, ?> node) {
        return new DefinitionsBuildingContext(node, findEdgesBydId(node.getUUID()));
    }

    private Map<String, Node> findEdgesBydId(String nodeId) {
        Map<String, Node> nodes = new HashMap<>();
        childEdges()
                .filter(e -> e.getSourceNode().getUUID().equals(nodeId))
                .map(Edge::getTargetNode)
                .forEach(n -> nodes.put(n.getUUID(), n)); // use forEach instead of collect to avoid issues with type inference
        return nodes;
    }

    private boolean isChild(EdgeT e) {
        return e.getContent() instanceof Child;
    }

    private boolean isDock(EdgeT e) {
        return e.getContent() instanceof Dock;
    }

    private boolean isViewConnector(EdgeT e) {
        return e.getContent() instanceof ViewConnector;
    }

    public Stream<? extends NodeT> lanes() {
        return nodes().filter(this::isLane);
    }

    private boolean isLane(NodeT n) {
        return n.getContent().getDefinition() instanceof Lane;
    }
}

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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddDockedNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A wrapper for graph command execution,
 * exposing a simple, method-based API.
 * <p>
 * A `GraphBuilder` object issues commands to the canvas while building the graph.
 * It is a wrapper around
 * <ul>
 * <li>GraphCommandExecutionContext</li>
 * <li>GraphCommandFactory</li>
 * <li>GraphCommandManager</li>
 * </ul>
 * <p>
 * `GraphBuilder` is used for convenience, to avoid explicitly creating command instances.
 * It also implements custom logic for some actions. For example, in the case of adding child nodes,
 * it translates the coordinates of a child node into the new reference system (the parent boundaries).
 * <p>
 * `GraphBuilder` builds the entire graph {@link GraphBuilder#buildGraph(BpmnNode)}
 * once all the conversions have took place: it traverses the entire directed graph described by the `BPMNNode`s
 * starting from the "root node", which represents the root of the diagram, and visiting
 * the parent/child relations in each BPMNNode and the `BPMNEdge` they may contain.
 */
public class GraphBuilder {

    private final GraphCommandExecutionContext executionContext;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;
    private final Graph<DefinitionSet, Node> graph;

    public GraphBuilder(
            Graph<DefinitionSet, Node> graph,
            DefinitionManager definitionManager,
            TypedFactoryManager typedFactoryManager,
            RuleManager ruleManager,
            GraphCommandFactory commandFactory,
            GraphCommandManager commandManager) {
        this.graph = graph;
        this.executionContext = new DirectGraphCommandExecutionContext(
                definitionManager,
                typedFactoryManager.untyped(),
                new MapIndexBuilder().build(graph));
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    /**
     * Clears the context and then walks the graph root
     * to draw it on the canvas
     */
    public void render(BpmnNode root) {
        clearGraph();
        buildGraph(root);
    }

    /**
     * Starting from the given root node,
     * it walks the graph breadth-first and issues
     * all the required commands to draw it on the canvas
     */
    public void buildGraph(BpmnNode rootNode) {
        this.addNode(rootNode.value());
        rootNode.getEdges().forEach(this::addEdge);
        List<BpmnNode> nodes = rootNode.getChildren();

        Deque<BpmnNode> workingSet =
                new ArrayDeque<>(prioritized(nodes));

        Set<BpmnNode> workedOff = new HashSet<>();
        while (!workingSet.isEmpty()) {
            BpmnNode current = workingSet.pop();
            // ensure we visit this node only once
            if (workedOff.contains(current)) {
                continue;
            }
            workedOff.add(current);
            workingSet.addAll(
                    prioritized(current.getChildren()));

            this.addChildNode(current);
            current.getEdges().forEach(this::addEdge);
        }
    }

    // make sure that docked nodes are processed *after* its siblings
    // for compat with drawing routines
    private Collection<BpmnNode> prioritized(List<BpmnNode> children) {
        ArrayDeque<BpmnNode> prioritized = new ArrayDeque<>();
        for (BpmnNode node : children) {
            if (node.isDocked()) {
                prioritized.add(node);
            } else {
                prioritized.push(node);
            }
        }
        return prioritized;
    }

    private void addDockedNode(Node parent, Node candidate) {
        AddDockedNodeCommand addNodeCommand = commandFactory.addDockedNode(parent, candidate);
        execute(addNodeCommand);
    }

    private void addChildNode(BpmnNode current) {
        addChildNode(current.getParent().value(), current.value());
        if (!current.isDocked()) {
            Point2D translationFactors = calculateTranslationFactors(current);
            translate(
                    current.value(),
                    translationFactors.getX(), translationFactors.getY());
        }
    }

    private Point2D calculateTranslationFactors(BpmnNode current) {
        double xFactor = 0;
        double yFactor = 0;
        Bounds bounds;
        BpmnNode parent = current.getParent();
        while (parent != null) {
            bounds = parent.value().getContent().getBounds();
            xFactor += bounds.getX();
            yFactor += bounds.getY();
            parent = parent.getParent();
        }
        return Point2D.create(xFactor, yFactor);
    }

    private void addChildNode(Node<? extends View, ?> parent, Node<? extends View, ?> child) {
        AddChildNodeCommand addChildNodeCommand = commandFactory.addChildNode(parent, child);
        execute(addChildNodeCommand);
    }

    /**
     * Move node into a new coordinate system with origin in newOrigin.
     * <p>
     * E.g., assume origin is currently (0,0), and consider node at (10,11).
     * If we move node into a new coordinate system where the origin is in (3, 4)
     * then the new coordinates for node are: (10-3, 11-4) = (7,7)
     */
    private void translate(Node<? extends View, ?> node, double deltaX, double deltaY) {

        Bounds childBounds = node.getContent().getBounds();
        double constrainedX = childBounds.getUpperLeft().getX() - deltaX;
        double constrainedY = childBounds.getUpperLeft().getY() - deltaY;

        Point2D coords = Point2D.create(constrainedX, constrainedY);
        updatePosition(node, coords);
    }

    private void updatePosition(Node node, Point2D position) {
        UpdateElementPositionCommand updateElementPositionCommand =
                commandFactory.updatePosition(node, position);
        execute(updateElementPositionCommand);
    }

    private void addNode(Node node) {
        AddNodeCommand addNodeCommand = commandFactory.addNode(node);
        execute(addNodeCommand);
    }

    @SuppressWarnings("unchecked")
    private void addEdge(
            Edge<? extends View<?>, Node> edge,
            Node source,
            Connection sourceConnection,
            List<Point2D> controlPoints,
            Node target,
            Connection targetConnection) {
        final DeferredCompositeCommand.Builder<GraphCommandExecutionContext, RuleViolation> commandBuilder =
                new DeferredCompositeCommand.Builder<>();
        addConnector(commandBuilder, source, edge, sourceConnection);
        final ControlPoint[] cps = new ControlPoint[controlPoints.size()];
        for (int i = 0; i < cps.length; i++) {
            final ControlPoint cp = ControlPoint.build(controlPoints.get(i));
            addControlPoint(commandBuilder, edge, cp, i);
        }
        setTargetNode(commandBuilder, target, edge, targetConnection);
        execute(commandBuilder.build());
    }

    private void addConnector(final DeferredCompositeCommand.Builder<GraphCommandExecutionContext, RuleViolation> commandBuilder,
                              final Node<? extends View<?>, Edge> sourceNode,
                              final Edge<? extends View<?>, Node> edge,
                              final Connection connection) {
        commandBuilder.deferCommand(() -> commandFactory.addConnector(sourceNode, edge, connection));
    }

    private void setTargetNode(final DeferredCompositeCommand.Builder<GraphCommandExecutionContext, RuleViolation> commandBuilder,
                               final Node<? extends View<?>, Edge> targetNode,
                               final Edge<? extends View<?>, Node> edge,
                               final Connection connection) {
        commandBuilder.deferCommand(() -> commandFactory.setTargetNode(targetNode, edge, connection));
    }

    private void addControlPoint(final DeferredCompositeCommand.Builder<GraphCommandExecutionContext, RuleViolation> commandBuilder,
                                 final Edge edge,
                                 final ControlPoint controlPoint,
                                 final int index) {
        commandBuilder.deferCommand(() -> commandFactory.addControlPoint(edge, controlPoint, index));
    }

    private CommandResult<RuleViolation> execute(Command<GraphCommandExecutionContext, RuleViolation> command) {
        return commandManager.execute(executionContext, command);
    }

    private CommandResult<RuleViolation> clearGraph() {
        return commandManager.execute(executionContext, commandFactory.clearGraph());
    }

    private void addEdge(BpmnEdge edge) {
        if (edge.isDocked()) {
            addDockedNode(edge.getSource().value(),
                          edge.getTarget().value());
        } else {
            final BpmnEdge.Simple e = (BpmnEdge.Simple) edge;
            addEdge(e.getEdge(),
                    e.getSource().value(),
                    e.getSourceConnection(),
                    e.getControlPoints(),
                    e.getTarget().value(),
                    e.getTargetConnection());
        }
    }
}

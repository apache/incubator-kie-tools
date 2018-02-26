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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddDockedNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphBuildingContext {

    private static final Logger logger = LoggerFactory.getLogger(GraphBuildingContext.class);

    private final GraphCommandExecutionContext executionContext;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;

    public GraphBuildingContext(
            GraphCommandExecutionContext executionContext,
            GraphCommandFactory commandFactory,
            GraphCommandManager commandManager) {
        this.executionContext = executionContext;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    public void addDockedNode(String parentId, String candidateId) {
        Node parent = executionContext.getGraphIndex().getNode(parentId);
        Node candidate = executionContext.getGraphIndex().getNode(candidateId);

        AddDockedNodeCommand addNodeCommand = commandFactory.addDockedNode(parent, candidate);
        execute(addNodeCommand);
    }

    public void addChildNode(String parentId, String childId) {
        Node parent = getNode(parentId);
        Node child = executionContext.getGraphIndex().getNode(childId);

        AddChildNodeCommand addChildNodeCommand = commandFactory.addChildNode(parent, child);
        execute(addChildNodeCommand);
    }

    public Node getNode(String id) {
        return executionContext.getGraphIndex().getNode(id);
    }

    public void addChildNode(Node<? extends View, ?> parent, Node<? extends View, ?> child) {
        AddChildNodeCommand addChildNodeCommand = commandFactory.addChildNode(parent, child);
        execute(addChildNodeCommand);

        translate(child, parent.getContent().getBounds());
    }

    public void translate(Node<? extends View, ?> node, Bounds constraints) {

        logger.info("Translating {} into constraints {}", node.getContent().getBounds(), constraints);

        Bounds childBounds = node.getContent().getBounds();
        double constrainedX = childBounds.getUpperLeft().getX() - constraints.getUpperLeft().getX();
        double constrainedY = childBounds.getUpperLeft().getY() - constraints.getUpperLeft().getY();

        Point2D coords = Point2D.create(constrainedX, constrainedY);
        updatePosition(node, coords);
    }

    public void updatePosition(Node node, Point2D position) {
        UpdateElementPositionCommand updateElementPositionCommand = new UpdateElementPositionCommand(node, position);
        execute(updateElementPositionCommand);
    }

    public void addNode(Node node) {
        AddNodeCommand addNodeCommand = commandFactory.addNode(node);
        execute(addNodeCommand);
    }

    public void addEdge(
            Edge<? extends View<?>, Node> edge,
            Node source,
            Connection sourceConnection,
            Node target,
            Connection targetConnection) {
        SetConnectionSourceNodeCommand setSourceNode =
                commandFactory.setSourceNode(source, edge, sourceConnection);

        SetConnectionTargetNodeCommand setTargetNode =
                commandFactory.setTargetNode(target, edge, targetConnection);

        execute(setSourceNode);
        execute(setTargetNode);
    }

    public void addEdge(
            Edge<? extends View<?>, Node> edge,
            String sourceId,
            Connection sourceConnection,
            String targetId,
            Connection targetConnection) {

        Node source = executionContext.getGraphIndex().getNode(sourceId);
        Node target = executionContext.getGraphIndex().getNode(targetId);

        Objects.requireNonNull(source);
        Objects.requireNonNull(target);

        addEdge(edge, source, sourceConnection, target, targetConnection);
    }

    public void setBounds(String elementId, int x1, int y1, int x2, int y2) {
        Element<? extends View<?>> element = executionContext.getGraphIndex().get(elementId);
        element.getContent().setBounds(BoundsImpl.build(x1, y1, x2, y2));
    }

    private CommandResult<RuleViolation> execute(Command<GraphCommandExecutionContext, RuleViolation> command) {
        return commandManager.execute(executionContext, command);
    }

    public GraphCommandExecutionContext executionContext() {
        return executionContext;
    }

    public CommandResult<RuleViolation> clearGraph() {
        return commandManager.execute(executionContext, commandFactory.clearGraph());
    }
}

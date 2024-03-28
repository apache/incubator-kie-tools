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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.ArrayList;
import java.util.List;

import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

/**
 * Clone a node shape into de canvas.
 */
public class CloneCanvasNodeCommand extends AbstractCanvasCommand {

    private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> commands;
    private transient ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor;
    private final Node parent;
    private final Node candidate;
    private final String shapeSetId;

    public CloneCanvasNodeCommand(Node parent, Node candidate, String shapeSetId, final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        this.parent = parent;
        this.candidate = candidate;
        this.shapeSetId = shapeSetId;
        this.childrenTraverseProcessor = childrenTraverseProcessor;
    }

    /**
     * Creates the {@link AbstractCanvasCommand} responsible to add the clone node to canvas, may be override in case of
     * a specification on the command.
     * @return
     */
    public AbstractCanvasCommand createAddCanvasChildNodeCommand(Node parent, Node candidate, String shapeSetId) {
        return new AddCanvasChildNodeCommand(parent, candidate, shapeSetId);
    }

    /**
     * Creates a {@link CloneCanvasNodeCommand} that is used to clone children nodes and may be override in case a
     * specification of {@link CloneCanvasNodeCommand}.
     * @param parent
     * @param candidate
     * @return
     */
    public CloneCanvasNodeCommand createCloneCanvasNodeCommand(Node parent, Node candidate, String shapeSetId) {
        return new CloneCanvasNodeCommand(parent, candidate, shapeSetId, childrenTraverseProcessor);
    }

    @Override
    public CommandResult<CanvasViolation> execute(AbstractCanvasHandler context) {
        commands = new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .reverse()
                .build();

        //first add the candidate clone
        commands.addCommand(createAddCanvasChildNodeCommand(getParent(), getCandidate(), getShapeSetId()));

        //process clone children nodes
        if (GraphUtils.hasChildren(getCandidate())) {
            Graph graph = context.getGraphIndex().getGraph();
            List<Edge> clonedEdges = new ArrayList<>();

            childrenTraverseProcessor.get()
                    .setRootUUID(getCandidate().getUUID())
                    .traverse(graph, new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
                        @Override
                        public boolean startNodeTraversal(List<Node<View, Edge>> parents, Node<View, Edge> node) {
                            commands.addCommand(createCloneCanvasNodeCommand(getCandidate(), node, getShapeSetId()));
                            clonedEdges.addAll(node.getOutEdges());
                            //just traverse the first level children of the root node
                            return false;
                        }
                    });

            //process children edges -> connectors and dock
            clonedEdges.stream()
                    .filter(edge -> edge.getContent() instanceof Dock)
                    .forEach(edge -> commands.addCommand(new CanvasDockNodeCommand(edge.getSourceNode(), edge.getTargetNode())));
            clonedEdges
                    .stream()
                    .filter(edge -> edge.getContent() instanceof ViewConnector)
                    .forEach(edge -> commands.addCommand(new AddCanvasConnectorCommand((Edge) edge, getShapeSetId())));
        }

        //process clone docked nodes on the root
        if (GraphUtils.hasDockedNodes(getCandidate())) {
            List<Edge> edges = getCandidate().getOutEdges();
            edges.stream()
                    .filter(edge -> edge.getContent() instanceof Dock)
                    .map(edge -> edge.getTargetNode())
                    .forEach(targetNode -> {
                        commands.addCommand(new AddCanvasChildNodeCommand(getParent(), targetNode, getShapeSetId()));
                        commands.addCommand(new CanvasDockNodeCommand(getCandidate(), targetNode));
                    });
        }

        return commands.execute(context);
    }

    @Override
    public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context) {
        return commands.undo(context);
    }

    protected CompositeCommand<AbstractCanvasHandler, CanvasViolation> getCommands() {
        return commands;
    }

    public ManagedInstance<ChildrenTraverseProcessor> getChildrenTraverseProcessor() {
        return childrenTraverseProcessor;
    }

    public Node getParent() {
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + toUUID(getCandidate()) + "," +
                "parent=" + toUUID(getParent()) + "," +
                "shapeSet=" + getShapeSetId() + "]";
    }
}

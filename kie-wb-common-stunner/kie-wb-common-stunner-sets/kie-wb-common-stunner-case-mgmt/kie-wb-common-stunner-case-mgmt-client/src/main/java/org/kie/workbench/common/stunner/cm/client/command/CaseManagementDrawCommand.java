/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;

import static java.util.function.Function.identity;
import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.childPredicate;
import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.sequencePredicate;

/**
 * Draws the whole Case Management diagram. This implementation does not use Commands since loading cannot be "undone".
 */
public class CaseManagementDrawCommand extends AbstractCanvasCommand {

    private final ChildrenTraverseProcessor childrenTraverseProcessor;

    public CaseManagementDrawCommand(final ChildrenTraverseProcessor childrenTraverseProcessor) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Diagram diagram = context.getDiagram();
        final String shapeSetId = context.getDiagram().getMetadata().getShapeSetId();

        this.sortNodes(diagram);

        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder =
                new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>().forward();

        childrenTraverseProcessor
                .traverse(diagram.getGraph(),
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                              @Override
                              public void startNodeTraversal(final Node<View, Edge> node) {
                                  super.startNodeTraversal(node);
                                  commandBuilder.addCommand(new AddCanvasNodeCommand((Node) node, shapeSetId));
                              }

                              @Override
                              public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                                                final Node<View, Edge> node) {
                                  super.startNodeTraversal(parents, node);

                                  if (isDrawable(parents.get(parents.size() - 1), node)) {
                                      commandBuilder.addCommand(new AddCanvasChildNodeCommand(parents.get(parents.size() - 1),
                                                                                              node,
                                                                                              shapeSetId));
                                  }

                                  return true;
                              }

                              @Override
                              public void endGraphTraversal() {
                                  super.endGraphTraversal();
                              }
                          });

        return executeCommands(context, commandBuilder);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        throw new UnsupportedOperationException("Draw cannot be undone, yet.");
    }

    protected CommandResult<CanvasViolation> executeCommands(final AbstractCanvasHandler context,
                                                             final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        return commandBuilder.build().execute(context);
    }

    boolean isDrawable(final Node<View, Edge> parent, final Node<View, Edge> child) {
        if (child.getContent().getDefinition() instanceof UserTask
                || child.getContent().getDefinition() instanceof ReusableSubprocess) {
            // Draw UserTask and ReusableSubprocess only if they are child of Stage
            if (!(parent.getContent().getDefinition() instanceof AdHocSubprocess)) {
                return false;
            }

            final List<Node> childNodes = parent.getOutEdges().stream()
                    .filter(childPredicate())
                    .map(Edge::getTargetNode).collect(Collectors.toList());

            return childNodes.stream().allMatch(CaseManagementCommandUtil::isSubStageNode);
        } else if (child.getContent().getDefinition() instanceof AdHocSubprocess) {
            // Draw Stages only if they are child of the diagram and only have children of UserTask and ReusableSubprocess
            if (!(parent.getContent().getDefinition() instanceof CaseManagementDiagram)) {
                return false;
            }

            final List<Node> childNodes = child.getOutEdges().stream()
                    .filter(childPredicate())
                    .map(Edge::getTargetNode).collect(Collectors.toList());

            return childNodes.isEmpty()
                    || childNodes.stream().allMatch(CaseManagementCommandUtil::isSubStageNode);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    void sortNodes(final Diagram<Graph<DefinitionSet, Node>, Metadata> diagram) {
        List<Node<View<?>, Edge>> nodes = StreamSupport.stream(diagram.getGraph().nodes().spliterator(), false)
                .map(n -> (Node<View<?>, Edge>) n).collect(Collectors.toList());

        nodes.stream()
                .filter(node -> node.getContent().getDefinition() instanceof CaseManagementDiagram)
                .findAny().ifPresent(root -> {

            // Sort the Stages by the SequenceFlow from StartNoneEvent to EndNoneEvent of the diagram
            Map<Node<View<?>, Edge>, Edge> childNodes = root.getOutEdges().stream()
                    .filter(childPredicate()).collect(Collectors.toMap(e -> (Node<View<?>, Edge>) e.getTargetNode(), identity()));

            Node<View<?>, Edge> startNode = root.getOutEdges().stream().map(Edge::getTargetNode)
                    .filter(n -> ((Node<View<?>, Edge>) n).getContent().getDefinition() instanceof StartNoneEvent).findAny()
                    .orElseGet(() -> childNodes.keySet().stream().filter(n -> n.getInEdges().size() == 1).findAny().orElse(null));

            if (startNode != null) {
                Node<View<?>, Edge> node = startNode;
                List<Edge> childEdges = new LinkedList<>();

                do {
                    childEdges.add(0, childNodes.get(node));
                    node = node.getOutEdges().stream().filter(sequencePredicate())
                            .map(Edge::getTargetNode).filter(childNodes::containsKey).findAny().orElse(null);
                } while (node != null);

                childEdges.forEach(e -> root.getOutEdges().remove(e));
                childEdges.forEach(e -> root.getOutEdges().add(0, e));
            }

            // Sort the child nodes in Stages by their coordinates
            Stream<Node<View<?>, Edge>> stageStream = root.getOutEdges().stream().filter(childPredicate()).map(Edge::getTargetNode);
            stageStream.filter(CaseManagementCommandUtil::isStageNode)
                    .forEach(n -> {
                        List<Edge> edges = n.getOutEdges().stream().filter(childPredicate()).collect(Collectors.toList());
                        Collections.sort(edges, (e1, e2) -> {
                            final double y1 = ((Node<View, Edge>) e1.getTargetNode()).getContent().getBounds().getY();
                            final double y2 = ((Node<View, Edge>) e2.getTargetNode()).getContent().getBounds().getY();
                            return Double.compare(y2, y1);
                        });

                        edges.forEach(e -> n.getOutEdges().remove(e));
                        edges.forEach(e -> n.getOutEdges().add(0, e));
                    });
        });
    }
}

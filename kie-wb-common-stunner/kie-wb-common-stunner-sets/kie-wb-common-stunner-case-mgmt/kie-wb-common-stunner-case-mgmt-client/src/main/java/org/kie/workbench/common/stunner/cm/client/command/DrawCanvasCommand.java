/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

/**
 * Draws the whole Case Management diagram. This implementation does not use Commands since loading cannot be "undone".
 */
public class DrawCanvasCommand extends org.kie.workbench.common.stunner.core.client.canvas.command.DrawCanvasCommand {

    public DrawCanvasCommand() {
        super(new TreeWalkTraverseProcessorImpl());
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Diagram diagram = context.getDiagram();
        final String shapeSetId = context.getDiagram().getMetadata().getShapeSetId();

        treeWalkTraverseProcessor
                .useEdgeVisitorPolicy(TreeWalkTraverseProcessor.EdgeVisitorPolicy.VISIT_EDGE_AFTER_TARGET_NODE)
                .traverse(diagram.getGraph(),
                          new AbstractTreeTraverseCallback<Graph, Node, Edge>() {

                              @Override
                              @SuppressWarnings("unchecked")
                              public boolean startNodeTraversal(final Node node) {
                                  if (node.getContent() instanceof View) {
                                      context.register(shapeSetId,
                                                       node);
                                      context.applyElementMutation(node,
                                                                   MutationContext.STATIC);
                                      return true;
                                  }
                                  return false;
                              }

                              @Override
                              @SuppressWarnings("unchecked")
                              public boolean startEdgeTraversal(final Edge edge) {
                                  final Object content = edge.getContent();

                                  // The edge policy is "visit after node" therefore the View itself has already been
                                  // added and we therefore only need to register the client node on the parent node.
                                  if (content instanceof Child) {
                                      final Node child = edge.getTargetNode();
                                      final Node parent = edge.getSourceNode();
                                      final Object childContent = child.getContent();
                                      if (childContent instanceof View) {
                                          context.addChild(parent,
                                                           child);
                                      }
                                      return true;
                                  }
                                  return false;
                              }

                              @Override
                              public void endGraphTraversal() {
                                  // Draw the canvas shapes.
                                  context.getCanvas().draw();
                              }
                          });
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        throw new UnsupportedOperationException("Draw cannot be undone, yet.");
    }
}

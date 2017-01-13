/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

@ApplicationScoped
public class CanvasCommandFactory {

    private final TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    protected CanvasCommandFactory() {
        this.treeWalkTraverseProcessor = null;
    }

    @Inject
    public CanvasCommandFactory(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    public AddNodeCommand addNode(final Node candidate,
                                  final String shapeSetId) {
        return new AddNodeCommand(candidate,
                                  shapeSetId);
    }

    public AddChildNodeCommand addChildNode(final Node parent,
                                            final Node candidate,
                                            final String shapeSetId) {
        return new AddChildNodeCommand(parent,
                                       candidate,
                                       shapeSetId);
    }

    public AddDockedNodeCommand addDockedNode(final Node parent,
                                              final Node candidate,
                                              final String shapeSetId) {
        return new AddDockedNodeCommand(parent,
                                        candidate,
                                        shapeSetId);
    }

    public AddConnectorCommand addConnector(final Node sourceNode,
                                            final Edge candidate,
                                            final int magnetIndex,
                                            final String shapeSetId) {
        return new AddConnectorCommand(sourceNode,
                                       candidate,
                                       magnetIndex,
                                       shapeSetId);
    }

    public SetChildNodeCommand setChildNode(final Node parent,
                                            final Node candidate) {
        return new SetChildNodeCommand(parent,
                                       candidate);
    }

    public DockNodeCommand dockNode(final Node parent,
                                    final Node candidate) {
        return new DockNodeCommand(parent,
                                   candidate);
    }

    public DeleteNodeCommand deleteNode(final Node candidate) {
        return new DeleteNodeCommand(candidate);
    }

    public RemoveChildCommand removeChild(final Node parent,
                                          final Node candidate) {
        return new RemoveChildCommand(parent,
                                      candidate);
    }

    public UnDockNodeCommand unDockNode(final Node parent,
                                        final Node candidate) {
        return new UnDockNodeCommand(parent,
                                     candidate);
    }

    public DeleteConnectorCommand deleteConnector(final Edge candidate) {
        return new DeleteConnectorCommand(candidate);
    }

    public DrawCanvasCommand draw() {
        return new DrawCanvasCommand(treeWalkTraverseProcessor);
    }

    public MorphNodeCommand morphNode(final Node<? extends Definition<?>, Edge> candidate,
                                      final MorphDefinition morphDefinition,
                                      final String morphTarget,
                                      final String shapeSetId) {
        return new MorphNodeCommand(candidate,
                                    morphDefinition,
                                    morphTarget,
                                    shapeSetId);
    }

    public SetConnectionSourceNodeCommand setSourceNode(final Node<? extends View<?>, Edge> node,
                                                        final Edge<? extends View<?>, Node> edge,
                                                        final int magnetIndex) {
        return new SetConnectionSourceNodeCommand(node,
                                                  edge,
                                                  magnetIndex);
    }

    public SetConnectionTargetNodeCommand setTargetNode(final Node<? extends View<?>, Edge> node,
                                                        final Edge<? extends View<?>, Node> edge,
                                                        final int magnetIndex) {
        return new SetConnectionTargetNodeCommand(node,
                                                  edge,
                                                  magnetIndex);
    }

    public UpdateElementPositionCommand updatePosition(final Node<View<?>, Edge> element,
                                                       final Double x,
                                                       final Double y) {
        return new UpdateElementPositionCommand(element,
                                                x,
                                                y);
    }

    public UpdateElementPropertyCommand updatePropertyValue(final Element element,
                                                            final String propertyId,
                                                            final Object value) {
        return new UpdateElementPropertyCommand(element,
                                                propertyId,
                                                value);
    }

    public ClearCommand clearCanvas() {
        return new ClearCommand();
    }
}

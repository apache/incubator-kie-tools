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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@ApplicationScoped
public class GraphCommandFactory {

    public AddNodeCommand addNode(final Node candidate) {
        return new AddNodeCommand(candidate);
    }

    @SuppressWarnings("unchecked")
    public AddChildNodeCommand addChildNode(final Node parent,
                                            final Node candidate) {
        return new AddChildNodeCommand(parent,
                                       candidate,
                                       null,
                                       null);
    }

    @SuppressWarnings("unchecked")
    public AddChildNodeCommand addChildNode(final Node parent,
                                            final Node candidate,
                                            final Double x,
                                            final Double y) {
        return new AddChildNodeCommand(parent,
                                       candidate,
                                       x,
                                       y);
    }

    @SuppressWarnings("unchecked")
    public AddDockedNodeCommand addDockedNode(final Node parent,
                                              final Node candidate) {
        return new AddDockedNodeCommand(parent,
                                        candidate);
    }

    @SuppressWarnings("unchecked")
    public AddConnectorCommand addConnector(final Node target,
                                            final Edge edge,
                                            final Magnet magnet) {
        return new AddConnectorCommand(target,
                                       edge,
                                       magnet);
    }

    @SuppressWarnings("unchecked")
    public SetChildNodeCommand setChildNode(final Node parent,
                                            final Node candidate) {
        return new SetChildNodeCommand(parent,
                                       candidate);
    }

    @SuppressWarnings("unchecked")
    public SetParentNodeCommand setParentNode(final Node parent,
                                              final Node candidate) {
        return new SetParentNodeCommand(parent,
                                        candidate);
    }

    @SuppressWarnings("unchecked")
    public DockNodeCommand dockNode(final Node parent,
                                    final Node candidate) {
        return new DockNodeCommand(parent,
                                   candidate);
    }

    public MorphNodeCommand morphNode(final Node<Definition, Edge> candidate,
                                      final MorphDefinition morphDefinition,
                                      final String morphTarget) {
        return new MorphNodeCommand(candidate,
                                    morphDefinition,
                                    morphTarget);
    }

    public SetConnectionSourceNodeCommand setSourceNode(final Node<? extends View<?>, Edge> sourceNode,
                                                        final Edge<? extends View<?>, Node> edge,
                                                        final Magnet magnet) {
        return new SetConnectionSourceNodeCommand(sourceNode,
                                                  edge,
                                                  magnet,
                                                  true);
    }

    public SetConnectionTargetNodeCommand setTargetNode(final Node<? extends View<?>, Edge> targetNode,
                                                        final Edge<? extends View<?>, Node> edge,
                                                        final Magnet magnet) {
        return new SetConnectionTargetNodeCommand(targetNode,
                                                  edge,
                                                  magnet,
                                                  true);
    }

    public UpdateElementPositionCommand updatePosition(final Node<? extends View<?>, Edge> element,
                                                       final Double x,
                                                       final Double y) {
        return new UpdateElementPositionCommand(element,
                                                x,
                                                y);
    }

    @SuppressWarnings("unchecked")
    public UpdateElementPropertyValueCommand updatePropertyValue(final Node element,
                                                                 final String propertyId,
                                                                 final Object value) {
        return new UpdateElementPropertyValueCommand(element,
                                                     propertyId,
                                                     value);
    }

    @SuppressWarnings("unchecked")
    public SafeDeleteNodeCommand safeDeleteNode(final Node candidate) {
        return new SafeDeleteNodeCommand(candidate);
    }

    @SuppressWarnings("unchecked")
    public DeleteNodeCommand deleteNode(final Node candidate) {
        return new DeleteNodeCommand(candidate);
    }

    @SuppressWarnings("unchecked")
    public RemoveChildCommand removeChild(final Node parent,
                                          final Node candidate) {
        return new RemoveChildCommand(parent,
                                      candidate);
    }

    @SuppressWarnings("unchecked")
    public RemoveParentCommand removeFromParent(final Node parent,
                                                final Node candidate) {
        return new RemoveParentCommand(parent,
                                       candidate);
    }

    @SuppressWarnings("unchecked")
    public UnDockNodeCommand unDockNode(final Node parent,
                                        final Node candidate) {
        return new UnDockNodeCommand(parent,
                                     candidate);
    }

    @SuppressWarnings("unchecked")
    public DeleteConnectorCommand deleteConnector(final Edge<? extends View, Node> edge) {
        return new DeleteConnectorCommand(edge);
    }

    public ClearGraphCommand clearGraph() {
        return new ClearGraphCommand("");
    }

    public ClearGraphCommand clearGraph(final String rootUUID) {
        return new ClearGraphCommand(rootUUID);
    }
}

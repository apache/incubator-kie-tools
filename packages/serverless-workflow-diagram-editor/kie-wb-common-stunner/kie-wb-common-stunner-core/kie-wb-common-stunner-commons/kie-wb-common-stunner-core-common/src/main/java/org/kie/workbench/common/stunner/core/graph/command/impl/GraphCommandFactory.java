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


package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@ApplicationScoped
public class GraphCommandFactory {

    public AddNodeCommand addNode(final Node candidate) {
        return new AddNodeCommand(candidate);
    }

    @SuppressWarnings("unchecked")
    public AddChildNodeCommand addChildNode(final Node parent,
                                            final Node candidate) {
        return addChildNode(parent,
                            candidate,
                            null);
    }

    @SuppressWarnings("unchecked")
    public AddChildNodeCommand addChildNode(final Node parent,
                                            final Node candidate,
                                            final Point2D location) {
        return new AddChildNodeCommand(parent,
                                       candidate,
                                       location);
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
                                            final Connection connection) {
        return new AddConnectorCommand(target,
                                       edge,
                                       connection);
    }

    @SuppressWarnings("unchecked")
    public SetChildrenCommand setChild(final Node parent,
                                       final Node candidate) {
        return new SetChildrenCommand(parent,
                                      candidate);
    }

    @SuppressWarnings("unchecked")
    public SetChildrenCommand setChildren(final Node parent,
                                          final Collection candidates) {
        return new SetChildrenCommand(parent,
                                      candidates);
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
                                                        final Connection connection) {
        return new SetConnectionSourceNodeCommand(sourceNode,
                                                  edge,
                                                  connection);
    }

    public SetConnectionTargetNodeCommand setTargetNode(final Node<? extends View<?>, Edge> targetNode,
                                                        final Edge<? extends View<?>, Node> edge,
                                                        final Connection connection) {
        return new SetConnectionTargetNodeCommand(targetNode,
                                                  edge,
                                                  connection);
    }

    public UpdateElementPositionCommand updatePosition(final Node<? extends View<?>, Edge> element,
                                                       final Point2D location) {
        return new UpdateElementPositionCommand(element,
                                                location);
    }

    @SuppressWarnings("unchecked")
    public UpdateElementPropertyValueCommand updatePropertyValue(final Element element,
                                                                 final String field,
                                                                 final Object value) {
        return new UpdateElementPropertyValueCommand(element,
                                                     field,
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
    public DeleteElementsCommand delete(final Collection<Element> elements) {
        return new DeleteElementsCommand(() -> elements);
    }

    @SuppressWarnings("unchecked")
    public RemoveChildrenCommand removeChild(final Node parent,
                                             final Node candidate) {
        return new RemoveChildrenCommand(parent,
                                         candidate);
    }

    @SuppressWarnings("unchecked")
    public RemoveChildrenCommand removeChildren(final Node parent,
                                                final Collection candidates) {
        return new RemoveChildrenCommand(parent,
                                         candidates);
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

    public AddControlPointCommand addControlPoint(final Edge edge,
                                                  final ControlPoint controlPoint,
                                                  final int index) {
        return new AddControlPointCommand(edge.getUUID(), controlPoint, index);
    }
}

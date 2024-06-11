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

import java.util.Collection;
import java.util.function.Consumer;

import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;

public abstract class DefaultCanvasCommandFactory implements CanvasCommandFactory<AbstractCanvasHandler> {

    private final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessors;
    private final ManagedInstance<ViewTraverseProcessor> viewTraverseProcessors;

    protected DefaultCanvasCommandFactory(final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessors,
                                          final ManagedInstance<ViewTraverseProcessor> viewTraverseProcessors) {
        this.childrenTraverseProcessors = childrenTraverseProcessors;
        this.viewTraverseProcessors = viewTraverseProcessors;
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addNode(final Node candidate,
                                                        final String shapeSetId) {
        return new AddNodeCommand(candidate,
                                  shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addChildNode(final Node parent,
                                                             final Node candidate,
                                                             final String shapeSetId) {
        return new AddChildNodeCommand(parent,
                                       candidate,
                                       shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addDockedNode(final Node parent,
                                                              final Node candidate,
                                                              final String shapeSetId) {
        return new AddDockedNodeCommand(parent,
                                        candidate,
                                        shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> deleteNode(final Node candidate) {
        return new DeleteNodeCommand(candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> delete(final Collection<Element> candidates) {
        return new DeleteElementsCommand(candidates);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addConnector(final Node sourceNode,
                                                             final Edge candidate,
                                                             final Connection connection,
                                                             final String shapeSetId) {
        return new AddConnectorCommand(sourceNode,
                                       candidate,
                                       connection,
                                       shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> deleteConnector(final Edge candidate) {
        return new DeleteConnectorCommand(candidate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CanvasCommand<AbstractCanvasHandler> setChildNode(final Node parent,
                                                             final Node candidate) {
        return new SetChildrenCommand(parent,
                                      candidate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CanvasCommand<AbstractCanvasHandler> removeChild(final Node parent,
                                                            final Node candidate) {
        return new RemoveChildrenCommand(parent,
                                         candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateChildNode(final Node parent,
                                                                final Node candidate) {
        return new UpdateChildrenCommand(parent,
                                         candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateChildren(final Node parent,
                                                               final Collection<Node> candidates) {
        return new UpdateChildrenCommand(parent,
                                         candidates);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> dockNode(final Node parent,
                                                         final Node candidate) {
        return new DockNodeCommand(parent,
                                   candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> unDockNode(final Node parent,
                                                           final Node candidate) {
        return new UnDockNodeCommand(parent,
                                     candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateDockNode(final Node parent,
                                                               final Node candidate) {
        return new UpdateDockNodeCommand(parent,
                                         candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateDockNode(final Node parent,
                                                               final Node candidate,
                                                               final boolean adjustPosition) {
        return new UpdateDockNodeCommand(parent, candidate, adjustPosition);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> draw() {
        return new DrawCanvasCommand(newChildrenTraverseProcessor(),
                                     newViewTraverseProcessor());
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> morphNode(final Node<? extends Definition<?>, Edge> candidate,
                                                          final MorphDefinition morphDefinition,
                                                          final String morphTarget,
                                                          final String shapeSetId) {
        return new MorphNodeCommand(candidate,
                                    morphDefinition,
                                    morphTarget,
                                    shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setSourceNode(final Node<? extends View<?>, Edge> node,
                                                              final Edge<? extends ViewConnector<?>, Node> edge,
                                                              final Connection connection) {
        return new SetConnectionSourceNodeCommand(node,
                                                  edge,
                                                  connection);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setTargetNode(final Node<? extends View<?>, Edge> node,
                                                              final Edge<? extends ViewConnector<?>, Node> edge,
                                                              final Connection connection) {
        return new SetConnectionTargetNodeCommand(node,
                                                  edge,
                                                  connection);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updatePosition(final Node<View<?>, Edge> element,
                                                               final Point2D location) {
        return new UpdateElementPositionCommand(element,
                                                location);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updatePropertyValue(final Element element,
                                                                    final String field,
                                                                    final Object value) {
        return new UpdateElementPropertyCommand(element,
                                                field,
                                                value);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateDomainObjectPropertyValue(final DomainObject domainObject,
                                                                                final String propertyId,
                                                                                final Object value) {
        return new UpdateDomainObjectPropertyCommand(domainObject,
                                                     propertyId,
                                                     value);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> clearCanvas() {
        return new ClearCommand();
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> cloneNode(Node candidate, String parentUuid, Point2D cloneLocation, Consumer<Node> cloneNodeCallback) {
        return new CloneNodeCommand(candidate, parentUuid, cloneLocation, cloneNodeCallback, childrenTraverseProcessors);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> cloneConnector(Edge candidate, String sourceUUID, String targetUUID, String shapeSetId, Consumer<Edge> callback) {
        return new CloneConnectorCommand(candidate, sourceUUID, targetUUID, shapeSetId, callback);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addControlPoint(Edge candidate, ControlPoint controlPoint, int index) {
        return new AddControlPointCommand(candidate, controlPoint, index);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> deleteControlPoint(Edge candidate, int index) {
        return new DeleteControlPointCommand(candidate, index);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateControlPointPosition(Edge candidate, ControlPoint[] controlPoints) {
        return new UpdateControlPointPositionCommand(candidate, controlPoints);
    }

    protected ChildrenTraverseProcessor newChildrenTraverseProcessor() {
        return childrenTraverseProcessors.get();
    }

    protected ViewTraverseProcessor newViewTraverseProcessor() {
        return viewTraverseProcessors.get();
    }

    protected ManagedInstance<ChildrenTraverseProcessor> getChildrenTraverseProcessors() {
        return childrenTraverseProcessors;
    }
}

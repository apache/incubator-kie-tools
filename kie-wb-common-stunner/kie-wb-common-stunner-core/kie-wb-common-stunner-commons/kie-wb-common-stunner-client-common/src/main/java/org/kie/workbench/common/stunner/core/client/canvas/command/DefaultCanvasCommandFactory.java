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

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;

@ApplicationScoped
public class DefaultCanvasCommandFactory implements CanvasCommandFactory<AbstractCanvasHandler> {

    private final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessors;
    private final ManagedInstance<ViewTraverseProcessor> viewTraverseProcessors;

    protected DefaultCanvasCommandFactory() {
        this(null,
             null);
    }

    @Inject
    public DefaultCanvasCommandFactory(final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessors,
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
    public CanvasCommand<AbstractCanvasHandler> addConnector(final Node sourceNode,
                                                             final Edge candidate,
                                                             final Magnet magnet,
                                                             final String shapeSetId) {
        return new AddConnectorCommand(sourceNode,
                                       candidate,
                                       magnet,
                                       shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> deleteConnector(final Edge candidate) {
        return new DeleteConnectorCommand(candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setChildNode(final Node parent,
                                                             final Node candidate) {
        return new SetChildNodeCommand(parent,
                                       candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> removeChild(final Node parent,
                                                            final Node candidate) {
        return new RemoveChildCommand(parent,
                                      candidate);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateChildNode(final Node parent,
                                                                final Node candidate) {
        return new UpdateChildNodeCommand(parent,
                                          candidate);
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
                                                              final Edge<? extends View<?>, Node> edge,
                                                              final Magnet magnet,
                                                              boolean isNewConnection) {
        return new SetConnectionSourceNodeCommand(node,
                                                  edge,
                                                  magnet,
                                                  isNewConnection);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setTargetNode(final Node<? extends View<?>, Edge> node,
                                                              final Edge<? extends View<?>, Node> edge,
                                                              final Magnet magnet,
                                                              boolean isNewConnection) {
        return new SetConnectionTargetNodeCommand(node,
                                                  edge,
                                                  magnet,
                                                  isNewConnection);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updatePosition(final Node<View<?>, Edge> element,
                                                               final Double x,
                                                               final Double y) {
        return new UpdateElementPositionCommand(element,
                                                x,
                                                y);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updatePropertyValue(final Element element,
                                                                    final String propertyId,
                                                                    final Object value) {
        return new UpdateElementPropertyCommand(element,
                                                propertyId,
                                                value);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> clearCanvas() {
        return new ClearCommand();
    }

    protected ChildrenTraverseProcessor newChildrenTraverseProcessor() {
        return childrenTraverseProcessors.get();
    }

    protected ViewTraverseProcessor newViewTraverseProcessor() {
        return viewTraverseProcessors.get();
    }
}

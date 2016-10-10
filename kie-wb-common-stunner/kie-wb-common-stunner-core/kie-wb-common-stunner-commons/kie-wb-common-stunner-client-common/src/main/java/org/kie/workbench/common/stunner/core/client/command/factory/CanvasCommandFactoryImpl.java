/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command.factory;

import org.kie.workbench.common.stunner.core.client.command.impl.*;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CanvasCommandFactoryImpl implements CanvasCommandFactory {

    private TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    protected CanvasCommandFactoryImpl() {
        this.treeWalkTraverseProcessor = null;
    }

    @Inject
    public CanvasCommandFactoryImpl( final TreeWalkTraverseProcessor treeWalkTraverseProcessor ) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    @Override
    public AddCanvasNodeCommand ADD_NODE( Node candidate, ShapeFactory factory ) {
        return new AddCanvasNodeCommand( candidate, factory );
    }

    @Override
    public AddCanvasEdgeCommand ADD_EDGE( Node parent, Edge candidate, ShapeFactory factory ) {
        return new AddCanvasEdgeCommand( parent, candidate, factory );
    }

    @Override
    public DeleteCanvasNodeCommand DELETE_NODE( Node candidate ) {
        return new DeleteCanvasNodeCommand( candidate );
    }

    @Override
    public DeleteCanvasEdgeCommand DELETE_EDGE( Edge candidate ) {
        return new DeleteCanvasEdgeCommand( candidate );
    }

    @Override
    public DrawCanvasCommand DRAW() {
        return new DrawCanvasCommand( treeWalkTraverseProcessor );
    }

    @Override
    public ClearCanvasCommand CLEAR_CANVAS() {
        return new ClearCanvasCommand();
    }

    @Override
    public AddCanvasChildEdgeCommand ADD_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new AddCanvasChildEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteCanvasChildEdgeCommand DELETE_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new DeleteCanvasChildEdgeCommand( parent, candidate );
    }

    @Override
    public AddCanvasParentEdgeCommand ADD_PARENT_EDGE( Node parent,
                                                       Node candidate ) {
        return new AddCanvasParentEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteCanvasParentEdgeCommand DELETE_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new DeleteCanvasParentEdgeCommand( parent, candidate );
    }

    @Override
    public AddCanvasDockEdgeCommand ADD_DOCK_EDGE( final Node parent,
                                                   final Node candidate ) {
        return new AddCanvasDockEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteCanvasDockEdgeCommand DELETE_DOCK_EDGE( final Node parent,
                                                         final Node candidate ) {
        return new DeleteCanvasDockEdgeCommand( parent, candidate );
    }

    @Override
    public UpdateCanvasElementPositionCommand UPDATE_POSITION( final Element element,
                                                               final Double x,
                                                               final Double y ) {
        return new UpdateCanvasElementPositionCommand( element, x, y );
    }

    @Override
    public UpdateCanvasElementPropertyCommand UPDATE_PROPERTY( final Element element,
                                                               final String propertyId,
                                                               final Object value ) {
        return new UpdateCanvasElementPropertyCommand( element, propertyId, value );
    }

    @Override
    public UpdateCanvasElementPropertiesCommand UPDATE_PROPERTIES( final Element element ) {
        return new UpdateCanvasElementPropertiesCommand( element );
    }

    @Override
    public AddCanvasChildNodeCommand ADD_CHILD_NODE( final Node parent,
                                                     final Node candidate,
                                                     final ShapeFactory factory ) {
        return new AddCanvasChildNodeCommand( parent, candidate, factory );
    }

    @Override
    public AddCanvasDockedNodeCommand ADD_DOCKED_NODE( final Node parent,
                                                       final Node candidate,
                                                       final ShapeFactory factory ) {
        return new AddCanvasDockedNodeCommand( parent, candidate, factory );
    }

    @Override
    public SetCanvasConnectionSourceNodeCommand SET_SOURCE_NODE( final Node<? extends View<?>, Edge> node,
                                                                 final Edge<? extends View<?>, Node> edge,
                                                                 final int magnetIndex ) {
        return new SetCanvasConnectionSourceNodeCommand( node, edge, magnetIndex );
    }

    @Override
    public SetCanvasConnectionTargetNodeCommand SET_TARGET_NODE( final Node<? extends View<?>, Edge> node,
                                                                 final Edge<? extends View<?>, Node> edge,
                                                                 final int magnetIndex ) {
        return new SetCanvasConnectionTargetNodeCommand( node, edge, magnetIndex );
    }

    @Override
    public MorphCanvasNodeCommand MORPH_NODE( final Node<? extends Definition<?>, Edge> candidate,
                                              final MorphDefinition morphDefinition,
                                              final String morphTarget,
                                              final ShapeFactory factory ) {
        return new MorphCanvasNodeCommand( candidate, morphDefinition, morphTarget, factory );
    }

}

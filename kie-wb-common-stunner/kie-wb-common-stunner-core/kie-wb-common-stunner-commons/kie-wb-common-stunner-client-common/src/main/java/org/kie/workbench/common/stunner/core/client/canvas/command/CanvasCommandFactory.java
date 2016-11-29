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

package org.kie.workbench.common.stunner.core.client.canvas.command;

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
public class CanvasCommandFactory {

    private final TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    protected CanvasCommandFactory() {
        this.treeWalkTraverseProcessor = null;
    }

    @Inject
    public CanvasCommandFactory( final TreeWalkTraverseProcessor treeWalkTraverseProcessor ) {
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
    }

    public AddNodeCommand ADD_NODE( Node candidate, String shapeSetId ) {
        return new AddNodeCommand( candidate, shapeSetId );
    }

    public AddChildNodeCommand ADD_CHILD_NODE( final Node parent,
                                               final Node candidate,
                                               final String shapeSetId ) {
        return new AddChildNodeCommand( parent, candidate, shapeSetId );
    }

    public AddDockedNodeCommand ADD_DOCKED_NODE( final Node parent,
                                                 final Node candidate,
                                                 final String shapeSetId ) {
        return new AddDockedNodeCommand( parent, candidate, shapeSetId );
    }

    public AddConnectorCommand ADD_CONNECTOR( Node sourceNode,
                                              Edge candidate,
                                              int magnetIndex,
                                              String shapeSetId ) {
        return new AddConnectorCommand( sourceNode, candidate, magnetIndex, shapeSetId );
    }

    public SetChildNodeCommand SET_CHILD_NODE( final Node parent, final Node candidate ) {
        return new SetChildNodeCommand( parent, candidate );
    }

    public DockNodeCommand DOCK_NODE( final Node parent,
                                      final Node candidate ) {
        return new DockNodeCommand( parent, candidate );
    }

    public DeleteNodeCommand DELETE_NODE( Node candidate ) {
        return new DeleteNodeCommand( candidate );
    }

    public RemoveChildCommand REMOVE_CHILD( final Node parent, final Node candidate ) {
        return new RemoveChildCommand( parent, candidate );
    }

    public UnDockNodeCommand UNDOCK_NODE( final Node parent,
                                          final Node candidate ) {
        return new UnDockNodeCommand( parent, candidate );
    }

    public DeleteConnectorCommand DELETE_CONNECTOR( Edge candidate ) {
        return new DeleteConnectorCommand( candidate );
    }

    public DrawCanvasCommand DRAW() {
        return new DrawCanvasCommand( treeWalkTraverseProcessor );
    }

    public MorphNodeCommand MORPH_NODE( final Node<? extends Definition<?>, Edge> candidate,
                                        final MorphDefinition morphDefinition,
                                        final String morphTarget,
                                        final String shapeSetId ) {
        return new MorphNodeCommand( candidate, morphDefinition, morphTarget, shapeSetId );
    }

    public SetConnectionSourceNodeCommand SET_SOURCE_NODE( final Node<? extends View<?>, Edge> node,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionSourceNodeCommand( node, edge, magnetIndex );
    }

    public SetConnectionTargetNodeCommand SET_TARGET_NODE( final Node<? extends View<?>, Edge> node,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionTargetNodeCommand( node, edge, magnetIndex );
    }

    public UpdateElementPositionCommand UPDATE_POSITION( final Node<View<?>, Edge> element,
                                                         final Double x,
                                                         final Double y ) {
        return new UpdateElementPositionCommand( element, x, y );
    }

    public UpdateElementPropertyCommand UPDATE_PROPERTY( final Element element,
                                                         final String propertyId,
                                                         final Object value ) {
        return new UpdateElementPropertyCommand( element, propertyId, value );
    }

    public ClearCommand CLEAR_CANVAS() {
        return new ClearCommand();
    }

}

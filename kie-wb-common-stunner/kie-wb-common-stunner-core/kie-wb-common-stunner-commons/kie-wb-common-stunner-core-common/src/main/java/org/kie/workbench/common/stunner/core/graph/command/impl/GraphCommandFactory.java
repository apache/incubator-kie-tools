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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GraphCommandFactory {

    @SuppressWarnings( "unchecked" )
    public AddChildNodeCommand ADD_CHILD_NODE( final Node parent,
                                               final Node candidate ) {
        return new AddChildNodeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public AddDockedNodeCommand ADD_DOCKED_NODE( final Node parent,
                                                 final Node candidate ) {
        return new AddDockedNodeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public SafeDeleteNodeCommand SAFE_DELETE_NODE( final Node candidate ) {
        return new SafeDeleteNodeCommand( candidate );
    }

    @SuppressWarnings( "unchecked" )
    public AddEdgeCommand ADD_EDGE( final Node target, final Edge edge ) {
        return new AddEdgeCommand( target, edge );
    }

    public AddNodeCommand ADD_NODE( final Node candidate ) {
        return new AddNodeCommand( candidate );
    }

    public ClearGraphCommand CLEAR_GRAPH() {
        return new ClearGraphCommand( "" );
    }

    public ClearGraphCommand CLEAR_GRAPH( final String rootUUID ) {
        return new ClearGraphCommand( rootUUID );
    }

    @SuppressWarnings( "unchecked" )
    public AddChildEdgeCommand ADD_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new AddChildEdgeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public AddParentEdgeCommand ADD_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new AddParentEdgeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteChildEdgeCommand DELETE_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new DeleteChildEdgeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteParentEdgeCommand DELETE_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new DeleteParentEdgeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public AddDockEdgeCommand ADD_DOCK_EDGE( final Node parent, final Node candidate ) {
        return new AddDockEdgeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteDockEdgeCommand DELETE_DOCK_EDGE( final Node parent, final Node candidate ) {
        return new DeleteDockEdgeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteEdgeCommand DELETE_EDGE( final Edge<? extends View, Node> edge ) {
        return new DeleteEdgeCommand( edge );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteNodeCommand DELETE_NODE( final Node candidate ) {
        return new DeleteNodeCommand( candidate );
    }

    public SetConnectionSourceNodeCommand SET_SOURCE_NODE( final Node<? extends View<?>, Edge> sourceNode,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionSourceNodeCommand( sourceNode, edge, magnetIndex );
    }

    public SetConnectionTargetNodeCommand SET_TARGET_NODE( final Node<? extends View<?>, Edge> targetNode,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionTargetNodeCommand( targetNode, edge, magnetIndex );
    }

    public UpdateElementPositionCommand UPDATE_POSITION( final Node<?, Edge> element,
                                                         final Double x,
                                                         final Double y ) {
        return new UpdateElementPositionCommand( element, x, y );
    }

    @SuppressWarnings( "unchecked" )
    public UpdateElementPropertyValueCommand UPDATE_PROPERTY_VALUE( final Node element,
                                                                    final String propertyId,
                                                                    final Object value ) {
        return new UpdateElementPropertyValueCommand( element, propertyId, value );
    }

    public MorphNodeCommand MORPH_NODE( final Node<Definition, Edge> candidate,
                                        final MorphDefinition morphDefinition,
                                        final String morphTarget ) {
        return new MorphNodeCommand( candidate, morphDefinition, morphTarget );
    }

}

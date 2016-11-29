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

    public AddNodeCommand ADD_NODE( final Node candidate ) {
        return new AddNodeCommand( candidate );
    }

    @SuppressWarnings( "unchecked" )
    public AddChildNodeCommand ADD_CHILD_NODE( final Node parent,
                                               final Node candidate ) {
        return new AddChildNodeCommand( parent, candidate, null, null );
    }

    @SuppressWarnings( "unchecked" )
    public AddChildNodeCommand ADD_CHILD_NODE( final Node parent,
                                               final Node candidate,
                                               final Double x,
                                               final Double y ) {
        return new AddChildNodeCommand( parent, candidate, x, y );
    }

    @SuppressWarnings( "unchecked" )
    public AddDockedNodeCommand ADD_DOCKED_NODE( final Node parent,
                                                 final Node candidate ) {
        return new AddDockedNodeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public AddConnectorCommand ADD_CONNECTOR( final Node target, final Edge edge, final int magnetIdx ) {
        return new AddConnectorCommand( target, edge, magnetIdx );
    }

    @SuppressWarnings( "unchecked" )
    public SetChildNodeCommand SET_CHILD_NODE( final Node parent, final Node candidate ) {
        return new SetChildNodeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public SetParentNodeCommand SET_PARENT_NODE( final Node parent, final Node candidate ) {
        return new SetParentNodeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DockNodeCommand DOCK_NODE( final Node parent, final Node candidate ) {
        return new DockNodeCommand( parent, candidate );
    }

    public MorphNodeCommand MORPH_NODE( final Node<Definition, Edge> candidate,
                                        final MorphDefinition morphDefinition,
                                        final String morphTarget ) {
        return new MorphNodeCommand( candidate, morphDefinition, morphTarget );
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

    @SuppressWarnings( "unchecked" )
    public SafeDeleteNodeCommand SAFE_DELETE_NODE( final Node candidate ) {
        return new SafeDeleteNodeCommand( candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteNodeCommand DELETE_NODE( final Node candidate ) {
        return new DeleteNodeCommand( candidate );
    }

    @SuppressWarnings( "unchecked" )
    public RemoveChildCommand REMOVE_CHILD( final Node parent, final Node candidate ) {
        return new RemoveChildCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public RemoveParentCommand REMOVE_PARENT( final Node parent, final Node candidate ) {
        return new RemoveParentCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public UnDockNodeCommand UNDOCK_NODE( final Node parent, final Node candidate ) {
        return new UnDockNodeCommand( parent, candidate );
    }

    @SuppressWarnings( "unchecked" )
    public DeleteConnectorCommand DELETE_CONNECTOR( final Edge<? extends View, Node> edge ) {
        return new DeleteConnectorCommand( edge );
    }

    public ClearGraphCommand CLEAR_GRAPH() {
        return new ClearGraphCommand( "" );
    }

    public ClearGraphCommand CLEAR_GRAPH( final String rootUUID ) {
        return new ClearGraphCommand( rootUUID );
    }

}

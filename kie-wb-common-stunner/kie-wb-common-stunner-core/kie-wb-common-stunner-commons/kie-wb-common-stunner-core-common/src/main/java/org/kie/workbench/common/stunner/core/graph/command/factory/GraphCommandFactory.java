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

package org.kie.workbench.common.stunner.core.graph.command.factory;

import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.*;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GraphCommandFactory {

    public AddChildNodeCommand ADD_CHILD_NODE( final Node parent,
                                               final Node candidate ) {
        return new AddChildNodeCommand( parent.getUUID(), candidate );
    }

    public AddDockedNodeCommand ADD_DOCKED_NODE( final Node parent,
                                                 final Node candidate ) {
        return new AddDockedNodeCommand( parent.getUUID(), candidate );
    }

    public SafeDeleteNodeCommand SAFE_DELETE_NODE( final Node candidate ) {
        return new SafeDeleteNodeCommand( candidate.getUUID() );
    }

    public AddEdgeCommand ADD_EDGE( final Node target, final Edge edge ) {
        return new AddEdgeCommand( target.getUUID(), edge );
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

    public AddChildEdgeCommand ADD_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new AddChildEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

    public AddParentEdgeCommand ADD_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new AddParentEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

    public DeleteChildEdgeCommand DELETE_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new DeleteChildEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

    public DeleteParentEdgeCommand DELETE_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new DeleteParentEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

    public AddDockEdgeCommand ADD_DOCK_EDGE( final Node parent, final Node candidate ) {
        return new AddDockEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

    public DeleteDockEdgeCommand DELETE_DOCK_EDGE( final Node parent, final Node candidate ) {
        return new DeleteDockEdgeCommand( parent.getUUID(), candidate.getUUID() );
    }

    public DeleteEdgeCommand DELETE_EDGE( final Edge<? extends View, Node> edge ) {
        return new DeleteEdgeCommand( edge.getUUID() );
    }

    public DeleteNodeCommand DELETE_NODE( final Node candidate ) {
        return new DeleteNodeCommand( candidate.getUUID() );
    }

    public SetConnectionSourceNodeCommand SET_SOURCE_NODE( final Node<? extends View<?>, Edge> sourceNode,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionSourceNodeCommand( null != sourceNode ? sourceNode.getUUID() : null,
                edge, magnetIndex );
    }

    public SetConnectionTargetNodeCommand SET_TARGET_NODE( final Node<? extends View<?>, Edge> targetNode,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionTargetNodeCommand( null != targetNode ? targetNode.getUUID() : null,
                edge, magnetIndex );
    }

    public UpdateElementPositionCommand UPDATE_POSITION( final Element<?> element,
                                                         final Double x,
                                                         final Double y ) {
        return new UpdateElementPositionCommand( element.getUUID(), x, y );
    }

    public UpdateElementPropertyValueCommand UPDATE_PROPERTY_VALUE( final Element element,
                                                                    final String propertyId,
                                                                    final Object value ) {
        return new UpdateElementPropertyValueCommand( element.getUUID(), propertyId, value );
    }

    public MorphNodeCommand MORPH_NODE( final Node<Definition, Edge> candidate,
                                        final MorphDefinition morphDefinition,
                                        final String morphTarget ) {
        return new MorphNodeCommand( candidate, morphDefinition, morphTarget );
    }

}

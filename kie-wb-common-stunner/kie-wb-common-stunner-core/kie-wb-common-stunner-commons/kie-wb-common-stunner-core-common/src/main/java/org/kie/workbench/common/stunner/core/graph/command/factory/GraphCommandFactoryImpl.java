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
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.*;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GraphCommandFactoryImpl implements GraphCommandFactory {

    public GraphCommandFactoryImpl() {
    }

    @Override
    public AddChildNodeCommand ADD_CHILD_NODE( final Graph target,
                                               final Node parent,
                                               final Node candidate ) {
        return new AddChildNodeCommand( target, parent, candidate );
    }

    @Override
    public AddDockedNodeCommand ADD_DOCKED_NODE( final Graph target,
                                                 final Node parent,
                                                 final Node candidate ) {
        return new AddDockedNodeCommand( target, parent, candidate );
    }

    @Override
    public SafeDeleteNodeCommand SAFE_DELETE_NODE( final Graph target,
                                                   final Node candidate ) {
        return new SafeDeleteNodeCommand( target, candidate );
    }

    @Override
    public AddEdgeCommand ADD_EDGE( final Node target, final Edge edge ) {
        return new AddEdgeCommand( target, edge );
    }

    @Override
    public AddNodeCommand ADD_NODE( final Graph target,
                                    final Node candidate ) {
        return new AddNodeCommand( target, candidate );
    }

    @Override
    public ClearGraphCommand CLEAR_GRAPH( final Graph target ) {
        return new ClearGraphCommand( target, "" );
    }

    @Override
    public ClearGraphCommand CLEAR_GRAPH( final Graph target,
                                          final String rootUUID ) {
        return new ClearGraphCommand( target, rootUUID );
    }

    @Override
    public AddChildEdgeCommand ADD_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new AddChildEdgeCommand( parent, candidate );
    }

    @Override
    public AddParentEdgeCommand ADD_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new AddParentEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteChildEdgeCommand DELETE_CHILD_EDGE( final Node parent, final Node candidate ) {
        return new DeleteChildEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteParentEdgeCommand DELETE_PARENT_EDGE( final Node parent, final Node candidate ) {
        return new DeleteParentEdgeCommand( parent, candidate );
    }

    @Override
    public AddDockEdgeCommand ADD_DOCK_EDGE( final Node parent, final Node candidate ) {
        return new AddDockEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteDockEdgeCommand DELETE_DOCK_EDGE( final Node parent, final Node candidate ) {
        return new DeleteDockEdgeCommand( parent, candidate );
    }

    @Override
    public DeleteEdgeCommand DELETE_EDGE( final Edge<? extends View, Node> edge ) {
        return new DeleteEdgeCommand( edge );
    }

    @Override
    public DeleteNodeCommand DELETE_NODE( final Graph target,
                                          final Node candidate ) {
        return new DeleteNodeCommand( target, candidate );
    }

    @Override
    public SetConnectionSourceNodeCommand SET_SOURCE_NODE( final Node<? extends View<?>, Edge> sourceNode,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionSourceNodeCommand( sourceNode, edge, magnetIndex );
    }

    @Override
    public SetConnectionTargetNodeCommand SET_TARGET_NODE( final Node<? extends View<?>, Edge> targetNode,
                                                           final Edge<? extends View<?>, Node> edge,
                                                           final int magnetIndex ) {
        return new SetConnectionTargetNodeCommand( targetNode, edge, magnetIndex );
    }

    @Override
    public UpdateElementPositionCommand UPDATE_POSITION( final Element element,
                                                         final Double x,
                                                         final Double y ) {
        return new UpdateElementPositionCommand( element, x, y );
    }

    @Override
    public UpdateElementPropertyValueCommand UPDATE_PROPERTY_VALUE( final Element element,
                                                                    final String propertyId,
                                                                    final Object value ) {
        return new UpdateElementPropertyValueCommand( element, propertyId, value );
    }

    @Override
    public MorphNodeCommand MORPH_NODE( final Node<Definition, Edge> candidate,
                                        final MorphDefinition morphDefinition,
                                        final String morphTarget ) {
        return new MorphNodeCommand( candidate, morphDefinition, morphTarget );
    }

}

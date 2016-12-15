/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Stack;

@Dependent
public final class ChildrenTraverseProcessorImpl extends AbstractContentTraverseProcessor<Child, Node<View, Edge>, Edge<Child, Node>, ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>>
        implements ChildrenTraverseProcessor {

    private final Stack<Node<View, Edge>> parents = new Stack<>();
    private String rootUUID = null;

    @Inject
    public ChildrenTraverseProcessorImpl( final TreeWalkTraverseProcessor treeWalkTraverseProcessor ) {
        super( treeWalkTraverseProcessor );
    }

    @Override
    protected TreeWalkTraverseProcessor.EdgeVisitorPolicy getPolicy() {
        return TreeWalkTraverseProcessor.EdgeVisitorPolicy.VISIT_EDGE_BEFORE_TARGET_NODE;

    }

    @Override
    protected void doStartGraphTraversal( final Graph graph,
                                          final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback ) {
        super.doStartGraphTraversal( graph, callback );
        parents.clear();

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected boolean doStartEdgeTraversal( final Edge edge,
                                            final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback ) {
        if ( accepts( edge ) ) {
            final Node<View, Edge> parent = edge.getSourceNode();
            parents.push( parent );
            if ( hasParentsRootUUID() ) {
                return super.doStartEdgeTraversal( edge, callback );

            }
            return true;

        }
        return false;

    }

    @Override
    protected boolean doEndEdgeTraversal( final Edge edge,
                                          final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback ) {
        if ( accepts( edge ) ) {
            parents.pop();
            return super.doEndEdgeTraversal( edge, callback );
        }
        return false;

    }

    @Override
    protected void doEndGraphTraversal( final Graph graph,
                                        final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback ) {
        super.doEndGraphTraversal( graph, callback );
        parents.clear();

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected boolean doStartNodeTraversal( final Node node,
                                            final ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> callback ) {
        if ( parents.isEmpty() ) {
            return super.doStartNodeTraversal( node, callback );

        }
        if ( hasParentsRootUUID() ) {
            return callback.startNodeTraversal( parents.iterator(), node );

        }
        return true;
    }

    @Override
    protected boolean accepts( final Edge edge ) {
        return edge.getContent() instanceof Child;
    }

    @Override
    public ChildrenTraverseProcessor setRootUUID( final String rootUUID ) {
        this.rootUUID = rootUUID;
        return this;
    }

    protected boolean hasParentsRootUUID() {
        if ( isEmpty( rootUUID ) ) {
            return true;

        } else if ( !parents.isEmpty() ) {
            for ( final Node parent : parents ) {
                if ( isRootUUID( parent ) ) {
                    return true;

                }

            }

        }
        return false;
    }

    protected boolean isRootUUID( final Node node ) {
        if ( isEmpty( rootUUID ) ) {
            return false;
        }
        return null != node && node.getUUID().equals( rootUUID );
    }

    private boolean isEmpty( final String s ) {
        return s == null || s.trim().length() == 0;
    }

}

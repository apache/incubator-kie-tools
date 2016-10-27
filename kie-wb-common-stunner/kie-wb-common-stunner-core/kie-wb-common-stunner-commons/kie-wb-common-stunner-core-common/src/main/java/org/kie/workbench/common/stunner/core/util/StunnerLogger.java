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

package org.kie.workbench.common.stunner.core.util;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractFullContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.FullContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.FullContentTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Just for development use.
 */
public class StunnerLogger {

    private static Logger LOGGER = Logger.getLogger( StunnerLogger.class.getName() );

    private static String getDefinitionId( final Object o ) {
        return BindableAdapterUtils.getDefinitionId( o.getClass() );
    }

    public static void logCommandResults( final Iterable<CommandResult> results ) {
        if ( results == null ) {
            log( "Results is null" );
        } else {
            for ( CommandResult result : results ) {
                logCommandResult( result );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public static void logCommandResult( final CommandResult result ) {
        String message = result.getMessage();
        CommandResult.Type type = result.getType();
        log( "Command Result [message=" + message + "] [type" + type.name() );
        Iterable<RuleViolation> violations = result.getViolations();
        logRuleViolations( violations );
    }

    public static void logRuleViolations( final Iterable<RuleViolation> violations ) {
        if ( violations == null ) {
            log( "Violations is null" );
        } else {
            for ( RuleViolation result : violations ) {
                logRuleViolation( result );
            }
        }
    }

    public static void logRuleViolation( final RuleViolation violation ) {
        String message = violation.getMessage();
        RuleViolation.Type type = violation.getViolationType();
        log( "Rule Violation [message=" + message + "] [type" + type.name() );
    }

    private static final FullContentTraverseCallback<Node<View, Edge>, Edge<Object, Node>> TREE_TRAVERSE_CALLBACK =
            new AbstractFullContentTraverseCallback<Node<View, Edge>, Edge<Object, Node>>() {

                private String indent = "";

                @Override
                public void startViewEdgeTraversal( final Edge<Object, Node> edge ) {
                    log( indent + "(View) Edge UUID: " + edge.getUUID() );
                    final View viewContent = ( View ) edge.getContent();
                    final String dId = getDefinitionId( viewContent.getDefinition() );
                    log( indent + "(View) Edge Id: " + dId );
                    final Node outNode = ( Node ) edge.getTargetNode();
                    if ( outNode == null ) {
                        log( indent + "  No outgoing node found" );
                    } else {
                        log( indent + "  Outgoing Node" );
                        log( indent + "  ==============" );
                    }
                }

                @Override
                public void startChildEdgeTraversal( final Edge<Object, Node> edge ) {
                    log( "(Child= Edge UUID: " + edge.getUUID() );
                    final Node outNode = edge.getTargetNode();
                    if ( outNode == null ) {
                        log( indent + "  No outgoing node found" );
                    } else {
                        log( indent + "  Outgoing Node" );
                        log( indent + "  ==============" );
                    }
                }

                @Override
                public void startParentEdgeTraversal( final Edge<Object, Node> edge ) {
                }

                @Override
                public void startEdgeTraversal( final Edge<Object, Node> edge ) {
                    log( indent + "Edge UUID: " + edge.getUUID() );
                    final Node outNode = ( Node ) edge.getTargetNode();
                    if ( outNode == null ) {
                        log( indent + "  No outgoing node found" );
                    } else {
                        log( indent + "  Outgoing Node" );
                        log( indent + "  ==============" );
                    }
                }

                @Override
                public void startGraphTraversal( final Graph<DefinitionSet, Node<View, Edge>> graph ) {
                    if ( graph == null ) {
                        error( "Graph is null!" );
                    } else {
                        final DefinitionSet view = graph.getContent();
                        final Bounds bounds = view.getBounds();
                        log( indent + "Graph UUID: " + graph.getUUID() );
                        log( indent + "Graph Bounds: " + bounds );
                        log( indent + "  Graph Starting nodes" );
                        log( indent + "  ====================" );
                    }
                }

                @Override
                public void startNodeTraversal( final Node<View, Edge> node ) {
                    log( indent + "(View) Node UUID: " + node.getUUID() );
                    final View view = node.getContent();
                    final String nId = getDefinitionId( view.getDefinition() );
                    final Bounds bounds = view.getBounds();
                    log( indent + "(View) Node Id: " + nId );
                    log( indent + "(View) Node Bounds: " + bounds );
                    final Node parent = getParent( node );
                    if ( null != parent ) {
                        log( indent + "(View) Node Parent is: " + parent.getUUID() );
                    }
                    Set<Edge> outEdges = new HashSet<>( node.getOutEdges() );
                    if ( outEdges.isEmpty() ) {
                        log( indent + "  No outgoing edges found" );
                    } else {
                        log( indent + "  Outgoing edges" );
                        log( indent + "  ==============" );
                    }
                }

                @Override
                public void endGraphTraversal() {
                }

            };

    @SuppressWarnings( "unchecked" )
    private static Node getParent( final Node node ) {
        List<Edge> inEdges = node.getInEdges();
        if ( null != inEdges && !inEdges.isEmpty() ) {
            for ( final Edge edge : inEdges ) {
                if ( edge.getContent() instanceof Child ) {
                    return edge.getSourceNode();
                }
            }
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public static void log( final Graph graph ) {
        if ( null != graph ) {
            new FullContentTraverseProcessorImpl( new TreeWalkTraverseProcessorImpl() )
                    .traverse( graph, TREE_TRAVERSE_CALLBACK );
        }
    }

    @SuppressWarnings( "unchecked" )
    public static void log( final Node<View, Edge> node ) {
        if ( null == node ) {
            log( "Node is null" );

        } else {
            log( "(View) Node UUID: " + node.getUUID() );
            final View view = node.getContent();
            final String nId = getDefinitionId( view.getDefinition() );
            final Bounds bounds = view.getBounds();
            log( "(View) Node Id: " + nId );
            log( "(View) Node Bounds: " + bounds );
            final Node parent = getParent( node );
            if ( null != parent ) {
                log( "(View) Node Parent is: " + parent.getUUID() );
            }
            Set<Edge> outEdges = new HashSet<>( node.getOutEdges() );
            if ( outEdges.isEmpty() ) {
                log( "No outgoing edges found" );
            } else {
                log( "Outgoing edges" );
                log( "==============" );
                for ( final Edge edge : outEdges ) {
                    log( edge );
                }
            }
            Set<Edge> inEdges = new HashSet<>( node.getInEdges() );
            if ( inEdges.isEmpty() ) {
                log( "No incoming edges found" );
            } else {
                log( "incoming edges" );
                log( "==============" );
                for ( final Edge edge : inEdges ) {
                    log( edge );
                }
            }
        }

    }

    public static void log( final Edge<?, Node> edge ) {
        log( "Edge UUID: " + edge.getUUID() );
        final Object content = edge.getContent();
        log( "  Edge Content: " + content.getClass().getName() );
        final Node inNode = edge.getSourceNode();
        final Node outNode = edge.getTargetNode();
        log( "  Edge In Node: " + ( null != inNode ? inNode.getUUID() : "null" ) );
        log( "  Edge Out Node: " + ( null != outNode ? outNode.getUUID() : "null" ) );
    }

    private static void log( final String message ) {
        LOGGER.log( Level.INFO, message );
    }

    private static void error( final String message ) {
        LOGGER.log( Level.SEVERE, message );
    }

}

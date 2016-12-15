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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.Collection;

/**
 * A Command to set the outgoing connection for an edge.
 * Note: if the connector's source is not set, the <code>sourceNode</code> can be null.
 */
@Portable
public final class SetConnectionSourceNodeCommand extends AbstractGraphCommand {

    private final String sourceNodeUUID;
    private final String edgeUUID;
    private final Integer magnetIndex;

    private String lastSourceNodeUUID;
    private Integer lastMagnetIndex;
    private transient Edge<? extends View, Node> edge;
    private transient Node<? extends View<?>, Edge> targetNode;
    private transient Node<? extends View<?>, Edge> sourceNode;

    @SuppressWarnings( "unchecked" )
    public SetConnectionSourceNodeCommand( @MapsTo( "sourceNodeUUID" ) String sourceNodeUUID,
                                           @MapsTo( "edgeUUID" ) String edgeUUID,
                                           @MapsTo( "magnetIndex" ) Integer magnetIndex ) {
        this.edgeUUID = PortablePreconditions.checkNotNull( "edgeUUID", edgeUUID );
        this.sourceNodeUUID = sourceNodeUUID;
        this.magnetIndex = PortablePreconditions.checkNotNull( "magnetIndex", magnetIndex );
    }

    @SuppressWarnings( "unchecked" )
    public SetConnectionSourceNodeCommand( Node<? extends View<?>, Edge> sourceNode,
                                           Edge<? extends View, Node> edge,
                                           Integer magnetIndex ) {
        this( null != sourceNode ? sourceNode.getUUID() : null, edge.getUUID(), magnetIndex );
        this.sourceNode = sourceNode;
        this.edge = edge;
        this.targetNode = edge.getTargetNode();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final CommandResult<RuleViolation> results = allow( context );
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            final Node<?, Edge> sourceNode = getSourceNode( context );
            final Edge<? extends View, Node> edge = getEdge( context );
            final Node<? extends View<?>, Edge> lastSourceNode = edge.getSourceNode();
            if ( null != lastSourceNode ) {
                this.lastSourceNodeUUID = lastSourceNode.getUUID();
                lastSourceNode.getOutEdges().remove( edge );
            }
            if ( null != sourceNode ) {
                sourceNode.getOutEdges().add( edge );
            }
            edge.setSourceNode( sourceNode );
            ViewConnector connectionContent = ( ViewConnector ) edge.getContent();
            lastMagnetIndex = connectionContent.getSourceMagnetIndex();
            connectionContent.setSourceMagnetIndex( magnetIndex );
        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> check( final GraphCommandExecutionContext context ) {
        final Node<View<?>, Edge> sourceNode = ( Node<View<?>, Edge> ) getSourceNode( context );
        final Edge<View<?>, Node> edge = ( Edge<View<?>, Node> ) getEdge( context );
        final GraphCommandResultBuilder resultBuilder = new GraphCommandResultBuilder();
        final Collection<RuleViolation> connectionRuleViolations =
                ( Collection<RuleViolation> ) context.getRulesManager()
                        .connection().evaluate( edge, sourceNode, targetNode ).violations();
        resultBuilder.addViolations( connectionRuleViolations );
        final Node<View<?>, Edge> currentSource = edge.getSourceNode();
        // If the edge has an outoutgoing source node, check cardinality for removing it.
        if ( null != currentSource ) {
            final Collection<RuleViolation> cardinalityRuleViolations =
                    ( Collection<RuleViolation> ) context.getRulesManager()
                            .edgeCardinality()
                            .evaluate( edge,
                                    currentSource,
                                    currentSource.getOutEdges(),
                                    EdgeCardinalityRule.Type.OUTGOING,
                                    RuleManager.Operation.DELETE )
                            .violations();
            resultBuilder.addViolations( cardinalityRuleViolations );
        }
        // If the new source node exist, evaluate cardinality rules for this edge.
        if ( null != sourceNode ) {
            final Collection<RuleViolation> cardinalityRuleViolations =
                    ( Collection<RuleViolation> ) context.getRulesManager()
                            .edgeCardinality()
                            .evaluate( edge,
                                    sourceNode,
                                    sourceNode.getOutEdges(),
                                    EdgeCardinalityRule.Type.OUTGOING,
                                    RuleManager.Operation.ADD )
                            .violations();
            resultBuilder.addViolations( cardinalityRuleViolations );
        }
        return resultBuilder.build();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> undo( final GraphCommandExecutionContext context ) {
        final SetConnectionTargetNodeCommand undoCommand =
                new SetConnectionTargetNodeCommand( ( Node<? extends View<?>, Edge> ) getNode( context, lastSourceNodeUUID ),
                        getEdge( context ), lastMagnetIndex );
        return undoCommand.execute( context );
    }

    @SuppressWarnings( "unchecked" )
    public Node<? extends View<?>, Edge> getTargetNode( final GraphCommandExecutionContext context ) {
        if ( null == targetNode ) {
            targetNode = getEdge( context ).getTargetNode();
        }
        return targetNode;
    }

    @SuppressWarnings( "unchecked" )
    public Node<? extends View<?>, Edge> getSourceNode( final GraphCommandExecutionContext context ) {
        if ( null == sourceNode ) {
            sourceNode = ( Node<? extends View<?>, Edge> ) getNode( context, sourceNodeUUID );
        }
        return sourceNode;
    }

    public Edge<? extends View, Node> getEdge( final GraphCommandExecutionContext context ) {
        if ( null == this.edge ) {
            this.edge = getViewEdge( context, edgeUUID );
        }
        return this.edge;
    }

    public Node<? extends View<?>, Edge> getSourceNode() {
        return sourceNode;
    }

    public Edge<? extends View, Node> getEdge() {
        return edge;
    }

    public Node<? extends View<?>, Edge> getTargetNode() {
        return targetNode;
    }

    public Integer getMagnetIndex() {
        return magnetIndex;
    }

    public Integer getLastSourceMagnetIndex() {
        return lastMagnetIndex;
    }

    @Override
    public String toString() {
        return "SetConnectionSourceNodeCommand [edge=" + edgeUUID
                + ", candidate=" + ( null != sourceNodeUUID ? sourceNodeUUID : "null" )
                + ", magnet=" + magnetIndex + "]";
    }

}

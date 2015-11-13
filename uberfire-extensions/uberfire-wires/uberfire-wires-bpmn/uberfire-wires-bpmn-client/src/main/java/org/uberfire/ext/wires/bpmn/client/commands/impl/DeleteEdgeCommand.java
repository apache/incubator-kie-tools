/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.bpmn.client.commands.impl;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

/**
 * A Command to delete an Edge between two GraphNodes
 */
public class DeleteEdgeCommand implements Command {

    private BpmnGraphNode outgoingNode;
    private BpmnGraphNode incomingNode;
    private BpmnEdge edge;

    public DeleteEdgeCommand( final BpmnGraphNode outgoingNode,
                              final BpmnGraphNode incomingNode,
                              final BpmnEdge edge ) {
        this.outgoingNode = PortablePreconditions.checkNotNull( "outgoingNode",
                                                                outgoingNode );
        this.incomingNode = PortablePreconditions.checkNotNull( "incomingNode",
                                                                incomingNode );
        this.edge = PortablePreconditions.checkNotNull( "edge",
                                                        edge );
    }

    @Override
    public Results apply( final RuleManager ruleManager ) {
        final Results results = new DefaultResultsImpl();
        boolean isEdgeOutgoing = false;
        boolean isEdgeIncoming = false;
        for ( BpmnEdge e : outgoingNode.getOutEdges() ) {
            if ( e.equals( edge ) ) {
                isEdgeOutgoing = true;
                break;
            }
        }
        for ( BpmnEdge e : incomingNode.getInEdges() ) {
            if ( e.equals( edge ) ) {
                isEdgeIncoming = true;
                break;
            }
        }
        if ( isEdgeOutgoing && isEdgeIncoming ) {
            results.getMessages().addAll( ruleManager.checkCardinality( outgoingNode,
                                                                        incomingNode,
                                                                        edge,
                                                                        RuleManager.Operation.DELETE ).getMessages() );
            if ( !results.contains( ResultType.ERROR ) ) {
                outgoingNode.getOutEdges().remove( edge );
                incomingNode.getInEdges().remove( edge );
            }
        } else {
            results.addMessage( new DefaultResultImpl( ResultType.WARNING,
                                                       "The Edge does not connect the given GraphNodes and hence was not deleted." ) );
        }
        return results;
    }

    @Override
    public Results undo( final RuleManager ruleManager ) {
        final Command undoCommand = new AddEdgeCommand( outgoingNode,
                                                        incomingNode,
                                                        edge );
        return undoCommand.apply( ruleManager );
    }

}

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
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

/**
 * A Command to delete a GraphNode from a Graph
 */
public class DeleteGraphNodeCommand implements Command {

    private BpmnGraph target;
    private BpmnGraphNode candidate;

    public DeleteGraphNodeCommand( final BpmnGraph target,
                                   final BpmnGraphNode candidate ) {
        this.target = PortablePreconditions.checkNotNull( "target",
                                                          target );
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                                                             candidate );
    }

    @Override
    public Results apply( final RuleManager ruleManager ) {
        final Results results = new DefaultResultsImpl();
        boolean isNodeInGraph = false;
        for ( BpmnGraphNode node : target ) {
            if ( node.equals( candidate ) ) {
                isNodeInGraph = true;
                break;
            }
        }
        if ( isNodeInGraph ) {
            results.getMessages().addAll( ruleManager.checkCardinality( target,
                                                                        candidate,
                                                                        RuleManager.Operation.DELETE ).getMessages() );
            if ( !results.contains( ResultType.ERROR ) ) {
                target.removeNode( candidate.getId() );
            }
        } else {
            results.addMessage( new DefaultResultImpl( ResultType.WARNING,
                                                       "GraphNode was not present in Graph and hence was not deleted." ) );
        }
        return results;
    }

    @Override
    public Results undo( final RuleManager ruleManager ) {
        final Command undoCommand = new AddGraphNodeCommand( target,
                                                             candidate );
        return undoCommand.apply( ruleManager );
    }

}

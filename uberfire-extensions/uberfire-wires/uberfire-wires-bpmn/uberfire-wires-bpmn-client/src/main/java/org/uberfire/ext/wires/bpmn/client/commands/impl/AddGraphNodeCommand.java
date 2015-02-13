/*
 * Copyright 2015 JBoss Inc
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
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;
import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

public class AddGraphNodeCommand implements Command {

    private Graph<Content> target;
    private GraphNode<Content> proposed;

    public AddGraphNodeCommand( final Graph<Content> target,
                                final GraphNode<Content> proposed ) {
        this.target = PortablePreconditions.checkNotNull( "target",
                                                          target );
        this.proposed = PortablePreconditions.checkNotNull( "proposed",
                                                            proposed );
    }

    @Override
    public Results apply( final RuleManager ruleManager ) {
        final Results results = new DefaultResultsImpl();
        results.getMessages().addAll( ruleManager.checkContainment( target,
                                                                    proposed ).getMessages() );
        results.getMessages().addAll( ruleManager.checkCardinality( target,
                                                                    proposed ).getMessages() );
        if ( !results.contains( ResultType.ERROR ) ) {
            target.addNode( proposed );
        }
        return results;
    }

    @Override
    public Results undo( final RuleManager ruleManager ) {
        target.removeNode( proposed.getId() );
        //We'll return results when all types of Rule are implemented as removing a Node could invalidate the Graph
        return new DefaultResultsImpl();
    }

}

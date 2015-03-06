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

import java.util.Collections;
import java.util.List;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;
import org.uberfire.ext.wires.bpmn.client.commands.Command;
import org.uberfire.ext.wires.bpmn.client.commands.Result;

public class AddGraphNodeCommand implements Command {

    private Graph<Content> graph;
    private GraphNode<Content> node;

    public AddGraphNodeCommand( final Graph<Content> process,
                                final GraphNode<Content> node ) {
        this.graph = PortablePreconditions.checkNotNull( "process",
                                                         process );
        this.node = PortablePreconditions.checkNotNull( "node",
                                                        node );
    }

    @Override
    public List<Result> apply() {
        graph.addNode( node );
        return Collections.emptyList();
    }

    @Override
    public List<Result> undo() {
        graph.removeNode( node.getId() );
        return Collections.emptyList();
    }

}

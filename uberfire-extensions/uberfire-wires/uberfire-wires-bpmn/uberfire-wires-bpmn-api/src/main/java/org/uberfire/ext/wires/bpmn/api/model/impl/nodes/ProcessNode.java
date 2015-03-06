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
package org.uberfire.ext.wires.bpmn.api.model.impl.nodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.content.DefaultContentImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.properties.DefaultPropertyImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.types.StringType;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphNodeImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.MapGraphStore;

/**
 * A BPMN "Process"
 */
@Portable
public class ProcessNode extends GraphNodeImpl<Content> implements Graph<Content> {

    private Graph<Content> graph = new GraphImpl<Content>( new MapGraphStore<Content>() );

    private Set<Role> roles = new HashSet<Role>() {{
        add( new DefaultRoleImpl( "all" ) );
        add( new DefaultRoleImpl( "Endevents_all" ) );
        add( new DefaultRoleImpl( "sequence_end" ) );
        add( new DefaultRoleImpl( "to_task_event" ) );
        add( new DefaultRoleImpl( "from_task_event" ) );
        add( new DefaultRoleImpl( "fromtoall" ) );
        add( new DefaultRoleImpl( "EndEventsMorph" ) );
    }};

    private Set<Property> properties = new HashSet<Property>() {{
        add( new DefaultPropertyImpl( "processn",
                                      new StringType(),
                                      "Process Name",
                                      "Process Name",
                                      false,
                                      false ) );
        add( new DefaultPropertyImpl( "documentation",
                                      new StringType(),
                                      "Documentation",
                                      "This attribute is used to annotate the BPMN element, such as descriptions and other documentation.",
                                      false,
                                      true ) );
    }};

    public ProcessNode() {
        setContent( new DefaultContentImpl( "BPMNProcess",
                                            "BPMN-Process",
                                            "A BPMN 2.0 Process.",
                                            roles,
                                            properties ) );
    }

    @Override
    public GraphNode<Content> addNode( final GraphNode<Content> node ) {
        return graph.addNode( node );
    }

    @Override
    public GraphNode<Content> removeNode( final int id ) {
        return graph.removeNode( id );
    }

    @Override
    public GraphNode<Content> getNode( final int id ) {
        return graph.getNode( id );
    }

    @Override
    public int size() {
        return graph.size();
    }

    @Override
    public Iterator<GraphNode<Content>> iterator() {
        return graph.iterator();
    }
}

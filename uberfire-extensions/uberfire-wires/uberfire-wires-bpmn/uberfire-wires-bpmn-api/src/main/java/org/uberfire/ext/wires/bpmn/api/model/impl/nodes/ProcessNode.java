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
package org.uberfire.ext.wires.bpmn.api.model.impl.nodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.content.DefaultContentImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.properties.DefaultPropertyImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.types.StringType;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphNodeImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.MapGraphStore;

/**
 * A BPMN "Process"
 */
@Portable
public class ProcessNode extends GraphNodeImpl<Content, BpmnEdge> implements
                                                                  BpmnGraph,
                                                                  BpmnGraphNode {

    private Graph<Content, BpmnGraphNode> graph = new GraphImpl<Content, BpmnGraphNode>( new MapGraphStore<BpmnGraphNode>() );

    private Set<Role> roles = new HashSet<Role>() {{
        add( new DefaultRoleImpl( "canContainArtifacts" ) );
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
    public BpmnGraphNode addNode( final BpmnGraphNode node ) {
        return graph.addNode( node );
    }

    @Override
    public BpmnGraphNode removeNode( final int id ) {
        return graph.removeNode( id );
    }

    @Override
    public BpmnGraphNode getNode( final int id ) {
        return graph.getNode( id );
    }

    @Override
    public int size() {
        return graph.size();
    }

    @Override
    public Iterator<BpmnGraphNode> iterator() {
        return graph.iterator();
    }

    @Override
    public ProcessNode copy() {
        final ProcessNode copy = new ProcessNode();
        copy.setContent( this.getContent().copy() );
        for ( BpmnGraphNode node : graph ) {
            copy.addNode( node.copy() );
        }
        return copy;
    }

    @Override
    public String toString() {
        return "ProcessNode{" +
                "graph=" + graph +
                ", roles=" + roles +
                ", properties=" + properties +
                '}';
    }

}

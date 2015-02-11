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
package org.uberfire.ext.wires.bpmn.beliefs.graph.impl;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Edge;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

@Portable
public class GraphNodeImpl<T> implements GraphNode<T> {

    private int id;
    private T content;

    private List<Edge> inEdges = new ArrayList<Edge>();
    private List<Edge> outEdges = new ArrayList<Edge>();

    public GraphNodeImpl( @MapsTo("id") int id ) {
        this.id = id;
    }

    public void setContent( T content ) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public int getId() {
        return this.id;
    }

    public void addInEdge( Edge outEdge ) {
        inEdges.add( outEdge );
    }

    public void addOutEdge( Edge inEdge ) {
        outEdges.add( inEdge );
    }

    public void removeInEdge( Edge outEdge ) {
        inEdges.remove( outEdge );
    }

    public void removeOutEdge( Edge inEdge ) {
        outEdges.remove( inEdge );
    }

    @Override
    public List<Edge> getInEdges() {
        return inEdges;
    }

    @Override
    public List<Edge> getOutEdges() {
        return outEdges;
    }

    @Override
    public String toString() {
        return "GraphNodeImpl{" +
                "id=" + id +
                ", content=" + content +
                ", inEdges=" + inEdges +
                ", outEdges=" + outEdges +
                '}';
    }

}

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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Edge;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

@Portable
public class GraphNodeImpl<T> implements GraphNode<T> {

    private int id;
    private T content;

    private List<Edge> inEdges = new ArrayList<Edge>();
    private List<Edge> outEdges = new ArrayList<Edge>();

    @Override
    public void setContent( T content ) {
        this.content = content;
    }

    @Override
    public T getContent() {
        return content;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId( int id ) {
        this.id = id;
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

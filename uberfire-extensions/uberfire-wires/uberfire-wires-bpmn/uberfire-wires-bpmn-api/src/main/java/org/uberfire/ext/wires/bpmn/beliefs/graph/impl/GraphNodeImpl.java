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
package org.uberfire.ext.wires.bpmn.beliefs.graph.impl;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Edge;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

@Portable
public class GraphNodeImpl<C, T extends Edge> implements GraphNode<C, T> {

    private int id;
    private C content;

    private List<T> inEdges = new ArrayList<T>();
    private List<T> outEdges = new ArrayList<T>();

    @Override
    public void setContent( C content ) {
        this.content = content;
    }

    @Override
    public C getContent() {
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
    public List<T> getInEdges() {
        return inEdges;
    }

    @Override
    public List<T> getOutEdges() {
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

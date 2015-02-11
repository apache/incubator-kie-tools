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

import java.util.Iterator;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

@Portable
public class GraphImpl<T> implements Graph<T>,
                                     Iterable<GraphNode<T>> {

    GraphStore<T> graphStore;

    public GraphImpl( @MapsTo("graphStore") GraphStore<T> graphStore ) {
        this.graphStore = graphStore;
    }

    protected int idCounter;

    public GraphNode<T> addNode() {
        return graphStore.addNode();
    }

    public GraphNode<T> removeNode( int id ) {
        return graphStore.removeNode( id );
    }

    @Override
    public GraphNode<T> getNode( int id ) {
        return graphStore.getNode( id );
    }

    @Override
    public int size() {
        return graphStore.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return graphStore.iterator();
    }

    @Override
    public String toString() {
        return "GraphImpl{" +
                "graphStore=" + graphStore +
                ", idCounter=" + idCounter +
                '}';
    }

}

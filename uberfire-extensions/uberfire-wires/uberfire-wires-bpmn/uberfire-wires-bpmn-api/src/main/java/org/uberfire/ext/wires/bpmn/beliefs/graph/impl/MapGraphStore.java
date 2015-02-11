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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

@Portable
public class MapGraphStore<T> implements GraphStore<T> {

    protected int idCounter;

    protected Map<Integer, GraphNode<T>> nodes = new HashMap();

    public Map<Integer, GraphNode<T>> getNodes() {
        return nodes;
    }

    @Override
    public GraphNode<T> addNode( GraphNode<T> node ) {
        node.setId( idCounter++ );
        nodes.put( node.getId(),
                   node );
        return node;
    }

    public GraphNode<T> removeNode( int id ) {
        return nodes.remove( id );
    }

    @Override
    public GraphNode<T> getNode( int id ) {
        return nodes.get( id );
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Iterator<GraphNode<T>> iterator() {
        return nodes.values().iterator();
    }
}

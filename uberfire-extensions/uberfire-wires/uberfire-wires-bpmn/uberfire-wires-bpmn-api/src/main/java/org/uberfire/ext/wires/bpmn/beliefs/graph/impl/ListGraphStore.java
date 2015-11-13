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
import java.util.Iterator;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

@Portable
public class ListGraphStore<T extends GraphNode> implements GraphStore<T> {

    private List<T> nodes = new ArrayList<T>();

    @Override
    public T addNode( T node ) {
        nodes.add( node );
        node.setId( nodes.size() );
        return node;
    }

    @Override
    public T removeNode( int id ) {
        throw new UnsupportedOperationException( "ListGraphStore is additive only." );
    }

    @Override
    public T getNode( int id ) {
        return nodes.get( id );
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Iterator<T> iterator() {
        return nodes.iterator();
    }
}

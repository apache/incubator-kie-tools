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
package org.uberfire.ext.wires.bpmn.beliefs.graph;

public interface Graph<T> extends Iterable<GraphNode<T>> {

    public GraphNode<T> addNode( GraphNode<T> node );

    public GraphNode<T> removeNode( int id );

    public GraphNode<T> getNode( int id );

    public T getContent();

    public void setContent( T content );

    public int size();

}

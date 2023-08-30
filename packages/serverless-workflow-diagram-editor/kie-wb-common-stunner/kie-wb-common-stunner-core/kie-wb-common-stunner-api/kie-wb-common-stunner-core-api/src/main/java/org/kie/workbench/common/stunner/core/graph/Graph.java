/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.graph;

/**
 * <p>The graph implementation is given by it's content, basically based on the <b>Labeled Property Graph Model</b>:</p>
 * <p>
 * <ul>
 * <li>
 * <p>Is made up of nodes, relationships (edges), properties and labels</p>
 * </li>
 * <li>
 * <p>Nodes contain properties. Think of nodes as documents that store properties in the form of arbitrary key-value pairs</p>
 * </li>
 * <li>
 * <p>Nodes can be tagged with one or more labels. Labels group nodes together, and indicate the roles they play within the data set</p>
 * </li>
 * <li>
 * <p>Relationships/edges connect nodes and structure the graph. A relationship always has a direction, a single name,
 * and a start node and an end node—there are no dangling relationships.
 * Together, a relationship’s direction and name add semantic clarity to the structuring of nodes</p>
 * </li>
 * <li>
 * <p>
 * Like nodes, relationships/edges can also have properties.
 * The ability to add properties to relationships is particularly useful for providing additional metadata for graph algorithms,
 * adding additional semantics to relationships (including quality and weight), and for constraining queries at runtime, if needed
 * </p>
 * </li>
 * <li>
 * <p>
 * You can create domain specific diagrams by providing rule constraints in the Definition Set
 * </p>
 * </li>
 * </ul>
 * <p>
 * <p>
 * <i>NOTE about E-R diagrams</i>: E-R diagrams are intrinsically supported as they're graphs as well,
 * but keep in mind that E-R diagrams allow only single, undirected, named relationships between entities,
 * and sometimes these diagrams are not enough to model rich and complex real scenarios where relationships
 * are several and semantically diverse
 * </p>
 */
public interface Graph<C, N extends Node> extends Element<C> {

    N addNode(final N node);

    N removeNode(final String uuid);

    N getNode(final String uuid);

    Iterable<N> nodes();

    void clear();
}

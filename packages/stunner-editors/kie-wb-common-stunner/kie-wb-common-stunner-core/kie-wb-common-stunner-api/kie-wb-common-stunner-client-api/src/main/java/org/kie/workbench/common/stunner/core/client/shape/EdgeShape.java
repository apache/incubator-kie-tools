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


package org.kie.workbench.common.stunner.core.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * A concrete extension of a Graph Shape type for Edges.
 * <p/>
 * Notice that the canvas representations for edges are not just connectors, edges
 * can be of other types, it depends on the graph element's content.
 * @param <W> The edge type.
 * @param <C> The edge's content type. It must be View or any subtype.
 * @param <V> The Shape View type.
 */
public interface EdgeShape<W, C extends View<W>, E extends Edge<C, Node>, V extends ShapeView>
        extends ElementShape<W, C, E, V> {

    /**
     * Update the edge view's connections.
     * @param element The edge instance.
     * @param source The source node's view.
     * @param target The target node's view.
     * @param mutationContext The mutation context.
     */
    void applyConnections(final E element,
                          final ShapeView<?> source,
                          final ShapeView<?> target,
                          final MutationContext mutationContext);
}

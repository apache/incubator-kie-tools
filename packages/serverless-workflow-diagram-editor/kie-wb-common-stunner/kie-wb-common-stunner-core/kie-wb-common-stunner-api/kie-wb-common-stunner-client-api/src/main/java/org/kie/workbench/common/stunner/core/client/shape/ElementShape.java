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
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * An extension of a Mutable Shape type whic bean is an element of a graph structure.
 * <p/>
 * This type do not care about nodes or edges, it just considers that the instance
 * is a graph element using a View content type.
 * @param <W> The graph element type.
 * @param <C> The graph element's content type. It must be View or any subtype.
 * @param <V> The Shape View type.
 */
public interface ElementShape<W, C extends View<W>, E extends Element<C>, V extends ShapeView> extends MutableShape<E, V> {

    /**
     * Update's the element's title.
     * @param title The new title value.
     * @param element The graph's element instance.
     * @param mutationContext The mutation context.
     */
    void applyTitle(final String title,
                    final E element,
                    final MutationContext mutationContext);

    /**
     * Updates the element's position.
     * @param element The graph's element instance.
     * @param mutationContext The mutation context.
     */
    void applyPosition(final E element,
                       final MutationContext mutationContext);
}

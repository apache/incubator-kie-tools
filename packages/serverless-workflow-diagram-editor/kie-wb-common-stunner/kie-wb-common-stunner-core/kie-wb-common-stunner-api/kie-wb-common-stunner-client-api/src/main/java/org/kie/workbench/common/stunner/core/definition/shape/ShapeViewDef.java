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


package org.kie.workbench.common.stunner.core.definition.shape;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * This type provides the way view is being updated from a given domain object instance.
 * @param <W> The domain object's type.
 * @param <V> The shape view type.
 */
public interface ShapeViewDef<W, V extends ShapeView> extends ShapeDef<W> {

    /**
     * Returns a new consumer instance for handling the view.
     * The view is being consumed each time the
     * object instance's properties are being changed.
     * @return A new view consumer instance.
     */
    BiConsumer<W, V> viewHandler();

    /**
     * If domain model and views support changing the view size,
     * a new view consumer instance is expected to be returned at this point
     * to handle the size attributes.
     * @return A new view consumer for handling the size, if any.
     */
    default Optional<BiConsumer<View<W>, V>> sizeHandler() {
        return Optional.empty();
    }

    /**
     * If the domain model or the view supports titles, a new view consumer
     * instance is expected to be returned at this point to handle the title value.
     * @return A new view consumer for the title.
     */
    default Optional<BiConsumer<String, V>> titleHandler() {
        return Optional.empty();
    }

    /**
     * If the domain model or the view supports titles and styling attributes for it,
     * a new view consumer  instance is expected to be returned at this point to handle
     * the font style attributes.
     * @return A new view consumer for the font attributes..
     */
    default Optional<BiConsumer<W, V>> fontHandler() {
        return Optional.empty();
    }
}

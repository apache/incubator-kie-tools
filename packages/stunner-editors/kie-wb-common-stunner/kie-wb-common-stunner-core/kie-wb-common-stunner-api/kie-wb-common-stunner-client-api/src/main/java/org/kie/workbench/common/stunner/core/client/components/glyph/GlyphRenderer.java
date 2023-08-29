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


package org.kie.workbench.common.stunner.core.client.components.glyph;

import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A type that renders a Glyph into a target view.
 * @param <G> The glyph type that this renderer supports.
 * @param <V> The target view where to display the glyph.
 */
public interface GlyphRenderer<G extends Glyph, V> {

    /**
     * The supported type of glyph.
     */
    Class<G> getGlyphType();

    /**
     * Renders a glyph instance for an specified size.
     * @param glyph The glyph instance.
     * @param width The width.
     * @param height The height.
     * @return The view instance displays the glyph
     */
    V render(final G glyph,
             final double width,
             final double height);

    static <R extends GlyphRenderer> R getRenderer(final Supplier<Spliterator<R>> instances,
                                                   final Class<?> glyphType) {
        return StreamSupport
                .stream(instances.get(),
                        false)
                .filter(renderer -> glyphType.equals(renderer.getGlyphType()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No glyph renderer found for type [" + glyphType + "]"));
    }
}

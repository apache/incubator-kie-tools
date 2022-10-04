/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.sugiyama.GraphLayer;

/**
 * A layout for the vertices in a graph.
 */
public class Layout {

    private final ArrayList<GraphLayer> layers;

    public Layout(final Collection<? extends GraphLayer> layers) {
        this.layers = new ArrayList<>(layers);
    }

    public List<VertexPosition> getVerticesPositions() {
        return layers.stream()
                .flatMap(graphLayer -> graphLayer.getVertices().stream())
                .collect(Collectors.toList());
    }
}

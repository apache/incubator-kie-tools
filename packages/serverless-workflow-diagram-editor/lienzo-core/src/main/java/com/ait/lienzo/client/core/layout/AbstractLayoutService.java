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

import java.util.List;

import com.ait.lienzo.client.core.layout.graph.Vertex;

public abstract class AbstractLayoutService {

    private static final double CLOSE_TO_ZERO_TOLERANCE = 0.1;

    protected static boolean isCloseToZero(final double value) {
        return Math.abs(value - 0) < CLOSE_TO_ZERO_TOLERANCE;
    }

    public abstract Layout createLayout(final List<Vertex> vertices,
                                        final String startingVertexId,
                                        final String endingVertexId);
}

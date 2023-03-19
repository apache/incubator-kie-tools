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

package com.ait.lienzo.client.core.layout.graph;

public class OutgoingEdge {

    private final String id;

    private final Vertex target;

    public OutgoingEdge(final String id, final Vertex target) {
        this.id = id;
        this.target = target;
    }

    public Vertex getTarget() {
        return target;
    }

    public String getId() {
        return id;
    }
}

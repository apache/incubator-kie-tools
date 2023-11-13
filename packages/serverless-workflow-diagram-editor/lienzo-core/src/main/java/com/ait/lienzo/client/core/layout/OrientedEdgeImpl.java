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


package com.ait.lienzo.client.core.layout;

import java.util.Objects;

import com.ait.lienzo.client.core.layout.sugiyama.OrientedEdge;

public class OrientedEdgeImpl implements OrientedEdge {

    private String from;
    private String to;
    private boolean isReversed;

    public OrientedEdgeImpl(final String from,
                            final String to) {
        this(from, to, false);
    }

    public OrientedEdgeImpl(final String from,
                            final String to,
                            final boolean isReversed) {
        this.from = from;
        this.to = to;
        this.isReversed = isReversed;
    }

    @Override
    public String getFromVertexId() {
        return this.from;
    }

    @Override
    public String getToVertexId() {
        return this.to;
    }

    /**
     * Checks if this edge is connected to the specified vertex.
     * @param vertexId The id of the vertex.
     * @return True if it is connected, false if it is not.
     */
    @Override
    public boolean isLinkedWithVertexId(final String vertexId) {
        return this.getFromVertexId().equals(vertexId) || this.getToVertexId().equals(vertexId);
    }

    @Override
    public boolean isReversed() {
        return isReversed;
    }

    @Override
    public void reverse() {
        final String oldTo = this.to;
        this.to = this.from;
        this.from = oldTo;
        this.isReversed = !this.isReversed;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof OrientedEdgeImpl) {
            final OrientedEdgeImpl that = (OrientedEdgeImpl) obj;
            return Objects.equals(getToVertexId(), that.getToVertexId())
                    && Objects.equals(getFromVertexId(), that.getFromVertexId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, isReversed);
    }
}

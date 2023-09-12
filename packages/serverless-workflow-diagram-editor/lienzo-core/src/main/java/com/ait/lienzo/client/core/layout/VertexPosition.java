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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VertexPosition implements Comparable<VertexPosition> {

    private final String id;
    private double median;
    private boolean isVirtual;
    private int x;
    private int y;
    private int width;
    private int height;
    private int bottomRightX;
    private int bottomRightY;
    private List<Edge> outgoingEdges;

    public VertexPosition(final String id) {
        this(id, false);
    }

    public VertexPosition(final String id,
                          final boolean isVirtual) {
        this.id = id;
        this.isVirtual = isVirtual;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBottomRightX() {
        return bottomRightX;
    }

    public void setBottomRightX(int bottomRightX) {
        this.bottomRightX = bottomRightX;
    }

    public int getBottomRightY() {
        return bottomRightY;
    }

    public void setBottomRightY(int bottomRightY) {
        this.bottomRightY = bottomRightY;
    }

    public boolean isVirtual() {
        return this.isVirtual;
    }

    private void setVirtual(final boolean virtual) {
        this.isVirtual = virtual;
    }

    private double getMedian() {
        return median;
    }

    public void setMedian(final double median) {
        this.median = median;
    }

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public String getId() {
        return this.id;
    }

    public List<Edge> getOutgoingEdges() {
        if (Objects.isNull(outgoingEdges)) {
            outgoingEdges = new ArrayList<>();
        }
        return outgoingEdges;
    }

    public VertexPosition copy() {
        final VertexPosition copy = new VertexPosition(this.id);
        copy.setMedian(this.median);
        copy.setVirtual(this.isVirtual);
        copy.setX(this.x);
        copy.setY(this.y);
        copy.setBottomRightX(this.bottomRightX);
        copy.setBottomRightY(this.bottomRightY);
        copy.setWidth(this.width);
        copy.setHeight(this.height);

        copy.outgoingEdges = this.outgoingEdges;
        return copy;
    }

    @Override
    public int compareTo(final VertexPosition other) {
        if (this.equals(other)) {
            return 0;
        } else if (this.getMedian() < other.getMedian()) {
            return -1;
        } else if (this.getMedian() > other.getMedian()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VertexPosition vertexPosition = (VertexPosition) o;
        return median == vertexPosition.median &&
                Objects.equals(id, vertexPosition.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, median);
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", median=" + median +
                ", isVirtual=" + isVirtual +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

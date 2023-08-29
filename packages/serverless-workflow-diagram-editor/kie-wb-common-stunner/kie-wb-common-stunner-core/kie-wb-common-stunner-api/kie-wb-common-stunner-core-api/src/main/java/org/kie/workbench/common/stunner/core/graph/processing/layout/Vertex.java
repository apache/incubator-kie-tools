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


package org.kie.workbench.common.stunner.core.graph.processing.layout;

import java.util.Objects;

public class Vertex implements Comparable<Vertex> {

    private final String id;
    private double median;
    private boolean isVirtual;
    private int x;
    private int y;

    public Vertex(final String id) {
        this(id, false);
    }

    public Vertex(final String id,
                  final boolean isVirtual) {
        this.id = id;
        this.isVirtual = isVirtual;
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

    public Vertex clone() {
        final Vertex clone = new Vertex(this.id);
        clone.setMedian(this.median);
        clone.setVirtual(this.isVirtual);
        clone.setX(this.x);
        clone.setY(this.y);
        return clone;
    }

    @Override
    public int compareTo(final Vertex other) {
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
        final Vertex vertex = (Vertex) o;
        return median == vertex.median &&
                Objects.equals(id, vertex.id);
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

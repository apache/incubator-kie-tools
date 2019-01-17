/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires;

public class OptionalBounds {

    public static OptionalBounds createEmptyBounds() {
        return new OptionalBounds();
    }

    public static OptionalBounds createMinBounds(final double minX,
                                                 final double minY) {
        return createEmptyBounds()
                .setMinX(minX)
                .setMinY(minY);
    }

    public static OptionalBounds create(final double minX,
                                        final double minY,
                                        final double maxX,
                                        final double maxY) {
        return createEmptyBounds()
                .setMinX(minX)
                .setMinY(minY)
                .setMaxX(maxX)
                .setMaxY(maxY);
    }

    private Double minX;
    private Double minY;
    private Double maxX;
    private Double maxY;

    public Double getMinX() {
        return minX;
    }

    public boolean hasMinX() {
        return null != minX;
    }

    public OptionalBounds setMinX(Double minX) {
        this.minX = minX;
        return this;
    }

    public Double getMinY() {
        return minY;
    }

    public boolean hasMinY() {
        return null != minY;
    }

    public OptionalBounds setMinY(Double minY) {
        this.minY = minY;
        return this;
    }

    public Double getMaxX() {
        return maxX;
    }

    public boolean hasMaxX() {
        return null != maxX;
    }

    public OptionalBounds setMaxX(Double maxX) {
        this.maxX = maxX;
        return this;
    }

    public Double getMaxY() {
        return maxY;
    }

    public boolean hasMaxY() {
        return null != maxY;
    }

    public OptionalBounds setMaxY(Double maxY) {
        this.maxY = maxY;
        return this;
    }

    public boolean lessOrEqualThanMinY(final double value) {
        return hasMinY() && value <= getMinY();

    }
    public boolean lessOrEqualThanMinX(final double value) {
        return hasMinX() && value <= getMinX();
    }

    public boolean biggerOrEqualThanMaxY(final double value) {
        return hasMaxY() && value >= getMaxY();

    }
    public boolean biggerOrEqualThanMaxX(final double value) {
        return hasMaxX() && value >= getMaxX();
    }


}

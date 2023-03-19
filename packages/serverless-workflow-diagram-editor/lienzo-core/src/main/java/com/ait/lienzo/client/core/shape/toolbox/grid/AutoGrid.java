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

package com.ait.lienzo.client.core.shape.toolbox.grid;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;

public class AutoGrid extends AbstractLayoutGrid<AutoGrid> implements SizeConstrainedGrid<Point2D> {

    public static boolean isHorizontal(final Direction direction) {
        switch (direction) {
            case EAST:
            case WEST:
                return true;
        }
        return false;
    }

    public static class Builder {

        private double pad = 5d;
        private double size = 15d;
        private Direction direction = Direction.SOUTH;
        private BoundingBox boundingBox = null;

        public Builder withPadding(double size) {
            this.pad = size;
            return this;
        }

        public Builder withIconSize(double size) {
            this.size = size;
            return this;
        }

        public Builder towards(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder forBoundingBox(BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
            return this;
        }

        public AutoGrid build() {
            assert null != boundingBox;
            final double max = isHorizontal(direction) ?
                    boundingBox.getWidth() :
                    boundingBox.getHeight();
            return new AutoGrid(pad,
                                size,
                                direction,
                                max);
        }
    }

    private Direction direction;
    private double maxSize;

    public AutoGrid(final double padding,
                    final double iconSize,
                    final Direction direction,
                    final double maxSize) {
        super(padding,
              iconSize);
        if (maxSize < 1 || null == direction) {
            throw new IllegalArgumentException("Not possible to instantiate grid.");
        }
        this.direction = direction;
        this.maxSize = maxSize;
    }

    public AutoGrid direction(final Direction direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public void setSize(final double width,
                        final double height) {
        if (isHorizontal(getDirection())) {
            maxSize(width);
        } else {
            maxSize(height);
        }
    }

    public AutoGrid maxSize(final double size) {
        this.maxSize = size;
        return this;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getMaxSize() {
        return maxSize;
    }

    @Override
    protected AbstractGridLayoutIterator createIterator() {
        return new AutoGridLayoutIterator(getPadding(),
                                          getIconSize(),
                                          getDirection(),
                                          getMaxSize());
    }

    private static class AutoGridLayoutIterator extends AbstractGridLayoutIterator {

        private final double padding;
        private final double iconSize;
        private final Direction towards;
        private final int maxRows;
        private final int maxCols;
        private int currentRow;
        private int currentColumn;

        private AutoGridLayoutIterator(final double padding,
                                       final double iconSize,
                                       final Direction direction,
                                       final double maxSize) {
            this.padding = padding;
            this.iconSize = iconSize;
            this.towards = direction;

            final double d1 = getPadding() + getIconSize();
            final int _maxItems = (int) (maxSize / d1); // Round down to an integer index value.
            final int maxItems = _maxItems > 0 ? _maxItems : 1;
            if (isHorizontal(direction)) {
                maxRows = -1;
                currentRow = 0;
                maxCols = maxItems;
                currentColumn = -1;
            } else {
                maxRows = maxItems;
                currentColumn = 0;
                maxCols = -1;
                currentRow = -1;
            }
        }

        @Override
        protected double getPadding() {
            return padding;
        }

        @Override
        protected double getIconSize() {
            return iconSize;
        }

        @Override
        protected Direction getTowards() {
            return towards;
        }

        // Provides an infinite grid, it just grows.
        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        protected int[] getNextIndex() {
            if (currentRow == (maxRows - 1)) {
                currentRow = -1;
                currentColumn++;
            } else if (currentColumn == (maxCols - 1)) {
                currentColumn = -1;
                currentRow++;
            }
            if (maxCols > -1) {
                currentColumn++;
            } else if (maxRows > -1) {
                currentRow++;
            }
            return new int[]{currentRow, currentColumn};
        }
    }
}

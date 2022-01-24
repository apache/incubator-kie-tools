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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;

public abstract class AbstractLayoutGrid<T extends AbstractLayoutGrid> implements Point2DGrid {

    private double padding;
    private double iconSize;

    public AbstractLayoutGrid(final double padding,
                              final double iconSize) {
        if (padding < 0 || iconSize < 0) {
            throw new IllegalArgumentException("Not possible to instantiate grid.");
        }
        this.padding = padding;
        this.iconSize = iconSize;
    }

    protected abstract AbstractGridLayoutIterator createIterator();

    public T padding(final double padding) {
        this.padding = padding;
        return cast();
    }

    public T iconSize(final double size) {
        this.iconSize = size;
        return cast();
    }

    public double getPadding() {
        return padding;
    }

    public double getIconSize() {
        return iconSize;
    }

    @Override
    public double getMargin() {
        return (iconSize + padding) / 2;
    }

    @Override
    public Iterator<Point2D> iterator() {
        return createIterator();
    }

    protected static abstract class AbstractGridLayoutIterator implements Iterator<Point2D> {

        protected abstract double getPadding();

        protected abstract double getIconSize();

        protected abstract Direction getTowards();

        protected abstract int[] getNextIndex();

        @Override
        public Point2D next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            final int[] next = getNextIndex();
            final int row = next[0];
            final int column = next[1];

            final Point2D location = getLocation(row,
                                                 column);
            final boolean isE = isEast(getTowards());
            final boolean isN = isNorth(getTowards());
            final int ox = isE ? 1 : -1;
            final double rx = isE ? 0 : calculateDistance(1);
            final int oy = isN ? -1 : 1;
            final double ry = isN ? calculateDistance(1) : 0;

            return new Point2D((location.getX() + rx) * ox,
                               (location.getY() + ry) * oy);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        protected Point2D getLocation(final int row,
                                      final int col) {
            double x = calculateDistance(col);
            double y = calculateDistance(row);
            return new Point2D(x,
                               y);
        }

        protected double calculateDistance(final int position) {
            return position * (getPadding() + getIconSize());
        }
    }

    private static boolean isEast(final Direction towards) {
        switch (towards) {
            case WEST:
            case SOUTH_WEST:
            case NORTH_WEST:
                return false;
        }
        return true;
    }

    private static boolean isNorth(final Direction towards) {
        switch (towards) {
            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
                return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}

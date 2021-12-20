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

import com.ait.lienzo.shared.core.types.Direction;

public class FixedLayoutGrid extends AbstractLayoutGrid<FixedLayoutGrid> {

    private int rows;
    private int cols;
    private Direction towards;

    public FixedLayoutGrid(final double padding,
                           final double iconSize,
                           final Direction towards,
                           final int rows,
                           final int cols) {
        super(padding,
              iconSize);
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Not possible to instantiate grid.");
        }
        this.rows = rows;
        this.cols = cols;
        this.towards = towards;
    }

    FixedLayoutGrid(final double padding,
                    final double iconSize) {
        this(padding,
             iconSize,
             Direction.NORTH,
             1,
             1);
    }

    public FixedLayoutGrid rows(final int rows) {
        this.rows = rows;
        return this;
    }

    public FixedLayoutGrid columns(final int cols) {
        this.cols = cols;
        return this;
    }

    public FixedLayoutGrid towards(final Direction towards) {
        this.towards = towards;
        return this;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Direction getTowards() {
        return towards;
    }

    @Override
    protected AbstractGridLayoutIterator createIterator() {
        return new FixedLayoutGridIterator(getPadding(),
                                           getIconSize(),
                                           getRows(),
                                           getCols(),
                                           getTowards());
    }

    private static class FixedLayoutGridIterator extends AbstractGridLayoutIterator {

        private final double padding;
        private final double iconSize;
        private final int rows;
        private final int cols;
        private final Direction towards;
        private int currentRow;
        private int currentColumn;

        private FixedLayoutGridIterator(final double padding,
                                        final double iconSize,
                                        final int rows,
                                        final int cols,
                                        final Direction towards) {
            this.padding = padding;
            this.iconSize = iconSize;
            this.rows = rows;
            this.cols = cols;
            this.towards = towards;
            this.currentRow = -1;
            this.currentColumn = cols - 1;
        }

        @Override
        public boolean hasNext() {
            final int[] next = _nextIndex();
            return next[0] <= lastRow() && next[1] <= lastColumn();
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

        @Override
        protected int[] getNextIndex() {
            // Obtain next col/row indexes.
            final int[] next = _nextIndex();
            currentRow = next[0];
            currentColumn = next[1];
            if (!isInRange(currentRow,
                           rows)) {
                throw new IllegalStateException(
                        currentRow + " is an incorrect row value. Value have to be from 0 to " + (rows - 1)
                );
            }
            if (!isInRange(currentColumn,
                           cols)) {
                throw new IllegalStateException(
                        currentColumn + " is an incorrect column value. Value have to be from 0 to " + (cols - 1)
                );
            }
            // Check ranges are valid.
            return new int[]{currentRow, currentColumn};
        }

        private int[] _nextIndex() {
            int cr = currentRow;
            int cc = currentColumn;
            if (cc == (cols - 1)) {
                cc = 0;
                cr++;
            } else {
                cc++;
            }
            return new int[]{cr, cc};
        }

        private int lastColumn() {
            return cols - 1;
        }

        private int lastRow() {
            return rows - 1;
        }

        private boolean isInRange(final int value,
                                  final int max) {
            return value >= 0 && value < max;
        }
    }
}

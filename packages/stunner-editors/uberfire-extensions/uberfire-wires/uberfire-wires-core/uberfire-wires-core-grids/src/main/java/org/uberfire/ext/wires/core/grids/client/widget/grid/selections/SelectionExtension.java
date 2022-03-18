/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections;

import java.io.Serializable;

/**
 * Possible directions in which a Selection can be extended.
 */
public enum SelectionExtension {

    LEFT(
            (int min, int max, int origin) -> {
                if (max > origin) {
                    return max - 1;
                } else {
                    return min - 1;
                }
            },
            (int min, int max, int origin) -> {
                if (max > origin) {
                    return max;
                } else {
                    return min;
                }
            },
            -1,
            0),
    RIGHT(
            (int min, int max, int origin) -> {
                if (min < origin) {
                    return min + 1;
                } else {
                    return max + 1;
                }
            },
            (int min, int max, int origin) -> {
                if (max > origin) {
                    return max;
                } else {
                    return min;
                }
            },
            1,
            0),
    UP(
            (int min, int max, int origin) -> {
                if (max > origin) {
                    return max;
                } else {
                    return min;
                }
            },
            (int min, int max, int origin) -> {
                if (max > origin) {
                    return max - 1;
                } else {
                    return min - 1;
                }
            },
            0,
            -1),
    DOWN(
            (int min, int max, int origin) -> {
                if (max > origin) {
                    return max;
                } else {
                    return min;
                }
            },
            (int min, int max, int origin) -> {
                if (min < origin) {
                    return min + 1;
                } else {
                    return max + 1;
                }
            },
            0,
            1);

    private NextIndexCalculator nextXCalculator;
    private NextIndexCalculator nextYCalculator;
    private int deltaX;
    private int deltaY;

    SelectionExtension(final NextIndexCalculator nextXCalculator,
                       final NextIndexCalculator nextYCalculator,
                       final int deltaX,
                       final int deltaY) {
        this.nextXCalculator = nextXCalculator;
        this.nextYCalculator = nextYCalculator;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getNextX(final int minIndex,
                        final int maxIndex,
                        final int originIndex) {
        return nextXCalculator.getNext(minIndex,
                                       maxIndex,
                                       originIndex);
    }

    public int getNextY(final int minIndex,
                        final int maxIndex,
                        final int originIndex) {
        return nextYCalculator.getNext(minIndex,
                                       maxIndex,
                                       originIndex);
    }

    public int getDeltaX() {
        return this.deltaX;
    }

    public int getDeltaY() {
        return this.deltaY;
    }

    private interface NextIndexCalculator extends Serializable {

        int getNext(final int minIndex,
                    final int maxIndex,
                    final int originIndex);
    }

}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.model;

import java.util.Map;

/**
 * Defines a row within the grid
 */
public interface GridRow {

    /**
     * Returns the height of the row
     * @return
     */
    double getHeight();

    /**
     * Sets the height of the row
     * @param height
     */
    void setHeight(final double height);

    /**
     * Collapsed rows have zero height. This returns the height of the row before it was collapsed.
     * This is currently used primarily during the "expand/collapse row(s)" animations.
     * @return The height of the row before it was collapsed.
     */
    double peekHeight();

    /**
     * Returns the cells within the row. This is an sparse map of column index to value.
     * Empty cells do not have an entry within the map. Empty cells should be considered
     * as "null" values; rather than empty Strings however the strict interpretation is up
     * to the implementations.
     * @return
     */
    Map<Integer, GridCell<?>> getCells();

    /**
     * Returns whether the row contains merged cells
     * @return true if merged
     */
    boolean isMerged();

    /**
     * Returns whether the row contains collapsed cells
     * @return true if collapsed
     */
    boolean isCollapsed();

    /**
     * Collapses all cells on the row.
     */
    void collapse();

    /**
     * Expands all cells on the row.
     */
    void expand();

    /**
     * Resets all cells on the row to a non-merged, non-collapsed state.
     */
    void reset();
}

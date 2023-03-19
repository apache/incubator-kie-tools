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

/**
 * Holds the value of a cell.
 * @param <T> The Type of the value
 */
public interface GridCellValue<T> {

    /**
     * Returns the value of the cell. It should be noted there is intentionally no "setter" as
     * mutation of the value may require further mutation to other data within the grid. Therefore mutation
     * of cell values is via the GridData interface to ensure the integrity of all data within the grid.
     * @return
     */
    T getValue();

    /**
     * Returns the (optional) <b>placeholder</b> to use when no actual value has been set
     * @return
     */
    String getPlaceHolder();
}

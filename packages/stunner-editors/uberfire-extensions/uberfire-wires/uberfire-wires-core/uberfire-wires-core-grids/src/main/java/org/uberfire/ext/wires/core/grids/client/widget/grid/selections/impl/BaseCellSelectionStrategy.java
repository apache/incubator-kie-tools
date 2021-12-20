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

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

public abstract class BaseCellSelectionStrategy implements CellSelectionStrategy {

    protected boolean hasSelectionChanged(final List<GridData.SelectedCell> currentSelections,
                                          final List<GridData.SelectedCell> originalSelections) {
        final List<GridData.SelectedCell> cloneCurrentSelections = new ArrayList<GridData.SelectedCell>(currentSelections);
        final List<GridData.SelectedCell> cloneOriginalSelections = new ArrayList<GridData.SelectedCell>(originalSelections);
        cloneCurrentSelections.removeAll(originalSelections);
        cloneOriginalSelections.removeAll(currentSelections);
        return cloneCurrentSelections.size() > 0 || cloneOriginalSelections.size() > 0;
    }
}

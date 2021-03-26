/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

public class TableSortComparator {

    private Optional<Integer> lastSorted = Optional.empty();

    /**
     *
     * @param rows All the rows to sort.
     * @param gridColumn Column that we sort by.
     * @return New order of the rows. Each number in the list shows what row should move to that location.
     * @throws ModelSynchronizer.VetoException
     */
    public List<Integer> sort(final List<GridRow> rows,
                              final GridColumn gridColumn) throws ModelSynchronizer.VetoException {

        if (lastSorted.isPresent() && lastSorted.get() == gridColumn.hashCode()) {
            Collections.reverse(rows);
            lastSorted = Optional.empty();
        } else {
            try {
                Collections.sort(rows,
                                 new GridRowComparator(gridColumn));
                lastSorted = Optional.of(gridColumn.hashCode());
            } catch (IllegalArgumentException e) {
                throw new ModelSynchronizer.VetoException();
            }
        }

        return listRowNumbersByNewOrder(rows);
    }

    private ArrayList<Integer> listRowNumbersByNewOrder(final List<GridRow> rows) {
        final ArrayList<Integer> rowNumbers = new ArrayList<>();
        for (final GridRow row : rows) {
            rowNumbers.add((Integer) row.getCells().get(0).getValue().getValue() - 1);
        }
        return rowNumbers;
    }
}

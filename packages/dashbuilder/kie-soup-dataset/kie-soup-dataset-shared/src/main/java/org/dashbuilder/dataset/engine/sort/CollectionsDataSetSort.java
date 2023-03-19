/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.engine.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.sort.ColumnSort;

/**
 * A basic sort algorithm takes relies on the default <tt>Collections.sort()</tt> implementation.
 */
public class CollectionsDataSetSort implements DataSetSortAlgorithm {


    public List<Integer> sort(DataSet dataSet, List<ColumnSort> columnSortList) {
        return sort(dataSet, null, columnSortList);
    }

    public List<Integer> sort(DataSet dataSet, List<Integer> rowNumbers, List<ColumnSort> columnSortList) {

        // Create the comparator.
        DataSetRowComparator comparator = new DataSetRowComparator();
        for (ColumnSort columnSort : columnSortList) {
            DataColumn column = dataSet.getColumnById(columnSort.getColumnId());
            if (column == null) throw new IllegalArgumentException("Sort column not found: " + columnSort.getColumnId());

            comparator.criteria(column, columnSort.getOrder());
        }
        // Create the row number list to sort.
        List<Integer> rows = new ArrayList<Integer>();
        if (rowNumbers != null) {
            rows.addAll(rowNumbers);
        } else {
            for (int i=0; i<dataSet.getRowCount(); i++) {
                rows.add(i);
            }
        }
        // Sort the row numbers.
        Collections.sort(rows, comparator);
        return rows;
    }
}

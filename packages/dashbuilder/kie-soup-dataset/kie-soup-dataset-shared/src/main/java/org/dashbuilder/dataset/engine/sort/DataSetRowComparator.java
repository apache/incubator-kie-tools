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
import java.util.Comparator;
import java.util.List;

import org.dashbuilder.comparator.ComparatorUtils;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.sort.SortOrder;

/**
 * A comparator of data set rows.
 */
public class DataSetRowComparator implements Comparator<Integer> {

    protected List<DataColumn> columns = new ArrayList<DataColumn>();
    protected List<SortOrder> orders = new ArrayList<SortOrder>();

    public DataSetRowComparator criteria(DataColumn column, SortOrder order) {
        columns.add(column);
        orders.add(order);
        return this;
    }

    public int compare(Integer row1, Integer row2) {
        // Check criteria.
        if (columns.isEmpty()) return 0;

        // Objects must be not null arrays.
        if (row1 == null && row2 != null) return -1;
        else if (row1 != null && row2 == null) return 1;
        else if (row1 == null) return 0;

        // Compare the two rows.
        for (int i=0; i<columns.size(); i++) {
            DataColumn column = columns.get(i);
            SortOrder order = orders.get(i);
            Comparable value1 = (Comparable) column.getValues().get(row1);
            Comparable value2 = (Comparable) column.getValues().get(row2);
            int comp = ComparatorUtils.compare(value1, value2, order.asInt());
            if (comp != 0) return comp;
        }
        return 0;
    }
}

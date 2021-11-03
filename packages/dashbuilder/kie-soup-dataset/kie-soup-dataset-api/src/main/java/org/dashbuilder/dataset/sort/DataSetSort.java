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
package org.dashbuilder.dataset.sort;

import java.util.List;
import java.util.ArrayList;

import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.impl.AbstractDataSetOp;

/**
 * A data set sort operation definition
 */
public class DataSetSort extends AbstractDataSetOp {

    protected List<ColumnSort> columnSortList = new ArrayList<ColumnSort>();

    public DataSetOpType getType() {
        return DataSetOpType.SORT;
    }

    public void addSortColumn(ColumnSort... columnSorts) {
        for (ColumnSort columnSort : columnSorts) {
            columnSortList.add(columnSort);
        }
    }

    public List<ColumnSort> getColumnSortList() {
        return columnSortList;
    }

    /**
     * Invert the sort order if this sort operation.
     */
    public DataSetSort invertOrder() {
        for (ColumnSort columnSort : getColumnSortList()) {
            SortOrder order = columnSort.getOrder();
            if (SortOrder.ASCENDING.equals(order)) columnSort.setOrder(SortOrder.DESCENDING);
            else if (SortOrder.DESCENDING.equals(order)) columnSort.setOrder(SortOrder.ASCENDING);
        }
        return this;
    }

    /**
     * Clone this sort operation.
     */
    public DataSetSort cloneInstance() {
        DataSetSort clone = new DataSetSort();
        for (ColumnSort columnSort : columnSortList) {
            clone.addSortColumn(columnSort.cloneInstance());
        }
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            DataSetSort other = (DataSetSort) obj;
            if (columnSortList.size() != other.columnSortList.size()) return false;
            for (int i = 0; i < columnSortList.size(); i++) {
                ColumnSort el = columnSortList.get(i);
                ColumnSort otherEl = other.columnSortList.get(i);
                if (!el.equals(otherEl)) return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}

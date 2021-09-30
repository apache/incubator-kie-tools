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
package org.dashbuilder.dataset.filter;

import java.util.List;
import java.util.ArrayList;

import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.impl.AbstractDataSetOp;

/**
 * A data set filter definition.
 */
public class DataSetFilter extends AbstractDataSetOp {

    protected List<ColumnFilter> columnFilterList = new ArrayList<ColumnFilter>();

    public DataSetOpType getType() {
        return DataSetOpType.FILTER;
    }

    public void addFilterColumn(ColumnFilter... columnFilters) {
        for (ColumnFilter columnFilter : columnFilters) {
            columnFilterList.add(columnFilter);
        }
    }

    public List<ColumnFilter> getColumnFilterList() {
        return columnFilterList;
    }

    public Integer getColumnFilterIdx(ColumnFilter columnFilter) {
        for (int i=0; i<columnFilterList.size(); i++) {
            ColumnFilter cf = columnFilterList.get(i);
            if (columnFilter.equals(cf)) {
                return i;
            }
        }
        return null;
    }

    public DataSetFilter cloneInstance() {
        DataSetFilter clone = new DataSetFilter();
        for (ColumnFilter columnFilter : columnFilterList) {
            clone.columnFilterList.add(columnFilter.cloneInstance());
        }
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            DataSetFilter other = (DataSetFilter) obj;
            if (columnFilterList.size() != other.columnFilterList.size()) return false;
            for (int i = 0; i < columnFilterList.size(); i++) {
                ColumnFilter el = columnFilterList.get(i);
                ColumnFilter otherEl = other.columnFilterList.get(i);
                if (!el.equals(otherEl)) return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (ColumnFilter columnFilter : columnFilterList) {
            out.append("\"").append(columnFilter.toString()).append("\" ");
        }
        return out.toString();
    }
}

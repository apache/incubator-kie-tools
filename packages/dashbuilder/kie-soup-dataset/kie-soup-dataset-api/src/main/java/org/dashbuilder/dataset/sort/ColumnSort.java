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

/**
 * A column sort criteria
 */
public class ColumnSort {

    protected String columnId = null;
    protected SortOrder order = SortOrder.UNSPECIFIED;

    public ColumnSort() {
    }

    public ColumnSort(String columnId, SortOrder order) {
        this.columnId = columnId;
        this.order = order;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public ColumnSort cloneInstance() {
        ColumnSort clone = new ColumnSort();
        clone.columnId = columnId;
        clone.order = order;
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            ColumnSort other = (ColumnSort) obj;
            if (columnId != null && !columnId.equals(other.columnId)) return false;
            if (order != null && !order.equals(other.order)) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}

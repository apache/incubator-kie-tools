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
package org.dashbuilder.dataset.engine.filter;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.filter.ColumnFilter;

/**
 * A data set function
 */
public abstract class DataSetFunction {

    protected DataSetFilterContext context;
    protected String columnId;
    protected DataColumn dataColumn;

    public DataSetFunction() {
    }

    public DataSetFunction(DataSetFilterContext ctx, ColumnFilter filter) {
        this.context = ctx;
        this.columnId = filter.getColumnId();
    }

    public DataSetFilterContext getContext() {
        return context;
    }

    public void setContext(DataSetFilterContext context) {
        this.context = context;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
        this.dataColumn = null;
        if (columnId != null) {
            this.dataColumn = context.getDataSet().getColumnById(columnId);
        }
    }

    public DataColumn getDataColumn() {
        if (dataColumn != null) return dataColumn;

        if (columnId == null) return null;
        return dataColumn = context.getDataSet().getColumnById(columnId);
    }

    /**
     * Evaluate the filter function.
     */
    public abstract boolean pass();
}

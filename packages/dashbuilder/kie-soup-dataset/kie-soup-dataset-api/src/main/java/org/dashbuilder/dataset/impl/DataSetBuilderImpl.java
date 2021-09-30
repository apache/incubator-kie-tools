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
package org.dashbuilder.dataset.impl;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetBuilder;
import org.dashbuilder.dataset.DataSetFactory;

public class DataSetBuilderImpl implements DataSetBuilder {

    protected DataSet dataSet = DataSetFactory.newEmptyDataSet();

    public DataSetBuilderImpl uuid(String uuid) {
        dataSet.setUUID(uuid);
        return this;
    }

    public DataSetBuilderImpl label(String columnId) {
        dataSet.addColumn(columnId, ColumnType.LABEL);
        return this;
    }

    public DataSetBuilderImpl text(String columnId) {
        dataSet.addColumn(columnId, ColumnType.TEXT);
        return this;
    }

    public DataSetBuilderImpl number(String columnId) {
        dataSet.addColumn(columnId, ColumnType.NUMBER);
        return this;
    }

    public DataSetBuilderImpl date(String columnId) {
        dataSet.addColumn(columnId, ColumnType.DATE);
        return this;
    }

    public DataSetBuilderImpl column(String columnId, ColumnType type) {
        dataSet.addColumn(columnId, type);
        return this;
    }

    public DataSetBuilderImpl row(Object... values) {
        dataSet.addValues(values);
        return this;
    }

    public DataSet buildDataSet() {
        return dataSet;
    }
}

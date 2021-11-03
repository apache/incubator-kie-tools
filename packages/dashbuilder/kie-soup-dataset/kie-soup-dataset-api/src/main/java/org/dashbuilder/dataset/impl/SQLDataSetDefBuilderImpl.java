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

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.def.SQLDataSetDefBuilder;

public class SQLDataSetDefBuilderImpl extends AbstractDataSetDefBuilder<SQLDataSetDefBuilderImpl> implements SQLDataSetDefBuilder<SQLDataSetDefBuilderImpl> {

    protected DataSetDef createDataSetDef() {
        return new SQLDataSetDef();
    }

    public SQLDataSetDefBuilderImpl dataSource(String dataSource) {
        ((SQLDataSetDef) def).setDataSource(dataSource);
        return this;
    }

    public SQLDataSetDefBuilderImpl dbSchema(String dbSchema) {
        ((SQLDataSetDef) def).setDbSchema(dbSchema);
        return this;
    }

    public SQLDataSetDefBuilderImpl dbTable(String dbTable, boolean allColumns) {
        ((SQLDataSetDef) def).setDbTable(dbTable);
        ((SQLDataSetDef) def).setAllColumnsEnabled(allColumns);
        return this;
    }

    public SQLDataSetDefBuilderImpl dbSQL(String dbSQL, boolean allColumns) {
        ((SQLDataSetDef) def).setDbSQL(dbSQL);
        ((SQLDataSetDef) def).setAllColumnsEnabled(allColumns);
        return this;
    }

    public SQLDataSetDefBuilderImpl estimateSize(boolean estimateSize) {
        ((SQLDataSetDef) def).setEstimateSize(estimateSize);
        return this;
    }
}

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
package org.dashbuilder.dataset.json;

import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.*;

public class SQLDefJSONMarshaller implements DataSetDefJSONMarshallerExt<SQLDataSetDef> {

    public static SQLDefJSONMarshaller INSTANCE = new SQLDefJSONMarshaller();

    public static final String DATA_SOURCE = "dataSource";
    public static final String DB_SCHEMA = "dbSchema";
    public static final String DB_TABLE = "dbTable";
    public static final String DB_SQL = "dbSQL";

    @Override
    public void fromJson(SQLDataSetDef def, JsonObject json) {
        String dataSource = json.getString(DATA_SOURCE);
        String dbTable = json.getString(DB_TABLE);
        String dbSchema = json.getString(DB_SCHEMA);
        String dbSQL = json.getString(DB_SQL);

        if (!isBlank(dataSource)) {
            def.setDataSource(dataSource);
        }
        if (!isBlank(dbSchema)) {
            def.setDbSchema(dbSchema);
        }
        if (!isBlank(dbTable)) {
            def.setDbTable(dbTable);
        }
        if (!isBlank(dbSQL)) {
            def.setDbSQL(dbSQL);
        }
    }

    @Override
    public void toJson(SQLDataSetDef dataSetDef, JsonObject json) {
        // Data source.
        json.put(DATA_SOURCE, dataSetDef.getDataSource());

        // Schema.
        json.put(DB_SCHEMA, dataSetDef.getDbSchema());

        // Table.
        if (dataSetDef.getDbTable() != null) {
            json.put(DB_TABLE, dataSetDef.getDbTable());
        }

        // Query.
        if (dataSetDef.getDbSQL() != null) {
            json.put(DB_SQL, dataSetDef.getDbSQL());
        }

        // All columns flag.
        json.put(ALL_COLUMNS, dataSetDef.isAllColumnsEnabled());
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver;

import org.dashbuilder.dataset.json.DataSetDefJSONMarshallerExt;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.ALL_COLUMNS;
import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.isBlank;

public class RemoteDefJSONMarshaller implements DataSetDefJSONMarshallerExt<RemoteDataSetDef> {

    public static RemoteDefJSONMarshaller INSTANCE = new RemoteDefJSONMarshaller();

    public static final String QUERY_TARGET = "queryTarget";
    public static final String SERVER_TEMPLATE_ID = "serverTemplateId";
    public static final String DATA_SOURCE = "dataSource";
    public static final String DB_SCHEMA = "dbSchema";
    public static final String DB_SQL = "dbSQL";


    @Override
    public void fromJson(RemoteDataSetDef def, JsonObject json) {
        String queryTarget = json.getString(QUERY_TARGET);
        String serverTemplateId = json.getString(SERVER_TEMPLATE_ID);
        String dataSource = json.getString(DATA_SOURCE);
        String dbSchema = json.getString(DB_SCHEMA);
        String dbSQL = json.getString(DB_SQL);

        if (!isBlank(queryTarget)) {
            def.setQueryTarget(queryTarget);
        }
        if (!isBlank(serverTemplateId)) {
            def.setServerTemplateId(serverTemplateId);
        }
        if (!isBlank(dataSource)) {
            def.setDataSource(dataSource);
        }
        if (!isBlank(dbSchema)) {
            def.setDbSchema(dbSchema);
        }
        if (!isBlank(dbSQL)) {
            def.setDbSQL(dbSQL);
        }
    }

    @Override
    public void toJson(RemoteDataSetDef dataSetDef, JsonObject json) {
        // Data source.
        json.put(DATA_SOURCE, dataSetDef.getDataSource());

        // Schema.
        json.put(DB_SCHEMA, dataSetDef.getDbSchema());

        // Query.
        if (dataSetDef.getDbSQL() != null) {
            json.put(DB_SQL, dataSetDef.getDbSQL());
        }

        json.put(QUERY_TARGET, dataSetDef.getQueryTarget());
        
        json.put(SERVER_TEMPLATE_ID, dataSetDef.getServerTemplateId());
        
        // All columns flag.
        json.put(ALL_COLUMNS, dataSetDef.isAllColumnsEnabled());
    }
}
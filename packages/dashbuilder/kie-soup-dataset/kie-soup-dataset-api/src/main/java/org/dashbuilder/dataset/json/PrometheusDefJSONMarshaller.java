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
package org.dashbuilder.dataset.json;

import org.dashbuilder.dataset.def.PrometheusDataSetDef;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.ALL_COLUMNS;
import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.isBlank;

public class PrometheusDefJSONMarshaller implements DataSetDefJSONMarshallerExt<PrometheusDataSetDef> {

    public static final PrometheusDefJSONMarshaller INSTANCE = new PrometheusDefJSONMarshaller();

    public static final String SERVER_URL = "serverUrl";
    public static final String QUERY = "query";
    public static final String USER = "user";
    public static final String PASSWORD = "password";

    @Override
    public void fromJson(PrometheusDataSetDef def, JsonObject json) {
        String serverUrl = json.getString(SERVER_URL);
        String query = json.getString(QUERY);
        String user = json.getString(USER);
        String password = json.getString(PASSWORD);

        if (!isBlank(serverUrl)) {
            def.setServerUrl(serverUrl);
        }
        if (!isBlank(query)) {
            def.setQuery(query);
        }
        if (!isBlank(user)) {
            def.setUser(user);
        }
        if (!isBlank(password)) {
            def.setPassword(password);
        }
    }

    @Override
    public void toJson(PrometheusDataSetDef dataSetDef, JsonObject json) {
        json.put(SERVER_URL, dataSetDef.getServerUrl());
        json.put(QUERY, dataSetDef.getQuery());
        json.put(USER, dataSetDef.getUser());
        json.put(PASSWORD, dataSetDef.getPassword());
        json.put(ALL_COLUMNS, dataSetDef.isAllColumnsEnabled());
    }
}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.sql;

import javax.sql.DataSource;

import nl.cwi.monetdb.jdbc.MonetDataSource;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.def.SQLDataSetDef;

public class MonetDBTestSettings extends DatabaseTestSettings {

    @Override
    public String getDatabaseType() {
        return MONETDB;
    }

    @Override
    public SQLDataSourceLocator getDataSourceLocator() {
        return new SQLDataSourceLocator() {
            public DataSource lookup(SQLDataSetDef def) throws Exception {
                String url = connectionSettings.getProperty("url");
                String user = connectionSettings.getProperty("user");
                String password = connectionSettings.getProperty("password");

                MonetDataSource ds = new MonetDataSource();
                ds.setDatabaseName(url);
                if (!StringUtils.isBlank(user)) {
                    ds.setUser(user);
                }
                if (!StringUtils.isBlank(password)) {
                    ds.setPassword(password);
                }
                return ds;
            }
        };
    }
}

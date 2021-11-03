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
package org.dashbuilder.dataprovider.sql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataprovider.sql.SQLDataSourceLocator;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.h2.jdbcx.JdbcDataSource;

public class DatabaseTestSettings {

    public static final String H2 = "h2";
    public static final String H2MEM = "h2mem";
    public static final String POSTGRES = "postgres";
    public static final String MYSQL = "mysql";
    public static final String MARIADB = "mariadb";
    public static final String ORACLE = "oracle";
    public static final String DB2 = "db2";
    public static final String SQLSERVER = "sqlserver";
    public static final String SYBASE = "sybase";
    public static final String MONETDB = "monetdb";

    protected Properties connectionSettings;
    
    public DatabaseTestSettings() {
        this(false);
    }

    public DatabaseTestSettings(boolean externalDatasourceConfig) {
        
        if (!externalDatasourceConfig) {
            String type = getDatabaseType();
            connectionSettings = new Properties();
            String propsFile = "testdb-" + type + ".properties";
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsFile);
            if (is != null) {
                try {
                    connectionSettings.load(is);
                } catch (IOException e) {
                    throw new RuntimeException("Database settings file load error: " + propsFile, e);
                }
            } else {
                throw new IllegalArgumentException("Database settings file not found in classpath: " + propsFile);
            }
        }
    }

    public Properties getConnectionSettings() {
        return connectionSettings;
    }

    public String getExpenseReportsTableDsetFile() {
        return "expenseReports.dset";
    }

    public String getExpenseReportsQueryDsetFile() {
        return "expenseReports_query.dset";
    }

    public String getExpenseReportsSqlDsetFile() {
        return "expenseReports_sql.dset";
    }

    public boolean isH2() {
        return H2.equals(getDatabaseType()) || H2MEM.equals(getDatabaseType());
    }

    public boolean isMySQL() {
        return MYSQL.equals(getDatabaseType());
    }

    public boolean isMariaDB() {
        return MARIADB.equals(getDatabaseType());
    }

    public boolean isPostgres() {
        return POSTGRES.equals(getDatabaseType());
    }

    public boolean isOracle() {
        return ORACLE.equals(getDatabaseType());
    }

    public boolean isSqlServer() {
        return SQLSERVER.equals(getDatabaseType());
    }

    public boolean isDb2() {
        return DB2.equals(getDatabaseType());
    }

    public boolean isSybase() {
        return SYBASE.equals(getDatabaseType());
    }

    public boolean isMonetDB() {
        return MONETDB.equals(getDatabaseType());
    }

    public String getDatabaseType() {
        return H2MEM;
    }

    public SQLDataSourceLocator getDataSourceLocator() {
        return new SQLDataSourceLocator() {
            public DataSource lookup(SQLDataSetDef def) throws Exception {
                String url = connectionSettings.getProperty("url");
                String user = connectionSettings.getProperty("user");
                String password = connectionSettings.getProperty("password");

                JdbcDataSource ds = new JdbcDataSource();
                ds.setURL(url);
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

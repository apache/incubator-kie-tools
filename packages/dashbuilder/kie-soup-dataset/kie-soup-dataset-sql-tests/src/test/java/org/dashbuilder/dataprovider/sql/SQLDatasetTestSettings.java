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
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.dashbuilder.dataprovider.sql.util.DataSourceFactory;
import org.dashbuilder.dataprovider.sql.util.DatabaseProvider;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDatasetTestSettings extends DatabaseTestSettings {
    
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    
    private static final Logger log = LoggerFactory.getLogger(SQLDatasetTestSettings.class);
    
    private static Properties defaultProperties = null;
    
    private static Properties props = getDatasourceProperties();
    
    public SQLDatasetTestSettings() {
        super(true);
    }

    @Override
    public String getDatabaseType() {
        String driverClass = props.getProperty("driverClassName");
        return DatabaseProvider.fromDriverClassName(driverClass);
    }
    
    @Override
    public SQLDataSourceLocator getDataSourceLocator() {
        return new SQLDataSourceLocator() {
            
            @Override
            public DataSource lookup(SQLDataSetDef def) throws Exception {
                return DataSourceFactory.setupPoolingDataSource("test", props);
            }
        };
    }
    
    public static Properties getDatasourceProperties() {
        String propertiesNotFoundMessage = "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]";
        boolean propertiesNotFound = false;

        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");

        InputStream propsInputStream = SQLDatasetTestSettings.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        if (propsInputStream == null) {
            throw new IllegalStateException(propertiesNotFoundMessage);
        }
        Properties props = new Properties();
        try {
            props.load(propsInputStream);
        } catch (IOException ioe) {
            propertiesNotFound = true;
            log.error("Unable to find properties, using default H2 properties: " + ioe.getMessage());
            ioe.printStackTrace();
        }

        String password = props.getProperty("password");
        if ("${maven.jdbc.password}".equals(password) || propertiesNotFound) {
            props = getDefaultProperties();
        }

        return props;
    }
    
    private static Properties getDefaultProperties() {
        if (defaultProperties == null) {
            String[] keyArr = { "serverName", "portNumber", "databaseName", "url", "user", "password", "driverClassName",
                    "className", "maxPoolSize", "allowLocalTransactions" };
            String[] defaultPropArr = { "", "", "", "jdbc:h2:tcp://localhost/TestDS", "sa", "", "org.h2.Driver",
                    "org.h2.jdbcx.JdbcDataSource", "16", "true" };
            if (keyArr.length != defaultPropArr.length) {
                throw new IllegalStateException("Unequal number of keys for default properties!");
            }
            defaultProperties = new Properties();
            IntStream.range(0, keyArr.length)
                     .forEach(i -> defaultProperties.put(keyArr[i], defaultPropArr[i]));
        }

        return defaultProperties;
    }
    
}

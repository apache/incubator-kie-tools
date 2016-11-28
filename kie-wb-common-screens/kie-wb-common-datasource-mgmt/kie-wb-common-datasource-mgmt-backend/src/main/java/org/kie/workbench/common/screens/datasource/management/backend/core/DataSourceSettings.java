/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.core;

import java.io.InputStream;
import java.util.Properties;

import org.kie.workbench.common.screens.datasource.management.backend.core.impl.DataSourceProviderFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceSettings {

    public static final String DATASOURCE_MANAGEMENT_PREFIX = "datasource.management";

    private static final Logger logger = LoggerFactory.getLogger( DataSourceSettings.class );

    private static final String DATASOURCE_MANAGEMENT_PROPERTIES = "datasource-management.properties";

    private static DataSourceSettings instance;

    private Properties properties;

    private DataSourceSettings( Properties properties ) {
        this.properties = properties;
    }

    public static DataSourceSettings getInstance() {
        if ( instance == null ) {
            instance = new DataSourceSettings( loadSettings() );
        }
        return instance;
    }

    public String getProperty( String value ) {
        return properties.getProperty( value );
    }

    public Properties getProperties() {
        return properties;
    }

    private static Properties loadSettings() {
        InputStream inputStream =
                DataSourceProviderFactoryImpl.class.getResourceAsStream( "/datasource-management.properties" );

        Properties properties = new Properties( );
        if ( inputStream == null ) {
            logger.warn( "Data source management configuration file: " + DATASOURCE_MANAGEMENT_PROPERTIES +
                    " was not found. Some features may be disabled in current installation.");
            return properties;
        }

        try {
            properties.load( inputStream );
        } catch ( Exception e ) {
            logger.error( "An error was produced during data source configuration file reading: " +
                    DATASOURCE_MANAGEMENT_PROPERTIES, e );
        } finally {
            try {
                inputStream.close( );
            } catch ( Exception e ) {
                logger.warn( "An error was produced during data source configuration file closing: " +
                        DATASOURCE_MANAGEMENT_PROPERTIES, e);
            }
        }
        return properties;
    }
}
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

package org.kie.workbench.common.screens.datasource.management.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for opening jdbc connections by using a jdbc driver form a url.
 */
public class URLConnectionFactory {

    private String driverClass;

    private URL driverURL;

    private String connectionURL;

    private Properties properties;

    private ClassLoader parent;

    private Driver driver;

    public URLConnectionFactory( URL driverURL,
            String driverClass,
            ClassLoader parent,
            String connectionURL,
            Properties properties ) throws Exception {
        this.driverURL = driverURL;
        this.driverClass = driverClass;
        this.parent = parent;
        this.connectionURL = connectionURL;
        this.properties = properties;
        init();
    }

    public URLConnectionFactory( URL driverURL,
            String driverClass,
            String connectionURL,
            Properties properties ) throws Exception {
        this( driverURL, driverClass, null, connectionURL, properties );
        init();
    }

    private void init() throws Exception {

        ClassLoader classLoader;
        final URL[] urls = {driverURL};

        if ( parent != null ) {
            classLoader = new URLClassLoader( urls, parent );
        } else {
            classLoader = new URLClassLoader( urls );
        }

        try {
            final Class clazz = classLoader.loadClass( driverClass );
            if ( !Driver.class.isAssignableFrom( clazz ) ) {
                throw new Exception( "Class: " + driverClass + " must extend: " + Driver.class.getName() );
            } else {
                driver = (Driver) clazz.newInstance();
            }
        } catch ( ClassNotFoundException e ) {
            throw new Exception( "Driver class: " + driverClass + " was not found.", e );
        }
    }

    public Connection createConnection() throws SQLException {
        return driver.connect( connectionURL, properties );
    }
}
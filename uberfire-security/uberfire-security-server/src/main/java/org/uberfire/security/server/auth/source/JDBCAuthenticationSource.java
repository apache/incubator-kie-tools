/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.security.server.auth.source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.auth.AuthenticationSource;

public class JDBCAuthenticationSource extends AbstractDatabaseAuthSource implements AuthenticationSource {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCAuthenticationSource.class);

    private String dbDriver;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;

    public void initialize(final Map<String, ?> options) {
        try {
            dbDriver = (String) options.get("dbDriver");
            dbUrl = (String) options.get("dbUrl");
            dbUserName = (String) options.get("dbUserName");
            dbPassword = (String) options.get("dbPassword");

            if (dbUserName == null) {
                dbUserName = "";
            }

            if (dbPassword == null) {
                dbPassword = "";
            }

            if (dbDriver != null) {
                Class.forName(dbDriver);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.toString(), e);
        }
    }

    public Connection getConnection() {
        if (!((dbDriver != null) && (dbUrl != null))) {
            throw new IllegalStateException("Database connection information not configured");
        }

        LOG.debug("Connecting using dbDriver=" + dbDriver + "+ dbUserName=" + dbUserName + ", dbPassword=" + dbUrl);

        try {
            return DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
        } catch (final SQLException e) {
            throw new IllegalStateException(e.toString(), e);
        }
    }

}

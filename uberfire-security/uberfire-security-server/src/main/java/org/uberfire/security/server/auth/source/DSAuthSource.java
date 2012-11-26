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
import java.sql.SQLException;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.uberfire.security.auth.AuthenticationSource;

public class DSAuthSource extends AbstractDatabaseAuthSource implements AuthenticationSource {

    private DataSource dataSource;

    public void initialize(final Map<String, ?> options) {
        try {
            super.initialize(options);

            //get the datasource jndi name
            final String dbJNDIName = (String) options.get("dbJNDIName");

            final InitialContext ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("java:comp/env/" + dbJNDIName);
        } catch (NamingException e) {
            throw new IllegalStateException(e.toString(), e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (final SQLException e) {
            throw new IllegalStateException(e.toString(), e);
        }
    }
}

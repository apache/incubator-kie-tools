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

package org.kie.workbench.common.screens.datasource.management.backend.core.wildfly;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.kie.workbench.common.screens.datasource.management.backend.core.impl.AbstractDataSource;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceStatus;

public class WildlfyDataSource
        extends AbstractDataSource
        implements javax.sql.DataSource {

    private String externalJndi;

    public WildlfyDataSource( javax.sql.DataSource dataSource, String externalJndi ) {
        this.dataSource = dataSource;
        this.externalJndi = externalJndi;
    }

    public void setStatus( DataSourceStatus status ) {
        this.status = status;
        notifyStatusChange( status );
    }

    public String getExternalJndi() {
        return externalJndi;
    }

    @Override public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override public void setLogWriter( PrintWriter out ) throws SQLException {
        dataSource.setLogWriter( out );
    }

    @Override public void setLoginTimeout( int seconds ) throws SQLException {
        dataSource.setLoginTimeout( seconds );
    }

    @Override public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @Override public Connection getConnection( String username, String password ) throws SQLException {
        return dataSource.getConnection();
    }

    @Override public <T> T unwrap( Class<T> iface ) throws SQLException {
        return dataSource.unwrap( iface );
    }

    @Override public boolean isWrapperFor( Class<?> iface ) throws SQLException {
        return dataSource.isWrapperFor( iface );
    }
}
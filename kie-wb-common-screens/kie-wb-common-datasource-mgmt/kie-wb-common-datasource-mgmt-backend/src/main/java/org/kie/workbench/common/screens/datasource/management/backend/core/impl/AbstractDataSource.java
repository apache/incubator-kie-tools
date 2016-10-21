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

package org.kie.workbench.common.screens.datasource.management.backend.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceListener;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceStatus;

/**
 * Base class for implementing data sources
 */
public abstract class AbstractDataSource
        implements DataSource {

    protected javax.sql.DataSource dataSource;

    protected DataSourceStatus status = DataSourceStatus.NEW;

    protected List<DataSourceListener> listeners = new ArrayList<>( );

    @Override
    public DataSourceStatus getStatus() {
        return status;
    }

    @Override
    public boolean isReferenced() {
        return DataSourceStatus.REFERENCED.equals( status );
    }

    @Override
    public boolean isStale() {
        return DataSourceStatus.STALE.equals( status );
    }

    @Override
    public boolean isNew() {
        return DataSourceStatus.NEW.equals( status );
    }

    @Override
    public void addDataSourceListener( DataSourceListener listener ) {
        if ( !listeners.contains( listener ) ) {
            listeners.add( listener );
        }
    }

    @Override
    public void removeDataSourceListener( DataSourceListener listener ) {
        listeners.remove( listener );
    }

    protected void notifyStatusChange( DataSourceStatus newStatus ) {
        for ( DataSourceListener listener : listeners ) {
            listener.statusChanged( newStatus );
        }
    }

    public javax.sql.DataSource getInternalDataSource() {
        return dataSource;
    }
}

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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.schemas;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.util.InitializeCallback;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.SchemaMetadata;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;

@Dependent
public class DatabaseSchemaExplorer
        implements DatabaseSchemaExplorerView.Presenter, IsElement {

    private DatabaseSchemaExplorerView view;

    private Caller< DatabaseMetadataService > metadataService;

    private AsyncDataProvider< DatabaseSchemaRow > dataProvider;

    private TranslationService translationService;

    private List< DatabaseSchemaRow > rows = new ArrayList<>( );

    private Settings settings;

    private DatabaseSchemaExplorerView.Handler handler;

    public DatabaseSchemaExplorer( ) {
    }

    @Inject
    public DatabaseSchemaExplorer( DatabaseSchemaExplorerView view,
                                   Caller< DatabaseMetadataService > metadataService,
                                   TranslationService translationService ) {
        this.view = view;
        view.init( this );
        this.metadataService = metadataService;
        this.translationService = translationService;
    }

    @Override
    public HTMLElement getElement( ) {
        return view.getElement( );
    }

    @PostConstruct
    protected void init( ) {
        dataProvider = new AsyncDataProvider< DatabaseSchemaRow >( ) {
            @Override
            protected void onRangeChanged( HasData< DatabaseSchemaRow > display ) {
                updateRowCount( rows.size( ), true );
                updateRowData( 0, rows );
            }
        };
        view.setDataProvider( dataProvider );
    }

    public void initialize( Settings settings ) {
        initialize( settings, null );
    }

    public void initialize( Settings settings, InitializeCallback initializeCallback ) {
        this.settings = settings;
        loadSchemas( settings.dataSourceUuid( ), initializeCallback );
    }

    public void addHandler( DatabaseSchemaExplorerView.Handler handler ) {
        this.handler = handler;
    }

    @Override
    public void onOpen( DatabaseSchemaRow row ) {
        if ( handler != null ) {
            handler.onOpen( row.getName( ) );
        }
    }

    public boolean hasItems( ) {
        return !rows.isEmpty( );
    }

    /**
     * Intended for helping testing.
     */
    protected List<DatabaseSchemaRow> getItems() {
        return rows;
    }

    private void loadSchemas( String dataSourceUuid, InitializeCallback initializeCallback ) {
        clear( );
        view.showBusyIndicator( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseSchemaExplorerViewImpl_loadingDbSchemas ) );
        metadataService.call( new RemoteCallback< DatabaseMetadata >( ) {
            @Override
            public void callback( DatabaseMetadata metadata ) {
                view.hideBusyIndicator( );
                loadSchemas( metadata.getSchemas( ) );
                if ( initializeCallback != null ) {
                    initializeCallback.onInitializeSuccess( );
                }
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                boolean result = super.error( message, throwable );
                if ( initializeCallback != null ) {
                    initializeCallback.onInitializeError( throwable );
                }
                return result;
            }
        } ).getMetadata( dataSourceUuid, false, true );
    }

    private void loadSchemas( List< SchemaMetadata > schemas ) {
        for ( SchemaMetadata metadata : schemas ) {
            rows.add( new DatabaseSchemaRow( metadata.getSchemaName( ) ) );
        }
        refreshRows();
    }

    private void clear( ) {
        rows.clear( );
        refreshRows();
    }

    private void refreshRows( ) {
        dataProvider.updateRowCount( rows.size( ), true );
        dataProvider.updateRowData( 0, rows );
        view.redraw( );
    }

    public static class Settings {

        /**
         * Configures the data source that will be explored.
         */
        private String dataSourceUuid;

        public Settings( ) {
        }

        public String dataSourceUuid( ) {
            return dataSourceUuid;
        }

        public Settings dataSourceUuid( String selectedDataSourceUuid ) {
            this.dataSourceUuid = selectedDataSourceUuid;
            return this;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass( ) != o.getClass( ) ) return false;

            Settings settings = ( Settings ) o;

            return dataSourceUuid != null ? dataSourceUuid.equals( settings.dataSourceUuid ) : settings.dataSourceUuid == null;

        }

        @Override
        public int hashCode( ) {
            int result = dataSourceUuid != null ? dataSourceUuid.hashCode( ) : 0;
            result = ~~result;
            return result;
        }
    }
}
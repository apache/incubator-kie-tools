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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.dbobjects;

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
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.InitializeCallback;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.SchemaMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.TableMetadata;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;

@Dependent
public class DatabaseObjectExplorer
        implements DatabaseObjectExplorerView.Presenter, IsElement {

    private DatabaseObjectExplorerView view;

    private Caller< DatabaseMetadataService > metadataService;

    private AsyncDataProvider< DatabaseObjectRow > dataProvider;

    private TranslationService translationService;

    private List< DatabaseObjectRow > rows = new ArrayList<>( );

    private Settings settings;

    private DatabaseObjectExplorerView.Handler handler;

    protected static final DatabaseMetadata.TableType[] availableSearchTypes =
            new DatabaseMetadata.TableType[] { DatabaseMetadata.TableType.VIEW, DatabaseMetadata.TableType.TABLE };

    public DatabaseObjectExplorer( ) {
    }

    @Inject
    public DatabaseObjectExplorer( DatabaseObjectExplorerView view,
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
        setObjectOptions( );
        dataProvider = new AsyncDataProvider< DatabaseObjectRow >( ) {
            @Override
            protected void onRangeChanged( HasData< DatabaseObjectRow > display ) {
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
        view.showSchemaSelector( settings.isShowSchemaSelection( ) );
        view.showObjectTypeFilter( settings.isShowObjectTypeFilter( ) );
        view.showObjectNameFilter( settings.isShowObjectNameFilter( ) );
        boolean hasFilter = settings.isShowSchemaSelection( ) ||
                settings.isShowObjectTypeFilter( ) || settings.isShowObjectNameFilter( );
        view.showFilterButton( hasFilter );
        view.showHeaderPanel( hasFilter );
        if ( settings.isShowSchemaSelection( ) ) {
            loadSchemas( settings.dataSourceUuid( ), settings.schemaName( ), initializeCallback );
        } else {
            search( settings.dataSourceUuid( ),
                    settings.schemaName( ), DatabaseMetadata.TableType.ALL.name( ), "%", initializeCallback );
        }
    }

    public void addHandler( DatabaseObjectExplorerView.Handler handler ) {
        this.handler = handler;
    }

    @Override
    public void onSearch( ) {
        search( settings.dataSourceUuid( ), getSchema( ), view.getObjectType( ), view.getFilterTerm( ) );
    }

    @Override
    public void onOpen( DatabaseObjectRow row ) {
        handler.onOpen( getSchema( ), row.getName( ) );
    }

    /**
     * Intended for helping testing.
     */
    protected List< DatabaseObjectRow > getItems() {
        return rows;
    }

    private String getSchema( ) {
        if ( settings.isShowSchemaSelection( ) ) {
            return view.getSchema( );
        } else {
            return settings.schemaName( );
        }
    }

    private void clear( ) {
        rows.clear( );
        refreshRows( );
    }

    private void refreshRows( ) {
        dataProvider.updateRowCount( rows.size( ), true );
        dataProvider.updateRowData( 0, rows );
        view.redraw( );
    }

    private void loadSchemas( String dataSourceUuid, String selectedSchema, InitializeCallback initializeCallback ) {
        view.showBusyIndicator( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_loadingDbSchemas ) );
        metadataService.call( getLoadSchemasSuccessCallback( selectedSchema, initializeCallback ),
                new HasBusyIndicatorDefaultErrorCallback( view ) {
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

    private RemoteCallback< DatabaseMetadata > getLoadSchemasSuccessCallback( String selectedSchema,
                                                                              InitializeCallback initializeCallback ) {
        return new RemoteCallback< DatabaseMetadata >( ) {
            @Override
            public void callback( DatabaseMetadata metadata ) {
                view.hideBusyIndicator( );
                String currentSchema = selectedSchema;
                if ( currentSchema == null && !metadata.getSchemas( ).isEmpty( ) ) {
                    currentSchema = metadata.getSchemas( ).get( 0 ).getSchemaName( );
                }
                loadSchemas( metadata, currentSchema );
                search( settings.dataSourceUuid( ),
                        currentSchema, DatabaseMetadata.TableType.ALL.name( ), "%", initializeCallback );
            }
        };
    }

    private void loadSchemas( DatabaseMetadata metadata, String selectedSchema ) {
        String currentSchema = null;
        List< Pair< String, String > > options = new ArrayList<>( );
        for ( SchemaMetadata schemaMetadata : metadata.getSchemas( ) ) {
            if ( schemaMetadata.getSchemaName( ).equals( selectedSchema ) ) {
                currentSchema = selectedSchema;
            }
            options.add( new Pair<>( schemaMetadata.getSchemaName( ), schemaMetadata.getSchemaName( ) ) );
        }
        if ( currentSchema == null && !metadata.getSchemas( ).isEmpty( ) ) {
            currentSchema = metadata.getSchemas( ).get( 0 ).getSchemaName( );
        }
        view.loadSchemaOptions( options, currentSchema );
    }

    private void search( String dataSource,
                         String schema,
                         String databaseObjectType,
                         String searchTerm ) {
        search( dataSource, schema, databaseObjectType, searchTerm, null );
    }

    private void search( String dataSource,
                         String schema,
                         String databaseObjectType,
                         String searchTerm,
                         InitializeCallback initializeCallback ) {
        clear( );
        view.showBusyIndicator( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_loadingDbObjects ) );
        metadataService.call( getSearchSuccessCallback( initializeCallback ),
                new HasBusyIndicatorDefaultErrorCallback( view ) {
                    @Override
                    public boolean error( Message message, Throwable throwable ) {
                        boolean result = super.error( message, throwable );
                        if ( initializeCallback != null ) {
                            initializeCallback.onInitializeError( throwable );
                        }
                        return result;
                    }
                } ).findTables( dataSource,
                schema, buildSearchTerm( searchTerm ), buildSearchType( view.getObjectType() ) );
    }

    private RemoteCallback< List< TableMetadata > > getSearchSuccessCallback( InitializeCallback initializeCallback ) {
        return new RemoteCallback< List< TableMetadata > >( ) {
            @Override
            public void callback( List< TableMetadata > response ) {
                view.hideBusyIndicator( );
                loadTables( response );
                if ( initializeCallback != null ) {
                    initializeCallback.onInitializeSuccess( );
                }
            }
        };
    }

    private void loadTables( List< TableMetadata > response ) {
        rows.clear( );
        for ( TableMetadata metadata : response ) {
            rows.add( new DatabaseObjectRow( metadata.getTableName( ), metadata.getTableType( ) ) );
        }
        refreshRows( );
    }

    private void setObjectOptions( ) {
        List< Pair< String, String > > options = new ArrayList<>( );
        options.add( new Pair<>( DatabaseMetadata.TableType.ALL.name( ), DatabaseMetadata.TableType.ALL.name( ) ) );
        options.add( new Pair<>( DatabaseMetadata.TableType.TABLE.name( ), DatabaseMetadata.TableType.TABLE.name( ) ) );
        options.add( new Pair<>( DatabaseMetadata.TableType.VIEW.name( ), DatabaseMetadata.TableType.VIEW.name( ) ) );
        view.loadDatabaseObjectTypeOptions( options );
    }

    private String buildSearchTerm( String searchTerm ) {
        if ( searchTerm == null || searchTerm.trim( ).isEmpty( ) ) {
            return "%";
        } else {
            return "%" + searchTerm.trim( ) + "%";
        }
    }

    private DatabaseMetadata.TableType[] buildSearchType( String searchType ) {
        if ( searchType == null || DatabaseMetadata.TableType.ALL.name().equals( searchType ) ) {
            return availableSearchTypes;
        } else {
            return new DatabaseMetadata.TableType[] { DatabaseMetadata.TableType.valueOf( searchType ) };
        }
    }

    public static class Settings {

        /**
         * Configures the data source that will be explored.
         */
        private String dataSourceUuid;

        /**
         * When set it's the pre-configured database schema, otherwise the first available schema will be set.
         */
        private String schemaName;

        /**
         * Indicates if the schema selector should be visible.
         */
        private boolean showSchemaSelection;

        /**
         * When true the filtering of data objects by type is available.
         */
        private boolean showObjectTypeFilter;

        /**
         * When true the filtering of data objects by name is available.
         */
        private boolean showObjectNameFilter;

        public Settings( ) {
        }

        public String dataSourceUuid( ) {
            return dataSourceUuid;
        }

        public Settings dataSourceUuid( String selectedDataSourceUuid ) {
            this.dataSourceUuid = selectedDataSourceUuid;
            return this;
        }

        public String schemaName( ) {
            return schemaName;
        }

        public Settings schemaName( String schemaName ) {
            this.schemaName = schemaName;
            return this;
        }

        public boolean isShowSchemaSelection( ) {
            return showSchemaSelection;
        }

        public Settings showSchemaSelection( boolean showSchemaSelection ) {
            this.showSchemaSelection = showSchemaSelection;
            return this;
        }

        public boolean isShowObjectTypeFilter( ) {
            return showObjectTypeFilter;
        }

        public Settings showObjectTypeFilter( boolean showObjectTypeFilter ) {
            this.showObjectTypeFilter = showObjectTypeFilter;
            return this;
        }

        public boolean isShowObjectNameFilter( ) {
            return showObjectNameFilter;
        }

        public Settings showObjectNameFilter( boolean showObjectNameFilter ) {
            this.showObjectNameFilter = showObjectNameFilter;
            return this;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass( ) != o.getClass( ) ) return false;

            Settings settings = ( Settings ) o;

            if ( showSchemaSelection != settings.showSchemaSelection ) return false;
            if ( showObjectTypeFilter != settings.showObjectTypeFilter ) return false;
            if ( showObjectNameFilter != settings.showObjectNameFilter ) return false;
            if ( dataSourceUuid != null ? !dataSourceUuid.equals( settings.dataSourceUuid ) : settings.dataSourceUuid != null )
                return false;
            return schemaName != null ? schemaName.equals( settings.schemaName ) : settings.schemaName == null;

        }

        @Override
        public int hashCode( ) {
            int result = dataSourceUuid != null ? dataSourceUuid.hashCode( ) : 0;
            result = ~~result;
            result = 31 * result + ( schemaName != null ? schemaName.hashCode( ) : 0 );
            result = ~~result;
            result = 31 * result + ( showSchemaSelection ? 1 : 0 );
            result = ~~result;
            result = 31 * result + ( showObjectTypeFilter ? 1 : 0 );
            result = ~~result;
            result = 31 * result + ( showObjectNameFilter ? 1 : 0 );
            result = ~~result;
            return result;
        }
    }
}
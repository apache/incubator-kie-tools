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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer.tblviewer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.screens.datasource.management.service.DataManagementService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

@Dependent
public class TableObjectViewer
        implements TableObjectViewerView.Presenter, IsElement {

    private TableObjectViewerView view;

    private DisplayerViewer displayerViewer;

    private UUIDGenerator uuidGenerator;

    private Caller< DataManagementService > managementService;

    private Settings settings;

    public TableObjectViewer( ) {
    }

    @Inject
    public TableObjectViewer( TableObjectViewerView view,
                              DisplayerViewer displayerViewer,
                              UUIDGenerator uuidGenerator,
                              Caller< DataManagementService > managementService ) {
        this.view = view;
        view.init( this );
        this.displayerViewer = displayerViewer;
        this.uuidGenerator = uuidGenerator;
        this.managementService = managementService;
    }

    @Override
    public HTMLElement getElement( ) {
        return view.getElement( );
    }

    public void initialize( Settings settings ) {
        this.settings = settings;
        managementService.call( new RemoteCallback< DisplayerSettings >( ) {
            @Override
            public void callback( DisplayerSettings displayerSettings ) {
                initializeDisplayer( displayerSettings );
            }
        }).getDisplayerSettings( settings.dataSourceUuid( ),
                settings.schemaName( ), settings.tableName( ) );
    }

    private void initializeDisplayer( DisplayerSettings displayerSettings ) {
        displayerViewer.setIsShowRendererSelector( false );
        if ( displayerSettings.getUUID( ) == null ) {
            displayerSettings.setUUID( uuidGenerator.newUuid( ) );
        }
        displayerViewer.init( displayerSettings );
        view.setContent( displayerViewer );
        displayerViewer.draw( );
    }

    public static class Settings {

        /**
         * Configures the data source where the schema and table object belongs.
         */
        private String dataSourceUuid;

        /**
         * When set it's the schema where the table object is located. Can be null for some DBMSs.
         */
        private String schemaName;

        /**
         * The name of the table object to display.
         */
        private String tableName;

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

        public String tableName( ) {
            return tableName;
        }

        public Settings tableName( String tableName ) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass( ) != o.getClass( ) ) return false;

            Settings settings = ( Settings ) o;

            if ( dataSourceUuid != null ? !dataSourceUuid.equals( settings.dataSourceUuid ) : settings.dataSourceUuid != null )
                return false;
            if ( schemaName != null ? !schemaName.equals( settings.schemaName ) : settings.schemaName != null )
                return false;
            return tableName != null ? tableName.equals( settings.tableName ) : settings.tableName == null;

        }

        @Override
        public int hashCode( ) {
            int result = dataSourceUuid != null ? dataSourceUuid.hashCode( ) : 0;
            result = ~~result;
            result = 31 * result + ( schemaName != null ? schemaName.hashCode( ) : 0 );
            result = ~~result;
            result = 31 * result + ( tableName != null ? tableName.hashCode( ) : 0 );
            result = ~~result;
            return result;
        }
    }
}
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureTestConstants;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.SchemaMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.TableMetadata;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DatabaseObjectExplorerTest
        implements DatabaseStructureTestConstants {

    @Mock
    private DatabaseObjectExplorerView view;

    @Mock
    private DatabaseMetadataService  metadataService;

    @Mock
    private TranslationService translationService;

    private DatabaseObjectExplorer objectExplorer;

    @Mock
    private DatabaseMetadata metadata;

    @Mock
    private DatabaseObjectExplorerView.Handler handler;

    @Mock
    private Command command;

    private List<SchemaMetadata> schemas = new ArrayList<>(  );

    private List<TableMetadata> dbObjects = new ArrayList<>(  );

    @Before
    public void setup() {
        objectExplorer = new DatabaseObjectExplorer( view,
                new CallerMock<>( metadataService ), translationService );
        // emulate the @PostConstruct invocation.
        objectExplorer.init();
        schemas.add( new SchemaMetadata( SCHEMA_NAME ) );
        schemas.add( new SchemaMetadata( "schema2" ) );
        schemas.add( new SchemaMetadata( "schema3" ) );

        dbObjects.add( new TableMetadata( CATALOG_NAME, SCHEMA_NAME, "table1", DatabaseMetadata.TableType.TABLE.name( ) ) );
        dbObjects.add( new TableMetadata( CATALOG_NAME, SCHEMA_NAME, "table2", DatabaseMetadata.TableType.TABLE.name( ) ) );
    }

    /**
     * Tests the initialization when the schema selection is enabled.
     */
    @Test
    public void testInitializeWithSchemaSelectionEnabled() {

        DatabaseObjectExplorer.Settings settings = new DatabaseObjectExplorer.Settings()
                .dataSourceUuid( DATASOURCE_ID )
                .schemaName( SCHEMA_NAME )
                .showSchemaSelection( true );

        // schemas are loaded in this case and also the database objects.
        when( metadataService.getMetadata( settings.dataSourceUuid(), false, true ) ).thenReturn( metadata );
        when( metadataService.findTables(
                settings.dataSourceUuid(), SCHEMA_NAME, "%%%", objectExplorer.availableSearchTypes ) ).thenReturn( dbObjects );
        when( metadata.getSchemas() ).thenReturn( schemas );
        when( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_loadingDbSchemas ) ).thenReturn( LOADING_MESSAGE1 );
        when( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_loadingDbObjects ) ).thenReturn( LOADING_MESSAGE2 );


        objectExplorer.initialize( settings );

        verifyInitialize( settings );

        // the schemas should have been loaded in the selector.
        verify( view, times( 1 ) ).showBusyIndicator( LOADING_MESSAGE1 );
        verify( view, times( 1 ) ).loadSchemaOptions( buildExpectedSchemaOptions(), SCHEMA_NAME );

        // the database objects should have been loaded.
        verify( view, times( 1 ) ).showBusyIndicator( LOADING_MESSAGE2 );
        assertEquals( buildExpectedRows(), objectExplorer.getItems() );
    }

    /**
     * Tests the initialization when the schema selection is disabled.
     */
    @Test
    public void testInitializeWithSchemaSelectionDisabled() {

        DatabaseObjectExplorer.Settings settings = new DatabaseObjectExplorer.Settings()
                .dataSourceUuid( DATASOURCE_ID )
                .schemaName( SCHEMA_NAME )
                .showSchemaSelection( false );

        // database objects are loaded in this case
        when( metadataService.findTables(
                settings.dataSourceUuid(), SCHEMA_NAME, "%%%", objectExplorer.availableSearchTypes ) ).thenReturn( dbObjects );
        when( metadata.getSchemas() ).thenReturn( schemas );
        when( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_loadingDbObjects ) ).thenReturn( LOADING_MESSAGE1 );


        objectExplorer.initialize( settings );

        verifyInitialize( settings );

        // the database objects should have been loaded.
        verify( view, times( 1 ) ).showBusyIndicator( LOADING_MESSAGE1 );
        assertEquals( buildExpectedRows(), objectExplorer.getItems() );
    }

    /**
     * Tests the selection of a row when the schema selection is enabled.
     */
    @Test
    public void testOpenWithSchemaSelectionEnabled() {
        testInitializeWithSchemaSelectionEnabled();
        when ( view.getSchema() ).thenReturn( SCHEMA_NAME );
        testOpen();
    }

    /**
     * Tests the selection of a row when the schema selection is enabled.
     */
    @Test
    public void testOpenWithSchemaSelectionDisabled() {
        testInitializeWithSchemaSelectionDisabled();
        testOpen();
    }

    private void testOpen() {
        objectExplorer.addHandler( handler );
        //emulates that database object row has been selected in the UI.
        objectExplorer.onOpen( new DatabaseObjectRow( DATABASE_OBJECT_NAME, "type" ) );
        //the handler should have been properly invoked.
        verify( handler, times( 1 ) ).onOpen( SCHEMA_NAME, DATABASE_OBJECT_NAME );
    }

    /**
     * Tests the execution of the search action when the schema selection is enabled.
     */
    @Test
    public void testSearchWithSchemaSelectionEnabled() {
        testInitializeWithSchemaSelectionEnabled();
        when( view.getSchema() ).thenReturn( SCHEMA_NAME );
        testSearch();
    }

    /**
     * Tests the execution of the search action when the schema selection is disabled.
     */
    @Test
    public void testSearchWithSchemaSelectionDisabled() {
        testInitializeWithSchemaSelectionDisabled();
        testSearch();
    }

    private void testSearch() {
        when( view.getObjectType() ).thenReturn( "ALL" );
        when( view.getFilterTerm() ).thenReturn( "filterTerm" );

        when( metadataService.findTables(
                DATASOURCE_ID, SCHEMA_NAME, "%filterTerm%", objectExplorer.availableSearchTypes ) ).thenReturn( dbObjects );
        when( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseObjectExplorerViewImpl_loadingDbObjects ) ).thenReturn( LOADING_MESSAGE1 );

        // the seach action was executed from the UI.
        objectExplorer.onSearch();
        // the database objects should have been loaded.
        verify( view, times( 2 ) ).showBusyIndicator( LOADING_MESSAGE1 );
        assertEquals( buildExpectedRows(), objectExplorer.getItems() );
    }

    /**
     * Common verifications for the initialize method.
     */
    private void verifyInitialize( DatabaseObjectExplorer.Settings settings ) {
        // db object type selector should have been properly initialized.
        verify( view, times( 1 ) ).loadDatabaseObjectTypeOptions( buildExpectedObjectOptions() );
        verify( view, times( 1 ) ).showSchemaSelector( settings.isShowSchemaSelection() );
        verify( view, times( 1 ) ).showObjectTypeFilter( settings.isShowObjectTypeFilter() );
        verify( view, times( 1 ) ).showObjectNameFilter( settings.isShowObjectNameFilter() );
        if ( settings.isShowObjectTypeFilter() || settings.isShowObjectNameFilter() || settings.isShowSchemaSelection() ) {
            verify( view, times( 1 ) ).showFilterButton( true );
            verify( view, times( 1 ) ).showHeaderPanel( true );
        } else {
            verify( view, times( 1 ) ).showFilterButton( false );
            verify( view, times( 1 ) ).showHeaderPanel( false );
        }
    }

    private List< Pair< String, String > > buildExpectedObjectOptions( ) {
        List< Pair< String, String > > options = new ArrayList<>( );
        options.add( new Pair<>( DatabaseMetadata.TableType.ALL.name( ), DatabaseMetadata.TableType.ALL.name( ) ) );
        options.add( new Pair<>( DatabaseMetadata.TableType.TABLE.name( ), DatabaseMetadata.TableType.TABLE.name( ) ) );
        options.add( new Pair<>( DatabaseMetadata.TableType.VIEW.name( ), DatabaseMetadata.TableType.VIEW.name( ) ) );
        return options;
    }

    private List< Pair< String, String > > buildExpectedSchemaOptions( ) {
        List< Pair< String, String > > options = new ArrayList<>( );
        options.add( new Pair<>( SCHEMA_NAME, SCHEMA_NAME ) );
        options.add( new Pair<>( "schema2", "schema2" ) );
        options.add( new Pair<>( "schema3", "schema3" ) );
        return options;
    }

    private List<DatabaseObjectRow> buildExpectedRows() {
        List<DatabaseObjectRow> rows = new ArrayList<>(  );
        for ( TableMetadata metadata : dbObjects ) {
            rows.add( new DatabaseObjectRow( metadata.getTableName(), metadata.getTableType() ) );
        }
        return rows;
    }
}
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

package org.kie.workbench.common.screens.datasource.management.client.dbexplorer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.dbobjects.DatabaseObjectExplorer;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.schemas.DatabaseSchemaExplorer;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.tblviewer.TableObjectViewer;
import org.kie.workbench.common.screens.datasource.management.client.util.InitializeCallback;
import org.kie.workbench.common.screens.datasource.management.client.widgets.BreadcrumbItem;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DatabaseStructureExplorerTest
        implements DatabaseStructureTestConstants {

    @Mock
    private DatabaseStructureExplorerView view;

    @Mock
    private DatabaseSchemaExplorer schemaExplorer;

    @Mock
    private DatabaseObjectExplorer objectExplorer;

    @Mock
    private TableObjectViewer objectViewer;

    @Mock
    private ManagedInstance< BreadcrumbItem > itemManagedInstance;

    @Mock
    private TranslationService translationService;

    @Mock
    private DatabaseStructureExplorer structureExplorer;

    @Mock
    private BreadcrumbItem dataSourceBreadcrumbItem;

    @Mock
    private BreadcrumbItem schemasBreadcrumbItem;

    @Mock
    private BreadcrumbItem objectsBreadcrumbItem;

    @Mock
    private BreadcrumbItem objectViewerBreadcrumbItem;

    private DatabaseStructureExplorer.Settings settings;

    @Before
    public void setup() {
        settings = new DatabaseStructureExplorer.Settings( )
                .dataSourceUuid( DATASOURCE_ID )
                .dataSourceName( DATASOURCE_NAME );

        structureExplorer = new DatabaseStructureExplorer( view,
                schemaExplorer, objectExplorer, objectViewer, itemManagedInstance, translationService ) {

            @Override
            protected BreadcrumbItem createDataSourceBreadcrumbItem( ) {
                return dataSourceBreadcrumbItem;
            }

            @Override
            protected BreadcrumbItem createSchemasBreadcrumbItem( ) {
                return schemasBreadcrumbItem;
            }

            @Override
            protected BreadcrumbItem createObjectsBreadcrumbItem( ) {
                return objectsBreadcrumbItem;
            }

            @Override
            protected BreadcrumbItem createObjectViewerBreadcrumbItem( ) {
                return objectViewerBreadcrumbItem;
            }
        };
        // emulates the @PostConstruct invocation.
        structureExplorer.init();
    }

    /**
     * Tests the properly initialization in the case of a database with schemas.
     */
    @Test
    public void testInitializeWithSchemas() {
        testInitialize();

        // emulate the successful schemaExplorer initialization in the case where the database has schemas.
        when( schemaExplorer.hasItems() ).thenReturn( true );
        structureExplorer.showSchemas();

        // the breadcrumbs should have been cleared.
        verifyBreadcrumbsCleared();

        // the dataSourceBreadcrumbItem and schemasBreadcrumbItem should be now available.
        verifyBreadCrumbsAdded( dataSourceBreadcrumbItem, schemasBreadcrumbItem );
        // the schemaExplorer should now be visible and the schemasBreadcrumbItem should be the active breadcrumb.
        verifyVisibleContent( schemaExplorer );
        verifyIsActive( schemasBreadcrumbItem );
    }

    /**
     * Tests the properly initialization in the case of a database with no schemas.
     */
    @Test
    public void testInitializeWithNoSchemas() {
        testInitialize();

        // emulate the successful schemaExplorer initialization in the case where the database hasn't schemas.
        when( schemaExplorer.hasItems() ).thenReturn( false );
        structureExplorer.showSchemas();

        // the breadcrumbs should have been cleared.
        verifyBreadcrumbsCleared();

        // the dataSourceBreadcrumbItem and objectsBreadcrumbItem should be now available.
        verifyBreadCrumbsAdded( dataSourceBreadcrumbItem, objectsBreadcrumbItem );
        // the objectsExplorer should now be visible and the objectsBreadcrumbItem should be the active breadcrumb.
        verifyVisibleContent( objectExplorer );
        verifyIsActive( objectsBreadcrumbItem );
    }

    /**
     * Tests the case where a schema was selected in the schemaBrowser.
     */
    @Test
    public void testSchemaSelected( ) {
        // the structure explorer was previously initialized.
        testInitialize();

        when( schemaExplorer.hasItems() ).thenReturn( true );

        // emulates the execution of the schema selection by the schemaExplorer.
        structureExplorer.onSchemaSelected( SCHEMA_NAME );

        // the breadcrumbs should have been cleared.
        verifyBreadcrumbsCleared();

        // the objectExplorer should have been initialized with the given dataSource and schema.
        DatabaseObjectExplorer.Settings explorerSettings = new DatabaseObjectExplorer.Settings()
                .dataSourceUuid( DATASOURCE_ID )
                .schemaName( SCHEMA_NAME );

        verify ( objectExplorer, times( 1 ) ).initialize( explorerSettings );

        // the dataSourceBreadcrumbItem, schemasBreadcrumbItem and objectsBreadcrumbItem should be now available.
        verifyBreadCrumbsAdded( dataSourceBreadcrumbItem, schemasBreadcrumbItem, objectsBreadcrumbItem );
        // the objectsExplorer should now be visible and the objectsBreadcrumbItem should be the active breadcrumb.
        verifyVisibleContent( objectExplorer );
        verifyIsActive( objectsBreadcrumbItem );
    }

    /**
     * Tests the case where a database object was selected in the objectsExplorer.
     */
    @Test
    public void testDatabaseObjectSelected() {
        // the structure explorer was previously initialized.
        testInitialize();

        when( schemaExplorer.hasItems() ).thenReturn( true );

        // emulates the execution of a database object selection by the objectExplorer.
        structureExplorer.onDataBaseObjectSelected( SCHEMA_NAME, DATABASE_OBJECT_NAME );

        // the breadcrumbs should have been cleared.
        verifyBreadcrumbsCleared();

        // the objectViewer should have been initialized with the given dataSource, schema, and database object name.
        TableObjectViewer.Settings viewerSettings = new TableObjectViewer.Settings()
                .dataSourceUuid( DATASOURCE_ID )
                .schemaName( SCHEMA_NAME )
                .tableName( DATABASE_OBJECT_NAME );

        verify( objectViewer, times( 1 ) ).initialize( viewerSettings );

        // the dataSourceBreadcrumbItem, schemasBreadcrumbItem, objectsBreadcrumbItem and the objectViewerBreadcrumbItem
        // should be now available.
        verifyBreadCrumbsAdded( dataSourceBreadcrumbItem,
                schemasBreadcrumbItem, objectsBreadcrumbItem, objectViewerBreadcrumbItem );
        // the objectViewer should now be visible and the objectViewerBreadcrumbItem should be the active breadcrumb.
        verifyVisibleContent( objectViewer );
        verifyIsActive( objectViewerBreadcrumbItem );
    }

    /**
     * Common initialization checks.
     */
    private void testInitialize() {
        structureExplorer.initialize( settings, new InitializeCallback( ) {
            @Override
            public void onInitializeError( Throwable throwable ) {

            }

            @Override
            public void onInitializeSuccess( ) {

            }
        } );

        DatabaseSchemaExplorer.Settings explorerSettings = new DatabaseSchemaExplorer.Settings()
                .dataSourceUuid( settings.dataSourceUuid() );

        verify ( dataSourceBreadcrumbItem, times( 1 ) ).setName( DATASOURCE_NAME );
        verify( schemaExplorer, times( 1 ) ).initialize( eq( explorerSettings ), any( InitializeCallback.class )  );
    }

    /**
     * Verifies that the breadcrumb items has been cleared.
     */
    private void verifyBreadcrumbsCleared() {
        verify( view, times( 1 ) ).clearBreadcrumbs();
        verify( dataSourceBreadcrumbItem, times( 1 ) ).setActive( false );
        verify( schemasBreadcrumbItem, times( 1 ) ).setActive( false );
        verify( objectsBreadcrumbItem, times( 1 ) ).setActive( false );
        verify( objectViewerBreadcrumbItem, times( 1 ) ).setActive( false );
    }

    /**
     * Verifies that the expected breadcrumbs where properly set.
     */
    private void verifyBreadCrumbsAdded( BreadcrumbItem ... items ) {
        for ( BreadcrumbItem item : items ) {
            verify( view, times( 1) ).addBreadcrumbItem( item );
        }
    }

    /**
     * Verifies that view the content was set to the expected values.
     */
    private void verifyVisibleContent( IsElement content ) {
        verify( view, times( 1 ) ).clearContent();
        verify( view, times( 1 ) ).setContent( content );
    }

    /**
     * Verifies that a given breadcrumb has been activated.
     */
    private void verifyIsActive( BreadcrumbItem item ) {
        verify( item, times( 1 ) ).setActive( true );
    }
}
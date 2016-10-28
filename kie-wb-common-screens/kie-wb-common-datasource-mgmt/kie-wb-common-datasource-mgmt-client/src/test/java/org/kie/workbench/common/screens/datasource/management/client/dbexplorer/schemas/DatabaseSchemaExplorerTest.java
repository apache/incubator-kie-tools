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

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureTestConstants;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.metadata.DatabaseMetadata;
import org.kie.workbench.common.screens.datasource.management.metadata.SchemaMetadata;
import org.kie.workbench.common.screens.datasource.management.service.DatabaseMetadataService;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DatabaseSchemaExplorerTest
        implements DatabaseStructureTestConstants {

    @Mock
    private DatabaseSchemaExplorerView view;

    @Mock
    private DatabaseMetadataService metadataService;

    @Mock
    private TranslationService translationService;

    private DatabaseSchemaExplorer schemaExplorer;

    @Mock
    private DatabaseMetadata metadata;

    private List<SchemaMetadata> schemas = new ArrayList<>(  );

    @Mock
    private DatabaseSchemaExplorerView.Handler handler;

    @Before
    public void setup() {
        schemaExplorer = new DatabaseSchemaExplorer( view, new CallerMock<>( metadataService ), translationService );
        // emulate the @PostConstruct invocation
        schemaExplorer.init();
        schemas.add( new SchemaMetadata( "schema1" ) );
        schemas.add( new SchemaMetadata( "schema2" ) );
    }

    @Test
    public void testInitialize() {

        DatabaseSchemaExplorer.Settings settings = new DatabaseSchemaExplorer.Settings().dataSourceUuid( DATASOURCE_ID );

        when( metadataService.getMetadata( settings.dataSourceUuid(), false, true ) ).thenReturn( metadata );
        when( metadata.getSchemas() ).thenReturn( schemas );
        when( translationService.getTranslation(
                DataSourceManagementConstants.DatabaseSchemaExplorerViewImpl_loadingDbSchemas ) ).thenReturn( LOADING_MESSAGE1 );

        schemaExplorer.initialize( settings );

        // expected rows
        List<DatabaseSchemaRow> rows = new ArrayList<>(  );
        for ( SchemaMetadata schema : schemas ) {
            rows.add( new DatabaseSchemaRow( schema.getSchemaName() ) );
        }
        assertEquals( rows, schemaExplorer.getItems() );

        // the UI should have been updated.
        verify( view, times( 1 ) ).setDataProvider( any( AsyncDataProvider.class ) );
        verify( view, times( 1 ) ).showBusyIndicator( LOADING_MESSAGE1 );
        verify( view, times( 1 ) ).hideBusyIndicator();
        verify( view, times( 2 ) ).redraw();
    }

    @Test
    public void testOpen() {
        schemaExplorer.addHandler( handler );
        //emulates that a row has been selected in the UI.
        schemaExplorer.onOpen( new DatabaseSchemaRow( SCHEMA_NAME ) );
        //the handler should have been properly invoked.
        verify( handler, times( 1 ) ).onOpen( SCHEMA_NAME );
    }
}
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureTestConstants;
import org.kie.workbench.common.screens.datasource.management.service.DataManagementService;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class TableObjectViewerTest
        implements DatabaseStructureTestConstants {

    @Mock
    private TableObjectViewerView view;

    @Mock
    private DisplayerViewer displayerViewer;

    @Mock
    private UUIDGenerator uuidGenerator;

    @Mock
    private DataManagementService managementService;

    private TableObjectViewer viewer;

    @Mock
    private DisplayerSettings displayerSettings;

    @Before
    public void setup() {
        viewer = new TableObjectViewer( view, displayerViewer, uuidGenerator, new CallerMock<>( managementService ) );
    }

    @Test
    public void testInitialize() {
        TableObjectViewer.Settings settings = new TableObjectViewer.Settings()
                .dataSourceUuid( DATASOURCE_ID )
                .schemaName( SCHEMA_NAME )
                .tableName( DATABASE_OBJECT_NAME );
        when ( managementService.getDisplayerSettings( DATASOURCE_ID, SCHEMA_NAME, DATABASE_OBJECT_NAME ) )
                .thenReturn( displayerSettings );

        viewer.initialize( settings );

        verify( displayerViewer, times( 1 ) ).setIsShowRendererSelector( false );
        verify( displayerViewer, times( 1 ) ).init( displayerSettings );
        verify( displayerViewer, times( 1 ) ).draw();
        verify( view, times( 1 ) ).setContent( displayerViewer );
    }
}
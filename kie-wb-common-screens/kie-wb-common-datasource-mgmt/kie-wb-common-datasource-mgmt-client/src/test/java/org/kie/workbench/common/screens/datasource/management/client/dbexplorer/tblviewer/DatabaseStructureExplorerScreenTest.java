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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureExplorer;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureExplorerScreen;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureExplorerScreenView;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureTestConstants;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DatabaseStructureExplorerScreenTest
        implements DatabaseStructureTestConstants {

    @Mock
    private DatabaseStructureExplorerScreenView view;

    @Mock
    private TranslationService translationService;

    private DatabaseStructureExplorerScreen explorerScreen;

    @Mock
    private PlaceRequest placeRequest;

    @Before
    public void setup() {
        explorerScreen = new DatabaseStructureExplorerScreen( view, translationService );
    }

    /**
     * Tests the properly initialization of the screen.
     */
    @Test
    public void testOnStartup() {
        when ( translationService.getTranslation( DataSourceManagementConstants.DatabaseStructureExplorerScreen_title ) )
                .thenReturn( TRANSLATION_TEXT );
        when ( placeRequest.getParameter( DatabaseStructureExplorerScreen.DATASOURCE_UUID_PARAM, null ) )
                .thenReturn( DATASOURCE_ID );
        when( placeRequest.getParameter( DatabaseStructureExplorerScreen.DATASOURCE_NAME_PARAM, "" ) )
                .thenReturn( DATASOURCE_NAME );

        explorerScreen.onStartup( placeRequest );

        DatabaseStructureExplorer.Settings settings = new DatabaseStructureExplorer.Settings()
                .dataSourceUuid( DATASOURCE_ID )
                .dataSourceName( DATASOURCE_NAME );
        // the view should have been properly initialized with the parameters from the request.
        verify( view, times( 1 ) ).initialize( settings );
        // the screen title should be the expected one
        String expectedTitle = DATASOURCE_NAME + " - " + TRANSLATION_TEXT;
        assertEquals( expectedTitle, explorerScreen.getTitle() );
    }
}
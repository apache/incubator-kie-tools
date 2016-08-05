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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.type.DataSourceDefType;
import org.kie.workbench.common.screens.datasource.management.client.util.ClientValidationServiceMock;
import org.kie.workbench.common.screens.datasource.management.client.util.DataSourceManagementTestConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.client.validation.ClientValidationService;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceRuntimeManagerClientService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataSourceDefEditorTest
        implements DataSourceManagementTestConstants {

    @GwtMock
    private DataSourceDefEditorView view;

    @GwtMock
    private DataSourceDefMainPanelView mainPanelView;

    private DataSourceDefMainPanel mainPanel;

    private DataSourceDefEditorHelper editorHelper;

    @Mock
    private TranslationService translationService;

    @Mock
    private ClientValidationService clientValidationService;

    private DataSourceDefType type;

    @Mock
    private SavePopUpPresenter savePopupPresenter;

    @Mock
    private DeletePopUpPresenter deletePopUpPresenter;

    @Mock
    private DataSourceDefEditorService editorService;

    private Caller<DataSourceDefEditorService> editorServiceCaller;

    @Mock
    private DataSourceRuntimeManagerClientService dataSourceManagerClient;

    private Caller<DataSourceRuntimeManagerClientService> dataSourceManagerClientCaller;

    @Mock
    private DataSourceDefQueryService queryService;

    private Caller<DataSourceDefQueryService> queryServiceCaller;

    @Mock
    private ObservablePath path;

    @GwtMock
    private VersionRecordManager versionRecordManager;

    @Mock
    private PlaceRequest placeRequest;

    @GwtMock
    private PopupsUtil popupsUtil;

    private DataSourceDefEditor editor;

    private DataSourceDefEditorContent content;

    @Mock
    private DriverDefInfo driver1;

    @Mock
    private DriverDefInfo driver2;

    private List<DriverDefInfo> drivers;

    private List<Pair<String, String>> options;


    @Before
    public void setup() {
        drivers = new ArrayList<>(  );
        drivers.add( driver1 );
        drivers.add( driver2 );

        when( driver1.getName() ).thenReturn( "Driver1.name" );
        when( driver1.getUuid() ).thenReturn( DRIVER_UUID );
        when( driver2.getName() ).thenReturn( "Driver2.name" );
        when( driver2.getUuid() ).thenReturn( DRIVER_UUID_2 );

        options = new ArrayList<>(  );
        options.add( new Pair( "Driver1.name", DRIVER_UUID ) );
        options.add( new Pair( "Driver2.name", DRIVER_UUID_2 ) );

        mainPanel = new DataSourceDefMainPanel( mainPanelView );
        clientValidationService = new ClientValidationServiceMock();
        editorServiceCaller = new CallerMock<>( editorService );
        queryServiceCaller = new CallerMock<>( queryService );

        editorHelper = new DataSourceDefEditorHelper( translationService,
                editorServiceCaller, queryServiceCaller, clientValidationService, popupsUtil );

        editor = new DataSourceDefEditor( view,
                mainPanel, editorHelper, popupsUtil, type, savePopupPresenter, deletePopUpPresenter,
                editorServiceCaller, dataSourceManagerClientCaller ) {
            {
                this.versionRecordManager = DataSourceDefEditorTest.this.versionRecordManager;
                this.menuBuilder = mock( BasicFileMenuBuilder.class );
            }
        };

        //auxiliary for the test menu caption.
        when( editorHelper.getMessage( DataSourceManagementConstants.DataSourceDefEditor_TestDataSourceMenu ) )
                .thenReturn( "TestMenuCaption" );

                        verify( view, times( 1 ) ).init( editor );
        verify( view, times( 1 ) ).setMainPanel( mainPanel );
    }

    private void prepareLoadFileSuccessful() {
        //opens the editor with a valid content.
        content = createContent();
        when( queryService.findProjectDrivers( path ) ).thenReturn( drivers );
        when( queryService.findGlobalDrivers() ).thenReturn( drivers );
        when( versionRecordManager.getCurrentPath() ).thenReturn( path );
        when( editorService.loadContent( path ) ).thenReturn( content );

        editor.onStartup( path, placeRequest );
    }

    @Test
    public void testLoadFileSuccessFul(){

        prepareLoadFileSuccessful();

        //verifies the content was properly loaded.
        verify( view, times( 1 ) ).showLoading();
        verify( view, times( 1 ) ).hideBusyIndicator();
        assertEquals( content, editor.getContent() );

        verify( mainPanelView, times( 1 ) ).loadDriverOptions( eq( options ), eq( true ) );
        verify( mainPanelView, times( 1 ) ).setName( content.getDataSourceDef().getName() );
        verify( mainPanelView, times( 1 ) ).setConnectionURL( content.getDataSourceDef().getConnectionURL() );
        verify( mainPanelView, times( 1 ) ).setUser( content.getDataSourceDef().getUser() );
        verify( mainPanelView, times( 1 ) ).setPassword( content.getDataSourceDef().getPassword() );
        verify( mainPanelView, times( 2 ) ).setDriver( content.getDataSourceDef().getDriverUuid() );
    }

    @Test
    public void testEditorChanges() {

        //open the editor with a valid content
        prepareLoadFileSuccessful();

        //emulates some valid changes in the editor.
        when( mainPanelView.getName() ).thenReturn( NAME_2 );
        when( mainPanelView.getConnectionURL() ).thenReturn( CONNECTION_URL_2 );
        when( mainPanelView.getUser() ).thenReturn( USER_2 );
        when( mainPanelView.getPassword() ).thenReturn( PASSWORD_2 );
        when( mainPanelView.getDriver() ).thenReturn( DRIVER_UUID_2 );

        mainPanel.onNameChange();
        mainPanel.onConnectionURLChange();
        mainPanel.onUserChange();
        mainPanel.onPasswordChange();
        mainPanel.onDriverChange();

        //the content of the editor should have been properly modified.
        assertEquals( NAME_2, content.getDataSourceDef().getName() );
        assertEquals( CONNECTION_URL_2, content.getDataSourceDef().getConnectionURL() );
        assertEquals( USER_2, content.getDataSourceDef().getUser() );
        assertEquals( PASSWORD_2, content.getDataSourceDef().getPassword() );
        assertEquals( DRIVER_UUID_2, content.getDataSourceDef().getDriverUuid() );
    }

    private DataSourceDefEditorContent createContent() {
        DataSourceDefEditorContent content = new DataSourceDefEditorContent();
        content.setDataSourceDef( new DataSourceDef() );
        content.getDataSourceDef().setName( NAME );
        content.getDataSourceDef().setDriverUuid( DRIVER_UUID );
        content.getDataSourceDef().setConnectionURL( CONNECTION_URL );
        content.getDataSourceDef().setUser( USER );
        content.getDataSourceDef().setPassword( PASSWORD );
        return content;
    }

}

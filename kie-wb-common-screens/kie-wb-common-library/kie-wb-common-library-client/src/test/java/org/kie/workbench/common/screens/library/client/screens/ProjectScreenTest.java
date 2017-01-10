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

package org.kie.workbench.common.screens.library.client.screens;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.LibraryContextSwitchEvent;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryBreadcrumbs;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ProjectScreenTest {

    public static final String PROJECT_PATH = "projectPath";
    public static final String PROJECT_NAME = "projectName";

    @Mock
    private ProjectScreen.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private LibraryBreadcrumbs libraryBreadcrumbs;

    @Mock
    private Event<LibraryContextSwitchEvent> libraryContextSwitchEventEvent;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private TranslationService ts;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private Classifier assetClassifier;

    @Mock
    private Event<AssetDetailEvent> assetDetailEventEvent;

    @Captor
    private ArgumentCaptor<LibraryContextSwitchEvent> libraryContextSwitchEventArgumentCaptor;

    private ProjectScreen projectScreen;

    private List<FolderItem> assets;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>( libraryService );

        projectScreen = spy( new ProjectScreen( view,
                                                placeManager,
                                                libraryBreadcrumbs,
                                                libraryContextSwitchEventEvent,
                                                sessionInfo,
                                                authorizationManager,
                                                ts,
                                                libraryServiceCaller,
                                                assetClassifier,
                                                assetDetailEventEvent ) );

        mockClientResourceType();
        mockAssets();
    }

    @Test
    public void onStartupTest() {
        final Project project = mock( Project.class );
        doReturn( "projectName" ).when( project ).getProjectName();
        doReturn( "projectPath" ).when( project ).getIdentifier();

        projectScreen.onStartup( new ProjectDetailEvent( project ) );

        verify( libraryBreadcrumbs ).setupLibraryBreadCrumbsForProject( project );
        verify( libraryService ).getProjectAssets( project );
        verify( view ).clearAssets();
        verify( view, times( 2 ) ).addAsset( anyString(),
                                             anyString(),
                                             any( IsWidget.class ),
                                             any( Command.class ),
                                             any( Command.class ) );
    }

    @Test
    public void selectCommandTest() {
        final Project project = mock( Project.class );
        doReturn( "projectName" ).when( project ).getProjectName();
        doReturn( "projectPath" ).when( project ).getIdentifier();

        final Path assetPath = mock( Path.class );
        projectScreen.onStartup( new ProjectDetailEvent( project ) );
        projectScreen.selectCommand( "file2.txt", assetPath ).execute();

        verify( placeManager ).goTo( LibraryPlaces.ASSET_PERSPECTIVE );
        verify( assetDetailEventEvent ).fire( eq( new AssetDetailEvent( project, assetPath ) ) );
    }

    @Test
    public void filterAssetsTest() {
        assertEquals( 3, projectScreen.filterAssets( assets, "f" ).size() );
        assertEquals( 1, projectScreen.filterAssets( assets, "folder" ).size() );
        assertEquals( 2, projectScreen.filterAssets( assets, "file" ).size() );
        assertEquals( 1, projectScreen.filterAssets( assets, "file2" ).size() );
        assertEquals( 0, projectScreen.filterAssets( assets, "fileX" ).size() );
    }

    private void mockAssets() {
        final FolderItem asset1 = mock( FolderItem.class );
        doReturn( FolderItemType.FOLDER ).when( asset1 ).getType();
        doReturn( "folder1" ).when( asset1 ).getFileName();

        final FolderItem asset2 = mock( FolderItem.class );
        doReturn( FolderItemType.FILE ).when( asset2 ).getType();
        doReturn( "file2.txt" ).when( asset2 ).getFileName();

        final FolderItem asset3 = mock( FolderItem.class );
        doReturn( FolderItemType.FILE ).when( asset3 ).getType();
        doReturn( "file3.txt" ).when( asset3 ).getFileName();

        assets = Arrays.asList( asset1, asset2, asset3 );
        doReturn( assets ).when( libraryService ).getProjectAssets( any( Project.class ) );
    }

    private void mockClientResourceType() {
        ClientResourceType clientResourceType = mock( ClientResourceType.class );
        doReturn( ".txt" ).when( clientResourceType ).getSuffix();
        doReturn( "Text file" ).when( clientResourceType ).getDescription();
        doReturn( clientResourceType ).when( assetClassifier ).findResourceType( any( FolderItem.class ) );
    }
}

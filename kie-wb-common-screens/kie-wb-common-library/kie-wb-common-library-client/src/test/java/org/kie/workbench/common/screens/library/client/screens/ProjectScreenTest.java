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
import java.util.Date;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenTest {

    public static final String PROJECT_PATH = "projectPath";
    public static final String PROJECT_NAME = "projectName";

    @Mock
    private ProjectScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private TranslationService ts;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private Classifier assetClassifier;

    @Mock
    private Event<AssetDetailEvent> assetDetailEvent;

    @Mock
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    private ProjectScreen projectScreen;

    private ProjectInfo projectInfo;

    private List<AssetInfo> assets;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);

        projectScreen = spy(new ProjectScreen(view,
                                              libraryPlaces,
                                              ts,
                                              libraryServiceCaller,
                                              assetClassifier,
                                              assetDetailEvent,
                                              projectContextChangeEvent,
                                              busyIndicatorView));

        doReturn("createdTime").when(projectScreen).getCreatedTime(any(AssetInfo.class));
        doReturn("lastModifiedTime").when(projectScreen).getLastModifiedTime(any(AssetInfo.class));

        mockClientResourceType();
        mockAssets();

        projectInfo = createProjectInfo();
        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));
    }

    @Test
    public void onStartupTest() {
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(libraryService).getProjectAssets(projectInfo.getProject());
        verify(view).clearAssets();
        verify(view,
               times(2)).addAsset(anyString(),
                                  anyString(),
                                  anyString(),
                                  any(IsWidget.class),
                                  anyString(),
                                  anyString(),
                                  any(Command.class),
                                  any(Command.class));
        verify(busyIndicatorView).hideBusyIndicator();
        verify(view).setProjectName("projectName");
    }

    @Test
    public void refreshOnFocusTest() {
        final PlaceRequest place = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = new PlaceGainFocusEvent(place);

        projectScreen.refreshOnFocus(placeGainFocusEvent);

        verify(busyIndicatorView,
               times(2)).showBusyIndicator(anyString());
        verify(libraryService,
               times(2)).getProjectAssets(projectInfo.getProject());
        verify(view,
               times(2)).clearAssets();
        verify(view,
               times(4)).addAsset(anyString(),
                                  anyString(),
                                  anyString(),
                                  any(IsWidget.class),
                                  anyString(),
                                  anyString(),
                                  any(Command.class),
                                  any(Command.class));
        verify(busyIndicatorView,
               times(2)).hideBusyIndicator();
    }

    @Test
    public void dontRefreshOnFocusOnAnotherScreenTest() {
        final PlaceRequest place = new DefaultPlaceRequest("anotherScreen");
        final PlaceGainFocusEvent placeGainFocusEvent = new PlaceGainFocusEvent(place);

        projectScreen.refreshOnFocus(placeGainFocusEvent);

        verify(busyIndicatorView,
               times(1)).showBusyIndicator(anyString());
        verify(libraryService,
               times(1)).getProjectAssets(projectInfo.getProject());
        verify(view,
               times(1)).clearAssets();
        verify(view,
               times(2)).addAsset(anyString(),
                                  anyString(),
                                  anyString(),
                                  any(IsWidget.class),
                                  anyString(),
                                  anyString(),
                                  any(Command.class),
                                  any(Command.class));
        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
    }

    @Test
    public void updateAssetsByTest() {
        reset(view);
        projectScreen.updateAssetsBy("file3");

        verify(view).clearAssets();
        verify(view,
               times(1)).addAsset(eq("file3.txt"),
                                  anyString(),
                                  anyString(),
                                  any(IsWidget.class),
                                  anyString(),
                                  anyString(),
                                  any(Command.class),
                                  any(Command.class));
        verify(busyIndicatorView).hideBusyIndicator();
    }

    @Test
    public void goToSettingsTest() {
        projectScreen.goToSettings();

        verify(assetDetailEvent).fire(new AssetDetailEvent(projectInfo,
                                                           null));
    }

    @Test
    public void getProjectNameTest() {
        assertEquals("projectName",
                     projectScreen.getProjectName());
    }

    @Test
    public void filterAssetsTest() {
        assertEquals(3,
                     projectScreen.filterAssets(assets,
                                                "f").size());
        assertEquals(1,
                     projectScreen.filterAssets(assets,
                                                "folder").size());
        assertEquals(2,
                     projectScreen.filterAssets(assets,
                                                "file").size());
        assertEquals(1,
                     projectScreen.filterAssets(assets,
                                                "file2").size());
        assertEquals(0,
                     projectScreen.filterAssets(assets,
                                                "fileX").size());
    }

    @Test
    public void selectCommandTest() {
        final Path assetPath = mock(Path.class);

        projectScreen.selectCommand(assetPath).execute();

        verify(libraryPlaces).goToAsset(projectInfo,
                                        assetPath);
    }

    @Test
    public void detailsCommandTest() {
        final Path assetPath = mock(Path.class);

        projectScreen.detailsCommand(assetPath).execute();

        verify(libraryPlaces).goToAsset(projectInfo,
                                        assetPath);
    }

    private void mockAssets() {
        final Path asset1Path = mock(Path.class);
        doReturn("git://projectPath/folder1").when(asset1Path).toURI();
        final FolderItem asset1 = mock(FolderItem.class);
        doReturn(FolderItemType.FOLDER).when(asset1).getType();
        doReturn("folder1").when(asset1).getFileName();
        doReturn(asset1Path).when(asset1).getItem();

        final Path asset2Path = mock(Path.class);
        doReturn("git://projectPath/file2.txt").when(asset2Path).toURI();
        final FolderItem asset2 = mock(FolderItem.class);
        doReturn(FolderItemType.FILE).when(asset2).getType();
        doReturn("file2.txt").when(asset2).getFileName();
        doReturn(asset2Path).when(asset2).getItem();

        final Path asset3Path = mock(Path.class);
        doReturn("git://projectPath/file3.txt").when(asset3Path).toURI();
        final FolderItem asset3 = mock(FolderItem.class);
        doReturn(FolderItemType.FILE).when(asset3).getType();
        doReturn("file3.txt").when(asset3).getFileName();
        doReturn(asset3Path).when(asset3).getItem();

        assets = Arrays.asList(new AssetInfo(asset1,
                                             new Date(),
                                             new Date()),
                               new AssetInfo(asset2,
                                             new Date(),
                                             new Date()),
                               new AssetInfo(asset3,
                                             new Date(),
                                             new Date()));
        doReturn(assets).when(libraryService).getProjectAssets(any(Project.class));
    }

    private void mockClientResourceType() {
        ClientResourceType clientResourceType = mock(ClientResourceType.class);
        doReturn(".txt").when(clientResourceType).getSuffix();
        doReturn("Text file").when(clientResourceType).getDescription();
        doReturn(clientResourceType).when(assetClassifier).findResourceType(any(FolderItem.class));
    }

    private ProjectInfo createProjectInfo() {
        final Path rootPath = mock(Path.class);
        doReturn("git://projectPath").when(rootPath).toURI();
        final Project project = mock(Project.class);
        doReturn("projectName").when(project).getProjectName();
        doReturn("projectPath").when(project).getIdentifier();
        doReturn(rootPath).when(project).getRootPath();

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repository = mock(Repository.class);
        final String branch = "master";
        return new ProjectInfo(organizationalUnit,
                               repository,
                               branch,
                               project);
    }
}

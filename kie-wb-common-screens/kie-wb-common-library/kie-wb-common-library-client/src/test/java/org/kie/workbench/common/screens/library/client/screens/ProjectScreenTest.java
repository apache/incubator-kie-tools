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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenTest
        extends ProjectScreenTestBase {

    @Mock
    private Timer timer;

    @Before
    public void setup() {
        projectScreen = spy(new ProjectScreen(view,
                                              libraryPlaces,
                                              mock(ProjectsDetailScreen.class),
                                              ts,
                                              new CallerMock<>(libraryService),
                                              assetClassifier,
                                              assetDetailEvent,
                                              busyIndicatorView,
                                              projectController) {
            @Override
            protected void reload() {
                onFilterChange();
            }

            @Override
            protected Timer createTimer() {
                return timer;
            }
        });

        doReturn("createdTime").when(projectScreen).getCreatedTime(any(AssetInfo.class));
        doReturn("lastModifiedTime").when(projectScreen).getLastModifiedTime(any(AssetInfo.class));

        doAnswer(a -> {
            projectScreen.onTimerAction();
            return null;
        }).when(timer).schedule(anyInt());

        mockClientResourceType();
        mockAssets();

        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(15);

        doReturn(true).when(projectController).canUpdateProject(any());

        projectInfo = createProjectInfo();
        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));
    }

    @Test
    public void onStartupTest() {
        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(libraryService).getProjectAssets(queryArgumentCaptor.capture());
        assertEquals(projectInfo.getProject(),
                     queryArgumentCaptor.getValue().getProject());
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
        verify(view).setupAssetsActions();
    }

    @Test
    public void onStartupDoesNotAddNewAssetButtonWhenUserDoesNotHaveUpdateProjectPermissionTest() {
        reset(view);
        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(15);
        doReturn(false).when(projectController).canUpdateProject(any());

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(view,
               never()).setupAssetsActions();
    }

    @Test
    public void refreshOnFocusTest() {
        final PlaceRequest place = new DefaultPlaceRequest(LibraryPlaces.PROJECT_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = new PlaceGainFocusEvent(place);

        projectScreen.refreshOnFocus(placeGainFocusEvent);

        verify(busyIndicatorView,
               times(2)).showBusyIndicator(anyString());
        verify(libraryService,
               times(2)).getProjectAssets(any(ProjectAssetsQuery.class));
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
               times(1)).getProjectAssets(queryArgumentCaptor.capture());
        assertEquals(projectInfo.getProject(),
                     queryArgumentCaptor.getValue().getProject());
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
        reset(view,
              libraryService);

        when(view.getFilterValue()).thenReturn("file3");
        when(view.getStep()).thenReturn(100);
        when(view.getFirstIndex()).thenReturn(12200);

        projectScreen.onFilterChange();

        verify(libraryService).getProjectAssets(queryArgumentCaptor.capture());
        assertEquals("file3",
                     queryArgumentCaptor.getValue().getFilter());
        assertEquals(100,
                     queryArgumentCaptor.getValue().getAmount());
        assertEquals(12200,
                     queryArgumentCaptor.getValue().getStartIndex());
    }

    @Test
    public void updatedSearchParameterResetsToFirstPage() throws Exception {
        reset(view);
        when(view.getFilterValue()).thenReturn("something");
        projectScreen.onFilterChange();

        verify(view).resetPageRangeIndicator();
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
}

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenButtonDisableEnableTest
        extends ProjectScreenTestBase {

    @Before
    public void setup() {

        projectScreen = spy(new ProjectScreen(view,
                                              libraryPlaces,
                                              ts,
                                              new CallerMock<>(libraryService),
                                              assetClassifier,
                                              assetDetailEvent,
                                              busyIndicatorView) {
            @Override
            protected void reload() {
                onUpdateAssets();
            }
        });

        doReturn("createdTime").when(projectScreen).getCreatedTime(any(AssetInfo.class));
        doReturn("lastModifiedTime").when(projectScreen).getLastModifiedTime(any(AssetInfo.class));

        mockClientResourceType();
        mockAssets();

        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(15);

        projectInfo = createProjectInfo();
        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));
    }

    @Test
    public void preventGoingBackWhenOnFirstPage() throws Exception {

        reset(libraryService,
              view);
        doReturn(assets).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));

        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(3);
        when(view.getPageNumber()).thenReturn(1);

        projectScreen.onUpdateAssets();

        verify(view).setForwardDisabled(eq(false));
        verify(view).setBackwardDisabled(eq(true));
    }

    @Test
    public void enableEveryThingWhenOnMiddle() throws Exception {

        reset(libraryService,
              view);
        doReturn(assets).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));

        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(3);
        when(view.getPageNumber()).thenReturn(2);

        projectScreen.onUpdateAssets();

        verify(view).setForwardDisabled(eq(false));
        verify(view).setBackwardDisabled(eq(false));
    }

    @Test
    public void disableForwardIfPageNotFull() throws Exception {

        reset(libraryService,
              view);
        doReturn(assets).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));

        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(5);
        when(view.getPageNumber()).thenReturn(2);

        projectScreen.onUpdateAssets();

        verify(view).setForwardDisabled(eq(true));
        verify(view).setBackwardDisabled(eq(false));
    }

    @Test
    public void blockEveryThingOnFirstPageThatIsNotFull() throws Exception {

        reset(libraryService,
              view);
        doReturn(assets).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));

        when(view.getFilterValue()).thenReturn("");
        when(view.getStep()).thenReturn(5);
        when(view.getPageNumber()).thenReturn(1);

        projectScreen.onUpdateAssets();

        verify(view).setForwardDisabled(eq(true));
        verify(view).setBackwardDisabled(eq(true));
    }
}

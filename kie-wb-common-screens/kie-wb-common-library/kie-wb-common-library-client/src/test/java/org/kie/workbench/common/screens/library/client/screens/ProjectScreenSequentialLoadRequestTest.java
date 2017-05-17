/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import com.google.gwt.user.client.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenSequentialLoadRequestTest
        extends ProjectScreenTestBase {

    @Mock
    private Timer timer;

    @Captor
    private ArgumentCaptor<ProjectAssetsQuery> queryCaptor;

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
            projectScreen.loadProjectInfo();
            return null;
        }).when(timer).schedule(anyInt());

        mockClientResourceType();
        mockAssets();

        when(view.getStep()).thenReturn(15);

        projectInfo = createProjectInfo();
    }

    @Test
    public void sequentialLoadRequestTest() {
        final boolean[] inCall = {true};
        doAnswer(a -> {
            //This mocks a successive request to load the asset list whilst the first request is incomplete with a filter "ab"
            if (inCall[0]) {
                when(view.getFilterValue()).thenReturn("ab");
                projectScreen.onFilterChange();
                inCall[0] = false;
            }

            return assets;
        }).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));

        //This invokes the first request to load the asset list with a filter "a"
        when(view.getFilterValue()).thenReturn("a");
        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(libraryService,
               times(2)).getProjectAssets(queryCaptor.capture());
        final List<ProjectAssetsQuery> queries = queryCaptor.getAllValues();
        assertEquals(2,
                     queries.size());
        assertEquals("a",
                     queries.get(0).getFilter());
        assertEquals("ab",
                     queries.get(1).getFilter());
        verify(view,
               times(2)).getFilterValue();
    }
}

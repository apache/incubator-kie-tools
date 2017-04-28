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

import java.util.Date;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenReloadTest
        extends ProjectScreenTestBase {

    private int numberOfCalls;

    @Before
    public void setup() {
        numberOfCalls = 0;

        projectScreen = new ProjectScreen(view,
                                          libraryPlaces,
                                          ts,
                                          new CallerMock<>(libraryService),
                                          assetClassifier,
                                          assetDetailEvent,
                                          busyIndicatorView) {
            @Override
            String getCreatedTime(AssetInfo asset) {
                return "";
            }

            @Override
            String getLastModifiedTime(AssetInfo asset) {
                return "";
            }

            @Override
            protected void reload() {
                numberOfCalls++;

                onUpdateAssets();
            }
        };

        mockClientResourceType();
        when(libraryService.getProjectAssets(any(ProjectAssetsQuery.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

                if (numberOfCalls == 2) {
                    assets.add(getAssetInfo("git://projectPath/folder1",
                                            FolderItemType.FOLDER,
                                            "folder1"));

                    assets.add(getAssetInfo("git://projectPath/file2.txt",
                                            FolderItemType.FILE,
                                            "file2.txt"));
                } else if (numberOfCalls == 3) {
                    assets.add(getAssetInfo("git://projectPath/file3.txt",
                                            FolderItemType.FILE,
                                            "file3.txt"));
                }

                return assets;
            }
        });

        when(view.getFilterValue()).thenReturn("");
        when(view.getPageNumber()).thenReturn(1);
        when(view.getStep()).thenReturn(15);

        projectInfo = createProjectInfo();
    }

    @Test
    public void noReloadIfFirstRunReturnsAssets() throws Exception {
        assets.add(getAssetInfo("git://projectPath/file2.txt",
                                FolderItemType.FILE,
                                "file2.txt"));

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(view,
               never()).showIndexingIncomplete();
        verify(view).hideEmptyState();

        assertEquals(0,
                     numberOfCalls);
    }

    @Test
    public void noReloadOnEmptySearch() throws Exception {

        when(view.getFilterValue()).thenReturn("my asset");

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(view,
               never()).showIndexingIncomplete();
        verify(view).showSearchHitNothing();
        verify(view).hideEmptyState();

        assertEquals(0,
                     numberOfCalls);
    }

    @Test
    public void onlyReloadOnFirstPage() throws Exception {

        when(view.getPageNumber()).thenReturn(2);

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(view,
               never()).showIndexingIncomplete();
        verify(view,
               never()).showSearchHitNothing();
        verify(view).showNoMoreAssets();
        verify(view).hideEmptyState();

        assertEquals(0,
                     numberOfCalls);
    }

    @Test
    public void doNotReloadSearchOnLaterPages() throws Exception {

        when(view.getFilterValue()).thenReturn("some asset");

        when(view.getPageNumber()).thenReturn(2);

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(view,
               never()).showIndexingIncomplete();
        verify(view,
               never()).showSearchHitNothing();
        verify(view).showNoMoreAssets();
        verify(view).hideEmptyState();

        assertEquals(0,
                     numberOfCalls);
    }

    @Test
    public void stopReloadingIfPageIsFull() throws Exception {

        when(view.getStep()).thenReturn(3);

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        assertEquals(3,
                     numberOfCalls);
    }

    @Test
    public void reloadOnEmptyList() throws Exception {

        projectScreen.onStartup(new ProjectDetailEvent(projectInfo));

        verify(view,
               atLeastOnce()).showIndexingIncomplete();

        assertEquals(4,
                     numberOfCalls);
    }



}

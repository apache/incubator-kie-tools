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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProjectScreenTestBase {

    @Mock
    protected ProjectScreen.View view;

    @Mock
    protected LibraryPlaces libraryPlaces;

    @Mock
    protected TranslationService ts;

    @Mock
    protected LibraryService libraryService;

    @Mock
    protected Classifier assetClassifier;

    @Mock
    protected EventSourceMock<AssetDetailEvent> assetDetailEvent;

    @Mock
    protected BusyIndicatorView busyIndicatorView;

    @Captor
    protected ArgumentCaptor<ProjectAssetsQuery> queryArgumentCaptor;

    protected ProjectScreen projectScreen;

    protected ProjectInfo projectInfo;

    protected List<AssetInfo> assets = new ArrayList<>();

    protected void mockClientResourceType() {
        final ClientResourceType clientResourceType = mock(ClientResourceType.class);
        doReturn(".txt").when(clientResourceType).getSuffix();
        doReturn("Text file").when(clientResourceType).getDescription();
        doReturn(clientResourceType).when(assetClassifier).findResourceType(any(FolderItem.class));
    }

    protected ProjectInfo createProjectInfo() {
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

    protected AssetInfo getAssetInfo(final String assetPathString,
                                     final FolderItemType itemType,
                                     final String itemName) {
        final Path assetPath = mock(Path.class);
        doReturn(assetPathString).when(assetPath).toURI();
        final FolderItem asset = mock(FolderItem.class);
        doReturn(itemType).when(asset).getType();
        doReturn(itemName).when(asset).getFileName();
        doReturn(assetPath).when(asset).getItem();

        return new AssetInfo(asset,
                             new Date(),
                             new Date());
    }

    protected void mockAssets() {

        assets.add(getAssetInfo("git://projectPath/folder1",
                                FolderItemType.FOLDER,
                                "folder1"));
        assets.add(getAssetInfo("git://projectPath/file2.txt",
                                FolderItemType.FILE,
                                "file2.txt"));
        assets.add(getAssetInfo("git://projectPath/file3.txt",
                                FolderItemType.FILE,
                                "file3.txt"));

        doReturn(assets).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));
    }
}

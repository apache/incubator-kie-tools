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
import java.util.Optional;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
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
    protected WorkspaceProjectListAssetsPresenter.View view;

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

    @Mock
    protected ProjectController projectController;

    @Captor
    protected ArgumentCaptor<ProjectAssetsQuery> queryArgumentCaptor;

    protected WorkspaceProjectListAssetsPresenter workspaceProjectListAssetsPresenter;

    protected WorkspaceProject project;

    protected List<AssetInfo> assets = new ArrayList<>();

    protected void mockClientResourceType() {
        final ClientResourceType clientResourceType = mock(ClientResourceType.class);
        doReturn(".txt").when(clientResourceType).getSuffix();
        doReturn("Text file").when(clientResourceType).getDescription();
        doReturn(clientResourceType).when(assetClassifier).findResourceType(any(FolderItem.class));
    }

    protected WorkspaceProject createProject() {
        final Path rootPath = mock(Path.class);
        doReturn("git://modulePath").when(rootPath).toURI();
        final Module module = mock(Module.class);
        doReturn("mainModuleName").when(module).getModuleName();
        doReturn("modulePath").when(module).getIdentifier();
        doReturn(rootPath).when(module).getRootPath();

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repository = mock(Repository.class);
        final Path repositoryRootPath = mock(Path.class);

        doReturn(Optional.of(new Branch("master",
                                        repositoryRootPath))).when(repository).getDefaultBranch();
        doReturn("rootpath").when(repositoryRootPath).toURI();

        return new WorkspaceProject(organizationalUnit,
                                    repository,
                                    new Branch("master",
                                               mock(Path.class)),
                                    module);
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

        assets.add(getAssetInfo("git://modulePath/folder1",
                                FolderItemType.FOLDER,
                                "folder1"));
        assets.add(getAssetInfo("git://modulePath/file2.txt",
                                FolderItemType.FILE,
                                "file2.txt"));
        assets.add(getAssetInfo("git://modulePath/file3.txt",
                                FolderItemType.FILE,
                                "file3.txt"));

        doReturn(assets).when(libraryService).getProjectAssets(any(ProjectAssetsQuery.class));
    }
}

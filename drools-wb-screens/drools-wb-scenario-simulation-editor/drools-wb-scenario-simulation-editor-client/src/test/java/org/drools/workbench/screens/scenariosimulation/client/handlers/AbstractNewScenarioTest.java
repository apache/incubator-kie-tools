/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetQueryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public abstract class AbstractNewScenarioTest {

    @Mock
    protected LibraryPlaces libraryPlacesMock;
    @Mock
    protected WorkspaceProject workspaceProjectMock;
    @Mock
    protected AssetQueryService assetQueryServiceMock;
    @Mock
    protected AssetQueryService.Invoker<AssetQueryResult> invokerMock;

    @Before
    public void setup() throws Exception {
        when(assetQueryServiceMock.getAssets(isA(ProjectAssetsQuery.class))).thenReturn(invokerMock);
        when(libraryPlacesMock.getActiveWorkspace()).thenReturn(workspaceProjectMock);
    }
}
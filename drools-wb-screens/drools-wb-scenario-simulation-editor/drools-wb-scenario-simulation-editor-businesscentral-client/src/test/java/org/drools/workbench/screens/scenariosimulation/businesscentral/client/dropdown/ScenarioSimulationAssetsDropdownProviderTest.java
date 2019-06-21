/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.dropdown;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.dropdown.AbstractScenarioSimulationDropdownTest;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetQueryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationAssetsDropdownProviderTest extends AbstractScenarioSimulationDropdownTest {

    @Mock
    private Caller<ScenarioSimulationService> scenarioSimulationServiceCallerMock;

    @Mock
    private ScenarioSimulationService scenarioSimulationServiceMock;

    @Mock
    private LibraryPlaces libraryPlacesMock;

    @Mock
    private AssetQueryService assetQueryServiceMock;

    @Mock
    private WorkspaceProject workspaceProjectMock;

    @Mock
    private Path rootPathMock;

    @Mock
    private AssetQueryService.Invoker<AssetQueryResult> invokerMock;

    @Mock
    Consumer<List<KieAssetsDropdownItem>> assetListConsumerMock;

    private ScenarioSimulationAssetsDropdownProviderBCImpl scenarioSimulationAssetsDropdownProvider;
    private ProjectAssetsQuery projectAssetsQuery;

    @Before
    public void setup() {
        super.setup();
        when(scenarioSimulationServiceCallerMock.call(any())).thenReturn(scenarioSimulationServiceMock);
        when(libraryPlacesMock.getActiveWorkspace()).thenReturn(workspaceProjectMock);
        when(workspaceProjectMock.getRootPath()).thenReturn(rootPathMock);
        when(rootPathMock.toURI()).thenReturn("project/");
        when(assetQueryServiceMock.getAssets(eq(projectAssetsQuery))).thenReturn(invokerMock);
        scenarioSimulationAssetsDropdownProvider = spy(new ScenarioSimulationAssetsDropdownProviderBCImpl(scenarioSimulationServiceCallerMock,
                                                                                                          libraryPlacesMock,
                                                                                                          assetQueryServiceMock) {
            @Override
            protected ProjectAssetsQuery createProjectQuery() {
                return projectAssetsQuery;
            }
        });
        projectAssetsQuery = scenarioSimulationAssetsDropdownProvider.createProjectQuery();
    }

    @Test
    public void getItems() {
        Consumer<List<KieAssetsDropdownItem>> assetListConsumerMock = mock(Consumer.class);
        doAnswer(invocation -> null).when(scenarioSimulationAssetsDropdownProvider).updateAssets(isA(RemoteCallback.class));
        scenarioSimulationAssetsDropdownProvider.getItems(assetListConsumerMock);
        verify(scenarioSimulationAssetsDropdownProvider, times(1)).updateAssets(isA(RemoteCallback.class));
    }

    @Test
    public void updateAssets() {
        RemoteCallback<AssetQueryResult> remoteCallbackMock = mock(RemoteCallback.class);
        scenarioSimulationAssetsDropdownProvider.updateAssets(remoteCallbackMock);
        verify(assetQueryServiceMock, times(1)).getAssets(eq(projectAssetsQuery));
        verify(invokerMock, times(1)).call(eq(remoteCallbackMock), isA(DefaultErrorCallback.class));
    }

    @Test
    public void createProjectQuery() {
        scenarioSimulationAssetsDropdownProvider = spy(new ScenarioSimulationAssetsDropdownProviderBCImpl(scenarioSimulationServiceCallerMock,
                                                                                                          libraryPlacesMock,
                                                                                                          assetQueryServiceMock) {

        });
        final ProjectAssetsQuery retrieved = scenarioSimulationAssetsDropdownProvider.createProjectQuery();
        assertEquals(retrieved.getAmount(),1000);
        assertEquals(retrieved.getStartIndex(),0);
        assertEquals(retrieved.getFilter(), "");
        assertEquals(retrieved.getProject(), workspaceProjectMock);
    }

    @Test
    public void addAssets() {
        int size = 4;
        AssetQueryResult assetQueryResult = getAssetQueryResult(size);
        scenarioSimulationAssetsDropdownProvider.addAssets(assetQueryResult, assetListConsumerMock);
    }

    private AssetQueryResult getAssetQueryResult(int size) {
        return AssetQueryResult.normal(getAssetInfoList(size));
    }

    private List<AssetInfo> getAssetInfoList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> getAssetInfoMock())
                .collect(Collectors.toList());
    }

    private AssetInfo getAssetInfoMock() {
        AssetInfo toReturn = mock(AssetInfo.class);
        final FolderItem folderItemMock = getFolderItemMock();
        when(toReturn.getFolderItem()).thenReturn(folderItemMock);
        return toReturn;
    }

    private FolderItem getFolderItemMock() {
        FolderItem toReturn = mock(FolderItem.class);
        Path path = mock(Path.class);
        when(toReturn.getType()).thenReturn(FolderItemType.FILE);
        when(toReturn.getItem()).thenReturn(path);
        when(path.toURI()).thenReturn("project/test.dmn");
        return toReturn;
    }
}
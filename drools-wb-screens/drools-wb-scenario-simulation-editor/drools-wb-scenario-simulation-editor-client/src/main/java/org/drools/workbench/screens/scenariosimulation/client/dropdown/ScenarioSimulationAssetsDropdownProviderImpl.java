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
package org.drools.workbench.screens.scenariosimulation.client.dropdown;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.service.ScenarioSimulationService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetQueryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.util.URIUtil;

@Dependent
public class ScenarioSimulationAssetsDropdownProviderImpl implements ScenarioSimulationAssetsDropdownProvider {

    protected Caller<ScenarioSimulationService> scenarioSimulationService;
    protected LibraryPlaces libraryPlaces;
    protected AssetQueryService assetQueryService;

    @Inject
    public ScenarioSimulationAssetsDropdownProviderImpl(Caller<ScenarioSimulationService> scenarioSimulationService,
                                                        final LibraryPlaces libraryPlaces,
                                                        final AssetQueryService assetQueryService) {
        super();
        this.scenarioSimulationService = scenarioSimulationService;
        this.libraryPlaces = libraryPlaces;
        this.assetQueryService = assetQueryService;
    }

    @Override
    public void getItems(Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        updateAssets(response -> addAssets(response, assetListConsumer));
    }

    protected void updateAssets(RemoteCallback<AssetQueryResult> callback) {
        ProjectAssetsQuery query = createProjectQuery();
        assetQueryService.getAssets(query).call(callback, new DefaultErrorCallback());
    }

    protected ProjectAssetsQuery createProjectQuery() {
        List<String> suffixes = Collections.singletonList("dmn");
        return new ProjectAssetsQuery(libraryPlaces.getActiveWorkspace(),
                                      "",
                                      0,
                                      1000,
                                      suffixes);
    }

    protected void addAssets(AssetQueryResult result, Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        if (Objects.equals(AssetQueryResult.ResultType.Normal, result.getResultType())) {
            result.getAssetInfos().ifPresent(assetInfos -> addAssets(assetInfos, assetListConsumer));
        }
    }

    protected void addAssets(List<AssetInfo> assetInfos, Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        final List<KieAssetsDropdownItem> kieAssetsDropdownItems = assetInfos.stream()
                .filter(item -> item.getFolderItem().getType().equals(FolderItemType.FILE))
                .map(this::getKieAssetsDropdownItem)
                .collect(Collectors.toList());
        assetListConsumer.accept(kieAssetsDropdownItems);
    }

    protected KieAssetsDropdownItem getKieAssetsDropdownItem(final AssetInfo asset) {
        final String fullPath = ((Path) asset.getFolderItem().getItem()).toURI();
        final String projectRootPath = libraryPlaces.getActiveWorkspace().getRootPath().toURI();
        final String relativeAssetPath = fullPath.substring(projectRootPath.length());
        final String decodedRelativeAssetPath = URIUtil.decode(relativeAssetPath);
        final String fileName = ((Path) asset.getFolderItem().getItem()).getFileName();
        return new KieAssetsDropdownItem(fileName, decodedRelativeAssetPath, decodedRelativeAssetPath, new HashMap<>());
    }
}
/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.client.dropdown;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.kogito.client.dropdown.ScenarioSimulationKogitoCreationAssetsDropdownProvider;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

@Dependent
public class ScenarioSimulationKogitoRuntimeCreationAssetsDropdownProviderImpl implements ScenarioSimulationKogitoCreationAssetsDropdownProvider {

    protected static final String DMN_FILE_EXTENSION = "**/*.dmn";

    @Inject
    protected KogitoResourceContentService resourceContentService;
    @Inject
    protected ErrorPopupPresenter errorPopupPresenter;

    @Override
    public void getItems(Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        resourceContentService.getFilteredItems(DMN_FILE_EXTENSION,
                                                getRemoteCallback(assetListConsumer),
                                                getErrorCallback());
    }

    protected RemoteCallback<List<String>> getRemoteCallback(Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        return response -> {
            List<KieAssetsDropdownItem> toAccept = response.stream()
                    .map(this::getKieAssetsDropdownItem)
                    .sorted(Comparator.comparing(KieAssetsDropdownItem::getText, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            assetListConsumer.accept(toAccept);
        };
    }

    protected ErrorCallback<Object> getErrorCallback() {
        return (message, throwable) -> {
            errorPopupPresenter.showMessage(message + ": " + throwable.getMessage());
            return false;
        };
    }

    protected KieAssetsDropdownItem getKieAssetsDropdownItem(final String fullPath) {
        int idx = fullPath.replaceAll("\\\\", "/").lastIndexOf('/');
        final String fileName = idx >= 0 ? fullPath.substring(idx + 1) : fullPath;
        return new KieAssetsDropdownItem(fileName, fullPath, fullPath, new HashMap<>());
    }
}

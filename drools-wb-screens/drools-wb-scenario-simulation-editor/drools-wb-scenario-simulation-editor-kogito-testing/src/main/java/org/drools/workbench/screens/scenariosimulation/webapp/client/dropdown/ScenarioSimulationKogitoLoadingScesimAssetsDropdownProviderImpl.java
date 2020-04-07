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

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.drools.workbench.screens.scenariosimulation.webapp.client.services.TestingVFSService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItemsProvider;
import org.uberfire.backend.vfs.Path;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.ScenarioSimulationEditorKogitoTestingScreen.SCESIM_PATH;

@Dependent
public class ScenarioSimulationKogitoLoadingScesimAssetsDropdownProviderImpl implements KieAssetsDropdownItemsProvider {

    private static final String FILE_SUFFIX = "scesim";

    @Inject
    private TestingVFSService testingVFSService;

    @Override
    public void getItems(Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        getItems(response -> {
            List<KieAssetsDropdownItem> toAccept = response.stream()
                    .map(this::getKieAssetsDropdownItem)
                    .collect(Collectors.toList());
            assetListConsumer.accept(toAccept);
        }, (message, throwable) -> {
            GWT.log(message.getCommandType() + " " + message.toString(), throwable);
            return false;
        });
    }

    public void getItems(final RemoteCallback<List<Path>> callback, final ErrorCallback<Message> errorCallback) {
        testingVFSService.getItemsByPath(SCESIM_PATH, FILE_SUFFIX, callback, errorCallback);
    }

    KieAssetsDropdownItem getKieAssetsDropdownItem(final Path asset) {
        return new KieAssetsDropdownItem(asset.getFileName(), "", asset.toURI(), new HashMap<>());
    }
}

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

package org.kie.workbench.common.dmn.client.editors.included.modal.dropdown;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItemsProvider;

@Dependent
public class DMNAssetsDropdownItemsProvider implements KieAssetsDropdownItemsProvider {

    public static final String PATH_METADATA = "path";

    private final DMNIncludeModelsClient client;

    private final IncludedModelsPageState pageState;

    private final IncludedModelsIndex modelsIndex;

    @Inject
    public DMNAssetsDropdownItemsProvider(final DMNIncludeModelsClient client,
                                          final IncludedModelsPageState pageState,
                                          final IncludedModelsIndex modelsIndex) {
        this.client = client;
        this.pageState = pageState;
        this.modelsIndex = modelsIndex;
    }

    @Override
    public void getItems(final Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        client.loadModels(wrap(assetListConsumer));
    }

    Consumer<List<DMNIncludeModel>> wrap(final Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        return dmnIncludeModels -> assetListConsumer.accept(dmnIncludeModels
                                                                    .stream()
                                                                    .filter(this::isNotExisting)
                                                                    .filter(this::isNotCurrentDiagram)
                                                                    .map(this::asKieAsset)
                                                                    .collect(Collectors.toList()));
    }

    private boolean isNotExisting(final DMNIncludeModel data) {
        return modelsIndex
                .getIndexedImports()
                .stream()
                .noneMatch(anImport -> Objects.equals(data.getNamespace(), anImport.getNamespace()));
    }

    private boolean isNotCurrentDiagram(final DMNIncludeModel data) {
        return !Objects.equals(data.getNamespace(), pageState.getCurrentDiagramNamespace());
    }

    KieAssetsDropdownItem asKieAsset(final DMNIncludeModel dmnIncludeModel) {

        final String text = dmnIncludeModel.getModelName();
        final String subText = dmnIncludeModel.getModelPackage();
        final String value = dmnIncludeModel.getNamespace();
        final Map<String, String> metaData = buildMetaData(dmnIncludeModel);

        return new KieAssetsDropdownItem(text, subText, value, metaData);
    }

    private Map<String, String> buildMetaData(final DMNIncludeModel dmnIncludeModel) {
        return new Maps
                .Builder<String, String>()
                .put(PATH_METADATA, dmnIncludeModel.getPath())
                .build();
    }
}

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
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItemsProvider;

@Dependent
public class DMNAssetsDropdownItemsProvider implements KieAssetsDropdownItemsProvider {

    public static final String PATH_METADATA = "path";

    public static final String DRG_ELEMENT_COUNT_METADATA = "drg_element_count";

    public static final String ITEM_DEFINITION_COUNT_METADATA = "item_definition_count";

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

    Consumer<List<DMNIncludedModel>> wrap(final Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        return dmnIncludeModels -> assetListConsumer.accept(dmnIncludeModels
                                                                    .stream()
                                                                    .filter(this::isNotExisting)
                                                                    .filter(this::isNotCurrentDiagram)
                                                                    .map(this::asKieAsset)
                                                                    .collect(Collectors.toList()));
    }

    private boolean isNotExisting(final DMNIncludedModel data) {
        return modelsIndex
                .getIndexedImports()
                .stream()
                .noneMatch(anImport -> Objects.equals(data.getNamespace(), anImport.getNamespace()));
    }

    private boolean isNotCurrentDiagram(final DMNIncludedModel data) {
        return !Objects.equals(data.getNamespace(), pageState.getCurrentDiagramNamespace());
    }

    KieAssetsDropdownItem asKieAsset(final DMNIncludedModel dmnIncludedModel) {

        final String text = dmnIncludedModel.getModelName();
        final String subText = dmnIncludedModel.getModelPackage();
        final String value = dmnIncludedModel.getNamespace();
        final Map<String, String> metaData = buildMetaData(dmnIncludedModel);

        return new KieAssetsDropdownItem(text, subText, value, metaData);
    }

    private Map<String, String> buildMetaData(final DMNIncludedModel dmnIncludedModel) {
        return new Maps
                .Builder<String, String>()
                .put(PATH_METADATA, dmnIncludedModel.getPath())
                .put(DRG_ELEMENT_COUNT_METADATA, dmnIncludedModel.getDrgElementsCount().toString())
                .put(ITEM_DEFINITION_COUNT_METADATA, dmnIncludedModel.getItemDefinitionsCount().toString())
                .build();
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.dmn.client.editors.included.modal.dropdown;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItemsProvider;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DMNAssetsDropdownItemsProvider implements KieAssetsDropdownItemsProvider {

    public static final String PATH_METADATA = "path";

    public static final String IMPORT_TYPE_METADATA = "import_type";

    public static final String DRG_ELEMENT_COUNT_METADATA = "drg_element_count";

    public static final String ITEM_DEFINITION_COUNT_METADATA = "item_definition_count";

    public static final String PMML_MODEL_COUNT_METADATA = "pmml_model_count";

    private final DMNIncludeModelsClient client;

    private final IncludedModelsPageState pageState;

    private final IncludedModelsIndex modelsIndex;

    private final SessionManager sessionManager;

    @Inject
    public DMNAssetsDropdownItemsProvider(final DMNIncludeModelsClient client,
                                          final IncludedModelsPageState pageState,
                                          final IncludedModelsIndex modelsIndex,
                                          final SessionManager sessionManager) {
        this.client = client;
        this.pageState = pageState;
        this.modelsIndex = modelsIndex;
        this.sessionManager = sessionManager;
    }

    @Override
    public void getItems(final Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        client.loadModels(getDMNModelPath(),
                          wrap(assetListConsumer));
    }

    Path getDMNModelPath() {
        return sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata().getPath();
    }

    Consumer<List<IncludedModel>> wrap(final Consumer<List<KieAssetsDropdownItem>> assetListConsumer) {
        return dmnIncludeModels -> assetListConsumer.accept(dmnIncludeModels
                                                                    .stream()
                                                                    .filter(this::isNotExisting)
                                                                    .filter(this::isNotCurrentDiagram)
                                                                    .map(this::asKieAsset)
                                                                    .collect(Collectors.toList()));
    }

    private boolean isNotExisting(final IncludedModel data) {
        return modelsIndex
                .getIndexedImports()
                .stream()
                .noneMatch(anImport -> {
                    //It might be possible to import the _same_ DMN diagram that is in different files
                    //therefore check on the DMN diagrams' Namespace. However PMML files do not have a Namespace
                    //and therefore we need to fallback to the imports' URI.
                    if (data instanceof DMNIncludedModel) {
                        return Objects.equals(((DMNIncludedModel) data).getNamespace(), anImport.getNamespace());
                    }
                    return Objects.equals(data.getPath(), anImport.getLocationURI().getValue());
                });
    }

    private boolean isNotCurrentDiagram(final IncludedModel data) {
        // The list of IncludedModels returned from the backend can include that currently being authored.
        // However it is impossible for a PMML IncludedModel to be that being authored.
        if (data instanceof DMNIncludedModel) {
            return !Objects.equals(((DMNIncludedModel) data).getNamespace(), pageState.getCurrentDiagramNamespace());
        }
        return true;
    }

    KieAssetsDropdownItem asKieAsset(final IncludedModel includedModel) {

        final String text = includedModel.getModelName();
        final String subText = includedModel.getModelPackage();
        final String value = getKieAssetValue(includedModel);
        final Map<String, String> metaData = buildMetaData(includedModel);

        return new KieAssetsDropdownItem(text, subText, value, metaData);
    }

    private String getKieAssetValue(final IncludedModel includedModel) {
        if (includedModel instanceof DMNIncludedModel) {
            return ((DMNIncludedModel) includedModel).getNamespace();
        }
        return includedModel.getModelName();
    }

    private Map<String, String> buildMetaData(final IncludedModel includedModel) {
        if (includedModel instanceof DMNIncludedModel) {
            final DMNIncludedModel idm = (DMNIncludedModel) includedModel;
            return Stream.of(new AbstractMap.SimpleEntry<>(PATH_METADATA, includedModel.getPath()),
                             new AbstractMap.SimpleEntry<>(IMPORT_TYPE_METADATA, includedModel.getImportType()),
                             new AbstractMap.SimpleEntry<>(DRG_ELEMENT_COUNT_METADATA, idm.getDrgElementsCount().toString()),
                             new AbstractMap.SimpleEntry<>(ITEM_DEFINITION_COUNT_METADATA, idm.getItemDefinitionsCount().toString()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else if (includedModel instanceof PMMLIncludedModel) {
            final PMMLIncludedModel ipm = (PMMLIncludedModel) includedModel;
            return Stream.of(new AbstractMap.SimpleEntry<>(PATH_METADATA, includedModel.getPath()),
                             new AbstractMap.SimpleEntry<>(IMPORT_TYPE_METADATA, includedModel.getImportType()),
                             new AbstractMap.SimpleEntry<>(PMML_MODEL_COUNT_METADATA, ipm.getModelCount().toString()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Collections.emptyMap();
    }
}

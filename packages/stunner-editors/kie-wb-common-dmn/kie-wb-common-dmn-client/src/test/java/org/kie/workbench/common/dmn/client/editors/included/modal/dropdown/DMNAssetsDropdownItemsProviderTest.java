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

import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.DMN;
import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.PMML;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.DRG_ELEMENT_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.IMPORT_TYPE_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.ITEM_DEFINITION_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PATH_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PMML_MODEL_COUNT_METADATA;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNAssetsDropdownItemsProviderTest {

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private IncludedModelsPageState pageState;

    @Mock
    private IncludedModelsIndex modelsIndex;

    @Mock
    private Consumer<List<IncludedModel>> wrappedConsumer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private Path path;

    private DMNAssetsDropdownItemsProvider itemsProvider;

    @Before
    public void setup() {
        itemsProvider = spy(new DMNAssetsDropdownItemsProvider(client,
                                                               pageState,
                                                               modelsIndex,
                                                               sessionManager));
        doReturn(path).when(itemsProvider).getDMNModelPath();
    }

    @Test
    public void testGetItems() {

        final Consumer<List<KieAssetsDropdownItem>> assetListConsumer = (l) -> {/* Nothing. */};
        doReturn(wrappedConsumer).when(itemsProvider).wrap(assetListConsumer);

        itemsProvider.getItems(assetListConsumer);

        verify(client).loadModels(eq(path),
                                  eq(wrappedConsumer));
    }

    @Test
    public void testWrapDMNItems() {

        final IncludedModel includedModel1 = makeDMNIncludedModel(1);
        final IncludedModel includedModel2 = makeDMNIncludedModel(2);
        final IncludedModel includedModel3 = makeDMNIncludedModel(3);
        final IncludedModel includedModel4 = makeDMNIncludedModel(4);
        final IncludedModel includedModel5 = makeDMNIncludedModel(5);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final KieAssetsDropdownItem dropdownItem1 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem5 = mock(KieAssetsDropdownItem.class);
        final List<IncludedModel> t = asList(includedModel1, includedModel2, includedModel3, includedModel4, includedModel5);

        when(import1.getNamespace()).thenReturn("://namespace3");
        when(import2.getNamespace()).thenReturn("://namespace4");
        when(modelsIndex.getIndexedImports()).thenReturn(asList(import1, import2));
        when(pageState.getCurrentDiagramNamespace()).thenReturn("://namespace2");
        doReturn(dropdownItem1).when(itemsProvider).asKieAsset(includedModel1);
        doReturn(dropdownItem5).when(itemsProvider).asKieAsset(includedModel5);

        itemsProvider.wrap(actualList -> {
            //IncludedModel3 and IncludedModel4 are already imported. IncludedModel2 is the current diagram so only expect 1 and 5.
            final List<KieAssetsDropdownItem> expectedList = asList(dropdownItem1, dropdownItem5);
            assertEquals(expectedList, actualList);
        }).accept(t);
    }

    private DMNIncludedModel makeDMNIncludedModel(final int id) {
        return makeDMNIncludedModel(id, 0, 0);
    }

    private DMNIncludedModel makeDMNIncludedModel(final int id,
                                                  final int drgElementsCount,
                                                  final int itemDefinitionsCount) {
        return new DMNIncludedModel("name" + id,
                                    "com.kie.dmn",
                                    "/src/main/kie" + id,
                                    "://namespace" + id,
                                    DMN.getDefaultNamespace(),
                                    drgElementsCount,
                                    itemDefinitionsCount);
    }

    @Test
    public void testWrapPMMLItems() {

        final IncludedModel includedModel1 = makePMMLIncludedModel(1);
        final IncludedModel includedModel2 = makePMMLIncludedModel(2);
        final IncludedModel includedModel3 = makePMMLIncludedModel(3);
        final IncludedModel includedModel4 = makePMMLIncludedModel(4);
        final IncludedModel includedModel5 = makePMMLIncludedModel(5);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final LocationURI import1URI = new LocationURI("src/main/kie3.pmml");
        final LocationURI import2URI = new LocationURI("src/main/kie4.pmml");
        final KieAssetsDropdownItem dropdownItem1 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem2 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem5 = mock(KieAssetsDropdownItem.class);
        final List<IncludedModel> t = asList(includedModel1, includedModel2, includedModel3, includedModel4, includedModel5);

        when(import1.getLocationURI()).thenReturn(import1URI);
        when(import2.getLocationURI()).thenReturn(import2URI);
        when(modelsIndex.getIndexedImports()).thenReturn(asList(import1, import2));
        doReturn(dropdownItem1).when(itemsProvider).asKieAsset(includedModel1);
        doReturn(dropdownItem2).when(itemsProvider).asKieAsset(includedModel2);
        doReturn(dropdownItem5).when(itemsProvider).asKieAsset(includedModel5);

        itemsProvider.wrap(actualList -> {
            //IncludedModel3 and IncludedModel4 are already imported so only expect 1, 2 and 5.
            final List<KieAssetsDropdownItem> expectedList = asList(dropdownItem1, dropdownItem2, dropdownItem5);
            assertEquals(expectedList, actualList);
        }).accept(t);
    }

    private PMMLIncludedModel makePMMLIncludedModel(final int id) {
        return makePMMLIncludedModel(id, 0);
    }

    private PMMLIncludedModel makePMMLIncludedModel(final int id,
                                                    final int modelCount) {
        return new PMMLIncludedModel("name" + id,
                                     "com.kie.pmml",
                                     "src/main/kie" + id + ".pmml",
                                     PMML.getDefaultNamespace(),
                                     "https://kie.org/pmml#src/main/kie" + id + ".pmml",
                                     modelCount);
    }

    @Test
    public void testAsKieAssetForDMNIncludedModel() {
        final DMNIncludedModel model = makeDMNIncludedModel(1, 2, 3);

        final KieAssetsDropdownItem dropdownItem = itemsProvider.asKieAsset(model);

        assertEquals(model.getModelName(), dropdownItem.getText());
        assertEquals(model.getModelPackage(), dropdownItem.getSubText());
        assertEquals(model.getNamespace(), dropdownItem.getValue());
        assertEquals(model.getPath(), dropdownItem.getMetaData().get(PATH_METADATA));
        assertEquals(model.getImportType(), dropdownItem.getMetaData().get(IMPORT_TYPE_METADATA));
        assertEquals(model.getDrgElementsCount().toString(), dropdownItem.getMetaData().get(DRG_ELEMENT_COUNT_METADATA));
        assertEquals(model.getItemDefinitionsCount().toString(), dropdownItem.getMetaData().get(ITEM_DEFINITION_COUNT_METADATA));
    }

    @Test
    public void testAsKieAssetForPMMLIncludedModel() {
        final PMMLIncludedModel model = makePMMLIncludedModel(1, 2);

        final KieAssetsDropdownItem dropdownItem = itemsProvider.asKieAsset(model);

        assertEquals(model.getModelName(), dropdownItem.getText());
        assertEquals(model.getModelPackage(), dropdownItem.getSubText());
        assertEquals(model.getPath(), dropdownItem.getMetaData().get(PATH_METADATA));
        assertEquals(model.getImportType(), dropdownItem.getMetaData().get(IMPORT_TYPE_METADATA));
        assertEquals(model.getModelCount().toString(), dropdownItem.getMetaData().get(PMML_MODEL_COUNT_METADATA));
    }
}

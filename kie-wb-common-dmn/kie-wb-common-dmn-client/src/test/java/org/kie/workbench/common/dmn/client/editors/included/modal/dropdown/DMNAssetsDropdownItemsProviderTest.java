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
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
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
    private Consumer<List<DMNIncludeModel>> wrappedConsumer;

    private DMNAssetsDropdownItemsProvider itemsProvider;

    @Before
    public void setup() {
        itemsProvider = spy(new DMNAssetsDropdownItemsProvider(client, pageState, modelsIndex));
    }

    @Test
    public void testGetItems() {

        final Consumer<List<KieAssetsDropdownItem>> assetListConsumer = (l) -> {/* Nothing. */};
        doReturn(wrappedConsumer).when(itemsProvider).wrap(assetListConsumer);

        itemsProvider.getItems(assetListConsumer);

        verify(client).loadModels(wrappedConsumer);
    }

    @Test
    public void testWrap() {

        final DMNIncludeModel dmnIncludeModel1 = new DMNIncludeModel("name1", "com.kie.dmn", "/src/main/kie1", "://namespace1");
        final DMNIncludeModel dmnIncludeModel2 = new DMNIncludeModel("name2", "com.kie.dmn", "/src/main/kie2", "://namespace2");
        final DMNIncludeModel dmnIncludeModel3 = new DMNIncludeModel("name3", "com.kie.dmn", "/src/main/kie3", "://namespace3");
        final DMNIncludeModel dmnIncludeModel4 = new DMNIncludeModel("name4", "com.kie.dmn", "/src/main/kie4", "://namespace4");
        final DMNIncludeModel dmnIncludeModel5 = new DMNIncludeModel("name5", "com.kie.dmn", "/src/main/kie5", "://namespace5");
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final KieAssetsDropdownItem dropdownItem1 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem5 = mock(KieAssetsDropdownItem.class);
        final List<DMNIncludeModel> t = asList(dmnIncludeModel1, dmnIncludeModel2, dmnIncludeModel3, dmnIncludeModel4, dmnIncludeModel5);

        when(import1.getNamespace()).thenReturn("://namespace3");
        when(import2.getNamespace()).thenReturn("://namespace4");
        when(modelsIndex.getIndexedImports()).thenReturn(asList(import1, import2));
        when(pageState.getCurrentDiagramNamespace()).thenReturn("://namespace2");
        doReturn(dropdownItem1).when(itemsProvider).asKieAsset(dmnIncludeModel1);
        doReturn(dropdownItem5).when(itemsProvider).asKieAsset(dmnIncludeModel5);

        itemsProvider.wrap(actualList -> {
            final List<KieAssetsDropdownItem> expectedList = asList(dropdownItem1, dropdownItem5);
            assertEquals(expectedList, actualList);
        }).accept(t);
    }

    @Test
    public void testAsKieAsset() {

        final DMNIncludeModel model = new DMNIncludeModel("name1", "com.kie.dmn", "/src/main/kie1", "://namespace1");

        final KieAssetsDropdownItem dropdownItem = itemsProvider.asKieAsset(model);

        assertEquals(model.getModelName(), dropdownItem.getText());
        assertEquals(model.getModelPackage(), dropdownItem.getSubText());
        assertEquals(model.getNamespace(), dropdownItem.getValue());
        assertEquals(model.getPath(), dropdownItem.getMetaData().get("path"));
    }
}

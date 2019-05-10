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

package org.kie.workbench.common.dmn.client.editors.included.modal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.decision.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdown;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal.WIDTH;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.DRG_ELEMENT_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.ITEM_DEFINITION_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PATH_METADATA;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelModalTest {

    @Mock
    private IncludedModelModal.View view;

    @Mock
    private DMNAssetsDropdown dropdown;

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private IncludedModelsPagePresenter grid;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private EventSourceMock<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    private EventSourceMock<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    @Captor
    private ArgumentCaptor<RefreshDataTypesListEvent> refreshDataTypesListArgumentCaptor;

    private IncludedModelModalFake modal;

    @Before
    public void setup() {
        modal = spy(new IncludedModelModalFake(view, dropdown, recordEngine));
        modal.init(grid);
    }

    @Test
    public void testSetup() {

        final HTMLElement htmlElement = mock(HTMLElement.class);

        doReturn(htmlElement).when(modal).getInitializedDropdownElement();
        doNothing().when(modal).superSetup();
        doNothing().when(modal).setWidth(WIDTH);

        modal.setup();

        verify(modal).superSetup();
        verify(modal).setWidth(WIDTH);
        verify(view).init(modal);
        verify(view).setupAssetsDropdown(htmlElement);
    }

    @Test
    public void testShow() {
        doNothing().when(modal).superShow();

        modal.show();

        verify(dropdown).loadAssets();
        verify(view).initialize();
        verify(view).disableIncludeButton();
        verify(modal).superShow();
    }

    @Test
    public void testGetInitializedDropdownElement() {

        final Command onValueChanged = mock(Command.class);
        final HTMLElement expectedElement = mock(HTMLElement.class);
        doReturn(onValueChanged).when(modal).getOnValueChanged();
        when(dropdown.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = modal.getInitializedDropdownElement();

        verify(dropdown).initialize();
        verify(dropdown).registerOnChangeHandler(onValueChanged);
        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testInclude() {

        final KieAssetsDropdownItem dropdownItem = mock(KieAssetsDropdownItem.class);
        final IncludedModel includedModel = mock(IncludedModel.class);

        when(dropdown.getValue()).thenReturn(Optional.of(dropdownItem));
        doReturn(includedModel).when(modal).createIncludedModel(any());
        doNothing().when(modal).hide();

        modal.include();

        verify(modal).createIncludedModel(dropdownItem);
        verify(grid).refresh();
        verify(modal).hide();
        verify(refreshDecisionComponentsEvent).fire(any(RefreshDecisionComponents.class));
        verify(modal).refreshDataTypesList(includedModel);
    }

    @Test
    public void testRefreshDataTypesList() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final Consumer<List<ItemDefinition>> listConsumer = list -> {/* Nothing. */};
        final String modelName = "model1";
        final String namespace = "://namespace1";

        when(includedModel.getName()).thenReturn(modelName);
        when(includedModel.getNamespace()).thenReturn(namespace);
        doReturn(listConsumer).when(modal).getItemDefinitionConsumer();

        modal.refreshDataTypesList(includedModel);

        verify(client).loadItemDefinitionsByNamespace(modelName, namespace, listConsumer);
    }

    @Test
    public void testGetItemDefinitionConsumer() {

        final ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        final List<ItemDefinition> expectedNewItemDefinitions = asList(itemDefinition1, itemDefinition2);

        modal.getItemDefinitionConsumer().accept(expectedNewItemDefinitions);

        verify(refreshDataTypesListEvent).fire(refreshDataTypesListArgumentCaptor.capture());

        final List<ItemDefinition> actualNewItemDefinitions = refreshDataTypesListArgumentCaptor.getValue().getNewItemDefinitions();

        assertEquals(expectedNewItemDefinitions, actualNewItemDefinitions);
    }

    @Test
    public void testHide() {
        doNothing().when(modal).superHide();

        modal.hide();

        verify(modal).superHide();
        verify(dropdown).clear();
    }

    @Test
    public void testOnValueChangedWhenValuesAreValid() {
        doReturn(true).when(modal).isValidValues();

        modal.getOnValueChanged().execute();

        verify(view).enableIncludeButton();
    }

    @Test
    public void testOnValueChangedWhenValuesAreNotValid() {
        doReturn(false).when(modal).isValidValues();

        modal.getOnValueChanged().execute();

        verify(view).disableIncludeButton();
    }

    @Test
    public void testIsValidValuesWhenModelNameIsBlank() {
        when(view.getModelNameInput()).thenReturn("");
        assertFalse(modal.isValidValues());
    }

    @Test
    public void testIsValidValuesWhenDropDownIsNotPresent() {
        when(view.getModelNameInput()).thenReturn("name");
        when(dropdown.getValue()).thenReturn(Optional.empty());
        assertFalse(modal.isValidValues());
    }

    @Test
    public void testIsValidValuesWhenItReturnsTrue() {
        when(view.getModelNameInput()).thenReturn("name");
        when(dropdown.getValue()).thenReturn(Optional.of(mock(KieAssetsDropdownItem.class)));
        assertTrue(modal.isValidValues());
    }

    @Test
    public void testCreateIncludedModel() {

        final String name = "file.dmn";
        final String value = "://namespace";
        final String path = "/src/path/file";
        final String anPackage = "path.file.com";
        final Integer expectedDrgElementsCount = 2;
        final Integer expectedDataTypesCount = 3;
        final IncludedModel includedModel = spy(new IncludedModel(recordEngine));
        final Map<String, String> metaData = new Maps.Builder<String, String>()
                .put(PATH_METADATA, path)
                .put(DRG_ELEMENT_COUNT_METADATA, expectedDrgElementsCount.toString())
                .put(ITEM_DEFINITION_COUNT_METADATA, expectedDataTypesCount.toString())
                .build();

        when(view.getModelNameInput()).thenReturn(name);
        doReturn(includedModel).when(modal).createIncludedModel();

        modal.createIncludedModel(new KieAssetsDropdownItem(name, anPackage, value, metaData));

        assertEquals(name, includedModel.getName());
        assertEquals(value, includedModel.getNamespace());
        assertEquals(path, includedModel.getPath());
        assertEquals(expectedDrgElementsCount, includedModel.getDrgElementsCount());
        assertEquals(expectedDataTypesCount, includedModel.getDataTypesCount());
        verify(includedModel).create();
    }

    class IncludedModelModalFake extends IncludedModelModal {

        IncludedModelModalFake(final View view,
                               final DMNAssetsDropdown dropdown,
                               final ImportRecordEngine recordEngine) {
            super(view, dropdown, recordEngine, client, refreshDataTypesListEvent, refreshDecisionComponentsEvent);
        }

        @Override
        protected void setWidth(final String width) {
            // empty.
        }
    }
}

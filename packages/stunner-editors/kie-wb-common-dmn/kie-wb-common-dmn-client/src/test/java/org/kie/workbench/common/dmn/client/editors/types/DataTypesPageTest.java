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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.List;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(RootPanel.class)
public class DataTypesPageTest {

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private ItemDefinitionStore definitionStore;

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private DataTypeManagerStackStore stackIndex;

    @Mock
    private FlashMessages flashMessages;

    @Mock
    private DataTypeSearchBar searchBar;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private HTMLDivElement pageView;

    @Mock
    private DataTypeShortcuts dataTypeShortcuts;

    @Captor
    private ArgumentCaptor<List<DataType>> dataTypesCaptor;

    private DataTypesPageFake page;

    @Before
    public void setup() {
        page = spy(new DataTypesPageFake(dataTypeList,
                                         itemDefinitionUtils,
                                         definitionStore,
                                         dataTypeStore,
                                         dataTypeManager,
                                         stackIndex,
                                         flashMessages,
                                         searchBar,
                                         dmnGraphUtils,
                                         translationService,
                                         dataTypeShortcuts,
                                         pageView) {

            protected void setupPageCSSClass(final String cssClass) {
                // Do nothing.
            }
        });
    }

    @Test
    public void testInit() {

        page.init();

        verify(dataTypeShortcuts).init(dataTypeList);
    }

    @Test
    public void testOnFocusWhenPageIsLoaded() {

        doReturn(true).when(page).isLoaded();

        page.onFocus();

        verify(page, never()).reload();
        verify(page).refreshPageView();
        //verify(dataTypeList, times(1)).activateReactComponents();
    }

    @Test
    public void testOnFocusWhenPageIsNotLoaded() {

        doReturn(false).when(page).isLoaded();

        page.onFocus();

        verify(page).reload();
        verify(page).refreshPageView();
        //verify(dataTypeList, times(1)).activateReactComponents();
    }

    @Test
    public void testOnLostFocus() {
        page.onLostFocus();

        verify(flashMessages).hideMessages();
    }

    @Test
    public void testReload() {

        final String expected = "dmnModelNamespace";

        doReturn(expected).when(page).currentDMNModelNamespace();

        page.reload();

        final String actual = page.getLoadedDMNModelNamespace();

        verify(page).cleanDataTypeStore();
        verify(page).loadDataTypes();
        verify(page, times(1)).enableShortcuts();

        assertEquals(expected, actual);
    }

    @Test
    public void testRefreshPageView() {

        final HTMLElement flashMessagesElement = mock(HTMLElement.class);
        final HTMLElement treeListElement = mock(HTMLElement.class);
        final Element element = mock(Element.class);
        pageView.firstChild = element;

        when(pageView.removeChild(element)).then(a -> {
            pageView.firstChild = null;
            return element;
        });

        when(flashMessages.getElement()).thenReturn(flashMessagesElement);
        when(dataTypeList.getElement()).thenReturn(treeListElement);

        page.refreshPageView();

        verify(pageView).removeChild(element);
        verify(pageView).appendChild(flashMessagesElement);
        verify(pageView).appendChild(treeListElement);
    }

    @Test
    public void testIsLoadedWhenItIsNotLoaded() {

        doReturn("dmnModelNamespace1").when(page).currentDMNModelNamespace();
        doReturn("dmnModelNamespace2").when(page).getLoadedDMNModelNamespace();

        assertFalse(page.isLoaded());
    }

    @Test
    public void testIsLoadedWhenItIsLoaded() {

        doReturn("dmnModelNamespace1").when(page).currentDMNModelNamespace();
        doReturn("dmnModelNamespace1").when(page).getLoadedDMNModelNamespace();

        assertTrue(page.isLoaded());
    }

    @Test
    public void testCurrentDMNModelNamespaceWhenDefinitionsIsNull() {

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(null);

        final String actual = page.currentDMNModelNamespace();
        final String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testCurrentDMNModelNamespaceWhenNamespaceIsNull() {

        final Definitions definitions = mock(Definitions.class);

        when(definitions.getNamespace()).thenReturn(null);
        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);

        final String actual = page.currentDMNModelNamespace();
        final String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testCurrentDMNModelNamespace() {

        final Definitions definitions = mock(Definitions.class);
        final Text text = mock(Text.class);
        final String expected = "currentDMNModelNamespace";

        when(text.getValue()).thenReturn(expected);
        when(definitions.getNamespace()).thenReturn(text);
        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);

        final String actual = page.currentDMNModelNamespace();

        assertEquals(expected, actual);
    }

    @Test
    public void testCleanDataTypeStore() {
        page.cleanDataTypeStore();

        verify(definitionStore).clear();
        verify(dataTypeStore).clear();
        verify(stackIndex).clear();
        verify(searchBar).reset();
    }

    @Test
    public void testLoadDataTypes() {

        final ItemDefinition itemDefinition1 = makeItem("itemDefinition1");
        final ItemDefinition itemDefinition2 = makeItem("itemDefinition2");
        final ItemDefinition itemDefinition3 = makeItem("itemDefinition3");
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);

        final List<ItemDefinition> itemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3);

        when(itemDefinitionUtils.all()).thenReturn(itemDefinitions);
        doReturn(dataType1).when(page).makeDataType(itemDefinition1);
        doReturn(dataType2).when(page).makeDataType(itemDefinition2);
        doReturn(dataType3).when(page).makeDataType(itemDefinition3);

        page.loadDataTypes();

        verify(dataTypeList).setupItems(dataTypesCaptor.capture());

        final List<DataType> dataTypes = dataTypesCaptor.getValue();

        assertThat(dataTypes).containsExactly(dataType1, dataType2, dataType3);
    }

    @Test
    public void testMakeDataType() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final DataType expectedDataType = mock(DataType.class);

        when(dataTypeManager.from(itemDefinition)).thenReturn(dataTypeManager);
        when(dataTypeManager.get()).thenReturn(expectedDataType);

        final DataType actualDataType = page.makeDataType(itemDefinition);

        assertEquals(expectedDataType, actualDataType);
    }

    @Test
    public void testOnDataTypePageNavTabActiveEvent() {

        page.onDataTypePageNavTabActiveEvent(mock(DataTypePageTabActiveEvent.class));

        verify(page).onFocus();
    }

    @Test
    public void testEnableShortcuts() {

        page.enableShortcuts();

        verify(dataTypeShortcuts).setup();
    }

    @Test
    public void testOnRefreshDataTypesListWithNewItemDefinitionWhenPageIsNotLoaded() {

        final RefreshDataTypesListEvent event = new RefreshDataTypesListEvent(emptyList());

        doReturn(false).when(page).isLoaded();

        page.onRefreshDataTypesListWithNewItemDefinitions(event);

        verify(page, never()).refreshItemDefinitions(any());
        verify(page, never()).reload();
    }

    @Test
    public void testOnRefreshDataTypesListWithNewItemDefinitionWhenPageIsLoaded() {

        final ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        final List<ItemDefinition> newItemDefinitions = asList(itemDefinition1, itemDefinition2);
        final RefreshDataTypesListEvent event = new RefreshDataTypesListEvent(newItemDefinitions);

        doReturn(true).when(page).isLoaded();

        page.onRefreshDataTypesListWithNewItemDefinitions(event);

        verify(page).refreshItemDefinitions(newItemDefinitions);
        verify(page).reload();
    }

    @Test
    public void testDisableShortcuts() {

        page.disableShortcuts();

        verify(dataTypeShortcuts).teardown();
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }

    private class DataTypesPageFake extends DataTypesPage {

        DataTypesPageFake(final DataTypeList treeList,
                          final ItemDefinitionUtils itemDefinitionUtils,
                          final ItemDefinitionStore definitionStore,
                          final DataTypeStore dataTypeStore,
                          final DataTypeManager dataTypeManager,
                          final DataTypeManagerStackStore stackIndex,
                          final FlashMessages flashMessages,
                          final DataTypeSearchBar searchBar,
                          final DMNGraphUtils dmnGraphUtils,
                          final TranslationService translationService,
                          final DataTypeShortcuts dataTypeShortcuts,
                          final HTMLDivElement pageView) {
            super(treeList, itemDefinitionUtils, definitionStore, dataTypeStore, dataTypeManager, stackIndex, flashMessages, searchBar, dmnGraphUtils, translationService, dataTypeShortcuts, pageView);
        }

        @Override
        protected void setupPageCSSClass(final String cssClass) {
            super.setupPageCSSClass(cssClass);
        }
    }
}


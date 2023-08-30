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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeSearchBarTest {

    @Mock
    private DataTypeSearchBar.View view;

    @Mock
    private DataTypeSearchEngine searchEngine;

    @Mock
    private DataTypeList dataTypeList;

    private DataTypeSearchBar searchBar;

    @Before
    public void setup() {
        searchBar = spy(new DataTypeSearchBar(view, searchEngine, dataTypeList));
    }

    @Test
    public void testSetup() {
        searchBar.setup();

        verify(view).init(searchBar);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = searchBar.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRefresh() {

        final String currentSearch = "currentSearch";

        doReturn(currentSearch).when(searchBar).getCurrentSearch();

        searchBar.refresh();

        verify(searchBar).search(currentSearch);
    }

    @Test
    public void testReset() {
        searchBar.reset();

        verify(searchBar).setCurrentSearch("");
        verify(dataTypeList).showListItems();
        verify(view).resetSearchBar();
        verify(searchBar).restoreDataTypeListPositions();
    }

    @Test
    public void testSearchWhenKeywordIsNotEmpty() {

        final String keyword = "keyword";
        final List<DataType> results = asList(mock(DataType.class), mock(DataType.class));

        when(searchEngine.search(keyword)).thenReturn(results);

        searchBar.search(keyword);

        verify(searchBar).storeDataTypeListPositions();
        verify(dataTypeList).showListItems();
        verify(searchBar).setCurrentSearch(keyword);
        verify(view).showSearchResults(results);
    }

    @Test
    public void testSearchWhenTheSearchResultIsEmpty() {

        final String keyword = "keyword";
        final List<DataType> results = emptyList();

        when(searchEngine.search(keyword)).thenReturn(results);

        searchBar.search(keyword);

        verify(searchBar).storeDataTypeListPositions();
        verify(dataTypeList).showNoDataTypesFound();
        verify(searchBar).setCurrentSearch(keyword);
        verify(view).showSearchResults(results);
    }

    @Test
    public void testSearchWhenKeywordIsEmpty() {

        final String keyword = "";

        when(searchEngine.search(keyword)).thenReturn(emptyList());

        searchBar.search(keyword);

        verify(searchBar).storeDataTypeListPositions();
        verify(dataTypeList).showNoDataTypesFound();
        verify(searchBar, times(2)).setCurrentSearch(keyword);
        verify(dataTypeList).showListItems();
        verify(view).resetSearchBar();
    }

    @Test
    public void testSearchWhenKeywordIsNull() {

        final String keyword = null;

        when(searchEngine.search(keyword)).thenReturn(emptyList());

        searchBar.search(keyword);

        verify(searchBar).storeDataTypeListPositions();
        verify(dataTypeList).showNoDataTypesFound();
        verify(searchBar).setCurrentSearch(keyword);
        verify(dataTypeList).showListItems();
        verify(view).resetSearchBar();
    }

    @Test
    public void testIsEnabledWhenItReturnsTrue() {

        doReturn("something").when(searchBar).getCurrentSearch();

        assertTrue(searchBar.isEnabled());
    }

    @Test
    public void testIsEnabledWhenItReturnsFalse() {

        doReturn("").when(searchBar).getCurrentSearch();

        assertFalse(searchBar.isEnabled());
    }

    @Test
    public void testGetResultsContainer() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(dataTypeList.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = searchBar.getResultsContainer();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRestoreDataTypeListPositionsWhenSearchBarHasDataTypeListPositionsStored() {

        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataTypeListItem list0 = mock(DataTypeListItem.class);
        final DataTypeListItem list1 = mock(DataTypeListItem.class);
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final DNDListComponent dndListComponent = mock(DNDListComponent.class);
        final List<DataTypeListItem> items = asList(list0, list1);
        final String uuid0 = "0000-0000-0000-0000";
        final String uuid1 = "1111-1111-1111-1111";
        final Integer positionY0 = 2;
        final Integer positionY1 = -1;
        final Map<String, Integer> store = spy(Stream.of(new AbstractMap.SimpleEntry<>(uuid0, positionY0),
                                                         new AbstractMap.SimpleEntry<>(uuid1, positionY1))
                                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        final Map<String, Boolean> collapsedStore = spy(Stream.of(new AbstractMap.SimpleEntry<>(uuid0, false),
                                                                  new AbstractMap.SimpleEntry<>(uuid1, true))
                                                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        element0.classList = mock(DOMTokenList.class);
        element1.classList = mock(DOMTokenList.class);
        when(dndListComponent.getPositionY(element0)).thenReturn(positionY0);
        when(dndListComponent.getPositionY(element1)).thenReturn(positionY1);
        when(list0.getDragAndDropElement()).thenReturn(element0);
        when(list1.getDragAndDropElement()).thenReturn(element1);
        when(list0.getDataType()).thenReturn(dataType0);
        when(list1.getDataType()).thenReturn(dataType1);
        when(dataType0.getUUID()).thenReturn(uuid0);
        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataTypeList.getItems()).thenReturn(items);
        when(dataTypeList.getDNDListComponent()).thenReturn(dndListComponent);
        doReturn(store).when(searchBar).getDataTypeListPositionsStore();
        doReturn(collapsedStore).when(searchBar).getDataTypeListCollapsedStatusStore();

        searchBar.restoreDataTypeListPositions();

        verify(dndListComponent).setPositionY(element0, positionY0);
        verify(dndListComponent).setPositionY(element1, positionY1);
        verify(element0.classList).remove(HIDDEN_CSS_CLASS);
        verify(element1.classList).add(HIDDEN_CSS_CLASS);
        verify(dndListComponent).refreshItemsPosition();
        verify(list0, never()).collapse();
        verify(list1).collapse();
        verify(store).clear();
        verify(collapsedStore).clear();
    }

    @Test
    public void testRestoreDataTypeListPositionsWhenSearchBarDoesNotHaveDataTypeListPositionsStored() {

        final DNDListComponent dndListComponent = mock(DNDListComponent.class);
        final Map<String, Integer> store = spy(new HashMap<>());
        final Map<String, Boolean> collapsedStore = spy(new HashMap<>());

        when(dataTypeList.getDNDListComponent()).thenReturn(dndListComponent);
        doReturn(store).when(searchBar).getDataTypeListPositionsStore();
        doReturn(collapsedStore).when(searchBar).getDataTypeListCollapsedStatusStore();

        searchBar.restoreDataTypeListPositions();

        verify(dndListComponent, never()).setPositionY(any(), anyDouble());
        verify(dndListComponent, never()).refreshItemsPosition();
        verify(store, never()).clear();
        verify(collapsedStore, never()).clear();
    }

    @Test
    public void testStoreDataTypeListPositionsWhenSearchBarDoesNotHaveDataTypeListPositionsStored() {

        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataTypeListItem list0 = mock(DataTypeListItem.class);
        final DataTypeListItem list1 = mock(DataTypeListItem.class);
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final DNDListComponent dndListComponent = mock(DNDListComponent.class);
        final List<DataTypeListItem> items = asList(list0, list1);
        final String uuid0 = "0000-0000-0000-0000";
        final String uuid1 = "1111-1111-1111-1111";
        final Integer positionY0 = 2;
        final Integer positionY1 = 4;
        final Map<String, Integer> store = new HashMap<>();

        when(dndListComponent.getPositionY(element0)).thenReturn(positionY0);
        when(dndListComponent.getPositionY(element1)).thenReturn(positionY1);
        when(list0.getDragAndDropElement()).thenReturn(element0);
        when(list1.getDragAndDropElement()).thenReturn(element1);
        when(list0.getDataType()).thenReturn(dataType0);
        when(list1.getDataType()).thenReturn(dataType1);
        when(dataType0.getUUID()).thenReturn(uuid0);
        when(dataType1.getUUID()).thenReturn(uuid1);
        when(dataTypeList.getItems()).thenReturn(items);
        when(dataTypeList.getDNDListComponent()).thenReturn(dndListComponent);
        doReturn(store).when(searchBar).getDataTypeListPositionsStore();

        searchBar.storeDataTypeListPositions();

        assertEquals(2, store.size());
        assertEquals(positionY0, store.get(uuid0));
        assertEquals(positionY1, store.get(uuid1));
    }

    @Test
    public void testStoreDataTypeListPositionsWhenSearchBarHasDataTypeListPositionsStored() {

        final String uuid0 = "0000-0000-0000-0000";
        final String uuid1 = "1111-1111-1111-1111";
        final Integer positionY0 = 2;
        final Integer positionY1 = 4;
        final Map<String, Integer> store = spy(Stream.of(new AbstractMap.SimpleEntry<>(uuid0, positionY0),
                                                         new AbstractMap.SimpleEntry<>(uuid1, positionY1))
                                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        doReturn(store).when(searchBar).getDataTypeListPositionsStore();

        searchBar.storeDataTypeListPositions();

        verify(store, never()).put(Mockito.<String>any(), any());
    }

    @Test
    public void testGetDataTypeListItemsSortedByPositionY() {

        final DataTypeListItem list0 = mock(DataTypeListItem.class);
        final DataTypeListItem list1 = mock(DataTypeListItem.class);
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final DNDListComponent dndListComponent = mock(DNDListComponent.class);
        final Integer positionY0 = 3;
        final Integer positionY1 = 2;
        final Map<String, Integer> store = new HashMap<>();

        when(dndListComponent.getPositionY(element0)).thenReturn(positionY0);
        when(dndListComponent.getPositionY(element1)).thenReturn(positionY1);
        when(list0.getDragAndDropElement()).thenReturn(element0);
        when(list1.getDragAndDropElement()).thenReturn(element1);
        when(dataTypeList.getItems()).thenReturn(asList(list0, list1));
        when(dataTypeList.getDNDListComponent()).thenReturn(dndListComponent);
        doReturn(store).when(searchBar).getDataTypeListPositionsStore();

        final List<DataTypeListItem> actualListItems = searchBar.getDataTypeListItemsSortedByPositionY();
        final List<DataTypeListItem> expectedListItems = asList(list1, list0);

        assertEquals(expectedListItems, actualListItems);
    }
}

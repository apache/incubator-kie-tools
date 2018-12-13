/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
    }

    @Test
    public void testSearchWhenKeywordIsNotEmpty() {

        final String keyword = "keyword";
        final List<DataType> results = asList(mock(DataType.class), mock(DataType.class));

        when(searchEngine.search(keyword)).thenReturn(results);

        searchBar.search(keyword);

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

        verify(dataTypeList).showNoDataTypesFound();
        verify(searchBar).setCurrentSearch(keyword);
        verify(view).showSearchResults(results);
    }

    @Test
    public void testSearchWhenKeywordIsEmpty() {

        final String keyword = "";

        when(searchEngine.search(keyword)).thenReturn(emptyList());

        searchBar.search(keyword);

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

        when(dataTypeList.getListItemsElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = searchBar.getResultsContainer();

        assertEquals(expectedElement, actualElement);
    }
}

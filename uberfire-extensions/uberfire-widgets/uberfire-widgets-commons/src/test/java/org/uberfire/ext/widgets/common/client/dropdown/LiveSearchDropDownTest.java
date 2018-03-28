/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.dropdown;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class LiveSearchDropDownTest {

    @Mock
    LiveSearchDropDownView<String> view;

    @Mock
    LiveSearchCallback<String> searchCallback;

    @Spy
    LiveSearchService<String> searchService = new LiveSearchService<String>() {

        @Override
        public void search(String pattern,
                           int maxResults,
                           LiveSearchCallback<String> callback) {
            LiveSearchResults results = new LiveSearchResults();
            switch (pattern) {
                case "a":
                    results.add("1", "a");
                    break;
                case "b":
                    results.add("1", "a");
                    results.add("2", "b");
                    results.add("3", "c");
                    break;
            }

            callback.afterSearch(results);
        }

        @Override
        public void searchEntry(String key, LiveSearchCallback<String> callback) {
            LiveSearchResults results = new LiveSearchResults();
            switch (key) {
                case "1":
                    results.add("1", "a");
                    break;
                case "2":
                    results.add("2", "b");
                    break;
            }

            callback.afterSearch(results);
        }
    };

    @Spy
    SingleLiveSearchSelectionHandler<String> selectionHandler = new SingleLiveSearchSelectionHandler<>();

    @Mock
    Command onChangeCommand;

    @Mock
    ClickEvent clickEvent;

    LiveSearchDropDown<String> presenter;

    @Mock
    ManagedInstance<LiveSearchSelectorItem<String>> selectorItems;

    @Before
    public void setUp() {

        when(selectorItems.get()).thenAnswer((Answer<LiveSearchSelectorItem<String>>) invocationOnMock -> {
            final LiveSearchSelectorItem<String> result = mock(LiveSearchSelectorItem.class);

            doAnswer((Answer<Void>) invocationOnMock1 -> {
                String key = (String) invocationOnMock1.getArguments()[0];
                String value = (String) invocationOnMock1.getArguments()[1];

                when(result.getKey()).thenReturn(key);
                when(result.getValue()).thenReturn(value);
                return null;
            }).when(result).init(any(), any());

            return result;
        });

        presenter = spy(new LiveSearchDropDown(view, selectorItems));
        presenter.setOnChange(onChangeCommand);
        presenter.init(searchService, selectionHandler);

        doAnswer(invocationOnMock -> {
            ClickEvent event = (ClickEvent) invocationOnMock.getArguments()[0];
            event.stopPropagation();
            return null;
        }).when(view).onSearchClick(any());
    }

    @Test
    public void testInit() {
        presenter.setWidth(200);
        presenter.setMaxItems(10);
        presenter.setSearchHint("a");
        presenter.setSelectorHint("b");

        assertEquals(presenter.getMaxItems(),
                     10);

        verify(view).setWidth(200);
        verify(view).setSearchHint("a");
        verify(view).setDropDownText("b");
    }

    @Test
    public void testClear() {
        presenter.clear();

        assertNull(presenter.getLastSearch());
        verify(view).clearItems();
        verify(view).clearSearch();
    }

    @Test
    public void testSearch() {
        presenter.search("a");

        assertEquals(presenter.getLastSearch(),
                     "a");

        verify(view).clearItems();
        verify(view).addItem(any());
        verify(view).searchFinished();

        verify(searchService).search(eq("a"),
                                     anyInt(),
                                     any());
    }

    @Test
    public void testEmptySearch() {
        presenter.setSearchHint("s");
        presenter.setNotFoundMessage("n");
        presenter.setMaxItems(15);
        presenter.search("");

        assertEquals(presenter.getLastSearch(),
                     "");

        verify(view).searchInProgress("s");
        verify(view).clearItems();
        verify(view).noItems("n");
        verify(view).searchFinished();

        verify(searchService).search(eq(""),
                                     eq(15),
                                     any());
    }

    @Test
    public void testRepeatedSearch() {
        presenter.search("a");
        presenter.search("a"); // 2nd search is ignored as it's a repetition

        verify(view).clearItems();
        verify(view).addItem(any());
        verify(view).searchFinished();

        verify(searchService).search(eq("a"),
                                     anyInt(),
                                     any());
    }

    @Test
    public void testSearchCache() {
        presenter.search("a");
        presenter.search("");
        presenter.search("a");
        presenter.search("");
        presenter.search("a");
        presenter.search("");

        assertTrue(presenter.isSearchCacheEnabled());
        assertEquals(presenter.getLastSearch(),
                     "");

        verify(view,
               times(2)).searchInProgress(anyString());
        verify(searchService).search(eq("a"),
                                     anyInt(),
                                     any());
        verify(searchService).search(eq(""),
                                     anyInt(),
                                     any());
        verify(view,
               times(2)).searchFinished();

        verify(view,
               times(6)).clearItems();
        verify(view,
               times(3)).addItem(any());
        verify(view,
               times(3)).noItems(anyString());
    }

    @Test
    public void testSearchCacheDisabled() {
        presenter.setSearchCacheEnabled(false);

        presenter.search("a");
        presenter.search("");
        presenter.search("a");
        presenter.search("");
        presenter.search("a");
        presenter.search("");

        assertFalse(presenter.isSearchCacheEnabled());
        assertEquals(presenter.getLastSearch(),
                     "");

        verify(view,
               times(6)).searchInProgress(anyString());
        verify(searchService,
               times(3)).search(eq("a"),
                                anyInt(),
                                any());
        verify(searchService,
               times(3)).search(eq(""),
                                anyInt(),
                                any());
        verify(view,
               times(6)).searchFinished();

        verify(view,
               times(6)).clearItems();
        verify(view,
               times(3)).addItem(any());
        verify(view,
               times(3)).noItems(anyString());
    }

    @Test
    public void testItemsOrdered() {
        presenter.search("b");

        ArgumentCaptor<LiveSearchResults> resultsCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);
        verify(presenter).showResults(resultsCaptor.capture());

        LiveSearchResults<String> results = resultsCaptor.getValue();
        assertEquals(results.size(),
                     3);
        assertEquals(results.get(0).getValue(),
                     "a");
        assertEquals(results.get(1).getValue(),
                     "b");
        assertEquals(results.get(2).getValue(),
                     "c");
    }

    @Test
    public void testItemSelected() {

        doAnswer(invocationOnMock -> {
            LiveSearchResults results = new LiveSearchResults(1);
            results.add("1", "a");
            LiveSearchCallback callback = (LiveSearchCallback) invocationOnMock.getArguments()[1];
            callback.afterSearch(results);

            return null;
        }).when(searchService).searchEntry(anyString(), any());

        presenter.setSelectedItem("1");

        assertEquals("1", selectionHandler.getSelectedKey());
        verify(view).setDropDownText("a");
        verify(onChangeCommand, never()).execute();
    }

    @Test
    public void testOnClickSearchInput() {
        view.onSearchClick(clickEvent);

        verify(clickEvent).stopPropagation();
    }
}


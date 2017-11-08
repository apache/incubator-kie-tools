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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LiveSearchDropDownTest {

    @Mock
    LiveSearchDropDownView view;

    @Mock
    LiveSearchCallback searchCallback;

    @Spy
    LiveSearchService searchService = new LiveSearchService() {

        public void search(String pattern,
                           int max,
                           LiveSearchCallback callback) {
            LiveSearchResults results = new LiveSearchResults();
            switch (pattern) {
                case "a":
                    results.add("1", "a");
                    callback.afterSearch(results);
                    break;
                case "b":
                    results.add("1", "a");
                    results.add("2", "b");
                    results.add("3", "c");
                    callback.afterSearch(results);
                    break;
                default:
                    callback.afterSearch(results);
                    break;
            }
        }
    };

    @Mock
    Command onChangeCommand;

    @Mock
    ClickEvent clickEvent;

    LiveSearchDropDown presenter;

    @Before
    public void setUp() {
        presenter = spy(new LiveSearchDropDown(view));
        presenter.setOnChange(onChangeCommand);
        presenter.setSearchService(searchService);

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
        verify(view).addItem("1", "a");
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
        verify(view).addItem("1", "a");
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
               times(3)).addItem("1", "a");
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
               times(3)).addItem("1", "a");
        verify(view,
               times(3)).noItems(anyString());
    }

    @Test
    public void testItemsOrdered() {
        presenter.search("b");

        ArgumentCaptor<LiveSearchResults> resultsCaptor = ArgumentCaptor.forClass(LiveSearchResults.class);
        verify(presenter).showResults(resultsCaptor.capture());

        LiveSearchResults results = resultsCaptor.getValue();
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
        presenter.onItemSelected("1", "a");

        assertEquals(presenter.getSelectedKey(), "1");
        assertEquals(presenter.getSelectedValue(), "a");
        verify(view).setDropDownText("a");
        verify(onChangeCommand).execute();
    }

    @Test
    public void testOnClickSearchInput() {
        view.onSearchClick(clickEvent);

        verify(clickEvent).stopPropagation();
    }
}


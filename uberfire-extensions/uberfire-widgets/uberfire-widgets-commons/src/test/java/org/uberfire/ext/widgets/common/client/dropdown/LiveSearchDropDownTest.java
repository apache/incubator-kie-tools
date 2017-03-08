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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        public void search(String p,
                           int max,
                           LiveSearchCallback c) {
            switch (p) {
                case "a":
                    c.afterSearch(Collections.singletonList("a"));
                    break;
                case "b":
                    c.afterSearch(Arrays.asList("a",
                                                "b",
                                                "c"));
                    break;
                default:
                    c.afterSearch(Collections.emptyList());
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
        verify(view).addItem("a");
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
        verify(view).addItem("a");
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
               times(3)).addItem("a");
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
               times(3)).addItem("a");
        verify(view,
               times(3)).noItems(anyString());
    }

    @Test
    public void testItemsOrdered() {
        presenter.search("b");

        ArgumentCaptor<List> itemListCaptor = ArgumentCaptor.forClass(List.class);
        verify(presenter).showItemList(itemListCaptor.capture());

        List itemList = itemListCaptor.getValue();
        assertEquals(itemList.size(),
                     3);
        assertEquals(itemList.get(0),
                     "a");
        assertEquals(itemList.get(1),
                     "b");
        assertEquals(itemList.get(2),
                     "c");
    }

    @Test
    public void testItemSelected() {
        presenter.onItemSelected("a");

        assertEquals(presenter.getSelectedItem(),
                     "a");
        verify(view).setDropDownText("a");
        verify(onChangeCommand).execute();
    }

    @Test
    public void testOnClickSearchInput() {
        view.onSearchClick(clickEvent);

        verify(clickEvent).stopPropagation();
    }
}


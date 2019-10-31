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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView.ENABLED_SEARCH;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSearchBarView_Search;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeSearchBarViewTest {

    @Mock
    private HTMLInputElement searchBar;

    @Mock
    private HTMLElement searchIcon;

    @Mock
    private HTMLButtonElement closeSearch;

    @Mock
    private DataTypeSearchBar presenter;

    @Mock
    private TranslationService translationService;

    @Captor
    private ArgumentCaptor<SetTimeoutCallbackFn> callback;

    private DataTypeSearchBarView view;

    @Before
    public void setup() {
        view = spy(new DataTypeSearchBarView(searchBar, searchIcon, closeSearch, translationService));

        view.init(presenter);

        searchIcon.classList = mock(DOMTokenList.class);
        closeSearch.classList = mock(DOMTokenList.class);
    }

    @Test
    public void testSetupSearchBar() {

        final String search = "Search...";

        when(translationService.format(DataTypeSearchBarView_Search)).thenReturn(search);

        view.setupSearchBar();

        assertEquals(searchBar.placeholder, search);
    }

    @Test
    public void testOnSearchBarCloseButton() {

        view.onSearchBarCloseButton(mock(ClickEvent.class));

        verify(presenter).reset();
    }

    @Test
    public void testOnSearchBarKeyUpEventWhenKeyPressedIsEscape() {

        final KeyUpEvent event = mock(KeyUpEvent.class);

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);

        view.onSearchBarKeyUpEvent(event);

        verify(presenter).reset();
    }

    @Test
    public void testOnSearchBarKeyUpEventWhenKeyPressedIsNotEscape() {

        final KeyUpEvent event = mock(KeyUpEvent.class);

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_CTRL);
        doNothing().when(view).search();

        view.onSearchBarKeyUpEvent(event);

        verify(view).search();
    }

    @Test
    public void testOnSearchBarKeyDownEvent() {
        view.onSearchBarKeyDownEvent(mock(KeyDownEvent.class));

        verify(view).refreshSearchBarState();
    }

    @Test
    public void testOnSearchBarChangeEvent() {
        view.onSearchBarChangeEvent(mock(ChangeEvent.class));

        verify(view).refreshSearchBarState();
    }

    @Test
    public void testShowSearchResults() {

        final DNDListComponent dndListComponent = mock(DNDListComponent.class);
        final DataType dataType0 = mock(DataType.class);
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataTypeListItem listItem0 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem1 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem2 = mock(DataTypeListItem.class);
        final DataTypeListItem listItem3 = mock(DataTypeListItem.class);
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final HTMLElement element3 = mock(HTMLElement.class);
        final List<DataType> results = asList(dataType0, dataType1, dataType2);
        final List<DataTypeListItem> listItems = asList(listItem0, listItem1, listItem2, listItem3);

        element0.classList = mock(DOMTokenList.class);
        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);
        element3.classList = mock(DOMTokenList.class);

        when(listItem0.getDataType()).thenReturn(dataType0);
        when(listItem1.getDataType()).thenReturn(dataType1);
        when(listItem2.getDataType()).thenReturn(dataType2);
        when(listItem0.getDragAndDropElement()).thenReturn(element0);
        when(listItem1.getDragAndDropElement()).thenReturn(element1);
        when(listItem2.getDragAndDropElement()).thenReturn(element2);
        when(listItem3.getDragAndDropElement()).thenReturn(element3);
        when(presenter.getDataTypeListItemsSortedByPositionY()).thenReturn(listItems);
        when(presenter.getDNDListComponent()).thenReturn(dndListComponent);
        doNothing().when(view).enableSearch();

        view.showSearchResults(results);

        verify(element0.classList).remove(HIDDEN_CSS_CLASS);
        verify(element1.classList).remove(HIDDEN_CSS_CLASS);
        verify(element2.classList).remove(HIDDEN_CSS_CLASS);
        verify(element3.classList).add(HIDDEN_CSS_CLASS);
        verify(dndListComponent).setPositionY(element0, 0);
        verify(dndListComponent).setPositionY(element1, 1);
        verify(dndListComponent).setPositionY(element2, 2);
        verify(dndListComponent).setPositionY(element3, -1);
        verify(dndListComponent).refreshItemsPosition();
        verify(view).enableSearch();
    }

    @Test
    public void testResetSearchBar() {

        searchBar.value = "something";

        doNothing().when(view).disableSearch();

        view.resetSearchBar();

        assertEquals(searchBar.value, "");
        verify(view).refreshSearchBarState();
        verify(view).disableSearch();
    }

    @Test
    public void testEnableSearch() {

        final HTMLElement resultsContainer = mock(HTMLElement.class);

        resultsContainer.classList = mock(DOMTokenList.class);
        when(presenter.getResultsContainer()).thenReturn(resultsContainer);

        view.enableSearch();

        verify(resultsContainer.classList).add(ENABLED_SEARCH);
    }

    @Test
    public void testDisableSearch() {

        final HTMLElement resultsContainer = mock(HTMLElement.class);

        resultsContainer.classList = mock(DOMTokenList.class);
        when(presenter.getResultsContainer()).thenReturn(resultsContainer);

        view.disableSearch();

        verify(resultsContainer.classList).remove(ENABLED_SEARCH);
    }

    @Test
    public void testSearch() {

        final String keyword = "keyword";

        doNothing().when(view).setTimeout(any(), anyInt());
        searchBar.value = keyword;

        view.search();

        verify(view).setTimeout(callback.capture(), eq(500d));

        callback.getValue().onInvoke();

        verify(presenter).search(keyword);
    }

    @Test
    public void testRefreshSearchBarStateWhenItsActive() {

        searchBar.value = "keyword";

        view.refreshSearchBarState();

        verify(searchIcon.classList).add(HIDDEN_CSS_CLASS);
        verify(closeSearch.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testRefreshSearchBarStateWhenItsNotActive() {

        searchBar.value = "";

        view.refreshSearchBarState();

        verify(searchIcon.classList).remove(HIDDEN_CSS_CLASS);
        verify(closeSearch.classList).add(HIDDEN_CSS_CLASS);
    }
}

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
import java.util.Optional;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView.ENABLED_SEARCH;
import static org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView.RESULT_ENTRY_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSearchBarView_Search;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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

        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);
        final List<DataType> results = asList(dataType1, dataType2);

        doReturn(Optional.of(element1)).when(view).getResultEntry(dataType1);
        doReturn(Optional.of(element2)).when(view).getResultEntry(dataType2);
        doReturn(Optional.empty()).when(view).getResultEntry(dataType3);
        doNothing().when(view).enableSearch();
        doNothing().when(view).disableResults();

        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);

        view.showSearchResults(results);

        verify(view).enableSearch();
        verify(view).disableResults();
        verify(element1.classList).add(RESULT_ENTRY_CSS_CLASS);
        verify(element2.classList).add(RESULT_ENTRY_CSS_CLASS);
    }

    @Test
    public void testResetSearchBar() {

        searchBar.value = "something";

        doNothing().when(view).disableSearch();
        doNothing().when(view).disableResults();

        view.resetSearchBar();

        assertEquals(searchBar.value, "");
        verify(view).refreshSearchBarState();
        verify(view).disableSearch();
        verify(view).disableResults();
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
    public void testDisableResults() {

        final HTMLElement resultsContainer = mock(HTMLElement.class);
        final NodeList<Element> results = spy(new NodeList<>());
        final Element element1 = mock(Element.class);
        final Element element2 = mock(Element.class);

        when(presenter.getResultsContainer()).thenReturn(resultsContainer);
        when(resultsContainer.querySelectorAll("." + RESULT_ENTRY_CSS_CLASS)).thenReturn(results);
        doReturn(element1).when(results).getAt(0);
        doReturn(element2).when(results).getAt(1);
        element1.classList = mock(DOMTokenList.class);
        element2.classList = mock(DOMTokenList.class);
        results.length = 2;

        view.disableResults();

        verify(element1.classList).remove(RESULT_ENTRY_CSS_CLASS);
        verify(element2.classList).remove(RESULT_ENTRY_CSS_CLASS);
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

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

package org.kie.workbench.common.widgets.client.search.component;

import elemental2.dom.*;
import io.crysknife.ui.translation.api.spi.TranslationService;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.gwtproject.event.dom.client.KeyCodes.KEY_ENTER;
import static org.gwtproject.event.dom.client.KeyCodes.KEY_ESCAPE;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.SearchBarComponentView_Find;
import static org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView.HIDDEN;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@RunWith(GwtMockitoTestRunner.class)
public class SearchBarComponentViewTest {

    @Mock
    private SearchBarComponent<?> presenter;

    @Mock
    private HTMLButtonElement searchContainer;

    @Mock
    private HTMLButtonElement searchButton;

    @Mock
    private HTMLButtonElement prevElement;

    @Mock
    private HTMLButtonElement nextElement;

    @Mock
    private HTMLButtonElement closeSearch;

    @Mock
    private HTMLInputElement inputElement;

    @Mock
    private HTMLElement currentResult;

    @Mock
    private HTMLElement totalOfResults;

    @Mock
    private TranslationService translationService;

    private String placeholderText = "Find...";

    private SearchBarComponentView view;

    @Before
    public void setup() {

        searchContainer.classList = mock(DOMTokenList.class);
        view = spy(new SearchBarComponentView(searchButton, searchContainer, prevElement, nextElement, closeSearch, inputElement, translationService, currentResult, totalOfResults));

        doNothing().when(view).disableSearch();
        when(translationService.format(SearchBarComponentView_Find)).thenReturn(placeholderText);

        view.init(presenter);
    }

    //@Test
    public void testInit() {
        // 'view.init(presenter);' is called in the setup method

        assertEquals(placeholderText, inputElement.placeholder);
        verify(view).disableSearch();
    }

    //@Test
    public void testOnSearchButtonClickWhenTheSearchIsEnabled() {

        final MouseEvent clickEvent = mock(MouseEvent.class);

        doCallRealMethod().when(view).disableSearch();
        when(searchContainer.classList.contains(HIDDEN)).thenReturn(false);
        inputElement.value = "something...";

        view.onSearchButtonClick(clickEvent);

        assertEquals("", inputElement.value);
        verify(searchContainer.classList).add(HIDDEN);
        verify(presenter).closeIndex();
        verify(clickEvent).preventDefault();
        verify(clickEvent).stopPropagation();
    }

    //@Test
    public void testOnSearchButtonClickWhenTheSearchIsDisabled() {

        final MouseEvent clickEvent = mock(MouseEvent.class);
        when(searchContainer.classList.contains(HIDDEN)).thenReturn(true);

        view.onSearchButtonClick(clickEvent);

        verify(searchContainer.classList).remove(HIDDEN);
        verify(inputElement).focus();
        verify(clickEvent).preventDefault();
        verify(clickEvent).stopPropagation();
    }

    //@Test
    public void testOnNextElementClick() {

        final MouseEvent clickEvent = mock(MouseEvent.class);

        view.onNextElementClick(clickEvent);

        verify(presenter).nextResult();
        verify(clickEvent).preventDefault();
        verify(clickEvent).stopPropagation();
    }

    //@Test
    public void testOnPrevElementClick() {

        final MouseEvent clickEvent = mock(MouseEvent.class);

        view.onPrevElementClick(clickEvent);

        verify(presenter).previousResult();
        verify(clickEvent).preventDefault();
        verify(clickEvent).stopPropagation();
    }

    //@Test
    public void testOnCloseSearchClick() {

        final MouseEvent clickEvent = mock(MouseEvent.class);

        view.onCloseSearchClick(clickEvent);

        verify(view, times(2)).disableSearch(); // 2 times, since 'view.init(presenter);' is called in the setup method
        verify(clickEvent).preventDefault();
        verify(clickEvent).stopPropagation();
    }

    //@Test
    public void testOnSearchInputKeyPressWhenKeyIsEnter() {

        final KeyboardEvent keyEvent = mock(KeyboardEvent.class);
        final String term = "term";
        keyEvent.code = "Enter";

        inputElement.value = term;

        view.onSearchInputKeyPress(keyEvent);

        verify(presenter).search(inputElement.value);
        verify(view, times(1)).disableSearch(); // 1 time, since 'view.init(presenter);' is called in the setup method
    }

    //@Test
    public void testOnSearchInputKeyPressWhenKeyIsEscape() {

        final KeyboardEvent keyEvent = mock(KeyboardEvent.class);
        final String term = "term";
        keyEvent.code = "Escape";

        inputElement.value = term;

        view.onSearchInputKeyPress(keyEvent);

        verify(presenter, never()).search(inputElement.value);
        verify(view, times(2)).disableSearch(); // 2 times, since 'view.init(presenter);' is called in the setup method
    }

    //@Test
    public void testSetCurrentResultNumber() {
        currentResult.textContent = "something...";
        view.setCurrentResultNumber(42);
        assertEquals("42", currentResult.textContent);
    }

    //@Test
    public void testSetTotalOfResultsNumber() {
        totalOfResults.textContent = "something...";
        view.setTotalOfResultsNumber(42);
        assertEquals("42", totalOfResults.textContent);
    }
}

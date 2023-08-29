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


package org.kie.workbench.common.widgets.client.search.component;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ESCAPE;
import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.SearchBarComponentView_Find;

@Templated
@Dependent
public class SearchBarComponentView implements SearchBarComponent.View {

    static final String HIDDEN = "hidden";

    @DataField("search-container")
    private final HTMLButtonElement searchContainer;

    @DataField("search-button")
    private final HTMLButtonElement searchButton;

    @DataField("prev-element")
    private final HTMLButtonElement prevElement;

    @DataField("next-element")
    private final HTMLButtonElement nextElement;

    @DataField("close-search")
    private final HTMLButtonElement closeSearch;

    @DataField("search-input")
    private final HTMLInputElement inputElement;

    @DataField("current-result")
    private final HTMLElement currentResult;

    @DataField("total-of-results")
    private final HTMLElement totalOfResults;

    private final TranslationService translationService;

    private SearchBarComponent<?> presenter;

    @Inject
    public SearchBarComponentView(final HTMLButtonElement searchButton,
                                  final HTMLButtonElement searchContainer,
                                  final HTMLButtonElement prevElement,
                                  final HTMLButtonElement nextElement,
                                  final HTMLButtonElement closeSearch,
                                  final HTMLInputElement inputElement,
                                  final TranslationService translationService,
                                  final @Named("span") HTMLElement currentResult,
                                  final @Named("span") HTMLElement totalOfResults) {
        this.searchButton = searchButton;
        this.searchContainer = searchContainer;
        this.prevElement = prevElement;
        this.nextElement = nextElement;
        this.closeSearch = closeSearch;
        this.inputElement = inputElement;
        this.translationService = translationService;
        this.currentResult = currentResult;
        this.totalOfResults = totalOfResults;
    }

    @Override
    public void init(final SearchBarComponent searchBarComponent) {

        presenter = searchBarComponent;
        inputElement.placeholder = translationService.format(SearchBarComponentView_Find);

        disableSearch();
    }

    @EventHandler("search-button")
    public void onSearchButtonClick(final ClickEvent clickEvent) {
        toggle();
        clickEvent.preventDefault();
        clickEvent.stopPropagation();
    }

    @EventHandler("next-element")
    public void onNextElementClick(final ClickEvent clickEvent) {
        presenter.nextResult();
        clickEvent.preventDefault();
        clickEvent.stopPropagation();
    }

    @EventHandler("prev-element")
    public void onPrevElementClick(final ClickEvent clickEvent) {
        presenter.previousResult();
        clickEvent.preventDefault();
        clickEvent.stopPropagation();
    }

    @EventHandler("close-search")
    public void onCloseSearchClick(final ClickEvent clickEvent) {
        disableSearch();
        clickEvent.preventDefault();
        clickEvent.stopPropagation();
    }

    @EventHandler("search-input")
    public void onSearchInputKeyPress(final KeyUpEvent keyEvent) {
        final int keyCode = keyEvent.getNativeKeyCode();
        switch (keyCode) {
            case KEY_ENTER:
                search(inputElement.value);
                break;
            case KEY_ESCAPE:
                disableSearch();
                break;
        }
    }

    @Override
    public void setCurrentResultNumber(final Integer currentResultNumber) {
        currentResult.textContent = currentResultNumber.toString();
    }

    @Override
    public void setTotalOfResultsNumber(final Integer totalOfResultsNumber) {
        totalOfResults.textContent = totalOfResultsNumber.toString();
    }

    @Override
    public void disableSearch() {
        searchContainer.classList.add(HIDDEN);
        inputElement.value = "";
        presenter.closeIndex();
    }

    @Override
    public void setSearchButtonVisibility(final boolean visible) {
        if (visible) {
            searchButton.classList.remove(HIDDEN);
        } else {
            searchButton.classList.add(HIDDEN);
        }
    }

    private void search(final String value) {
        presenter.search(value);
    }

    private void toggle() {
        if (isContainerHidden()) {
            enableSearch();
        } else {
            disableSearch();
        }
    }

    private void enableSearch() {
        searchContainer.classList.remove(HIDDEN);
        inputElement.focus();
    }

    private boolean isContainerHidden() {
        return searchContainer.classList.contains(HIDDEN);
    }
}

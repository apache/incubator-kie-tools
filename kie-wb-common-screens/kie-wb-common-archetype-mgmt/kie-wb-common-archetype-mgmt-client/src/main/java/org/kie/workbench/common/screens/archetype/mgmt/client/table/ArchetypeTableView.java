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

package org.kie.workbench.common.screens.archetype.mgmt.client.table;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.AbstractArchetypeTablePresenter;

@Templated
public class ArchetypeTableView implements AbstractArchetypeTablePresenter.View,
                                           IsElement {

    private static final String DISABLED_CLASS = "disabled";
    private static final String PLACE_HOLDER = "placeholder";

    private AbstractArchetypeTablePresenter presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("add-archetype-button")
    private HTMLButtonElement addArchetypeButton;

    @Inject
    @DataField("empty-add-archetype-button")
    private HTMLButtonElement emptyAddArchetypeButton;

    @Inject
    @DataField("search-input")
    private HTMLInputElement searchInput;

    @Inject
    @DataField("table-container")
    private HTMLDivElement container;

    @Inject
    @DataField("no-results-container")
    private HTMLDivElement noResultsContainer;

    @Inject
    @DataField("table-toolbar-container")
    private HTMLDivElement toolbarContainer;

    @Inject
    @DataField("empty-container")
    private HTMLDivElement emptyContainer;

    @Inject
    @DataField("pagination-container")
    private HTMLDivElement paginationContainer;

    @Inject
    @Named("tbody")
    @DataField("archetype-table-body")
    private HTMLTableSectionElement tableBody;

    @Inject
    @Named("th")
    @DataField("archetype-header-include")
    private HTMLTableCellElement headerInclude;

    @Inject
    @Named("th")
    @DataField("archetype-column-status")
    private HTMLTableCellElement headerStatus;

    @Inject
    @DataField("page-indicator")
    private HTMLLabelElement pageIndicator;

    @Inject
    @DataField("total-pages")
    @Named("span")
    private HTMLElement totalPages;

    @Inject
    @DataField("current-page")
    private HTMLInputElement currentPage;

    @Inject
    @DataField("previous-page")
    private HTMLLIElement previousPage;

    @Inject
    @DataField("next-page")
    private HTMLLIElement nextPage;

    @Inject
    @DataField("first-page")
    private HTMLLIElement firstPage;

    @Inject
    @DataField("last-page")
    private HTMLLIElement lastPage;

    @Inject
    @Named("span")
    @DataField("include-tooltip")
    private HTMLElement includeTooltip;

    @Inject
    @Named("span")
    @DataField("group-id-tooltip")
    private HTMLElement groupIdTooltip;

    @Inject
    @Named("span")
    @DataField("artifact-id-tooltip")
    private HTMLElement artifactIdTooltip;

    @Inject
    @Named("span")
    @DataField("version-tooltip")
    private HTMLElement versionTooltip;

    @Inject
    @Named("span")
    @DataField("created-date-tooltip")
    private HTMLElement createdDateTooltip;

    @Inject
    @Named("span")
    @DataField("status-tooltip")
    private HTMLElement statusTooltip;

    @Inject
    @Named("span")
    @DataField("actions-tooltip")
    private HTMLElement actionsTooltip;

    @Inject
    @Named("span")
    @DataField("selection-counter")
    private HTMLElement selectionCounter;

    @Override
    public void init(final AbstractArchetypeTablePresenter presenter) {
        this.presenter = presenter;

        configureHeaderTooltips();

        searchInput.setAttribute(PLACE_HOLDER, ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Search));
    }

    @Override
    public void showAddAction(boolean isVisible) {
        addArchetypeButton.hidden = !isVisible;
        emptyAddArchetypeButton.hidden = !isVisible;
    }

    @Override
    public void showIncludeHeader(final boolean isVisible) {
        headerInclude.hidden = !isVisible;
    }

    @Override
    public void showStatusHeader(final boolean isVisible) {
        headerStatus.hidden = !isVisible;
    }

    @Override
    public Element getTableBody() {
        return tableBody;
    }

    @Override
    public void enablePreviousButton(final boolean isEnabled) {
        enableElement(previousPage,
                      isEnabled);
    }

    @Override
    public void enableNextButton(final boolean isEnabled) {
        enableElement(nextPage,
                      isEnabled);
    }

    @Override
    public void enableFirstButton(final boolean isEnabled) {
        enableElement(firstPage,
                      isEnabled);
    }

    @Override
    public void enableLastButton(final boolean isEnabled) {
        enableElement(lastPage,
                      isEnabled);
    }

    @Override
    public void setCurrentPage(final int currentPage) {
        this.currentPage.value = String.valueOf(currentPage);
    }

    @Override
    public void setTotalPages(final String totalText) {
        this.totalPages.textContent = totalText;
    }

    @Override
    public void setPageIndicator(final String indicatorText) {
        this.pageIndicator.textContent = indicatorText;
    }

    @Override
    public void setSelectionCounter(final String counterText) {
        selectionCounter.textContent = counterText;
    }

    @Override
    public void showSelectionCounter(final boolean isVisible) {
        selectionCounter.hidden = !isVisible;
    }

    @Override
    public void showNoResults(final boolean isVisible) {
        noResultsContainer.hidden = !isVisible;
    }

    @Override
    public void showToolbar(final boolean isVisible) {
        toolbarContainer.hidden = !isVisible;
    }

    @Override
    public void show(final boolean isVisible) {
        container.hidden = !isVisible;
    }

    @Override
    public void showEmpty(final boolean isVisible) {
        emptyContainer.hidden = !isVisible;
    }

    @Override
    public void showPagination(final boolean isVisible) {
        paginationContainer.hidden = !isVisible;
    }

    @EventHandler("current-page")
    public void currentPageTextChange(final KeyUpEvent event) {
        String pageNumber = currentPage.value;
        if (pageNumber.matches("\\d+")) {
            presenter.setCurrentPage(Integer.parseInt(pageNumber));
        }
    }

    @EventHandler("previous-page")
    public void onPreviousPageClicked(final ClickEvent event) {
        presenter.goToPreviousPage();
    }

    @EventHandler("next-page")
    public void onNextPageClicked(final ClickEvent event) {
        presenter.goToNextPage();
    }

    @EventHandler("first-page")
    public void onFirstPageClicked(final ClickEvent event) {
        presenter.goToFirstPage();
    }

    @EventHandler("last-page")
    public void onLastPageClicked(final ClickEvent event) {
        presenter.goToLastPage();
    }

    @EventHandler("add-archetype-button")
    public void onAddArchetypeButtonClicked(final ClickEvent event) {
        presenter.addArchetype();
    }

    @EventHandler("empty-add-archetype-button")
    public void onEmptyAddArchetypeButtonClicked(final ClickEvent event) {
        presenter.addArchetype();
    }

    @EventHandler("search-input")
    public void search(final KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            presenter.search(searchInput.value);
        }
    }

    private void enableElement(final Element element,
                               final boolean isEnabled) {
        if (isEnabled) {
            element.classList.remove(DISABLED_CLASS);
        } else {
            element.classList.add(DISABLED_CLASS);
        }
    }

    private void configureHeaderTooltips() {
        includeTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_IncludeTooltip);
        groupIdTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_GroupIdTooltip);
        artifactIdTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_ArtifactIdTooltip);
        versionTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_VersionTooltip);
        createdDateTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_CreatedDateTooltip);
        statusTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_StatusTooltip);
        actionsTooltip.title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_ActionsTooltip);
    }
}

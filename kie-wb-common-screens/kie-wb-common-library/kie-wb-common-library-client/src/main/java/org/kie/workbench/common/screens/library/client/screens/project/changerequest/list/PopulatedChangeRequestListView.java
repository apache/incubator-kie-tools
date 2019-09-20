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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.list.listitem.ChangeRequestListItemView;
import org.uberfire.ext.widgets.common.client.select.SelectComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOption;

@Templated
public class PopulatedChangeRequestListView implements IsElement,
                                                       PopulatedChangeRequestListPresenter.View {

    private static final String PLACE_HOLDER = "placeholder";

    @Inject
    @DataField("indexing-info")
    private HTMLDivElement indexingInfo;

    @Inject
    @DataField("change-requests-list")
    private HTMLDivElement changeRequestsList;

    @Inject
    @DataField("filter-type")
    private HTMLDivElement filterType;

    @Inject
    @DataField("filter-text")
    private HTMLInputElement filterText;

    @Inject
    @DataField("search")
    private HTMLButtonElement search;

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
    @DataField("prev-page")
    private HTMLButtonElement prevPage;

    @Inject
    @DataField("next-page")
    private HTMLButtonElement nextPage;

    @Inject
    @DataField("submit-change-request")
    private HTMLButtonElement submitChangeRequest;

    @Inject
    private SelectComponent selectComponent;

    @Inject
    private Elemental2DomUtil domUtil;

    private PopulatedChangeRequestListPresenter presenter;

    @Override
    public void init(PopulatedChangeRequestListPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("next-page")
    public void nextPage(final ClickEvent event) {
        this.presenter.nextPage();
    }

    @EventHandler("prev-page")
    public void prevPage(final ClickEvent event) {
        this.presenter.prevPage();
    }

    @EventHandler("search")
    public void search(final ClickEvent event) {
        doSearch();
    }

    @EventHandler("filter-text")
    public void search(final KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            doSearch();
        }
    }

    @EventHandler("current-page")
    public void currentPageTextChange(final KeyUpEvent event) {
        String pageNumber = currentPage.value;
        if (pageNumber.matches("\\d+")) {
            presenter.setCurrentPage(Integer.parseInt(pageNumber));
        }
    }

    @EventHandler("submit-change-request")
    public void onSubmitChangeRequestClicked(final ClickEvent event) {
        this.presenter.submitChangeRequest();
    }

    @Override
    public void addChangeRequestItem(final ChangeRequestListItemView item) {
        this.changeRequestsList.appendChild(item.getElement());
    }

    @Override
    public void setCurrentPage(final int currentPage) {
        this.currentPage.value = String.valueOf(currentPage);
    }

    @Override
    public void setPageIndicator(final String pageIndicatorText) {
        this.pageIndicator.textContent = pageIndicatorText;
    }

    @Override
    public void setTotalPages(final String totalText) {
        this.totalPages.textContent = totalText;
    }

    @Override
    public void clearList() {
        this.domUtil.removeAllElementChildren(this.changeRequestsList);
    }

    @Override
    public void enablePreviousButton(final boolean isEnabled) {
        this.prevPage.disabled = !isEnabled;
    }

    @Override
    public void enableNextButton(final boolean isEnabled) {
        this.nextPage.disabled = !isEnabled;
    }

    @Override
    public void setFilterTypes(final List<SelectOption> categories) {
        this.selectComponent.setup(categories,
                                   selectOption -> presenter.setFilterType(selectOption.getSelector()));
        this.filterType.appendChild(this.selectComponent.getView().getElement());
    }

    @Override
    public void clearSearch() {
        this.search.value = "";
    }

    @Override
    public void enableSubmitChangeRequestButton(final boolean isEnabled) {
        this.submitChangeRequest.disabled = !isEnabled;
    }

    @Override
    public void showEmptyState(final EmptyState emptyState) {
        indexingInfo.className = "blank-slate-pf";
        indexingInfo.innerHTML = emptyState.getElement().getOuterHTML();
    }

    @Override
    public void hideEmptyState(final EmptyState emptyState) {
        emptyState.clear();
        indexingInfo.className = "";
        indexingInfo.innerHTML = "";
    }

    @Override
    public void setFilterTextPlaceHolder(final String placeHolder) {
        filterText.setAttribute(PLACE_HOLDER, placeHolder);
    }

    private void doSearch() {
        presenter.search(this.filterText.value);
    }
}
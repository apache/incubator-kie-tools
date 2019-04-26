/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.assets;

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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.uberfire.ext.widgets.common.client.select.SelectComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.kie.workbench.common.screens.library.client.widgets.project.AssetItemWidget;

@Templated
public class PopulatedAssetsView implements PopulatedAssetsScreen.View,
                                            IsElement {

    @Inject
    @DataField("indexing-info")
    private HTMLDivElement indexingInfo;

    @Inject
    @DataField("assets-list")
    private HTMLDivElement assetsList;

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
    @DataField("import-asset")
    private HTMLButtonElement importAsset;

    @Inject
    @DataField("add-asset")
    private HTMLButtonElement addAsset;

    @Inject
    private TranslationService ts;

    @Inject
    private SelectComponent selectComponent;

    @Inject
    private Elemental2DomUtil domUtil;

    private PopulatedAssetsScreen presenter;

    public PopulatedAssetsView() {

    }

    @Override
    public void init(PopulatedAssetsScreen presenter) {
        this.presenter = presenter;
    }

    public void addAssetItem(AssetItemWidget item) {
        this.assetsList.appendChild(domUtil.asHTMLElement(item.getElement()));
    }

    @EventHandler("add-asset")
    public void addAsset(final ClickEvent event) {
        this.presenter.addAsset();
    }

    @EventHandler("import-asset")
    public void importAsset(final ClickEvent event) {
        this.presenter.importAsset();
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

    private void doSearch() {
        presenter.search(this.filterText.value);
    }

    @EventHandler("current-page")
    public void currentPageTextChange(final KeyUpEvent event) {
        String pageNumber = currentPage.value;
        if (pageNumber.matches("\\d+")) {
            presenter.setCurrentPage(Integer.valueOf(pageNumber));
        }
    }

    @Override
    public void showEmptyState(EmptyState emptyState) {
        indexingInfo.className = "blank-slate-pf";
        indexingInfo.innerHTML = emptyState.getElement().getOuterHTML();
    }

    @Override
    public void hideEmptyState(EmptyState emptyState) {
        emptyState.clear();
        indexingInfo.className = "";
        indexingInfo.innerHTML = "";
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage.value = String.valueOf(currentPage);
    }

    @Override
    public void setPageIndicator(int from,
                                 int to,
                                 int total) {
        this.pageIndicator.textContent = from + "-" + to + " " + ts.getTranslation(LibraryConstants.Of) + " " + total;
    }

    @Override
    public void setTotalPages(int totalPages) {
        this.totalPages.textContent = String.valueOf(totalPages);
    }

    @Override
    public void clear() {
        this.domUtil.removeAllElementChildren(this.assetsList);
    }

    @Override
    public void enablePreviousButton() {
        this.prevPage.disabled = false;
    }

    @Override
    public void disablePreviousButton() {
        this.prevPage.disabled = true;
    }

    @Override
    public void enableNextButton() {
        this.nextPage.disabled = false;
    }

    @Override
    public void disableNextButton() {
        this.nextPage.disabled = true;
    }

    @Override
    public void setCategories(List<SelectOption> categories) {
        this.selectComponent.setup(categories,
                                   selectOption -> presenter.setFilterType(selectOption.getSelector()));
        this.filterType.appendChild(this.selectComponent.getView().getElement());
    }

    @Override
    public void enableImportButton(boolean enable) {
        this.importAsset.disabled = !enable;
    }

    @Override
    public void enableAddAssetButton(boolean enable) {
        this.addAsset.disabled = !enable;
    }
}

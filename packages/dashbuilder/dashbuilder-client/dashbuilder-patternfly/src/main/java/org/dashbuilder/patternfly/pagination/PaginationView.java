/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.pagination;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * The pagination view should keep the status of the current page accessed by user. 
 * It should receive the page information (page size and total items) and tell the called when a page is selected
 *
 */
@Dependent
@Templated
public class PaginationView implements Pagination.View {

    private Pagination presenter;

    @Inject
    @DataField
    HTMLDivElement paginationContainer;

    @Inject
    @DataField
    HTMLButtonElement btnFirstPage;

    @Inject
    @DataField
    HTMLButtonElement btnLastPage;

    @Inject
    @DataField
    HTMLButtonElement btnNextPage;

    @Inject
    @DataField
    HTMLButtonElement btnPreviousPage;

    @Inject
    @DataField
    @Named("b")
    HTMLElement lblTotal;

    @Inject
    @DataField
    @Named("span")
    HTMLElement lblOffsetBegin;

    @Inject
    @DataField
    @Named("span")
    HTMLElement lblOffsetEnd;

    @Inject
    @DataField
    @Named("span")
    HTMLElement lblTotalPages;

    @Inject
    @DataField
    HTMLInputElement txtSelectedPage;

    @Inject
    @DataField
    @Named("nav")
    HTMLElement paginationNav;

    @Inject
    Elemental2DomUtil util;

    private int totalPages;

    private int totalItems;

    private int pageSize;

    private int boundEnd;

    private int boundBegin;

    private int currentPage;

    @Override
    public void init(Pagination presenter) {
        this.presenter = presenter;
        txtSelectedPage.min = "1";
        txtSelectedPage.onchange = e -> {
            var page = Integer.parseInt(txtSelectedPage.value);
            selectPage(page);
            return null;
        };
    }

    public void setup(int nRows,
                      int pageSize) {
        this.totalItems = nRows;
        this.pageSize = pageSize;
        this.totalPages = nRows / pageSize;
        this.totalPages += nRows % pageSize == 0 ? 0 : 1;

        this.currentPage = 1;
        txtSelectedPage.max = "" + totalPages;
        paginationNav.style.display = totalPages > 1 ? "" : "none";
        selectPage(1);
    }

    @Override
    public HTMLElement getElement() {
        return paginationContainer;
    }

    @EventHandler("btnPreviousPage")
    public void previousClick(ClickEvent e) {
        if (this.currentPage > 1) {
            selectPage(this.currentPage - 1);
        }
    }

    @EventHandler("btnNextPage")
    public void nextClick(ClickEvent e) {
        if (this.currentPage < totalPages) {
            selectPage(this.currentPage + 1);
        }
    }

    @EventHandler("btnFirstPage")
    public void firstClick(ClickEvent e) {
        if (this.currentPage != 1) {
            selectPage(1);
        }
    }

    @EventHandler("btnLastPage")
    public void lastClick(ClickEvent e) {
        if (this.currentPage != totalPages) {
            selectPage(totalPages);
        }
    }

    void selectPage(int page) {
        this.currentPage = page;
        updateBounds();
        updateUI();
        presenter.selectPage(page);
    }

    private void updateBounds() {
        boundBegin = (pageSize * (currentPage - 1)) + 1;
        boundEnd = pageSize * currentPage;

        if (boundEnd > totalItems) {
            boundEnd = totalItems;
            btnNextPage.disabled = true;
        } else {
            btnNextPage.disabled = false;
        }

        if (boundBegin > totalItems) {
            boundBegin = totalItems;
        }

        btnFirstPage.disabled = currentPage <= 1;

        if (currentPage == totalPages) {
            btnLastPage.disabled = true;
        }

        btnPreviousPage.disabled = btnFirstPage.disabled;
        btnLastPage.disabled = btnNextPage.disabled;
    }

    private void updateUI() {
        lblTotal.textContent = "" + totalItems;
        txtSelectedPage.value = "" + currentPage;
        lblOffsetBegin.textContent = "" + boundBegin;
        lblOffsetEnd.textContent = "" + boundEnd;
        txtSelectedPage.max = "" + totalPages;
    }

}

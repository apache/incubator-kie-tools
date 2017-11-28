/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.google.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.gwt.charts.client.event.SortEvent;
import com.googlecode.gwt.charts.client.event.SortHandler;
import com.googlecode.gwt.charts.client.options.TableSort;
import com.googlecode.gwt.charts.client.table.Table;
import com.googlecode.gwt.charts.client.table.TableOptions;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;

public class GoogleTableDisplayerView
        extends GoogleDisplayerView<GoogleTableDisplayer>
        implements GoogleTableDisplayer.View {

    private Table table;
    protected int pageSize = 10;
    protected int width = 500;
    protected int totalPages = 1;
    protected int currentPage = 1;
    protected int totalRows = 0;
    protected int leftMostPageNumber = 0;
    protected int rightMostPageNumber = 0;
    protected boolean pagerEnabled = false;
    protected boolean totalPagesHintEnabled = false;
    protected boolean totalRowsHintEnabled = false;

    @Override
    public String getGroupsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Rows();
    }

    @Override
    public String getColumnsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Columns();
    }

    @Override
    public void createTable() {
        table = new Table();
    }

    @Override
    public void setSortEnabled(boolean enabled) {
        if (enabled) {
            table.addSortHandler(new SortHandler() {
                public void onSort(SortEvent sortEvent) {
                    String columnId = getDataTable().getColumnId(sortEvent.getColumn());
                    getPresenter().sortBy(columnId);
                }
            } );
        }
    }

    @Override
    public void setTotalPagesHintEnabled(boolean enabled) {
        this.totalPagesHintEnabled = enabled;
    }

    @Override
    public void setTotalRowsHintEnabled(boolean enabled) {
        this.totalRowsHintEnabled = enabled;
    }

    @Override
    public void setPageSize(int size) {
        this.pageSize = size;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setPagerEnabled(boolean enabled) {
        this.pagerEnabled = enabled;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public void setTotalRows(int numberOfRows) {
        this.totalRows = numberOfRows;
    }

    @Override
    public void setTotalPages(int numberOfPages) {
        this.totalPages = numberOfPages;
    }

    @Override
    public void setLeftMostPageNumber(int n) {
        this.leftMostPageNumber = n;
    }

    @Override
    public void setRightMostPageNumber(int n) {
        this.rightMostPageNumber = n;
    }

    @Override
    public void nodata() {
        table.draw(getDataTable(), createOptions());

        FlowPanel tablePanel  = new FlowPanel();
        tablePanel.add(table);
        tablePanel.add(new Label(GoogleDisplayerConstants.INSTANCE.common_noData()));
        super.showDisplayer(tablePanel);
    }

    @Override
    public void drawTable() {
        table.draw(getDataTable(), createOptions());

        FlowPanel tablePanel  = new FlowPanel();
        tablePanel.add(table);
        if (pagerEnabled) {
            HorizontalPanel pager = createTablePager();
            tablePanel.add(pager);
        }
        super.showDisplayer(tablePanel);
    }

    protected TableOptions createOptions() {
        TableOptions options = TableOptions.create();
        options.setSort(TableSort.EVENT);
        options.setPageSize(pageSize);
        options.setShowRowNumber(false);
        options.setWidth(width);
        return options;
    }

    protected HorizontalPanel createTablePager() {
        HorizontalPanel pagerPanel = new HorizontalPanel();
        pagerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        pagerPanel.getElement().setAttribute("cellpadding", "5");

        Pagination pagination = new Pagination();
        pagination.setPaginationSize(PaginationSize.NONE);

        for (int i = leftMostPageNumber; i <= rightMostPageNumber; i++) {
            AnchorListItem pageLink = new AnchorListItem(Integer.toString(i));
            final Integer _currentPage = i;
            if (currentPage != i) {
                pageLink.setActive(false);
                pageLink.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        getPresenter().gotoPage(_currentPage.intValue());
                    }
                });
            } else {
                pageLink.setActive(true);
            }
            pagination.add(pageLink);
        }

        Icon leftPageIcon = new Icon(IconType.ANGLE_LEFT);
        leftPageIcon.setSize(IconSize.LARGE );
        leftPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        leftPageIcon.sinkEvents(Event.ONCLICK);
        leftPageIcon.addHandler(createGotoPageHandler(currentPage - 1), ClickEvent.getType());
        Tooltip leftPageTooltip = new Tooltip(GoogleDisplayerConstants.INSTANCE.googleTableDisplayer_gotoPreviousPage());
        leftPageTooltip.add(leftPageIcon);

        Icon rightPageIcon = new Icon(IconType.ANGLE_RIGHT);
        rightPageIcon.setSize(IconSize.LARGE);
        rightPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        rightPageIcon.sinkEvents(Event.ONCLICK);
        rightPageIcon.addHandler(createGotoPageHandler(currentPage + 1), ClickEvent.getType());
        Tooltip rightPageTooltip = new Tooltip( GoogleDisplayerConstants.INSTANCE.googleTableDisplayer_gotoNextPage() );
        rightPageTooltip.add(rightPageIcon);

        Icon firstPageIcon = new Icon(IconType.ANGLE_DOUBLE_LEFT);
        firstPageIcon.setSize(IconSize.LARGE);
        firstPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        firstPageIcon.sinkEvents(Event.ONCLICK);
        firstPageIcon.addHandler(createGotoPageHandler(1), ClickEvent.getType());
        Tooltip firstPageTooltip = new Tooltip(GoogleDisplayerConstants.INSTANCE.googleTableDisplayer_gotoFirstPage());
        firstPageTooltip.add(firstPageIcon);

        Icon lastPageIcon = new Icon(IconType.ANGLE_DOUBLE_RIGHT);
        lastPageIcon.setSize(IconSize.LARGE);
        lastPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        lastPageIcon.sinkEvents(Event.ONCLICK);
        lastPageIcon.addHandler(createGotoPageHandler(totalPages), ClickEvent.getType());
        Tooltip lastPageTooltip = new Tooltip(GoogleDisplayerConstants.INSTANCE.googleTableDisplayer_gotoLastPage());
        lastPageTooltip.add(lastPageIcon);

        pagerPanel.add(firstPageTooltip);
        pagerPanel.add(leftPageTooltip);
        pagerPanel.add(pagination);
        pagerPanel.add(rightPageTooltip);
        pagerPanel.add(lastPageTooltip);

        if (totalPagesHintEnabled) {
            pagerPanel.add(new Label(GoogleDisplayerConstants.INSTANCE.googleTableDisplayer_pages(
                    Integer.toString(leftMostPageNumber),
                    Integer.toString(rightMostPageNumber),
                    Integer.toString(totalPages))));
        }
        if (totalRowsHintEnabled) {
            int currentRowsShown = currentPage * pageSize > totalRows ? totalRows : currentPage * pageSize;
            pagerPanel.add(new Label(GoogleDisplayerConstants.INSTANCE.googleTableDisplayer_rows(
                    Integer.toString(((currentPage - 1) * pageSize) + 1),
                    Integer.toString(currentRowsShown),
                    Integer.toString(totalRows))));
        }
        return pagerPanel;
    }

    protected ClickHandler createGotoPageHandler(final int page) {
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                getPresenter().gotoPage(page);
            }
        };
    }
}

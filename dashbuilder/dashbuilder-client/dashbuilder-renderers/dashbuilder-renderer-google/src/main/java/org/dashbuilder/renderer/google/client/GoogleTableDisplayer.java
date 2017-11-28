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

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.Displayer;

@Dependent
public class GoogleTableDisplayer extends GoogleDisplayer<GoogleTableDisplayer.View> {

    public interface View extends GoogleDisplayer.View<GoogleTableDisplayer> {

        void createTable();

        void setSortEnabled(boolean enabled);

        void setTotalPagesHintEnabled(boolean enabled);

        void setTotalRowsHintEnabled(boolean enabled);

        void setPageSize(int size);

        void setWidth(int width);

        void setPagerEnabled(boolean enabled);

        void setCurrentPage(int currentPage);

        void setTotalRows(int numberOfRows);

        void setTotalPages(int numberOfPages);

        void setLeftMostPageNumber(int n);

        void setRightMostPageNumber(int n);

        void nodata();

        void drawTable();
    }

    protected View view;
    protected int pageSize = 20;
    protected int currentPage = 1;
    protected int numberOfRows = 0;
    protected int numberOfPages = 1;
    protected int pageSelectorSize = 6;
    protected String lastOrderedColumn = null;
    protected SortOrder lastSortOrder = null;
    protected boolean showTotalRowsHint = true;
    protected boolean showTotalPagesHint = true;

    @Inject
    public GoogleTableDisplayer(View view, FilterLabelSet filterLabelSet) {
        super(filterLabelSet);
        this.view = view;
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupAllowed(true)
                .setGroupRequired(false)
                .setExtraColumnsAllowed(true)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle());

        return new DisplayerConstraints(lookupConstraints)
                   .supportsAttribute( DisplayerAttributeDef.TYPE)
                   .supportsAttribute(DisplayerAttributeDef.RENDERER)
                   .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                   .supportsAttribute(DisplayerAttributeDef.COLUMN_EMPTY)
                   .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.TABLE_GROUP);
    }

    @Override
    protected void beforeDataSetLookup() {
        // Get the sort settings
        if (lastOrderedColumn == null) {
            String defaultSortColumn = displayerSettings.getTableDefaultSortColumnId();
            if (defaultSortColumn != null && !"".equals( defaultSortColumn)) {
                lastOrderedColumn = defaultSortColumn;
                lastSortOrder = displayerSettings.getTableDefaultSortOrder();
            }
        }
        // Apply the sort order specified (if any)
        if (lastOrderedColumn != null) {
            sortApply(lastOrderedColumn, lastSortOrder);
        }
        // Draw only the data subset corresponding to the current page.
        int pageSize = displayerSettings.getTablePageSize();
        int offset = (currentPage - 1) * pageSize;
        dataSetHandler.limitDataSetRows(offset, pageSize);
    }

    @Override
    protected void afterDataSetLookup(DataSet dataSet) {
        pageSize = displayerSettings.getTablePageSize();
        numberOfRows = dataSet.getRowCountNonTrimmed();
        numberOfPages = ((numberOfRows - 1) / pageSize) + 1;
        if (currentPage > numberOfPages) {
            currentPage = 1;
        }
    }

    @Override
    protected void createVisualization() {
        super.createVisualization();

        int tableWidth = displayerSettings.getTableWidth();

        view.createTable();
        view.setSortEnabled(displayerSettings.isTableSortEnabled());
        view.setTotalPagesHintEnabled(showTotalPagesHint);
        view.setTotalRowsHintEnabled(showTotalRowsHint);
        view.setPageSize(displayerSettings.getTablePageSize());
        view.setWidth(tableWidth > 0 ? tableWidth : dataSet.getColumns().size() * 100);

        this.updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        view.setPagerEnabled(displayerSettings.getTablePageSize() < dataSet.getRowCountNonTrimmed());
        view.setCurrentPage(currentPage);
        view.setTotalRows(numberOfRows);
        view.setTotalPages(numberOfPages);
        view.setLeftMostPageNumber(getLeftMostPageNumber());
        view.setRightMostPageNumber(getRightMostPageNumber());

        if (numberOfRows == 0) {
            view.nodata();
        } else {
            super.pushDataToView();
            view.drawTable();
        }
    }

    public void sortBy(String column) {
        if (displayerSettings.isTableSortEnabled()) {
            lastOrderedColumn = column;
            lastSortOrder = lastSortOrder != null ? lastSortOrder.reverse() : SortOrder.ASCENDING;
            super.redraw();
        }
    }

    public void gotoPage(int pageNumber) {
        if (pageNumber != currentPage && pageNumber > 0 && pageNumber < numberOfPages + 1) {
            currentPage = pageNumber;
            super.redraw();
        }
    }

    public int getLeftMostPageNumber() {
        int page = currentPage - pageSelectorSize/2;
        if (page < 1) return 1;
        return page;
    }

    public int getRightMostPageNumber() {
        int page = getLeftMostPageNumber() + pageSelectorSize - 1;
        if (page > numberOfPages) return numberOfPages;
        return page;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    // Reset the current navigation status on filter requests from external displayers.

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetGroup groupOp) {
        currentPage = 1;
        super.onFilterEnabled(displayer, groupOp);
    }

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetFilter filter) {
        currentPage = 1;
        super.onFilterEnabled(displayer, filter);
    }

    @Override
    public void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps) {
        currentPage = 1;
        super.onFilterReset(displayer, groupOps);
    }

    @Override
    public void onFilterReset(Displayer displayer, DataSetFilter filter) {
        currentPage = 1;
        super.onFilterReset(displayer, filter);
    }
}

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
package org.dashbuilder.renderer.client.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.export.ExportCallback;
import org.dashbuilder.displayer.client.export.ExportFormat;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.Command;

@Dependent
public class TableDisplayer extends AbstractGwtDisplayer<TableDisplayer.View> {

    public interface View extends AbstractGwtDisplayer.View<TableDisplayer> {

        String getGroupsTitle();

        String getColumnsTitle();

        void showTitle(String title);

        void createTable(int pageSize, FilterLabelSet widget);

        void redrawTable();

        void setWidth(int width);

        void setSortEnabled(boolean enabled);

        void setTotalRows(int rows, boolean isExact);

        void setPagerEnabled(boolean enabled);

        void setColumnPickerEnabled(boolean enabled);

        void setExportToCsvEnabled(boolean enabled);

        void setExportToXlsEnabled(boolean enabled);

        void addColumn(ColumnType columnType, String columnId, String columnName, int index, boolean selectEnabled, boolean sortEnabled);

        void gotoFirstPage();

        int getLastOffset();

        int getPageSize();

        void exportNoData();

        void exportTooManyRows(int rowNum, int limit);

        void exportFileUrl(String url);
    }

    protected View view;
    protected int totalRows = 0;
    protected String lastOrderedColumn = null;
    protected SortOrder lastSortOrder = null;
    protected List<Command> onCellSelectedCommands = new ArrayList<>();
    protected String selectedCellColumn = null;
    protected Integer selectedCellRow = null;
    protected int exportRowNumMax = 100000;
    protected FilterLabelSet filterLabelSet;

    @Inject
    public TableDisplayer(View view, FilterLabelSet filterLabelSet) {
        this.view = view;
        this.view.init(this);
        this.filterLabelSet = filterLabelSet;
        this.filterLabelSet.setOnClearAllCommand(this::onFilterClearAll);
    }

    @Override
    public View getView() {
        return view;
    }

    public FilterLabelSet getFilterLabelSet() {
        return filterLabelSet;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public String getLastOrderedColumn() {
        return lastOrderedColumn;
    }

    public SortOrder getLastSortOrder() {
        return lastSortOrder;
    }

    public String getSelectedCellColumn() {
        return selectedCellColumn;
    }

    public Integer getSelectedCellRow() {
        return selectedCellRow;
    }

    public int getExportRowNumMax() {
        return exportRowNumMax;
    }

    public void setExportRowNumMax(int exportRowNumMax) {
        this.exportRowNumMax = exportRowNumMax;
    }

    public void addOnCellSelectedCommand(Command onCellSelectedCommand) {
        this.onCellSelectedCommands.add(onCellSelectedCommand);
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
                .supportsAttribute( DisplayerAttributeDef.TYPE )
                .supportsAttribute( DisplayerAttributeDef.RENDERER )
                .supportsAttribute( DisplayerAttributeGroupDef.COLUMNS_GROUP )
                .supportsAttribute( DisplayerAttributeGroupDef.FILTER_GROUP )
                .supportsAttribute( DisplayerAttributeGroupDef.REFRESH_GROUP )
                .supportsAttribute( DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute( DisplayerAttributeGroupDef.EXPORT_GROUP)
                .supportsAttribute( DisplayerAttributeGroupDef.TABLE_GROUP );
    }

    @Override
    protected void beforeDataSetLookup() {
        // Get the sort settings
        if (lastOrderedColumn == null) {
            String defaultSortColumn = displayerSettings.getTableDefaultSortColumnId();
            if (!StringUtils.isBlank(defaultSortColumn)) {
                lastOrderedColumn = defaultSortColumn;
                lastSortOrder = displayerSettings.getTableDefaultSortOrder();
            }
        }
        // Apply the sort order specified (if any)
        if (lastOrderedColumn != null) {
            sortApply(lastOrderedColumn, lastSortOrder);
        }
        // Lookup only the target rows
        dataSetHandler.limitDataSetRows(view.getLastOffset(), getPageSize());
    }

    protected int getPageSize() {
        return view.getPageSize() == 0 ? displayerSettings.getTablePageSize() : view.getPageSize();
    }

    @Override
    protected void afterDataSetLookup(DataSet dataSet) {
        totalRows = dataSet.getRowCountNonTrimmed();
        view.setTotalRows(totalRows, isTotalRowsExact(dataSet, totalRows));
    }

    @Override
    protected void createVisualization() {
        if (displayerSettings.isTitleVisible()) {
            view.showTitle(displayerSettings.getTitle());
        }

        List<DataColumn> dataColumns = dataSet.getColumns();
        int width = displayerSettings.getTableWidth();

        view.createTable(displayerSettings.getTablePageSize(), filterLabelSet);
        view.setWidth(width == 0 ? dataColumns.size() * 100 + 40 : width);
        view.setSortEnabled(displayerSettings.isTableSortEnabled());
        view.setTotalRows(totalRows, isTotalRowsExact(dataSet, totalRows));
        view.setPagerEnabled(isPagerEnabled(dataSet, totalRows));
        view.setColumnPickerEnabled(displayerSettings.isTableColumnPickerEnabled());
        view.setExportToCsvEnabled(displayerSettings.isCSVExportAllowed());
        view.setExportToXlsEnabled(displayerSettings.isExcelExportAllowed());

        for ( int i = 0; i < dataColumns.size(); i++ ) {
            DataColumn dataColumn = dataColumns.get(i);
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);
            String columnName = columnSettings.getColumnName();
            switch (dataColumn.getColumnType()) {

                case LABEL: {
                    // Only label columns cells are selectable
                    view.addColumn(dataColumn.getColumnType(), dataColumn.getId(), columnName, i, displayerSettings.isFilterEnabled(), true);
                    break;
                }
                default: {
                    view.addColumn(dataColumn.getColumnType(), dataColumn.getId(), columnName, i, false, true);
                    break;
                }
            }
        }
        view.gotoFirstPage();
    }

    protected boolean isTotalRowsExact(DataSet dataSet,
                                       int totalRows) {
        return isRemoteProvider(dataSet) ? totalRows < getPageSize() : true;
    }

    protected boolean isPagerEnabled(DataSet dataSet,
                                     int totalRows) {
        return isRemoteProvider(dataSet) ? totalRows == getPageSize() : getPageSize() < dataSet.getRowCountNonTrimmed();
    }

    protected boolean isRemoteProvider(DataSet dataSet) {
        final DataSetDef def = dataSet.getDefinition();
        return def == null || def.getProvider() == null ? false : def.getProvider().getName().equals("REMOTE");
    }

    @Override
    protected void updateVisualization() {
        view.setTotalRows(totalRows, isTotalRowsExact(dataSet, totalRows));
        view.setPagerEnabled(isPagerEnabled(dataSet, totalRows));
        view.gotoFirstPage();
        view.redrawTable();
        updateFilterStatus();
    }

    protected void updateFilterStatus() {
        filterLabelSet.clear();
        Set<String> columnFilters = filterColumns();
        if (displayerSettings.isFilterEnabled() && !columnFilters.isEmpty()) {

            for (String columnId : columnFilters) {
                List<Interval> selectedValues = filterIntervals(columnId);
                DataColumn column = dataSet.getColumnById(columnId);
                for (Interval interval : selectedValues) {
                    String formattedValue = formatInterval(interval, column);
                    FilterLabel filterLabel = filterLabelSet.addLabel(formattedValue);
                    filterLabel.setOnRemoveCommand(() -> onFilterLabelRemoved(columnId, interval.getIndex()));
                }
            }
        }
    }

    public void sortBy(String column, SortOrder order) {
        if (displayerSettings.isTableSortEnabled()) {
            lastOrderedColumn = column;
            lastSortOrder = order;
            super.redraw();
        }
    }

    public void selectCell(String columnId, int rowIndex) {
        if (displayerSettings.isFilterEnabled()) {
            selectedCellColumn = columnId;
            selectedCellRow = rowIndex;
            for(Command cmd : onCellSelectedCommands){
                cmd.execute();
            }
            if (displayerSettings.isFilterSelfApplyEnabled()) {
                view.gotoFirstPage();
            }
            super.filterUpdate(columnId, rowIndex);
            updateFilterStatus();
        }
    }

    @Override
    public void filterReset(String columnId) {
        super.filterReset(columnId);
        if (selectedCellColumn != null && selectedCellColumn.equals(columnId)) {
            selectedCellColumn = null;
            selectedCellRow = null;
        }
    }

    @Override
    public void filterReset() {
        selectedCellColumn = null;
        selectedCellRow = null;
        filterLabelSet.clear();
        super.filterReset();
    }

    public void lookupCurrentPage(final Callback<Integer> callback) {
        try {
            beforeDataSetLookup();
            dataSetHandler.lookupDataSet(new DataSetReadyCallback() {
                public void callback(DataSet ds) {
                    try {
                        dataSet = ds;
                        afterDataSetLookup(dataSet);
                        callback.callback(dataSet.getRowCount());
                    }
                    catch (Exception e) {
                        showError(new ClientRuntimeError(e));
                    }
                }
                public void notFound() {
                    view.errorDataSetNotFound(displayerSettings.getDataSetLookup().getDataSetUUID());
                }
                public boolean onError(ClientRuntimeError error) {
                    showError(error);
                    return false;
                }
            });
        } catch (Exception e) {
            showError(new ClientRuntimeError(e));
        }
    }

    public void export(ExportFormat format) {
        super.export(format, exportRowNumMax, new ExportCallback() {

            @Override
            public void noData() {
                view.exportNoData();
            }

            @Override
            public void tooManyRows(int rowNum) {
                view.exportTooManyRows(rowNum, exportRowNumMax);
            }

            @Override
            public void exportFileUrl(String url) {
                view.exportFileUrl(url);
            }

            @Override
            public void error(ClientRuntimeError error) {
                view.error(error);
            }
        });
    }

    // Filter label set component notifications

    void onFilterLabelRemoved(String columnId, int row) {
        super.filterUpdate(columnId, row);

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    void onFilterClearAll() {
        filterReset();

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    // Reset the current navigation status on filter requests from external displayers

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetGroup groupOp) {
        view.gotoFirstPage();
        super.onFilterEnabled(displayer, groupOp);
    }

    @Override
    public void onFilterEnabled(Displayer displayer, DataSetFilter filter) {
        view.gotoFirstPage();
        super.onFilterEnabled(displayer, filter);
    }

    @Override
    public void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps) {
        view.gotoFirstPage();
        super.onFilterReset(displayer, groupOps);
    }

    @Override
    public void onFilterReset(Displayer displayer, DataSetFilter filter) {
        view.gotoFirstPage();
        super.onFilterReset(displayer, filter);
    }
}

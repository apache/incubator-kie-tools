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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.client.AbstractErraiDisplayer;
import org.dashbuilder.displayer.client.Displayer;

@Dependent
public class TableDisplayer extends AbstractErraiDisplayer<TableDisplayer.View> {

    public interface View extends AbstractErraiDisplayer.View<TableDisplayer> {

        String getGroupsTitle();

        String getColumnsTitle();

        void showTitle(String title);

        void redrawTable(List<String> columnsNames, String[][] data, int pageSize);

        void setWidth(int width);

        void fullWidth();

        void setSortEnabled(boolean enabled);

        void setColumnPickerEnabled(boolean enabled);

        void addColumn(ColumnType columnType,
                       String columnId,
                       String columnName,
                       int index,
                       boolean selectEnabled,
                       boolean sortEnabled);

        void gotoFirstPage();

        void setHeight(int chartHeight);

        void setSelectable(boolean filterNotificationEnabled);
    }

    protected View view;
    protected int totalRows = 0;
    protected String lastOrderedColumn = null;
    protected String selectedCellColumn = null;
    protected Integer selectedCellRow = null;
    protected FilterLabelSet filterLabelSet;

    @Inject
    public TableDisplayer(View view, FilterLabelSet filterLabelSet) {
        this.view = view;
        this.filterLabelSet = filterLabelSet;
        this.filterLabelSet.setOnClearAllCommand(this::onFilterClearAll);
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    public FilterLabelSet getFilterLabelSet() {
        return filterLabelSet;
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
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_RESIZABLE)
                .supportsAttribute(DisplayerAttributeGroupDef.EXPORT_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.TABLE_GROUP);
    }

    @Override
    protected void createVisualization() {
        if (displayerSettings.isTitleVisible()) {
            view.showTitle(displayerSettings.getTitle());
        }

        view.setSortEnabled(displayerSettings.isTableSortEnabled());
        view.setColumnPickerEnabled(displayerSettings.isTableColumnPickerEnabled());

        if (displayerSettings.isResizable()) {
            view.fullWidth();
        } else {
            view.setWidth(displayerSettings.getChartWidth());
        }
        
        view.setSelectable(displayerSettings.isFilterNotificationEnabled()); 
        view.setHeight(displayerSettings.getChartHeight());

        view.gotoFirstPage();
        if (dataSet.getRowCount() > 0) {
            this.updateVisualization();
        }
    }

    @Override
    protected void updateVisualization() {
        var columns = dataSet.getColumns();
        var columnsNames = columns.stream()
                .map(displayerSettings::getColumnSettings)
                .map(ColumnSettings::getColumnName)
                .collect(Collectors.toList());

        var data = new String[dataSet.getRowCount()][columns.size()];
        for (int i = 0; i < dataSet.getRowCount(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                var value = dataSet.getValueAt(i, j);
                var column = columns.get(j);
                data[i][j] = super.formatValue(value, column);
            }
        }

        view.gotoFirstPage();
        view.redrawTable(columnsNames, data, displayerSettings.getTablePageSize());
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

    public void selectCell(String columnId, int rowIndex) {
        if (displayerSettings.isFilterEnabled()) {
            selectedCellColumn = columnId;
            selectedCellRow = rowIndex;
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

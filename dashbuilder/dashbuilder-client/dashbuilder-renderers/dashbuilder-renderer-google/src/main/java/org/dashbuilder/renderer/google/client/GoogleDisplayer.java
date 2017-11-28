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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.dashbuilder.common.client.widgets.FilterLabel;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;

public abstract class GoogleDisplayer<V extends GoogleDisplayer.View> extends AbstractGwtDisplayer<V> {

    public interface View<P extends GoogleDisplayer> extends AbstractGwtDisplayer.View<P> {

        void draw();

        void dataClear();

        void dataRowCount(int rowCount);

        void dataAddColumn(ColumnType type, String id, String name);

        void dataSetValue(int row, int column, Date value);

        void dataSetValue(int row, int column, double value);

        void dataSetValue(int row, int column, String value);

        void dataFormatDateColumn(String pattern, int column);

        void dataFormatNumberColumn(String pattern, int column);

        String getGroupsTitle();

        String getColumnsTitle();

        void showTitle(String title);

        void setFilterLabelSet(FilterLabelSet widget);
    }

    private FilterLabelSet filterLabelSet;

    public GoogleDisplayer(FilterLabelSet filterLabelSet) {
        this.filterLabelSet = filterLabelSet;
        this.filterLabelSet.setOnClearAllCommand(this::onFilterClearAll);
    }

    public FilterLabelSet getFilterLabelSet() {
        return filterLabelSet;
    }

    /**
     * GCharts drawing is done asynchronously via the GoogleRenderer (see ready() method below)
     */
    @Override
    public void draw() {
        getView().draw();
    }

    /**
     * Invoked asynchronously by the GoogleRenderer when the displayer is ready for display
     */
    public void ready() {
        super.draw();
    }

    @Override
    protected void createVisualization() {
        getView().setFilterLabelSet(filterLabelSet);
        if (displayerSettings.isTitleVisible()) {
            getView().showTitle(displayerSettings.getTitle());
        }
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

    // Data generation

    public void pushDataToView() {

        getView().dataClear();
        getView().dataRowCount(dataSet.getRowCount());

        List<DataColumn> columns = dataSet.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            DataColumn dataColumn = columns.get(i);
            String columnId = dataColumn.getId();
            ColumnType columnType = dataColumn.getColumnType();
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);

            getView().dataAddColumn(dataColumn.getColumnType(), columnId, columnSettings.getColumnName());

            List columnValues = dataColumn.getValues();
            for (int j = 0; j < columnValues.size(); j++) {
                Object value = columnValues.get(j);

                if (ColumnType.DATE.equals(columnType)) {
                    getView().dataSetValue(j, i, value == null ? new Date() : (Date) value);
                }
                else if (ColumnType.NUMBER.equals(columnType)) {
                    if (value == null) {
                        getView().dataSetValue(j, i, 0d);
                    } else {
                        String valueStr = getEvaluator().evalExpression(value.toString(), columnSettings.getValueExpression());
                        getView().dataSetValue(j, i, Double.parseDouble(valueStr));
                    }
                }
                else {
                    String valueStr = super.formatValue(j, i);
                    getView().dataSetValue(j, i, valueStr);
                }
            }
        }

        // Format the table values
        for (int i = 0; i < dataSet.getColumns().size(); i++) {
            DataColumn dataColumn = columns.get(i);
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);
            String pattern = columnSettings.getValuePattern();

            if (ColumnType.DATE.equals(dataColumn.getColumnType())) {
                getView().dataFormatDateColumn(pattern, i);
            }
            else if (ColumnType.NUMBER.equals(dataColumn.getColumnType())) {
                getView().dataFormatNumberColumn(pattern, i);
            }
        }
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
        super.filterReset();

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }
}

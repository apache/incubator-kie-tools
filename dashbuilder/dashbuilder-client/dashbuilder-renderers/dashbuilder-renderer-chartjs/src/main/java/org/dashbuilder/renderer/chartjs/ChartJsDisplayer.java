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
package org.dashbuilder.renderer.chartjs;

import java.util.List;
import java.util.Set;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;

public abstract class ChartJsDisplayer<V extends ChartJsDisplayer.View> extends AbstractGwtDisplayer<V> {

    public interface View<P extends ChartJsDisplayer> extends AbstractGwtDisplayer.View<P> {

        void setWidth(int width);

        void setHeight(int height);

        void setMarginTop(int marginTop);

        void setMarginBottom(int marginBottom);

        void setMarginRight(int marginRight);

        void setMarginLeft(int marginLeft);

        void dataClear();

        void dataAddLabel(String label);

        void dataAddSerie(String columnName, String color, double[] values);

        String getGroupsTitle();

        String getColumnsTitle();

        void setTitle(String title);

        void setFilterEnabled(boolean enabled);

        void clearFilterStatus();

        void addFilterValue(String value);

        void addFilterReset();

        void nodata();

        void drawChart();
    }

    public static final String[] COLOR_ARRAY = new String[] {
            "blue",
            "red",
            "orange",
            "brown",
            "coral",
            "aqua",
            "fuchsia",
            "gold",
            "green",
            "grey",
            "lime",
            "magenta",
            "pink",
            "silver",
            "yellow"};

    @Override
    protected void createVisualization() {
        if (displayerSettings.isTitleVisible()) {
            getView().setTitle(displayerSettings.getTitle());
        }
        getView().setFilterEnabled(displayerSettings.isFilterEnabled());
        getView().setWidth(displayerSettings.getChartWidth());
        getView().setHeight(displayerSettings.getChartHeight());
        getView().setMarginTop(displayerSettings.getChartMarginTop());
        getView().setMarginBottom(displayerSettings.getChartMarginBottom());
        getView().setMarginLeft(displayerSettings.getChartMarginLeft());
        getView().setMarginRight(displayerSettings.getChartMarginRight());

        drawChart();
    }

    @Override
    protected void updateVisualization() {
        updateFilterStatus();
        drawChart();
    }

    protected void drawChart() {
        if (dataSet.getRowCount() == 0) {
            getView().nodata();
        } else {
            pushDataToView();
            getView().drawChart();
        }
    }

    protected void updateFilterStatus() {
        getView().clearFilterStatus();
        Set<String> columnFilters = filterColumns();
        if (displayerSettings.isFilterEnabled() && !columnFilters.isEmpty()) {

            for (String columnId : columnFilters) {
                List<Interval> selectedValues = filterIntervals(columnId);
                DataColumn column = dataSet.getColumnById(columnId);
                for (Interval interval : selectedValues) {
                    String formattedValue = formatInterval(interval, column);
                    getView().addFilterValue(formattedValue);
                }
            }
            getView().addFilterReset();
        }
    }

    // View notifications

    void onFilterResetClicked() {
        filterReset();

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    // Data generation

    protected void pushDataToView() {

        getView().dataClear();

        for (int i=0; i<dataSet.getRowCount(); i++) {
            String label = super.formatValue(i, 0);
            getView().dataAddLabel(label);
        }

        List<DataColumn> columns = dataSet.getColumns();
        for (int i=1; i<columns.size(); i++) {
            DataColumn seriesColumn = columns.get(0);
            ColumnSettings columnSettings = displayerSettings.getColumnSettings(seriesColumn);
            String columnName = columnSettings.getColumnName();
            String color = COLOR_ARRAY[i - 1];

            double[] values = new double[dataSet.getRowCount()];
            for (int j=0; j<dataSet.getRowCount(); j++) {
                values[j] = ((Number) dataSet.getValueAt(j, i)).doubleValue();
            }

            getView().dataAddSerie(columnName, color, values);
        }
    }
}

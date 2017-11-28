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
package org.dashbuilder.renderer.lienzo.client;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;

import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class LienzoDisplayer<V extends LienzoDisplayer.View> extends AbstractGwtDisplayer<V> {

    public interface View<P extends LienzoDisplayer> extends AbstractGwtDisplayer.View<P> {

        void showTitle(String  title);

        void setWidth(int width);

        void setHeight(int height);

        void setMarginTop(int marginTop);

        void setMarginBottom(int marginBottom);

        void setMarginRight(int marginRight);

        void setMarginLeft(int marginLeft);

        void setSubType(DisplayerSubType subType);

        void setFilterEnabled(boolean enabled);

        void setResizeEnabled(boolean enabled);

        void setFontFamily(String font);

        void setFontStyle(String style);

        void setFontSize(int size);

        String getGroupsTitle();

        String getColumnsTitle();

        void dataClear();

        void dataAddColumn(String columnId, String columnName, ColumnType columnType);

        void dataAddValue(String columnId, Date value);

        void dataAddValue(String columnId, Number value);

        void dataAddValue(String columnId, String value);

        void clearFilterStatus();

        void addFilterValue(String value);

        void addFilterReset();

        void nodata();

        void drawChart();

        void reloadChart();
    }

    @Override
    protected void createVisualization() {
        if (displayerSettings.isTitleVisible()) {
            getView().showTitle(displayerSettings.getTitle());
        }
        getView().setSubType(displayerSettings.getSubtype());
        getView().setWidth(getChartWidth());
        getView().setHeight(getChartHeight());
        getView().setMarginLeft(displayerSettings.getChartMarginLeft());
        getView().setMarginRight(displayerSettings.getChartMarginRight());
        getView().setMarginTop(displayerSettings.getChartMarginTop());
        getView().setMarginBottom(displayerSettings.getChartMarginBottom());
        getView().setFontFamily("Verdana");
        getView().setFontStyle("bold");
        getView().setFontSize(8);
        //getView().setLegendPosition(LegendPosition.RIGHT); // TODO: Custom displayer parameter.
        //getView().setCategoriesAxisLabelsPosition(LabelsPosition.LEFT); // TODO: Custom displayer parameter.
        //getView().setValuesAxisLabelsPosition(LabelsPosition.BOTTOM); // TODO: Custom displayer parameter.
        getView().setResizeEnabled(displayerSettings.isResizable());
        getView().setFilterEnabled(displayerSettings.isFilterEnabled());
        // TODO: Category and Number types?

        if (dataSet.getRowCount() == 0) {
            getView().nodata();
        } else {
            pushDataToView();
            getView().drawChart();
        }
    }

    @Override
    protected void updateVisualization() {
        updateFilterStatus();
        if (dataSet.getRowCount() == 0) {
            getView().nodata();
        } else {
            pushDataToView();
            getView().reloadChart();
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

    protected void pushDataToView() {

        getView().dataClear();

        List<DataColumn> columns = dataSet.getColumns();
        if (columns != null && !columns.isEmpty()) {
            for (int i = 0; i < columns.size(); i++) {
                DataColumn dataColumn = columns.get(i);
                List columnValues = dataColumn.getValues();
                ColumnType columnType = dataColumn.getColumnType();
                String columnId = dataColumn.getId();
                ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);
                String columnName = columnSettings.getColumnName();

                getView().dataAddColumn(columnId, columnName, dataColumn.getColumnType());

                for (int j = 0; j < columnValues.size(); j++) {
                    Object value = columnValues.get(j);
                    if (ColumnType.LABEL.equals(columnType)) {
                        value = super.formatValue(j, i);
                    }

                    if (ColumnType.DATE.equals(columnType)) {
                        getView().dataAddValue(columnId, value == null ? new Date() : (Date) value);
                    }
                    else if (ColumnType.NUMBER.equals(columnType)) {
                        getView().dataAddValue(columnId, value == null ? 0d : Double.parseDouble(value.toString()));
                    }
                    else {
                        getView().dataAddValue(columnId, value.toString());
                    }
                }
            }
        }
    }

    public int getChartWidth() {
        return displayerSettings.getChartWidth();
    }

    public int getChartHeight() {
        return  displayerSettings.getChartHeight();
    }

    public int getWidth() {
        int width = displayerSettings.isResizable() ? displayerSettings.getChartMaxWidth() : displayerSettings.getChartWidth();
        int left = displayerSettings.getChartMarginLeft();
        int right = displayerSettings.getChartMarginRight();
        return displayerSettings.getChartWidth()+right+left;
    }

    public int getHeight() {
        int height = displayerSettings.isResizable() ? displayerSettings.getChartMaxHeight() : displayerSettings.getChartHeight();
        int top = displayerSettings.getChartMarginTop();
        int bottom = displayerSettings.getChartMarginBottom();
        return displayerSettings.getChartHeight()+top+bottom;
    }

    // View notifications

    void onCategorySelected(String columnId, int row) {
        Integer maxSelections = displayerSettings.isFilterSelfApplyEnabled() ? null : dataSet.getRowCount();
        filterUpdate(columnId, row, maxSelections);

        // Update the displayer in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    void onFilterResetClicked() {
        filterReset();

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }
}

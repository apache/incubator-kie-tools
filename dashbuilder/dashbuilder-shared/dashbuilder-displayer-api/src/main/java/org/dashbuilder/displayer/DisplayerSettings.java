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
package org.dashbuilder.displayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DisplayerSettings {

    protected String UUID;
    protected DataSet dataSet;
    protected DataSetLookup dataSetLookup;
    protected List<ColumnSettings> columnSettingsList = new ArrayList<ColumnSettings>();
    protected Map<String, String> settings = new HashMap<String, String>(30);

    public DisplayerSettings() {}

    public DisplayerSettings(DisplayerType displayerType) {
        this();
        setType(displayerType);
    }

    public DisplayerSettings cloneInstance() {
        DisplayerSettings clone = new DisplayerSettings();
        clone.UUID = UUID;
        clone.settings = new HashMap(settings);
        clone.columnSettingsList = new ArrayList();
        for (ColumnSettings columnSettings : columnSettingsList) {
            clone.columnSettingsList.add(columnSettings.cloneInstance());
        }
        if (dataSet != null) {
            clone.dataSet = dataSet.cloneInstance();
        }
        if (dataSetLookup != null) {
            clone.dataSetLookup = dataSetLookup.cloneInstance();
        }
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            DisplayerSettings other = (DisplayerSettings) obj;
            if (other == this) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (UUID != null && !UUID.equals(other.UUID)) {
                return false;
            }
            if (dataSet != null && !dataSet.equals(other.dataSet)) {
                return false;
            }
            if (dataSetLookup != null && !dataSetLookup.equals(other.dataSetLookup)) {
                return false;
            }
            if (columnSettingsList.size() != other.columnSettingsList.size()) {
                return false;
            }
            for (int i = 0; i < columnSettingsList.size(); i++) {
                if (!columnSettingsList.get(i).equals(other.columnSettingsList.get(i))) {
                    return false;
                }
            }
            if (settings.size() != other.settings.size()) {
                return false;
            }
            for (String setting : settings.keySet()) {
                if (!settings.get(setting).equals(other.settings.get(setting))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public List<ColumnSettings> getColumnSettingsList() {
        return columnSettingsList;
    }

    public void setColumnSettingsList(List<ColumnSettings> columnSettingsList) {
        if (columnSettingsList != null) {
            this.columnSettingsList = columnSettingsList;
        } else {
            this.columnSettingsList.clear();
        }
    }

    public void removeColumnSettings(String columnId) {
        Iterator<ColumnSettings> it = columnSettingsList.iterator();
        while (it.hasNext()) {
            ColumnSettings columnSettings = it.next();
            if (columnSettings.getColumnId().equals(columnId)) {
                it.remove();
            }
        }
    }

    public ColumnSettings getColumnSettings(String columnId) {
        for (ColumnSettings columnSettings : columnSettingsList) {
            if (columnSettings.getColumnId().equals(columnId))
                return columnSettings;
        }
        return null;
    }

    public ColumnSettings getColumnSettings(DataColumn column) {
        ColumnSettings sourceSettings = getColumnSettings(column.getId());
        return ColumnSettings.cloneWithDefaults(sourceSettings, column);
    }

    public void setColumnName(String columnId, String name) {
        ColumnSettings columnSettings = getColumnSettings(columnId);
        if (columnSettings == null)
            columnSettingsList.add(columnSettings = new ColumnSettings(columnId));

        columnSettings.setColumnName(name);
    }

    public void setColumnValueExpression(String columnId, String expression) {
        ColumnSettings columnSettings = getColumnSettings(columnId);
        if (columnSettings == null)
            columnSettingsList.add(columnSettings = new ColumnSettings(columnId));

        columnSettings.setValueExpression(expression);
    }

    public void setColumnValuePattern(String columnId, String pattern) {
        ColumnSettings columnSettings = getColumnSettings(columnId);
        if (columnSettings == null)
            columnSettingsList.add(columnSettings = new ColumnSettings(columnId));

        columnSettings.setValuePattern(pattern);
    }

    public void setColumnEmptyTemplate(String columnId, String template) {
        ColumnSettings columnSettings = getColumnSettings(columnId);
        if (columnSettings == null)
            columnSettingsList.add(columnSettings = new ColumnSettings(columnId));

        columnSettings.setEmptyTemplate(template);
    }

    private String getSettingPath(DisplayerAttributeDef displayerAttributeDef) {
        return displayerAttributeDef.getFullId();
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.trim().length() == 0)
            return defaultValue;
        return Integer.parseInt(value);
    }

    private long parseLong(String value, long defaultValue) {
        if (value == null || value.trim().length() == 0)
            return defaultValue;
        return Long.parseLong(value);
    }

    private boolean parseBoolean(String value) {
        return parseBoolean(value, false);
    }

    private boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null || value.trim().length() == 0)
            return defaultValue;
        return Boolean.parseBoolean(value);
    }

    private String parseString(String value) {
        if (value == null || value.trim().length() == 0)
            return "";
        return value;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public DataSetLookup getDataSetLookup() {
        return dataSetLookup;
    }

    public void setDataSetLookup(DataSetLookup dataSetLookup) {
        this.dataSetLookup = dataSetLookup;
    }

    // 'Generic' getter method
    public String getDisplayerSetting(DisplayerAttributeDef displayerAttributeDef) {
        return settings.get(getSettingPath(displayerAttributeDef));
    }

    // 'Generic' setter method
    public void setDisplayerSetting(DisplayerAttributeDef displayerAttributeDef, String value) {
        settings.put(getSettingPath(displayerAttributeDef), value);
    }

    // 'Generic' setter method
    public void setDisplayerSetting(String displayerAttributeDef, String value) {
        settings.put(displayerAttributeDef, value);
    }

    // 'Generic' remove method
    public void removeDisplayerSetting(DisplayerAttributeDef displayerAttributeDef) {
        settings.remove(getSettingPath(displayerAttributeDef));
    }

    // 'Generic' remove method
    public void removeDisplayerSetting(DisplayerAttributeGroupDef displayerAttributeGroup) {
        for (DisplayerAttributeDef attributeDef : displayerAttributeGroup.getChildren()) {
            settings.remove(getSettingPath(attributeDef));
        }
    }

    // 'Generic' remove method
    public void removeDisplayerSetting(String displayerAttributeDef) {
        settings.remove(displayerAttributeDef);
    }

    public Map<String, String> getSettingsFlatMap() {
        return settings;
    }

    public void setSettingsFlatMap(Map<String, String> settings) {
        this.settings = settings;
    }

    public DisplayerType getType() {
        String strType = settings.get(getSettingPath(DisplayerAttributeDef.TYPE));
        return DisplayerType.getByName(strType);
    }

    public void setType(DisplayerType displayerType) {
        settings.put(getSettingPath(DisplayerAttributeDef.TYPE), displayerType.toString());
    }

    public DisplayerSubType getSubtype() {
        String strSubtype = settings.get(getSettingPath(DisplayerAttributeDef.SUBTYPE));
        return DisplayerSubType.getByName(strSubtype);
    }

    public void setSubtype(DisplayerSubType subtype) {
        if (subtype != null)
            settings.put(getSettingPath(DisplayerAttributeDef.SUBTYPE), subtype.toString());
        else
            settings.remove(getSettingPath(DisplayerAttributeDef.SUBTYPE));
    }

    public String getRenderer() {
        return settings.get(getSettingPath(DisplayerAttributeDef.RENDERER));
    }

    public void setRenderer(String renderer) {
        settings.put(getSettingPath(DisplayerAttributeDef.RENDERER), renderer);
    }

    public String getTitle() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.TITLE)));
    }

    public void setTitle(String title) {
        settings.put(getSettingPath(DisplayerAttributeDef.TITLE), title);
    }

    public boolean isTitleVisible() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.TITLE_VISIBLE)));
    }

    public void setTitleVisible(boolean titleVisible) {
        settings.put(getSettingPath(DisplayerAttributeDef.TITLE_VISIBLE), Boolean.toString(titleVisible));
    }

    public boolean isCSVExportAllowed() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.ALLOW_EXPORT_CSV))) || parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.EXPORT_TO_CSV)));
    }

    public void setCSVExportAllowed(boolean csvExportAllowed) {
        settings.put(getSettingPath(DisplayerAttributeDef.EXPORT_TO_CSV), Boolean.toString(csvExportAllowed));
    }

    public boolean isExcelExportAllowed() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.ALLOW_EXPORT_EXCEL))) || parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.EXPORT_TO_XLS)));
    }

    public void setExcelExportAllowed(boolean excelExportAllowed) {
        settings.put(getSettingPath(DisplayerAttributeDef.EXPORT_TO_XLS), Boolean.toString(excelExportAllowed));
    }

    public int getRefreshInterval() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.REFRESH_INTERVAL)), -1);
    }

    public void setRefreshInterval(int refreshInSeconds) {
        settings.put(getSettingPath(DisplayerAttributeDef.REFRESH_INTERVAL), Integer.toString(refreshInSeconds));
    }

    public boolean isRefreshStaleData() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.REFRESH_STALE_DATA)));
    }

    public void setRefreshStaleData(boolean refresh) {
        settings.put(getSettingPath(DisplayerAttributeDef.REFRESH_STALE_DATA), Boolean.toString(refresh));
    }

    public boolean isFilterEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.FILTER_ENABLED)));
    }

    public void setFilterEnabled(boolean filterEnabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.FILTER_ENABLED), Boolean.toString(filterEnabled));
    }

    public boolean isFilterSelfApplyEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.FILTER_SELFAPPLY_ENABLED)));
    }

    public void setFilterSelfApplyEnabled(boolean filterSelfApplyEnabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.FILTER_SELFAPPLY_ENABLED), Boolean.toString(filterSelfApplyEnabled));
    }

    public boolean isFilterNotificationEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.FILTER_NOTIFICATION_ENABLED)));
    }

    public void setFilterNotificationEnabled(boolean filterNotificationEnabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.FILTER_NOTIFICATION_ENABLED), Boolean.toString(filterNotificationEnabled));
    }

    public boolean isFilterListeningEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.FILTER_LISTENING_ENABLED)));
    }

    public void setFilterListeningEnabled(boolean filterListeningEnabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.FILTER_LISTENING_ENABLED), Boolean.toString(filterListeningEnabled));
    }

    public int getSelectorWidth() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.SELECTOR_WIDTH)), -1);
    }

    public void setSelectorWidth(int filterWidth) {
        settings.put(getSettingPath(DisplayerAttributeDef.SELECTOR_WIDTH), Integer.toString(filterWidth));
    }

    public boolean isSelectorMultiple() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.SELECTOR_MULTIPLE)));
    }

    public void setSelectorMultiple(boolean filterMultiple) {
        settings.put(getSettingPath(DisplayerAttributeDef.SELECTOR_MULTIPLE), Boolean.toString(filterMultiple));
    }

    public boolean isSelectorInputsEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.SELECTOR_SHOW_INPUTS)), true);
    }

    public void setSelectorInputsEnabled(boolean enabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.SELECTOR_SHOW_INPUTS), Boolean.toString(enabled));
    }

    public int getChartWidth() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_WIDTH)), 500);
    }

    public void setChartWidth(int chartWidth) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_WIDTH), Integer.toString(chartWidth));
    }

    public String getChartBackgroundColor() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.CHART_BGCOLOR)));
    }

    public void setChartBackgroundColor(String color) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_BGCOLOR), color);
    }

    public int getChartHeight() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_HEIGHT)), 300);
    }

    public void setChartHeight(int chartHeight) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_HEIGHT), Integer.toString(chartHeight));
    }

    public int getChartMarginTop() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_TOP)), 0);
    }

    public void setChartMarginTop(int chartMarginTop) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_TOP), Integer.toString(chartMarginTop));
    }

    public int getChartMarginBottom() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_BOTTOM)), 0);
    }

    public void setChartMarginBottom(int chartMarginBottom) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_BOTTOM), Integer.toString(chartMarginBottom));
    }

    public int getChartMarginLeft() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_LEFT)), 0);
    }

    public void setChartMarginLeft(int chartMarginLeft) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_LEFT), Integer.toString(chartMarginLeft));
    }

    public int getChartMarginRight() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_RIGHT)), 0);
    }

    public void setChartMarginRight(int chartMarginRight) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_MARGIN_RIGHT), Integer.toString(chartMarginRight));
    }

    public int getChartMaxWidth() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_MAX_WIDTH)), 600);
    }

    public void setChartMaxWidth(int chartWidth) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_MAX_WIDTH), Integer.toString(chartWidth));
    }

    public int getChartMaxHeight() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.CHART_MAX_HEIGHT)), 400);
    }

    public void setChartMaxHeight(int chartHeight) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_MAX_HEIGHT), Integer.toString(chartHeight));
    }

    public void setResizable(boolean resizable) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_RESIZABLE), Boolean.toString(resizable));
    }

    public boolean isResizable() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.CHART_RESIZABLE)));
    }

    public boolean isChartShowLegend() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.CHART_SHOWLEGEND)));
    }

    public void setChartShowLegend(boolean chartShowLegend) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_SHOWLEGEND), Boolean.toString(chartShowLegend));
    }

    public Position getChartLegendPosition() {
        Position pos = Position.getByName(settings.get(getSettingPath(DisplayerAttributeDef.CHART_LEGENDPOSITION)));
        if (pos == null)
            return Position.RIGHT;
        return pos;
    }

    public void setChartLegendPosition(Position chartLegendPosition) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_LEGENDPOSITION), chartLegendPosition.toString());
    }

    public int getTablePageSize() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.TABLE_PAGESIZE)), 10);
    }

    public void setTablePageSize(int tablePageSize) {
        settings.put(getSettingPath(DisplayerAttributeDef.TABLE_PAGESIZE), Integer.toString(tablePageSize));
    }

    public int getTableWidth() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.TABLE_WIDTH)), 0);
    }

    public void setTableWidth(int tableWidth) {
        settings.put(getSettingPath(DisplayerAttributeDef.TABLE_WIDTH), Integer.toString(tableWidth));
    }

    public boolean isTableSortEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.TABLE_SORTENABLED)));
    }

    public void setTableSortEnabled(boolean tableSortEnabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.TABLE_SORTENABLED), Boolean.toString(tableSortEnabled));
    }

    public void setTableColumnPickerEnabled(boolean tableColumnPickerEnabled) {
        settings.put(getSettingPath(DisplayerAttributeDef.TABLE_COLUMN_PICKER_ENABLED), Boolean.toString(tableColumnPickerEnabled));
    }

    public String getTableDefaultSortColumnId() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.TABLE_SORTCOLUMNID)));
    }

    public void setTableDefaultSortColumnId(String tableDefaultSortColumnId) {
        settings.put(getSettingPath(DisplayerAttributeDef.TABLE_SORTCOLUMNID), tableDefaultSortColumnId);
    }

    public SortOrder getTableDefaultSortOrder() {
        SortOrder order = SortOrder.getByName(settings.get(getSettingPath(DisplayerAttributeDef.TABLE_SORTORDER)));
        if (order == null)
            return SortOrder.ASCENDING;
        return order;
    }

    public void setTableDefaultSortOrder(SortOrder tableDefaultSortOrder) {
        settings.put(getSettingPath(DisplayerAttributeDef.TABLE_SORTORDER), tableDefaultSortOrder.toString());
    }

    public boolean isTableColumnPickerEnabled() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.TABLE_COLUMN_PICKER_ENABLED)), true);
    }

    public boolean isXAxisShowLabels() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.XAXIS_SHOWLABELS)));
    }

    public void setXAxisShowLabels(boolean axisShowLabels) {
        settings.put(getSettingPath(DisplayerAttributeDef.XAXIS_SHOWLABELS), Boolean.toString(axisShowLabels));
    }

    public int getXAxisLabelsAngle() {
        return parseInt(settings.get(getSettingPath(DisplayerAttributeDef.XAXIS_LABELSANGLE)), 0);
    }

    public void setXAxisLabelsAngle(int axisLabelsAngle) {
        settings.put(getSettingPath(DisplayerAttributeDef.XAXIS_LABELSANGLE), Integer.toString(axisLabelsAngle));
    }

    public String getXAxisTitle() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.XAXIS_TITLE)));
    }

    public void setXAxisTitle(String axisTitle) {
        settings.put(getSettingPath(DisplayerAttributeDef.XAXIS_TITLE), axisTitle);
    }

    public boolean isYAxisShowLabels() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.YAXIS_SHOWLABELS)));
    }

    public void setYAxisShowLabels(boolean axisShowLabels) {
        settings.put(getSettingPath(DisplayerAttributeDef.YAXIS_SHOWLABELS), Boolean.toString(axisShowLabels));
    }

    //    public int getYAxisLabelsAngle() {
    //        return parseInt( settings.get( getSettingPath( DisplayerAttributeDef.YAXIS_LABELSANGLE ) ), 10 );
    //    }
    //
    //    public void setYAxisLabelsAngle( int axisLabelsAngle ) {
    //        settings.put( getSettingPath( DisplayerAttributeDef.YAXIS_LABELSANGLE ), Integer.toString( axisLabelsAngle ) );
    //    }

    public String getYAxisTitle() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.YAXIS_TITLE)));
    }

    public void setYAxisTitle(String axisTitle) {
        settings.put(getSettingPath(DisplayerAttributeDef.YAXIS_TITLE), axisTitle);
    }

    public long getMeterStart() {
        return parseLong(settings.get(getSettingPath(DisplayerAttributeDef.METER_START)), 0);
    }

    public void setMeterStart(long meterStart) {
        settings.put(getSettingPath(DisplayerAttributeDef.METER_START), Long.toString(meterStart));
    }

    public long getMeterWarning() {
        return parseLong(settings.get(getSettingPath(DisplayerAttributeDef.METER_WARNING)), 60);
    }

    public void setMeterWarning(long meterWarning) {
        settings.put(getSettingPath(DisplayerAttributeDef.METER_WARNING), Long.toString(meterWarning));
    }

    public long getMeterCritical() {
        return parseLong(settings.get(getSettingPath(DisplayerAttributeDef.METER_CRITICAL)), 90);
    }

    public void setMeterCritical(long meterCritical) {
        settings.put(getSettingPath(DisplayerAttributeDef.METER_CRITICAL), Long.toString(meterCritical));
    }

    public long getMeterEnd() {
        return parseLong(settings.get(getSettingPath(DisplayerAttributeDef.METER_END)), 100);
    }

    public void setMeterEnd(long meterEnd) {
        settings.put(getSettingPath(DisplayerAttributeDef.METER_END), Long.toString(meterEnd));
    }

    public String getDonutHoleTitle() {
        return settings.get(getSettingPath(DisplayerAttributeDef.DONUT_HOLE_TITLE));
    }

    public void setDonutHoleTitle(String holeTitle) {
        settings.put(getSettingPath(DisplayerAttributeDef.DONUT_HOLE_TITLE), holeTitle);
    }

    public boolean isChart3D() {
        return parseBoolean(settings.get(getSettingPath(DisplayerAttributeDef.CHART_3D)));
    }

    public void setChart3D(boolean barchartThreeDimension) {
        settings.put(getSettingPath(DisplayerAttributeDef.CHART_3D), Boolean.toString(barchartThreeDimension));
    }

    public void setHtmlTemplate(String htmlTemplate) {
        settings.put(getSettingPath(DisplayerAttributeDef.HTML_TEMPLATE), htmlTemplate);
    }

    public void setJsTemplate(String jsTemplate) {
        settings.put(getSettingPath(DisplayerAttributeDef.JS_TEMPLATE), jsTemplate);
    }

    public String getHtmlTemplate() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.HTML_TEMPLATE)));
    }

    public String getJsTemplate() {
        return parseString(settings.get(getSettingPath(DisplayerAttributeDef.JS_TEMPLATE)));
    }

    public void setMapColorScheme(MapColorScheme colorScheme) {
        settings.put(getSettingPath(DisplayerAttributeDef.MAP_COLOR_SCHEME), colorScheme.toString());

    }

    public MapColorScheme getMapColorScheme() {
        return MapColorScheme.from(settings.get(getSettingPath(DisplayerAttributeDef.MAP_COLOR_SCHEME)));
    }

    public String getComponentId() {
        return settings.get(getSettingPath(DisplayerAttributeDef.EXTERNAL_COMPONENT_ID));
    }

    public String setComponentId(String componentId) {
        return settings.put(getSettingPath(DisplayerAttributeDef.EXTERNAL_COMPONENT_ID), componentId);
    }

    public void setComponentProperty(String key, String value) {
        String componentId = getComponentId();
        if (componentId != null) {
            String newParamKey = componentPrefix(componentId, key);
            settings.put(newParamKey, value);
        }
    }

    public String getComponentProperty(String key) {
        String componentId = getComponentId();
        if (componentId == null) {
            return null;
        }
        String newKey = componentPrefix(componentId, key);
        return settings.get(newKey);
    }

    public Map<String, String> getComponentProperties() {
        String componentId = getComponentId();
        if (componentId == null) {
            return Collections.emptyMap();
        }
        return settings.entrySet()
                       .stream()
                       .filter(e -> e.getKey().startsWith(componentId))
                       .collect(Collectors.toMap(e -> removeComponentPrefix(componentId, e.getKey()), Map.Entry::getValue));
    }

    public String getComponentPartition() {
        return settings.get(getSettingPath(DisplayerAttributeDef.EXTERNAL_COMPONENT_PARTITION));
    }

    public String setComponentPartition(String componentPartition) {
        return settings.put(getSettingPath(DisplayerAttributeDef.EXTERNAL_COMPONENT_PARTITION), componentPartition);
    }

    private String componentPrefix(String componentId, String key) {
        return componentId + "." + key;
    }

    private String removeComponentPrefix(String componentId, String key) {
        return key.replaceAll(componentId + ".", "");
    }

}
/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client;

import java.util.Iterator;
import java.util.List;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisX;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisY;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartData;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartSize;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Color;
import org.dashbuilder.renderer.c3.client.jsbinding.C3DataInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Legend;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Padding;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Point;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Selection;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Tick;

import com.google.gwt.i18n.client.NumberFormat;

import elemental2.core.JsObject;

public abstract class C3Displayer<V extends C3Displayer.View> extends C3AbstractDisplayer<V> {
    
    private static final double DEFAULT_POINT_RADIUS = 2.5;
    protected C3JsTypesFactory factory;
    
    public static final String[] COLOR_PATTERN = {
                                    "#0088CE", "#CC0000", "#EC7A08", "#3F9C35", "#F0AB00", "#703FEC", "#007A87", "#92D400", "#35CAED",
                                    "#00659C", "#A30000", "#B35C00", "#B58100", "#6CA100", "#2D7623", "#005C66", "#008BAD", "#40199A"};

    public interface View<P extends C3AbstractDisplayer> extends C3AbstractDisplayer.View<P> {

        void updateChart(C3ChartConf conf);
        
        String getType();

        String getGroupsTitle();

        String getColumnsTitle();
        
        void showTitle(String title);

        void setFilterLabelSet(FilterLabelSet filterLabelSet);
        
        void setBackgroundColor(String color);
        
        void setResizable(int maxWidth, int maxHeight);
        
        void setTableData(String[][] data);

    }
    
    public C3Displayer(FilterLabelSet filterLabelSet, C3JsTypesFactory builder) {
        super(filterLabelSet);
        this.factory = builder;
    }
    
    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxColumns(10)
                .setMinColumns(2)
                .setExtraColumnsAllowed(true)
                .setExtraColumnsType(ColumnType.NUMBER)
                .setColumnTypes(new ColumnType[]{
                        ColumnType.LABEL,
                        ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_LEGEND_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.AXIS_GROUP);      
    }

    @Override
    protected void updateVisualizationWithData() {
        if (displayerSettings.isResizable()) {
            getView().setResizable(displayerSettings.getChartWidth(),
                                   displayerSettings.getChartHeight());
        } 

        C3ChartConf conf = buildConfiguration();
        getView().updateChart(conf);
        applyPropertiesToView();
        String[][] tableData = getDataTable();
        getView().setTableData(tableData);
    }

    protected C3ChartConf buildConfiguration() {
        C3AxisInfo axis = createAxis();
        C3ChartData data = createData();
        C3Point point = createPoint();
        C3Padding padding = createPadding();
        C3Legend legend = factory.c3Legend(displayerSettings.isChartShowLegend(), 
                                           getLegendPosition());
        C3Color color = createColor();
        C3ChartSize size = createSize();
        return factory.c3ChartConf(
                    size,
                    data,
                    axis,
                    factory.c3Grid(true, true),
                    factory.c3Transition(0),
                    point,
                    padding, 
                    legend,
                    color
                );
    }

    protected C3Color createColor() {
        return factory.c3Color(COLOR_PATTERN);
    }

    protected C3ChartSize createSize() {
        C3ChartSize size = null;
        if (! displayerSettings.isResizable()) {
            size = factory.c3ChartSize(displayerSettings.getChartWidth(), 
                                       displayerSettings.getChartHeight());
        } 
        return size;
    }

    protected C3Padding createPadding() {
        return factory.c3Padding(displayerSettings.getChartMarginTop(), 
                                 displayerSettings.getChartMarginRight(), 
                                 displayerSettings.getChartMarginBottom(), 
                                 displayerSettings.getChartMarginLeft());
    }

    protected C3Point createPoint() {
        return factory.c3Point(d -> DEFAULT_POINT_RADIUS);
    }

    protected C3ChartData createData() {
        String[][] series = createSeries();
        String type = getView().getType();
        String[][] groups = createGroups();
        JsObject xs = createXs();
        C3Selection selection = createSelection();
        C3ChartData c3Data = factory.c3ChartData(series, type, groups, xs, selection);
        if (displayerSettings.isFilterNotificationEnabled()) {
            c3Data.setOnselected(this::addToSelection);
        }
        return c3Data;
    }

    protected C3Selection createSelection() {
        boolean filterEnabled = displayerSettings.isFilterNotificationEnabled();
        return factory.c3Selection(filterEnabled, true, false);
    }

    protected JsObject createXs() {
        return null;
    }

    protected String[][] createGroups() {
        return new String[0][0];
    }

    protected C3AxisInfo createAxis() {
        C3AxisX axisX = createAxisX();
        C3AxisY axisY = createAxisY();
        return factory.c3AxisInfo(false, axisX, axisY);
    }
    
    protected C3AxisX createAxisX() {
       String[] categories = createCategories();
       C3Tick tick = createTickX();
       return factory.c3AxisX("category", categories, tick);
    }
    
    protected C3Tick createTickX() {
        return factory.createC3Tick(null);
    }

    protected C3AxisY createAxisY() {
        C3Tick tickY = createTickY();
        return factory.c3AxisY(true, tickY);
     }

    protected C3Tick createTickY() {
        return factory.createC3Tick(f -> {
            try {
                double doubleFormat = Double.parseDouble(f);
                return NumberFormat.getFormat("#,###.##").format(doubleFormat);
            } catch(NumberFormatException e) {
                return f;
            }
        });
    }

    /**
     * This method extracts the categories of a dataset.
     * For most of the charts the first column of the dataset contains the categories. 
     * 
     * @return
     */
    protected String[] createCategories() {
        List<DataColumn> columns = dataSet.getColumns();
        DataColumn dataColumn = columns.get(0);
        String[] categories = null;
        if (columns.size() > 0) {
            List<?> values = dataColumn.getValues();
            categories = new String[values.size()];
            for (int i = 0; i < categories.length; i++) {
                Object val = values.get(i);
                if (val != null) {
                    categories[i] = super.formatValue(val, dataColumn);
                } else {
                    categories[i] = "cat_" + i;
                }
            }
        }
        return categories;
    }

    /**
     * Extracts the series of the column 1 and other columns
     * @return
     */
    protected String[][] createSeries() {
        List<DataColumn> columns = dataSet.getColumns();
        String[][] data  = null;
        if (columns.size() > 1) {
            data = new String[columns.size() - 1][];
            for (int i = 1; i < columns.size(); i++) {
                DataColumn dataColumn = columns.get(i);
                ColumnSettings columnSettings = displayerSettings.getColumnSettings(dataColumn);
                List<?> values = dataColumn.getValues();
                String[] seriesValues = new String[values.size() + 1];
                seriesValues[0] = columnSettings.getColumnName();
                for (int j = 0; j < values.size(); j++) {
                    seriesValues[j + 1] = values.get(j).toString(); 
                }
                data[i - 1] = seriesValues;
            }
        }
        return data;
    }
    
    protected int getSelectedRowIndex(C3DataInfo info) {
        return info.getIndex();
    }
    
    
    protected String getSelectedCategory(C3DataInfo info) {
        List<?> values = dataSet.getColumns().get(0).getValues();
        return values.get(info.getIndex()).toString();
    }
    
    protected String[][] getDataTable() {
        List<DataColumn> columns = dataSet.getColumns();
        String data[][] = new String[columns.size()][];
        for (int i = 0; i < columns.size(); i++) {
            List<?> values = columns.get(i).getValues();
            data[i] = new String[values.size()];
            for (int j = 0; j < values.size(); j++) {
                data[i][j] = columnValueToString(values.get(j));
            }
        }
        return data;
    }    
    
    private void addToSelection(C3DataInfo data) {
        int row = getSelectedRowIndex(data);
        addToSelection(row);
    }

    private void applyPropertiesToView() {
        if (displayerSettings.isTitleVisible()) {
            getView().showTitle(displayerSettings.getTitle());
        }
        getView().setBackgroundColor(displayerSettings.getChartBackgroundColor());
    }

    private String getLegendPosition() {
        Position legendPosition = displayerSettings.getChartLegendPosition();
        String c3LegendPosition = C3Legend.convertPosition(legendPosition);
        return c3LegendPosition;
    }
    
}
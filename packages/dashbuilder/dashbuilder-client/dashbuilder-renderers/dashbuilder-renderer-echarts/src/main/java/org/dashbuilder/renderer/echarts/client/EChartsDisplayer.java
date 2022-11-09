/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.echarts.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Option;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Series;
import org.dashbuilder.renderer.echarts.client.js.ECharts.XAxisType;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

public class EChartsDisplayer<V extends EChartsDisplayer.View<EChartsDisplayer<?>>> extends AbstractGwtDisplayer<V> {

    V view;

    EChartsTypeFactory echartsFactory;

    public interface View<P extends EChartsDisplayer<?>> extends AbstractGwtDisplayer.View<P> {

        void noData();

        void applyOption(Option option);

        void setSize(int width, int height, boolean resizable);

    }

    @Inject
    public EChartsDisplayer(V view, EChartsTypeFactory echartsFactory) {
        this.view = view;
        this.echartsFactory = echartsFactory;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public V getView() {
        return view;
    }

    @Override
    protected void createVisualization() {
        updateVisualization();
    }

    @Override
    protected void updateVisualization() {
        if (dataSet.getRowCount() == 0) {
            getView().noData();
        } else {
            updateVisualizationWithData();
        }
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setMaxColumns(20)
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

    // TODO: need some action to clear filter
    void onFilterClearAll() {
        super.filterReset();

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    void onFilterLabelRemoved(String columnId, int row) {
        super.filterUpdate(columnId, row);

        // Update the displayer view in order to reflect the current selection
        // (only if not has already been redrawn in the previous filterUpdate() call)
        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    protected void addToSelection(int row) {
        var columnId = dataSet.getColumns().get(0).getId();
        var maxSelections = displayerSettings.isFilterSelfApplyEnabled() ? null : dataSet.getRowCount();
        filterUpdate(columnId, row, maxSelections);

        if (!displayerSettings.isFilterSelfApplyEnabled()) {
            updateVisualization();
        }
    }

    public ECharts.Dataset buildDataSet() {
        var echartsDataSet = echartsFactory.newDataset();

        var dimensions = dataSet.getColumns()
                .stream().map(c -> displayerSettings.getColumnSettings(c).getColumnName())
                .toArray(String[]::new);

        var source = new Object[dataSet.getRowCount()][dataSet.getColumns().size()];
        var columns = dataSet.getColumns();
        for (var i = 0; i < dataSet.getRowCount(); i++) {
            source[i] = new String[columns.size()];
            for (int j = 0; j < columns.size(); j++) {
                source[i][j] = dataSet.getValueAt(i, j);
            }
        }
        echartsDataSet.setDimensions(dimensions);
        echartsDataSet.setSource(source);
        return echartsDataSet;
    }

    void updateVisualizationWithData() {
        var option = echartsFactory.newOption();
        var series = echartsFactory.newSeries();
        var title = echartsFactory.newTitle();
        var grid = echartsFactory.newGrid();
        var legend = echartsFactory.newLegend();
        var width = displayerSettings.getChartWidth();
        var height = displayerSettings.getChartHeight();
        var echartsDataSet = buildDataSet();
        var bgColor = displayerSettings.getChartBackgroundColor();
        var type = echartsFactory.convertDisplayerType(displayerSettings.getType()).name();

        title.setText(displayerSettings.getTitle());
        title.setShow(displayerSettings.isTitleVisible());

        series.setType(type);

        legend.setShow(displayerSettings.isChartShowLegend());
        switch(displayerSettings.getChartLegendPosition()) {
            case BOTTOM:
                legend.setTop("bottom");
                legend.setLeft("center");
                break;
            case IN:
                break;
            case LEFT:
                legend.setTop("bottom");
                legend.setLeft("left");
                break;
            case RIGHT:
                legend.setTop("bottom");
                legend.setLeft("right");
                break;
            case TOP:
                legend.setTop("top");
                legend.setLeft("center");
                break;
        };

        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_BOTTOM)) {
            grid.setBottom(displayerSettings.getChartMarginBottom());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_TOP)) {
            grid.setTop(displayerSettings.getChartMarginTop());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_LEFT)) {
            grid.setLeft(displayerSettings.getChartMarginLeft());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_RIGHT)) {
            grid.setRight(displayerSettings.getChartMarginRight());
        }
        
        option.setSeries(series);
        option.setDataset(echartsDataSet);
        option.setTitle(title);
        option.setLegend(legend);
        option.setGrid(grid);

        if (bgColor != null) {
            option.setBackgroundColor(bgColor);
        }

        if (displayerSettings.isZoomEnabled()) {
            option.setDataZoom(echartsFactory.newDataZoom());
        }

        if (isXYChart()) {
            setupXYChart(option, series);
        }

        view.setSize(width, height, displayerSettings.isResizable());
        view.applyOption(option);

    }

    private boolean isXYChart() {
        return displayerSettings.getType() == DisplayerType.LINECHART ||
               displayerSettings.getType() == DisplayerType.BARCHART ||
               displayerSettings.getType() == DisplayerType.AREACHART ||
               displayerSettings.getType() == DisplayerType.BUBBLECHART;
    }

    private void setupXYChart(ECharts.Option option, Series series) {
        // XY charts setup
        var xAxis = echartsFactory.newXAxis();
        var yAxis = echartsFactory.newYAxis();
        var axisLabelX = echartsFactory.newAxisLabel();
        var axisLabelY = echartsFactory.newAxisLabel();
        

        var splitLineX = echartsFactory.newSplitLine();
        var splitLineY = echartsFactory.newSplitLine();

        axisLabelX.setInterval(0);
        axisLabelX.setRotate(displayerSettings.getXAxisLabelsAngle());
        splitLineX.setShow(displayerSettings.isGridXOn(true));
        axisLabelX.setShow(displayerSettings.isXAxisShowLabels());
        xAxis.setSplitLine(splitLineX);
        xAxis.setName(displayerSettings.getXAxisTitle());
        xAxis.setAxisLabel(axisLabelX);
        xAxis.setType(XAxisType.category.name());

        axisLabelY.setShow(displayerSettings.isYAxisShowLabels());
        splitLineY.setShow(displayerSettings.isGridYOn(true));
        yAxis.setSplitLine(splitLineY);
        yAxis.setName(displayerSettings.getYAxisTitle());
        yAxis.setAxisLabel(axisLabelY);

        option.setXAxis(xAxis);
        option.setYAxis(yAxis);

        if (displayerSettings.getType() == DisplayerType.AREACHART) {
            series.setAreaStyle(echartsFactory.newAreaStyle());
        }

        if (displayerSettings.getSubtype() == DisplayerSubType.SMOOTH) {
            series.setSmooth(true);
        }

        // perhaps only do this for XY subtypes
        var nSeries = dataSet.getColumns().size() - 1;
        var allSeries = new ECharts.Series[nSeries];
        for (int i = 0; i < nSeries; i++) {
            allSeries[i] = series;
        }
        option.setSeries(allSeries);
    }

}

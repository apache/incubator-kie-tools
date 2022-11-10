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

import com.google.gwt.json.client.JSONParser;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.Mode;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Option;
import org.dashbuilder.renderer.echarts.client.js.ECharts.ValueFormatterCallback;
import org.dashbuilder.renderer.echarts.client.js.ECharts.XAxisType;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

public class EChartsDisplayer<V extends EChartsDisplayer.View<EChartsDisplayer<?>>> extends AbstractGwtDisplayer<V> {

    V view;

    EChartsTypeFactory echartsFactory;

    public interface View<P extends EChartsDisplayer<?>> extends AbstractGwtDisplayer.View<P> {

        void noData();

        void applyOption(Option option);

        void configureChart(int width, int height, boolean resizable, Mode mode);

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
                var column = columns.get(j);
                if (column.getColumnType() != ColumnType.NUMBER) {
                    source[i][j] = super.formatValue(i, j);
                } else {
                    var settings = displayerSettings.getColumnSettings(column);
                    var value = super.evaluateValueToString(dataSet.getValueAt(i, j), settings);
                    try {
                        source[i][j] = Double.parseDouble(value);
                    } catch (Exception e) {
                        source[i][j] = value;
                    }
                }
            }
        }
        echartsDataSet.setDimensions(dimensions);
        echartsDataSet.setSource(source);
        return echartsDataSet;
    }

    void updateVisualizationWithData() {
        var option = echartsFactory.newOption();
        var title = echartsFactory.newTitle();
        var legend = echartsFactory.newLegend();
        var tooltip = echartsFactory.newTooltip();
        var toolbox = echartsFactory.newToolbox();
        var toolboxFeature = echartsFactory.newToolBoxFeature();

        var width = displayerSettings.getChartWidth();
        var height = displayerSettings.getChartHeight();
        var bgColor = displayerSettings.getChartBackgroundColor();
        var legendPosition = displayerSettings.getChartLegendPosition();
        var mode = displayerSettings.getMode() == null ? Mode.LIGHT : displayerSettings.getMode();
        var extraConfiguration = displayerSettings.getExtraConfiguration();

        var echartsDataSet = buildDataSet();

        title.setText(displayerSettings.getTitle());
        if (DisplayerSubType.DONUT == displayerSettings.getSubtype()) {
            title.setTop("center");
        } 
        title.setLeft("center");
        title.setShow(displayerSettings.isTitleVisible());

        legend.setShow(displayerSettings.isChartShowLegend());
        legend.setTop(legendPosition == Position.TOP ? "top" : "bottom");

        if (displayerSettings.isPngExportAllowed()) {
            toolboxFeature.setSaveAsImage(echartsFactory.newSaveAsImage());
        }
        if (displayerSettings.isEditAllowed()) {
            var dataView = echartsFactory.newDataView();
            dataView.setReadOnly(false);
            toolboxFeature.setDataView(dataView);
        }
        if (displayerSettings.isZoomEnabled()) {
            var dataZoom = echartsFactory.newDataZoom();
            toolboxFeature.setDataZoom(dataZoom);
        }
        toolbox.setShow(true);
        toolbox.setFeature(toolboxFeature);

        switch (legendPosition) {
            case BOTTOM:
            case TOP:
                legend.setLeft("center");
                break;
            case IN:
                break;
            case LEFT:
                legend.setLeft("left");
                break;
            case RIGHT:
                legend.setLeft("right");
                break;
        }

        if (bgColor != null && !bgColor.isEmpty()) {
            option.setBackgroundColor(bgColor);
        }

        if (displayerSettings.isZoomEnabled()) {
            option.setDataZoom(echartsFactory.newDataZoom());
        }

        if (isXYChart()) {
            setupXYChart(option);
        } else {
            setupOtherCharts(option);

        }

        tooltip.setValueFormatter(buildNumberLabelFormatterForColumn(1));

        option.setColor(COLOR_PATTERN);
        option.setTooltip(tooltip);
        option.setDataset(echartsDataSet);
        option.setTitle(title);
        option.setLegend(legend);
        option.setToolbox(toolbox);

        view.configureChart(width,
                height,
                displayerSettings.isResizable(),
                mode);
        view.applyOption(option);
        
        if (extraConfiguration != null && !extraConfiguration.isEmpty()) {
            try {
                option = Js.cast(JSONParser.parseStrict(extraConfiguration).isObject().getJavaScriptObject());
                view.applyOption(option);
            } catch (Exception e) {
                DomGlobal.console.log("Extra configuration not valid: \n" + extraConfiguration);
            }
        }

    }

    private ValueFormatterCallback buildNumberLabelFormatterForColumn(int i) {
        return value -> {
            if (dataSet.getColumns().size() > i) {
                var column = dataSet.getColumns().get(i);
                if (column.getColumnType() == ColumnType.NUMBER) {
                    var settings = displayerSettings.getColumnSettings(column);
                    try {
                        return getFormatter().formatNumber(settings.getValuePattern(),
                                Double.parseDouble(value.toString()));
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                }
            }
            return value;
        };
    }

    private boolean isXYChart() {
        return displayerSettings.getType() == DisplayerType.LINECHART ||
               displayerSettings.getType() == DisplayerType.BARCHART ||
               displayerSettings.getType() == DisplayerType.AREACHART ||
               displayerSettings.getType() == DisplayerType.BUBBLECHART;
    }

    private void setupXYChart(ECharts.Option option) {
        var type = echartsFactory.convertDisplayerType(displayerSettings.getType()).name();

        // XY charts setup
        var xAxis = echartsFactory.newXAxis();
        var yAxis = echartsFactory.newYAxis();
        var axisLabelX = echartsFactory.newAxisLabel();
        var axisLabelY = echartsFactory.newAxisLabel();

        // XY Grid
        var grid = echartsFactory.newGrid();

        var splitLineX = echartsFactory.newSplitLine();
        var splitLineY = echartsFactory.newSplitLine();
        var subType = displayerSettings.getSubtype();
        boolean isBar = subType != null && (subType == DisplayerSubType.BAR || subType == DisplayerSubType.BAR_STACKED);
        var isStack = subType != null && (subType == DisplayerSubType.BAR_STACKED ||
                                          subType == DisplayerSubType.AREA_STACKED ||
                                          subType == DisplayerSubType.COLUMN_STACKED);

        if (!isBar) {
            axisLabelX.setInterval(0);
        }
        axisLabelX.setRotate(displayerSettings.getXAxisLabelsAngle());
        splitLineX.setShow(displayerSettings.isGridXOn(true));
        axisLabelX.setShow(displayerSettings.isXAxisShowLabels());
        // must format columns 0 if number
        axisLabelX.setFormatter(buildNumberLabelFormatterForColumn(0));
        xAxis.setSplitLine(splitLineX);
        xAxis.setName(displayerSettings.getXAxisTitle());
        xAxis.setAxisLabel(axisLabelX);
        xAxis.setType(isBar ? XAxisType.value.name() : XAxisType.category.name());

        axisLabelY.setShow(displayerSettings.isYAxisShowLabels());
        splitLineY.setShow(displayerSettings.isGridYOn(true));
        axisLabelY.setFormatter(buildNumberLabelFormatterForColumn(1));
        yAxis.setSplitLine(splitLineY);
        yAxis.setName(displayerSettings.getYAxisTitle());
        yAxis.setAxisLabel(axisLabelY);
        yAxis.setType(isBar ? XAxisType.category.name() : XAxisType.value.name());

        // perhaps only do this for XY subtypes
        var nColumns = dataSet.getColumns().size();
        var allSeries = new ECharts.Series[nColumns - 1];
        if (nColumns > 0) {
            var catColumn = displayerSettings.getColumnSettings(dataSet.getColumnByIndex(0)).getColumnName();
            for (int i = 1; i < nColumns; i++) {
                var series = echartsFactory.newSeries();
                var encode = echartsFactory.newEncode();
                var column = dataSet.getColumnByIndex(i);
                var settings = displayerSettings.getColumnSettings(column);
                var seriesColumn = settings.getColumnName();

                if (displayerSettings.getType() == DisplayerType.AREACHART) {
                    series.setAreaStyle(echartsFactory.newAreaStyle());
                }

                if (displayerSettings.getSubtype() == DisplayerSubType.SMOOTH) {
                    series.setSmooth(true);
                }
                if (isBar) {
                    encode.setX(seriesColumn);
                    encode.setY(catColumn);
                } else {
                    encode.setX(catColumn);
                    encode.setY(seriesColumn);
                }

                if (isStack) {
                    series.setStack(catColumn);
                }

                series.setName(seriesColumn);
                series.setEncode(encode);
                series.setType(type);

                allSeries[i - 1] = series;
            }
        }

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

        option.setGrid(grid);
        option.setXAxis(xAxis);
        option.setYAxis(yAxis);
        option.setSeries(allSeries);
    }

    private void setupOtherCharts(Option option) {
        var series = echartsFactory.newSeries();
        var type = echartsFactory.convertDisplayerType(displayerSettings.getType()).name();
        series.setType(type);

        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_BOTTOM)) {
            series.setBottom(displayerSettings.getChartMarginBottom());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_TOP)) {
            series.setTop(displayerSettings.getChartMarginTop());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_LEFT)) {
            series.setLeft(displayerSettings.getChartMarginLeft());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_RIGHT)) {
            series.setRight(displayerSettings.getChartMarginRight());
        }

        if (displayerSettings.getSubtype() == DisplayerSubType.DONUT) {
            var radius = new String[]{"50%", "70%"};
            series.setRadius(radius);
        }
        option.setSeries(series);
    }

}
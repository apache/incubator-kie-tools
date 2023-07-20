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

import java.util.ArrayList;

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
import org.dashbuilder.displayer.Mode;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.displayer.client.AbstractGwtDisplayer;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Option;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Renderer;
import org.dashbuilder.renderer.echarts.client.js.ECharts.ValueFormatterCallback;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

public abstract class EChartsAbstractDisplayer<V extends EChartsAbstractDisplayer.View> extends
                                              AbstractGwtDisplayer<V> {

    /**
     * Internal property to define the echarts renderer
     */
    private static final String ECHARTS_RENDERER = "_echarts_renderer";

    V view;

    EChartsTypeFactory echartsFactory;

    protected Option option;

    protected String echartsType;

    public interface View<P extends EChartsAbstractDisplayer<?>> extends AbstractGwtDisplayer.View<P> {

        void noData();

        void applyOption(Option option);

        void configureChart(ChartBootstrapParams bootstrapParams);

        void close();

    }

    @Inject
    public EChartsAbstractDisplayer(V view, EChartsTypeFactory echartsFactory) {
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
        var dimensions = retrieveDataSetDimensions();

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
        var echartsRenderer = displayerSettings.getSettingsFlatMap().get(ECHARTS_RENDERER);

        var echartsDataSet = buildDataSet();

        this.echartsType = echartsFactory.convertDisplayerType(displayerSettings.getType()).name();

        this.option = echartsFactory.newOption();

        title.setLeft("center");
        title.setText(displayerSettings.getTitle());
        title.setSubtext(displayerSettings.getSubtitle());
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

        if (legendPosition == Position.BOTTOM || legendPosition == Position.TOP) {
            legend.setLeft("center");
        } else {
            legend.setLeft(legendPosition.toString().toLowerCase());
        }

        if (bgColor != null && !bgColor.isEmpty()) {
            option.setBackgroundColor(bgColor);
        }

        if (displayerSettings.isZoomEnabled()) {
            option.setDataZoom(echartsFactory.newDataZoom());
        }

        tooltip.setValueFormatter(buildNumberLabelFormatterForColumn(1));

        option.setColor(COLOR_PATTERN);
        option.setTooltip(tooltip);
        option.setDataset(echartsDataSet);
        option.setTitle(title);
        option.setLegend(legend);
        option.setToolbox(toolbox);

        view.configureChart(
                ChartBootstrapParams.of(width,
                        height,
                        displayerSettings.isResizable(),
                        mode,
                        Renderer.byName(echartsRenderer)));

        chartSetup();

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

    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        var lookupConstraints = getDataSetLookupConstraints();

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

    ValueFormatterCallback buildNumberLabelFormatterForColumn(int i) {
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

    String[] retrieveDataSetDimensions() {
        var singleColumnsNames = new ArrayList<String>();
        var columnsNames = dataSet.getColumns()
                .stream().map(c -> displayerSettings.getColumnSettings(c).getColumnName())
                .toArray(String[]::new);
        for (int i = 0; i < columnsNames.length; i++) {
            var columnName = columnsNames[i];
            var groupFunction = dataSet.getColumnByIndex(i).getGroupFunction();
            if (singleColumnsNames.contains(columnName) &&
                groupFunction != null &&
                groupFunction.getFunction() != null) {
                columnName = columnName + " " + groupFunction.getFunction().name();
            }
            singleColumnsNames.add(columnName);
        }
        return singleColumnsNames.stream().toArray(String[]::new);
    }

    @Override
    public void close() {
        super.close();

        view.close();
    }

    abstract DataSetLookupConstraints getDataSetLookupConstraints();

    abstract void chartSetup();
}

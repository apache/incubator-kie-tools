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

import javax.inject.Inject;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Series;
import org.dashbuilder.renderer.echarts.client.js.ECharts.XAxisType;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

public abstract class EChartsXYDisplayer extends EChartsAbstractDisplayer<EChartsDisplayerView<?>> {

    protected boolean isBar;
    protected boolean isTimeseries;
    protected boolean isStack;

    @Inject
    public EChartsXYDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
        super(view, echartsFactory);
    }

    @Override
    void chartSetup() {
        var subType = displayerSettings.getSubtype();

        // XY charts setup
        var xAxis = echartsFactory.newXAxis();
        var yAxis = echartsFactory.newYAxis();
        var axisLabelX = echartsFactory.newAxisLabel();
        var axisLabelY = echartsFactory.newAxisLabel();

        // XY Grid
        var grid = echartsFactory.newGrid();

        var splitLineX = echartsFactory.newSplitLine();
        var splitLineY = echartsFactory.newSplitLine();

        this.isTimeseries = displayerSettings.getType() == DisplayerType.TIMESERIES;
        this.isBar = subType != null && (subType == DisplayerSubType.BAR || subType == DisplayerSubType.BAR_STACKED);
        this.isStack = subType != null && (subType == DisplayerSubType.BAR_STACKED ||
                                           subType == DisplayerSubType.AREA_STACKED ||
                                           subType == DisplayerSubType.COLUMN_STACKED);

        if (!isBar) {
            axisLabelX.setInterval(0);
        }
        axisLabelX.setRotate(displayerSettings.getXAxisLabelsAngle());
        splitLineX.setShow(displayerSettings.isGridXOn(true));
        axisLabelX.setShow(displayerSettings.isXAxisShowLabels());

        xAxis.setSplitLine(splitLineX);
        xAxis.setName(displayerSettings.getXAxisTitle());
        xAxis.setAxisLabel(axisLabelX);

        axisLabelY.setShow(displayerSettings.isYAxisShowLabels());
        splitLineY.setShow(displayerSettings.isGridYOn(true));

        yAxis.setSplitLine(splitLineY);
        yAxis.setName(displayerSettings.getYAxisTitle());
        yAxis.setAxisLabel(axisLabelY);

        // timeseries do not support format
        if (!isTimeseries) {
            // must format columns 0 if number
            axisLabelX.setFormatter(buildNumberLabelFormatterForColumn(0));
            axisLabelY.setFormatter(buildNumberLabelFormatterForColumn(1));
        }

        if (isBar) {
            xAxis.setType(XAxisType.value.name());
            yAxis.setType(XAxisType.category.name());

        } else if (isTimeseries) {
            xAxis.setType(XAxisType.time.name());
            yAxis.setType(XAxisType.value.name());

            var tooltip = echartsFactory.newTooltip();
            tooltip.setTrigger("axis");
            option.setTooltip(tooltip);
        } else {
            xAxis.setType(XAxisType.category.name());
            yAxis.setType(XAxisType.value.name());
        }

        var allSeries = buildSeries();

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

    @Override
    DataSetLookupConstraints getDataSetLookupConstraints() {
        return new DataSetLookupConstraints()
                .setMaxColumns(10)
                .setMinColumns(2)
                .setExtraColumnsAllowed(true)
                .setExtraColumnsType(ColumnType.NUMBER)
                .setColumnTypes(new ColumnType[]{
                                                 ColumnType.LABEL,
                                                 ColumnType.NUMBER});
    }

    protected abstract Series[] buildSeries();
}

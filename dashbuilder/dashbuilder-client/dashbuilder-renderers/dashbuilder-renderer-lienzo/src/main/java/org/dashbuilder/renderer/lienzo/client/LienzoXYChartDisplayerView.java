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

import com.ait.lienzo.charts.client.core.axis.CategoryAxis;
import com.ait.lienzo.charts.client.core.axis.NumericAxis;
import com.ait.lienzo.charts.client.core.xy.XYChart;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.event.ValueSelectedEvent;
import com.ait.lienzo.charts.client.core.xy.event.ValueSelectedHandler;
import com.ait.lienzo.charts.shared.core.types.ChartOrientation;
import com.google.gwt.core.client.GWT;

public abstract class LienzoXYChartDisplayerView<P extends LienzoXYChartDisplayer,C extends XYChart>
        extends LienzoCategoriesDisplayerView<P,C>
        implements LienzoXYChartDisplayer.View<P> {

    protected String xAxisTitle = null;
    protected String yAxisTitle = null;
    protected boolean showXLabels = false;
    protected boolean showYLabels = false;
    protected boolean horizontal = false;

    @Override
    public void setShowXLabels(boolean showXLabels) {
        this.showXLabels = showXLabels;
    }

    @Override
    public void setShowYLabels(boolean showYLabels) {
        this.showYLabels = showYLabels;
    }

    @Override
    public void setXAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
    }

    @Override
    public void setYAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
    }

    @Override
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    protected void configureChart(C chart) {

        chart.setOrientation(horizontal ? ChartOrientation.HORIZNONAL : ChartOrientation.VERTICAL);
        chart.setShowCategoriesAxisTitle(showXLabels);
        chart.setShowValuesAxisTitle(showYLabels);

        // TODO: Category and Number types?
        CategoryAxis categoryAxis = new CategoryAxis(xAxisTitle);
        NumericAxis numericAxis = new NumericAxis(yAxisTitle);
        chart.setCategoriesAxis(categoryAxis);
        chart.setValuesAxis(numericAxis);

        if (filterEnabled) {
            chart.addValueSelectedHandler(new ValueSelectedHandler() {
                public void onValueSelected(ValueSelectedEvent event) {
                    GWT.log("filtering by serie [" + event.getSeries() + "], " +
                            "column [" + event.getColumn() + "] " +
                            "and row [" + event.getRow() + "]");
                    getPresenter().onCategorySelected(event.getColumn(), event.getRow());
                }
            });
        }

        super.configureChart(chart);
    }

    // Data generation

    protected XYChartData createChartData() {
        XYChartData chartData = new XYChartData(getDataTable());
        if (categoriesColumn != null) {
            if (!seriesColumnList.isEmpty()) {
                chartData.setCategoryAxisProperty(categoriesColumn.columnId);
                for (int i=0; i<seriesColumnList.size(); i++) {
                    Column dataColumn = seriesColumnList.get(i);
                    String columnId = dataColumn.columnId;
                    String columnName = dataColumn.columnName;

                    XYChartSeries series = new XYChartSeries(columnName, getSeriesColor(i), columnId);
                    chartData.addSeries(series);
                }
            } else {
                GWT.log("No values columns specified.");
            }
        } else {
            GWT.log("No categories column specified.");
        }
        return chartData;
    }
}

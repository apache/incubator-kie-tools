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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.geochart.GeoChart;
import com.googlecode.gwt.charts.client.options.ChartArea;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.Options;
import com.googlecode.gwt.charts.client.options.VAxis;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;
import org.gwtbootstrap3.client.ui.Label;

public abstract class GoogleCategoriesDisplayerView<P extends GoogleCategoriesDisplayer>
        extends GoogleChartDisplayerView<P>
        implements GoogleCategoriesDisplayer.View<P> {

    private CoreChartWidget chart = null;
    protected String bgColor = null;
    protected boolean showXLabels = false;
    protected boolean showYLabels = false;
    protected String xAxisTitle = null;
    protected String yAxisTitle = null;
    protected int xAxisAngle = 0;
    protected boolean animationOn = false;
    protected int animationDuration = 700;
    protected String[] colors = null;

    @Override
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

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
    public void setXAxisAngle(int xAxisAngle) {
        this.xAxisAngle = xAxisAngle;
    }

    @Override
    public void setYAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
    }

    @Override
    public void setColors(String[] colors) {
        this.colors = colors;
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        if (enabled) {
            checkChartCreated();
            chart.addSelectHandler(createSelectHandler(chart));
        }
    }

    @Override
    public void setAnimationOn(boolean animationOn) {
        this.animationOn = animationOn;
    }

    @Override
    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    @Override
    public void createChart() {
        chart = _createChart();
    }

    @Override
    public void drawChart() {
        checkChartCreated();
        chart.draw(getDataTable(), createOptions());
        super.showDisplayer(chart);
    }

    @Override
    public void nodata() {
        FlowPanel noDataPanel = new FlowPanel();
        noDataPanel.setWidth(width + "px");
        noDataPanel.setHeight(height + "px");
        noDataPanel.add(new Label(GoogleDisplayerConstants.INSTANCE.common_noData()));

        super.showDisplayer(noDataPanel);
    }

    // Common methods used in subclasses

    protected void checkChartCreated() {
        if (chart == null) {
            throw new RuntimeException("Chart not created. Call to view.createChart() first");
        }
    }

    protected CoreChartWidget _createChart() {
        return null;
    }

    protected Options createOptions() {
        return null;
    }

    protected SelectHandler createSelectHandler(final CoreChartWidget selectable) {
        return new SelectHandler() {
            public void onSelect(SelectEvent event) {
                JsArray<Selection> selections = selectable.getSelection();
                for (int i = 0; i < selections.length(); i++) {
                    Selection selection = selections.get(i);
                    int row = selection.getRow();
                    getPresenter().onCategorySelected(getDataTable().getColumnId(0), row);
                }
            }
        };
    }

    protected SelectHandler createSelectHandler(final GeoChart selectable) {
        return new SelectHandler() {
            public void onSelect(SelectEvent event) {
                JsArray<Selection> selections = selectable.getSelection();
                for (int i = 0; i < selections.length(); i++) {
                    Selection selection = selections.get(i);
                    int row = selection.getRow();
                    getPresenter().onCategorySelected(getDataTable().getColumnId(0), row);
                }
            }
        };
    }

    protected ChartArea createChartArea() {
        int chartWidth = width - marginRight - marginLeft;
        int chartHeight = height - marginTop - marginBottom;

        ChartArea chartArea = ChartArea.create();
        chartArea.setLeft(marginLeft);
        chartArea.setTop(marginTop);
        chartArea.setWidth(chartWidth);
        chartArea.setHeight(chartHeight);
        return chartArea;
    }

    protected HAxis createHAxis() {
        HAxis xAxis = xAxisTitle == null ? HAxis.create() : HAxis.create(xAxisTitle);
        xAxis.setSlantedTextAngle(xAxisAngle);
        return xAxis;
    }

    protected VAxis createVAxis() {
        return yAxisTitle == null ? null : VAxis.create(yAxisTitle);
    }
}

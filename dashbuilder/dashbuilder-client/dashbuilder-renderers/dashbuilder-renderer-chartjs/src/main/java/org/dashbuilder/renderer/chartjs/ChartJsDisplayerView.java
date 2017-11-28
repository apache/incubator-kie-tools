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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.chartjs.lib.Chart;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartData;
import org.dashbuilder.renderer.chartjs.lib.data.AreaChartDataProvider;
import org.dashbuilder.renderer.chartjs.lib.data.AreaSeries;
import org.dashbuilder.renderer.chartjs.lib.data.SeriesBuilder;
import org.dashbuilder.renderer.chartjs.lib.event.DataSelectionEvent;
import org.dashbuilder.renderer.chartjs.lib.event.DataSelectionHandler;
import org.dashbuilder.renderer.chartjs.resources.i18n.ChartJsDisplayerConstants;
import org.gwtbootstrap3.client.ui.Label;

public abstract class ChartJsDisplayerView<P extends ChartJsDisplayer>
        extends AbstractGwtDisplayerView<P>
        implements ChartJsDisplayer.View<P> {

    private Panel container = new FlowPanel();
    private Panel filterPanel = new HorizontalPanel();
    private Panel displayerPanel = new FlowPanel();
    private HTML titleHtml = new HTML();
    private Chart chart = null;
    private List<String> labels = new ArrayList<String>();
    private JsArray<AreaSeries> series;

    protected boolean filterEnabled = false;
    protected String title = null;
    protected int width = 500;
    protected int height= 300;
    protected int marginTop = 10;
    protected int marginBottom = 10;
    protected int marginRight = 10;
    protected int marginLeft = 10;

    public void showDisplayer(Widget w) {
        displayerPanel.clear();
        displayerPanel.add(w);
    }

    @Override
    public void init(P presenter) {
        super.setPresenter(presenter);
        super.setVisualization(container);

        container.add(titleHtml);
        container.add(filterPanel);
        container.add(displayerPanel);

        filterPanel.getElement().setAttribute("cellpadding", "2");
    }

    @Override
    public void clear() {
        super.clear();
        ChartJsRenderer.closeDisplayer(getPresenter());
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    @Override
    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Override
    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    @Override
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Override
    public void dataClear() {
        labels.clear();
        series =  JavaScriptObject.createArray().cast();
    }

    @Override
    public void dataAddLabel(String label) {
        labels.add(label);
    }

    @Override
    public void dataAddSerie(String columnName, String color, double[] values) {
        series.push(SeriesBuilder.create()
                .withLabel(columnName)
                .withFillColor(color)
                .withStoreColor(color)
                .withPointColor(color)
                .withPointStrokeColor("#FFFF")
                .withData(values)
                .get());
    }

    @Override
    public String getGroupsTitle() {
        return ChartJsDisplayerConstants.INSTANCE.common_Categories();
    }

    @Override
    public String getColumnsTitle() {
        return ChartJsDisplayerConstants.INSTANCE.common_Series();
    }

    @Override
    public void setTitle(String title) {
        titleHtml.setText(title);
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        this.filterEnabled = enabled;
    }

    @Override
    public void clearFilterStatus() {
        if (filterPanel != null) {
            filterPanel.clear();
        }
    }

    @Override
    public void addFilterValue(String value) {
        filterPanel.add(new Label(value));
    }

    @Override
    public void addFilterReset() {
        Anchor anchor = new Anchor(ChartJsDisplayerConstants.INSTANCE.chartjsDisplayer_resetAnchor());
        filterPanel.add(anchor);
        anchor.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                getPresenter().onFilterResetClicked();
            }
        });
    }

    @Override
    public void nodata() {
        showDisplayer(new Label(ChartJsDisplayerConstants.INSTANCE.common_noData()));
    }

    @Override
    public void drawChart() {
        if (chart == null) {
            chart = createChart();
            showDisplayer(chart);
        }
        if (filterEnabled) {
            chart.addDataSelectionHandler(new DataSelectionHandler() {
                public void onDataSelected(DataSelectionEvent event) {
                    // TODO
                    Object o = event.getSource();
                }
            });
        }
        adjustChartSize(chart);
        chart.update();
    }

    // Common methods used in subclasses

    protected Chart createChart() {
        return null;
    }

    protected void adjustChartSize(Chart chart) {
        int chartWidth = width-marginLeft;
        int chartHeight = height-marginTop;

        chart.getElement().getStyle().setPaddingTop(marginTop, Style.Unit.PX);
        chart.getElement().getStyle().setPaddingLeft(marginLeft, Style.Unit.PX);
        chart.setPixelWidth(chartWidth);
        chart.setPixelHeight(chartHeight);
    }

    protected AreaChartDataProvider createAreaDataProvider() {
        return new AreaChartDataProvider() {
            public JavaScriptObject getData() {
                return createChartData();
            }
            public void reload(AsyncCallback<AreaChartData> callback) {
                AreaChartData data = createChartData();
                callback.onSuccess(data);
            }
        };
    }

    protected AreaChartData createChartData() {
        String[] labelsArray = new String[labels.size()];
        labels.toArray(labelsArray);
        AreaChartData data = JavaScriptObject.createObject().cast();
        data.setLabels(labelsArray);
        data.setSeries(series);
        return data;
    }
}

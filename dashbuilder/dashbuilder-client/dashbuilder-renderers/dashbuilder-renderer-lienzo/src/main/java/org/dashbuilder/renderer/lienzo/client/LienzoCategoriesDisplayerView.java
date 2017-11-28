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

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEvent;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEventHandler;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import org.dashbuilder.dataset.ColumnType;

public abstract class LienzoCategoriesDisplayerView<P extends LienzoDisplayer, C extends AbstractChart>
        extends LienzoDisplayerView<P> {

    public static final ColorName[] DEFAULT_SERIE_COLORS = new ColorName[] {
            ColorName.DEEPSKYBLUE, ColorName.RED, ColorName.YELLOWGREEN
    };

    public static final int PANEL_MARGIN = 50;
    public static final String PIXEL = "px";
    public static final int ANIMATION_DURATION = 500;

    private C chart = null;
    private Layer layer = new Layer();
    private LienzoPanel chartPanel = new LienzoPanel();

    @Override
    public void showTitle(String title) {
        super.showTitle(title);
        chart.setX(0).setY(0).setName(title);
    }

    @Override
    public void drawChart() {
        chart = _createChart();
        configureChart(chart);

        layer.setTransformable(true);
        layer.add(chart);

        chartPanel.add(layer);
        resizePanel(width, height);

        super.showDisplayer(chartPanel);
        layer.draw();
    }

    @Override
    public void reloadChart() {
        _reloadChart(chart);
        super.showDisplayer(chartPanel);
    }

    protected abstract C _createChart();

    protected abstract void _reloadChart(C chart);

    protected void configureChart(C chart) {
        chart.setWidth(width);
        chart.setHeight(height);
        chart.setMarginLeft(marginLeft);
        chart.setMarginRight(marginRight);
        chart.setMarginTop(marginTop);
        chart.setMarginBottom(marginBottom);
        chart.setFontFamily(fontFamily);
        chart.setFontStyle(fontStyle);
        chart.setFontSize(fontSize);
        chart.setShowTitle(true); // TODO: Bug in Lienzo charting -> If title not visible -> javascript error (nullpointer)
        chart.setResizable(resizeEnabled);

        if (resizeEnabled) {
            chart.addChartResizeEventHandler(new ChartResizeEventHandler() {
                public void onChartResize(ChartResizeEvent event) {
                    resizePanel((int) event.getWidth(), (int) event.getHeight());
                }
            });
        }

        chart.draw();

        // Create the Chart using animations.
        chart.init(AnimationTweener.LINEAR, ANIMATION_DURATION);
    }

    protected void resizePanel(int w, int h) {
        String _w = w + PANEL_MARGIN + PIXEL;
        String _h = h + PANEL_MARGIN + PIXEL;
        chartPanel.setSize(_w, _h);
    }

    protected IColor getSeriesColor(int index) {
        int defaultColorsSize = DEFAULT_SERIE_COLORS.length;
        if (index >= defaultColorsSize) {
            return ColorName.getValues().get(90 + index*2);
        }
        return DEFAULT_SERIE_COLORS[index];
    }

    // Data generation

    protected Column categoriesColumn = null;
    protected List<Column> seriesColumnList = new ArrayList<Column>();

    class Column {
        String columnId;
        String columnName;
        ColumnType columnType;

        public Column(String columnId, String columnName, ColumnType columnType) {
            this.columnId = columnId;
            this.columnName = columnName;
            this.columnType = columnType;
        }
    }

    @Override
    public void dataClear() {
        super.dataClear();
        categoriesColumn = null;
        seriesColumnList.clear();
    }

    @Override
    public void dataAddColumn(String columnId, String columnName, ColumnType columnType) {
        super.dataAddColumn(columnId, columnName, columnType);
        Column newColumn = new Column(columnId, columnName, columnType);
        if (categoriesColumn == null) {
            categoriesColumn = newColumn;
        } else {
            seriesColumnList.add(newColumn);
        }
    }
}

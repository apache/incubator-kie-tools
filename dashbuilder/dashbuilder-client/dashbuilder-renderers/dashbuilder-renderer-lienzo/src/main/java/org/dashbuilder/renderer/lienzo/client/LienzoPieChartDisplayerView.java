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

import com.ait.lienzo.charts.client.core.model.PieChartData;
import com.ait.lienzo.charts.client.core.pie.PieChart;
import com.ait.lienzo.charts.client.core.pie.event.ValueSelectedEvent;
import com.ait.lienzo.charts.client.core.pie.event.ValueSelectedHandler;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.google.gwt.core.client.GWT;
import org.dashbuilder.renderer.lienzo.client.resources.i18n.LienzoDisplayerConstants;

public class LienzoPieChartDisplayerView
        extends LienzoCategoriesDisplayerView<LienzoPieChartDisplayer,PieChart>
        implements LienzoPieChartDisplayer.View {

    @Override
    public String getColumnsTitle() {
        return LienzoDisplayerConstants.INSTANCE.values();
    }

    @Override
    protected PieChart _createChart() {
        PieChart chart = new PieChart();
        chart.setData(createPieChartData());
        return chart;
    }

    @Override
    protected void _reloadChart(PieChart chart) {
        chart.reload(createPieChartData(), AnimationTweener.LINEAR, ANIMATION_DURATION);
    }

    protected PieChartData createPieChartData() {
        return new PieChartData(getDataTable(),
                categoriesColumn.columnId,
                seriesColumnList.get(0).columnId);
    }

    protected void configureChart(PieChart chart) {

        if (filterEnabled) {
            chart.addValueSelectedHandler(new ValueSelectedHandler() {
                public void onValueSelected(ValueSelectedEvent event) {
                    GWT.log("filtering by column [" + event.getColumn() + "], row [" + event.getRow() + "]");
                    getPresenter().onCategorySelected(event.getColumn(), event.getRow());
                }
            });
        }
        super.configureChart(chart);
    }
}

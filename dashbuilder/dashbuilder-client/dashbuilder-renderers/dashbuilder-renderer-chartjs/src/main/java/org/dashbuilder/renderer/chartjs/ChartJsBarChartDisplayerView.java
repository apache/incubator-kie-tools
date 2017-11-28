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

import org.dashbuilder.renderer.chartjs.lib.BarChart;
import org.dashbuilder.renderer.chartjs.lib.Chart;

public class ChartJsBarChartDisplayerView
        extends ChartJsDisplayerView<ChartJsBarChartDisplayer>
        implements ChartJsBarChartDisplayer.View {

    @Override
    protected Chart createChart() {
        BarChart chart = new BarChart();
        if (title != null) {
            chart.setTitle(title);
        }
        String displayerId = getPresenter().getDisplayerId();
        chart.setTooltipTemplate("<%= label %>: <%= window.chartJsFormatValue('" + displayerId + "', value, 1) %>");
        chart.setMultiTooltipTemplate("<%= datasetLabel %>: <%= window.chartJsFormatValue('" + displayerId + "', value, 1) %>");
        chart.setScaleStepWidth(10);
        chart.setLegendTemplate("<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].fillColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>");
        chart.setDataProvider(super.createAreaDataProvider());
        return chart;
    }
}

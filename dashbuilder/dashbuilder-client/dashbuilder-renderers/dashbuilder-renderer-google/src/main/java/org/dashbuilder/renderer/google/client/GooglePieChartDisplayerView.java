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

import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.options.Options;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;

public class GooglePieChartDisplayerView
        extends GoogleCategoriesDisplayerView<GooglePieChartDisplayer>
        implements GooglePieChartDisplayer.View {

    @Override
    public String getColumnsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Values();
    }

    @Override
    protected CoreChartWidget _createChart() {
        return new PieChart();
    }

    @Override
    protected Options createOptions() {
        PieChartOptionsWrapper options = PieChartOptionsWrapper.newInstance();
        options.setWidth(width);
        options.setHeight(height);
        options.setBackgroundColor(bgColor);
        options.setLegend(createChartLegend());
        options.setColors(colors);
        options.setChartArea(createChartArea());
        options.setIs3D(DisplayerSubType.PIE_3D.equals(subType));
        options.setHole(DisplayerSubType.DONUT.equals(subType) ? 0.4d : 0);
        return options.get();
    }
}

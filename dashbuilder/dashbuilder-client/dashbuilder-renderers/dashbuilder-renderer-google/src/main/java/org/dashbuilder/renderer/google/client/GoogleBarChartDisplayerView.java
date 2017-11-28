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

import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.options.Animation;
import com.googlecode.gwt.charts.client.options.AnimationEasing;
import com.googlecode.gwt.charts.client.options.CoreOptions;
import com.googlecode.gwt.charts.client.options.Options;

public class GoogleBarChartDisplayerView
        extends GoogleCategoriesDisplayerView<GoogleBarChartDisplayer>
        implements GoogleBarChartDisplayer.View {

    protected boolean isBar = true;
    protected boolean isStacked = true;

    @Override
    public void setIsBar(boolean isBar) {
        this.isBar = isBar;
    }

    @Override
    public void setIsStacked(boolean isStacked) {
        this.isStacked = isStacked;
    }

    @Override
    protected CoreChartWidget _createChart() {
        return isBar ? new BarChart() : new ColumnChart();
    }

    @Override
    protected Options createOptions() {
        return isBar ? createBarOptions() : createColumnOptions();
    }

    protected CoreOptions createBarOptions() {
        BarChartOptions options = BarChartOptions.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setBackgroundColor(bgColor);
        options.setLegend(createChartLegend());
        options.setIsStacked(isStacked);
        options.setHAxis(createHAxis());
        options.setVAxis(createVAxis());

        if (animationOn) {
            Animation anim = Animation.create();
            anim.setDuration(animationDuration);
            anim.setEasing(AnimationEasing.IN_AND_OUT);
            options.setAnimation(anim);
        }
        // TODO: options.set3D(displayerSettings.is3d());
        options.setChartArea(createChartArea());
        options.setColors(colors);
        return options;
    }

    protected CoreOptions createColumnOptions() {
        ColumnChartOptions options = ColumnChartOptions.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setBackgroundColor(bgColor);
        options.setLegend(createChartLegend());
        options.setIsStacked(isStacked);
        options.setChartArea(createChartArea());
        options.setColors(colors);

        if (showXLabels) {
            options.setHAxis(createHAxis());
        }
        if (showYLabels) {
            options.setVAxis(createVAxis());
        }
        if (animationOn) {
            Animation anim = Animation.create();
            anim.setDuration(animationDuration);
            anim.setEasing(AnimationEasing.IN_AND_OUT);
            options.setAnimation(anim);
        }
        return options;
    }
}

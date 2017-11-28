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
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.Animation;
import com.googlecode.gwt.charts.client.options.AnimationEasing;
import com.googlecode.gwt.charts.client.options.CurveType;
import com.googlecode.gwt.charts.client.options.Options;
import org.dashbuilder.displayer.DisplayerSubType;

public class GoogleLineChartDisplayerView
        extends GoogleCategoriesDisplayerView<GoogleLineChartDisplayer>
        implements GoogleLineChartDisplayer.View {

    @Override
    protected  CoreChartWidget _createChart() {
        return new LineChart();
    }

    @Override
    protected Options createOptions() {
        boolean isLine = DisplayerSubType.LINE.equals(subType);

        LineChartOptions options = LineChartOptions.create();
        options.setCurveType(isLine ? CurveType.NONE : CurveType.FUNCTION);
        options.setWidth(width);
        options.setHeight(height);
        options.setBackgroundColor(bgColor);
        options.setChartArea(createChartArea());
        options.setLegend(createChartLegend());
        options.setColors(colors);
        options.setHAxis(createHAxis());
        options.setVAxis(createVAxis());

        if (animationOn) {
            Animation anim = Animation.create();
            anim.setDuration(animationDuration);
            anim.setEasing(AnimationEasing.IN_AND_OUT);
            options.setAnimation(anim);
        }
        return options;
    }
}

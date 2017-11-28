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

import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.options.Animation;
import com.googlecode.gwt.charts.client.options.AnimationEasing;

public class GoogleAreaChartDisplayerView
        extends GoogleCategoriesDisplayerView<GoogleAreaChartDisplayer>
        implements GoogleAreaChartDisplayer.View {

    protected boolean isStacked = true;

    @Override
    public void setIsStacked(boolean isStacked) {
        this.isStacked = isStacked;
    }

    @Override
    protected CoreChartWidget _createChart() {
        return new AreaChart();
    }

    @Override
    protected AreaChartOptions createOptions() {
        AreaChartOptions options = AreaChartOptions.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setBackgroundColor(bgColor);
        options.setLegend(createChartLegend());
        options.setChartArea(createChartArea());
        options.setColors(colors);
        options.setIsStacked(isStacked);
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

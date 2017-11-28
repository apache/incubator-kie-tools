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

import com.googlecode.gwt.charts.client.corechart.BubbleChart;
import com.googlecode.gwt.charts.client.corechart.BubbleChartOptions;
import com.googlecode.gwt.charts.client.corechart.CoreChartWidget;
import com.googlecode.gwt.charts.client.options.Animation;
import com.googlecode.gwt.charts.client.options.AnimationEasing;
import com.googlecode.gwt.charts.client.options.Options;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;

public class GoogleBubbleChartDisplayerView
        extends GoogleCategoriesDisplayerView<GoogleBubbleChartDisplayer>
        implements GoogleBubbleChartDisplayer.View {

    @Override
    public String getColumnsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Values();
    }

    @Override
    public String getXAxisColumnTitle() {
        return GoogleDisplayerConstants.INSTANCE.googleBubbleDisplayer_XAxis();
    }

    @Override
    public String getYAxisColumnTitle() {
        return GoogleDisplayerConstants.INSTANCE.googleBubbleDisplayer_YAxis();
    }

    @Override
    public String getBubbleColorColumnTitle() {
        return GoogleDisplayerConstants.INSTANCE.googleBubbleDisplayer_BubbleColor();
    }

    @Override
    public String getBubbleSizeColumnTitle() {
        return GoogleDisplayerConstants.INSTANCE.googleBubbleDisplayer_BubbleSize();
    }

    @Override
    protected CoreChartWidget _createChart() {
        return new BubbleChart();
    }

    @Override
    protected Options createOptions() {
        BubbleChartOptions options = BubbleChartOptions.create();
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

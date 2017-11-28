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

import com.googlecode.gwt.charts.client.gauge.Gauge;
import com.googlecode.gwt.charts.client.gauge.GaugeOptions;
import com.googlecode.gwt.charts.client.options.Animation;
import com.googlecode.gwt.charts.client.options.AnimationEasing;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;

public class GoogleMeterChartDisplayerView
        extends GoogleCategoriesDisplayerView<GoogleMeterChartDisplayer>
        implements GoogleMeterChartDisplayer.View {

    private Gauge meter = null;
    protected long meterStart = 0;
    protected long meterWarning = 600;
    protected long meterCritical = 800;
    protected long meterEnd = 1000;

    @Override
    public void setMeterStart(long meterStart) {
        this.meterStart = meterStart;
    }

    @Override
    public void setMeterWarning(long meterWarning) {
        this.meterWarning = meterWarning;
    }

    @Override
    public void setMeterCritical(long meterCritical) {
        this.meterCritical = meterCritical;
    }

    @Override
    public void setMeterEnd(long meterEnd) {
        this.meterEnd = meterEnd;
    }

    @Override
    public String getColumnsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Value();
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        // Metrics does not provide filter support
    }

    @Override
    public void createChart() {
        meter = new Gauge();
    }

    @Override
    public void drawChart() {
        meter.draw(getDataTable(), createMeterOptions());
        super.showDisplayer(meter);
    }

    protected GaugeOptions createMeterOptions() {
        GaugeOptions options = GaugeOptions.create();
        options.setWidth(width);
        options.setWidth(width);
        options.setHeight(height);
        options.setMin(meterStart);
        options.setMax(meterEnd);
        options.setGreenFrom(meterStart);
        options.setGreenTo(meterWarning);
        options.setYellowFrom(meterWarning);
        options.setYellowTo(meterCritical);
        options.setRedFrom(meterCritical);
        options.setRedTo(meterEnd);
        options.setGreenColor("#0088CE");
        options.setYellowColor("#EC7A08");
        options.setRedColor("#CC0000");
        if (animationOn) {
            Animation anim = Animation.create();
            anim.setDuration(animationDuration);
            anim.setEasing(AnimationEasing.IN_AND_OUT);
            options.setAnimation(anim);
        }
        return options;
    }
}

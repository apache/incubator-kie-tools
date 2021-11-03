/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.c3.client.jsbinding;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.renderer.c3.client.jsbinding.C3Point.RadiusCallback;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Tick.FormatterCallback;

import elemental2.core.JsObject;

/**
 * Build C3 JS Types
 */
@ApplicationScoped
public class C3JsTypesFactory {
    
    public C3Tick createC3Tick(FormatterCallback callback) {
        C3Tick instance = new C3Tick();
        instance.setFormat(callback);
        return instance;
    }
    
    public C3Grid c3Grid(boolean showX, boolean showY) {
        return C3Grid.create(C3GridConf.create(showX), 
                             C3GridConf.create(showY));
    }

    public C3Padding c3Padding(int top, int right, 
                               int bottom, int left) {
        return C3Padding.create(top, right, bottom, left);
    }

    public C3ChartSize c3ChartSize(double width, double height) {
        return C3ChartSize.create(width, height);
    }

    public C3Selection c3Selection(boolean enabled, boolean multiple, 
                                   boolean grouped) {
        return C3Selection.create(enabled, multiple, grouped);
    }

    public C3AxisY c3AxisY(boolean show, C3Tick tickY) {
        return C3AxisY.create(show, tickY);
    }
    public C3AxisX c3AxisX(String type, String[] categories, C3Tick tick) {
        return C3AxisX.create(type, categories, tick);
    }
    
    
    public C3Legend c3Legend(boolean show, String position) {
        return C3Legend.create(show, position);
    }

    public C3Point c3Point(RadiusCallback r) {
        return C3Point.create(r);
    }

    public C3ChartData c3ChartData(String[][] columns, String type, 
                                   String[][] groups, JsObject xs,
                                   C3Selection selection) {
        return C3ChartData.create(columns, type, groups, xs, selection);
    }

    public C3AxisInfo c3AxisInfo(boolean rotated, C3AxisX axisX, C3AxisY axisY) {
        return C3AxisInfo.create(rotated, axisX, axisY);
    }



    public C3ChartConf c3ChartConf(C3ChartSize size, 
                                   C3ChartData data, 
                                   C3AxisInfo axis, 
                                   C3Grid grid,
                                   C3Transition transition, 
                                   C3Point point, 
                                   C3Padding padding, 
                                   C3Legend legend,
                                   C3Color color) {
        return C3ChartConf.create(size, data, axis, grid, transition, point, padding, legend, color);
    }

    public C3Transition c3Transition(int duration) {
        return C3Transition.create(duration);
    }

    public C3AxisLabel createC3Label(String text, String position) {
        return C3AxisLabel.create(text, position);
    }
    
    public C3Color c3Color(String[] pattern) {
        return C3Color.create(pattern);
    }
    
    public C3Color c3Color(String[] pattern, C3Threshold threshold) {
        return C3Color.create(pattern, threshold);
    }
    
    public C3Threshold c3Threshold(int[] values) {
        return C3Threshold.create(values);
    }
    
    public C3Gauge c3Gauge(int min, int max) {
        return C3Gauge.create(min, max);
    }

    public C3Donut c3Donut(String holeTitle) {
        return C3Donut.create(holeTitle);
    }

    public C3Tooltip c3Tooltip(C3Format.FormatCallback callback) {
        C3Format format = C3Format.create(callback);
        return C3Tooltip.create(format);
    }    

}
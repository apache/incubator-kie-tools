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

import com.google.gwt.user.client.Element;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Bind the type that should be passed to c3.generate
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3ChartConf {  
  
    @JsOverlay
    static C3ChartConf create(C3ChartSize size, 
                              C3ChartData data, 
                              C3AxisInfo axis,
                              C3Grid grid,
                              C3Transition transition,
                              C3Point point,
                              C3Padding padding,
                              C3Legend legend,
                              C3Color color) {
        C3ChartConf instance = new C3ChartConf();
        instance.setSize(size);
        instance.setData(data);
        instance.setAxis(axis);
        instance.setGrid(grid);
        instance.setTransition(transition);
        instance.setPoint(point);
        instance.setPadding(padding);
        instance.setLegend(legend);
        instance.setColor(color);
        return instance;
    }
    
    @JsOverlay
    static C3ChartConf create(C3ChartSize size, 
                              C3ChartData data, 
                              C3AxisInfo axis,
                              C3Grid grid,
                              C3Transition transition,
                              C3Point point,
                              C3Padding padding,
                              C3Legend legend,
                              C3Color color,
                              C3Tooltip tooltip) {
        C3ChartConf instance = new C3ChartConf();
        instance.setSize(size);
        instance.setData(data);
        instance.setAxis(axis);
        instance.setGrid(grid);
        instance.setTransition(transition);
        instance.setPoint(point);
        instance.setPadding(padding);
        instance.setLegend(legend);
        instance.setColor(color);
        instance.setTooltip(tooltip);
        return instance;
    }    
    
    @JsProperty
    public native void setBindto(Element element);
    
    @JsProperty
    public native void setSize(C3ChartSize size);
    
    @JsProperty
    public native void setData(C3ChartData data);
    
    @JsProperty
    public native void setAxis(C3AxisInfo axis);
    
    @JsProperty
    public native C3AxisInfo getAxis();
    
    @JsProperty
    public native void setGrid(C3Grid grid);
    
    @JsProperty
    public native void setTransition(C3Transition transition);
    
    @JsProperty
    public native void setPoint(C3Point point);
    
    @JsProperty
    public native void setPadding(C3Padding padding);

    @JsProperty
    public native void setLegend(C3Legend legend);
    
    @JsProperty
    public native void setTooltip(C3Tooltip tooltip);    
    
    @JsProperty
    public native void setOnrendered(RenderedCallback callback);
    
    
    @JsFunction
    @FunctionalInterface
    public interface RenderedCallback {
        
        void callback();
    
    }
    
    @JsProperty
    public native void setColor(C3Color color);    
    
    @JsProperty
    public native void setGauge(C3Gauge gauge);

    @JsProperty
    public native void setDonut(C3Donut donut);

}
/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.echarts.client.js;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public interface ECharts {

    Chart init(HTMLElement element);

    Chart init(HTMLElement element, String theme);

    Chart init(HTMLElement element, String theme, ChartInitParams initParams);

    @JsType(isNative = true)
    public class Chart {

        public native void setOption(Option option);

        public native void dispose();

        public native void resize();

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class ChartInitParams {

        @JsProperty
        public native void setWidth(int width);

        @JsProperty
        public native void setHeight(int height);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Option {

        @JsProperty(name = "xAxis")
        public native void setXAxis(XAxis xAxis);

        @JsProperty(name = "yAxis")
        public native void setYAxis(YAxis yAxis);

        @JsProperty
        public native void setDataset(Dataset dataset);

        @JsProperty
        public native void setBackgroundColor(String bgColor);

        @JsProperty
        public native void setSeries(Series[] series);

        @JsProperty
        public native void setSeries(Series series);

        @JsProperty
        public native void setTitle(Title title);

        @JsProperty
        public native void setTooltip(Tooltip tooltip);

        @JsProperty
        public native void setGrid(Grid grid);

        @JsProperty
        public native void setDataZoom(DataZoom zoom);

        @JsProperty
        public native void setLegend(Legend legend);

        @JsProperty
        public native void setColor(String[] colors);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class DataZoom {

        @JsProperty
        public native void setType(String type);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class AreaStyle {

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Title {

        @JsProperty
        public native void setText(String title);

        @JsProperty
        public native void setLeft(String left);

        @JsProperty
        public native void setShow(boolean show);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Legend {

        @JsProperty
        public native void setShow(boolean show);

        @JsProperty
        public native void setLeft(String left);

        @JsProperty
        public native void setTop(String top);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Grid {

        @JsProperty
        public native void setContainLabel(boolean containLabel);

        @JsProperty
        public native void setLeft(int left);

        @JsProperty
        public native void setTop(int top);

        @JsProperty
        public native void setBottom(int bottom);

        @JsProperty
        public native void setRight(int right);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class SplitLine extends Axis {

        @JsProperty
        public native void setShow(boolean show);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    class Axis {

        @JsProperty
        public native void setSplitLine(SplitLine splitLine);

        @JsProperty
        public native void setAxisLabel(AxisLabel axisLabel);

        @JsProperty
        public native void setName(String name);

        @JsProperty
        public native void setType(String xAxisType);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class YAxis extends Axis {

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class XAxis extends Axis {

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class AxisLabel {

        @JsProperty
        public native void setInterval(int interval);

        @JsProperty
        public native void setRotate(int rotate);

        @JsProperty
        public native void setShow(boolean show);

        @JsProperty
        public native void setFormatter(ValueFormatterCallback valueFormatter);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Series {

        @JsProperty
        public native void setType(String seriesType);
        
        @JsProperty
        public native void setName(String name);

        @JsProperty
        public native void setAreaStyle(AreaStyle areaStyle);

        @JsProperty
        public native void setSmooth(boolean smooth);

        @JsProperty
        public native void setRadius(String[] radius);

        @JsProperty
        public native void setLeft(int left);

        @JsProperty
        public native void setTop(int top);

        @JsProperty
        public native void setBottom(int bottom);

        @JsProperty
        public native void setRight(int right);

        @JsProperty
        public native void setEncode(Encode encode);
        
        @JsProperty
        public native void setStack(String stackType);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Encode {

        @JsProperty
        public native void setX(String x);

        @JsProperty
        public native void setY(String y);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Tooltip {

        @JsProperty
        public native void setValueFormatter(ValueFormatterCallback callback);

    }

    @JsFunction
    @FunctionalInterface
    interface ValueFormatterCallback {

        Object execute(Object object);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Dataset {

        @JsProperty
        public native void setDimensions(String[] dimensions);

        @JsProperty
        public native void setSource(Object[][] source);

    }

    // Split line

    public enum SeriesType {
        bar,
        line,
        pie,
        gauge;

    }

    public enum XAxisType {
        category,
        value;
    }    

    public class Builder {

        @JsProperty(name = "echarts", namespace = JsPackage.GLOBAL)
        public static native ECharts get();
    }
}

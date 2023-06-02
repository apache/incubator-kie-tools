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

import java.util.Arrays;

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

        @JsProperty
        public native void setRenderer(String renderer);

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
        public native Series[] getSeries();

        @JsProperty
        public native void setTitle(Title title);

        @JsProperty
        public native Title getTitle();

        @JsProperty
        public native void setTooltip(Tooltip tooltip);

        @JsProperty
        public native void setToolbox(Toolbox toolbox);

        @JsProperty
        public native void setGrid(Grid grid);

        @JsProperty
        public native void setDataZoom(DataZoom zoom);

        @JsProperty
        public native void setLegend(Legend legend);

        @JsProperty
        public native void setColor(String[] colors);

        @JsProperty
        public native void setColor(String color);

        @JsProperty
        public native void setVisualMap(VisualMap visualMap);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class DataZoom {

        @JsProperty
        public native void setType(String type);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class AreaStyle {

        @JsProperty
        public native void setOpacity(Double opacity);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Title {

        @JsProperty
        public native void setText(String title);

        @JsProperty
        public native void setSubtext(String subText);

        @JsProperty
        public native void setLeft(String left);

        @JsProperty
        public native void setTop(String top);

        @JsProperty
        public native void setShow(boolean show);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class AxisTick {

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
    public class AxisLabel extends ItemStyle {

        @JsProperty
        public native void setInterval(int interval);

        @JsProperty
        public native void setRotate(int rotate);

        @JsProperty
        public native void setDistance(int distance);

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
        public native void setCenter(String[] radius);

        @JsProperty
        public native void setRadius(String radius);

        @JsProperty
        public native void setSplitNumber(int splitNumber);

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

        @JsProperty
        public native void setSymbolSize(SymbolSizeCallback callback);

        @JsProperty
        public native void setSymbolSize(int value);

        @JsProperty
        public native void setLabel(Label label);

        @JsProperty
        public native void setAxisLabel(AxisLabel label);

        @JsProperty
        public native void setStartAngle(int startAngle);

        @JsProperty
        public native void setEndAngle(int endAngle);

        @JsProperty
        public native void setMin(double min);

        @JsProperty
        public native void setMax(double max);

        @JsProperty
        public native void setPointer(Pointer pointer);

        @JsProperty
        public native void setProgress(Progress progress);

        @JsProperty
        public native void setTitle(Title title);

        @JsProperty
        public native void setAxisLine(AxisLine axisLine);

        @JsProperty
        public native void setAxisTick(AxisTick axisTick);

        @JsProperty
        public native void setDetail(ItemStyle detail);

        @JsProperty
        public native void setData(Data[] data);

        @JsProperty
        public native void setData(Object[] data);

        @JsProperty
        public native void setData(String[][] data);

        @JsProperty
        public native void setSymbol(String syumbol);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Data {

        @JsProperty
        public native void setValue(Object value);

        @JsProperty
        public native void setName(String name);

        @JsProperty
        public native void setDetail(ItemStyle detail);

        @JsProperty
        public native void setTitle(ItemStyle title);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class AxisLine {

        @JsProperty
        public native void setLineStyle(LineStyle lineStyle);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class ItemStyle {

        @JsProperty
        public native void setShow(boolean show);

        @JsProperty
        public native void setValueAnimation(boolean valueAnimation);

        @JsProperty
        public native void setWidth(int width);

        @JsProperty
        public native void setHeight(int height);

        @JsProperty
        public native void setFontSize(int fontSize);

        @JsProperty
        public native void setColor(String color);

        @JsProperty
        public native void setBackgroundColor(String bgCOlor);

        @JsProperty
        public native void setBorderRadius(int borderRadius);

        @JsProperty
        public native void setFormatter(String formatter);

        @JsProperty
        public native void setFormatter(Object formatter);

        @JsProperty
        public native void setOffsetCenter(String[] offsetCenter);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Piece {

        @JsProperty
        public native void setMin(double min);

        @JsProperty
        public native void setMax(double max);

        @JsProperty
        public native void setColor(String color);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class LineStyle {

        @JsProperty
        public native void setWidth(int width);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Encode {

        @JsProperty
        public native void setX(String x);

        @JsProperty
        public native void setY(String y);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Pointer {

        @JsProperty
        public native void setShow(boolean show);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Progress {

        @JsProperty
        public native void setShow(boolean show);

        @JsProperty
        public native void setOverlap(boolean overlap);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Label {

        @JsProperty
        public native void setFormatter(LabelFormatterCallback callback);

        @JsProperty
        public native void setShow(boolean show);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Tooltip {

        @JsProperty
        public native void setValueFormatter(ValueFormatterCallback callback);

        @JsProperty
        public native void setTrigger(String trigger);

    }

    @JsFunction
    @FunctionalInterface
    interface ValueFormatterCallback {

        Object execute(Object object);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    class CallbackParams {

        @JsProperty
        public native int getDataIndex();

        @JsProperty
        public native Object[] getData();

    }

    @JsFunction
    @FunctionalInterface
    interface LabelFormatterCallback {

        Object execute(CallbackParams params);
    }

    @JsFunction
    @FunctionalInterface
    interface SymbolSizeCallback {

        double execute(Number value, CallbackParams params);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class SaveAsImage {

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class DataView {

        @JsProperty
        public native void setReadOnly(boolean readOnly);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class ToolBoxFeature {

        @JsProperty
        public native void setSaveAsImage(SaveAsImage saveAsImage);

        @JsProperty
        public native void setDataView(DataView dataView);

        @JsProperty
        public native void setDataZoom(DataZoom dataZoom);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Toolbox {

        @JsProperty
        public native void setShow(boolean show);

        @JsProperty
        public native void setFeature(ToolBoxFeature dataView);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class Dataset {

        @JsProperty
        public native void setDimensions(String[] dimensions);

        @JsProperty
        public native void setSource(Object[][] source);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class VisualMap {

        @JsProperty
        public native void setShow(boolean show);

        @JsProperty
        public native void setType(String visualMapType);

        @JsProperty
        public native void setMin(double min);

        @JsProperty
        public native void setMax(double min);

        @JsProperty
        public native void setPieces(Piece[] pieces);

        @JsProperty
        public native void setInRange(InRange inRange);

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class InRange {

        @JsProperty
        public native void setColor(String[] color);
    }

    public enum VisualMapType {
        piecewise,
        continuous

    }

    public enum SeriesType {
        bar,
        line,
        pie,
        gauge,
        scatter;

    }

    public enum XAxisType {
        time,
        category,
        value;
    }

    public enum Renderer {

        svg,
        canvas;

        public static final Renderer DEFAULT_RENDERER = canvas;

        public static Renderer byName(String echartsRenderer) {
            if (echartsRenderer == null) {
                return DEFAULT_RENDERER;
            }
            return Arrays.stream(Renderer.values())
                    .filter(r -> r.name().equalsIgnoreCase(echartsRenderer))
                    .findAny().orElse(DEFAULT_RENDERER);
        }
    }

    public class Builder {

        @JsProperty(name = "echarts", namespace = JsPackage.GLOBAL)
        public static native ECharts get();
    }
}

/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.echarts.client.js;

import javax.enterprise.context.ApplicationScoped;

import jsinterop.base.Js;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.renderer.echarts.client.js.ECharts.InRange;
import org.dashbuilder.renderer.echarts.client.js.ECharts.SeriesType;

@ApplicationScoped
public class EChartsTypeFactory {

    public ECharts.Option newOption() {
        ECharts.Option option = Js.cast(new Object());
        return option;
    }

    public ECharts.Title newTitle() {
        ECharts.Title title = Js.cast(new Object());
        return title;
    }

    public ECharts.ChartInitParams newChartInitParams() {
        ECharts.ChartInitParams chartInitParams = Js.cast(new Object());
        return chartInitParams;
    }

    public ECharts.YAxis newYAxis() {
        ECharts.YAxis yAxis = Js.cast(new Object());
        return yAxis;
    }

    public ECharts.XAxis newXAxis() {
        ECharts.XAxis xAxis = Js.cast(new Object());
        return xAxis;
    }

    public ECharts.AxisLabel newAxisLabel() {
        ECharts.AxisLabel axisLabel = Js.cast(new Object());
        return axisLabel;
    }

    public ECharts.Series newSeries() {
        ECharts.Series series = Js.cast(new Object());
        return series;
    }

    public ECharts.AreaStyle newAreaStyle() {
        ECharts.AreaStyle areaStyle = Js.cast(new Object());
        return areaStyle;
    }

    public ECharts.Grid newGrid() {
        ECharts.Grid grid = Js.cast(new Object());
        return grid;
    }

    public ECharts.SplitLine newSplitLine() {
        ECharts.SplitLine splitLine = Js.cast(new Object());
        return splitLine;
    }

    public ECharts.DataZoom newDataZoom() {
        ECharts.DataZoom dataZoom = Js.cast(new Object());
        dataZoom.setType("inside");
        return dataZoom;
    }

    public ECharts.Legend newLegend() {
        ECharts.Legend legend = Js.cast(new Object());
        return legend;
    }

    public ECharts.Tooltip newTooltip() {
        ECharts.Tooltip tooltip = Js.cast(new Object());
        return tooltip;
    }

    public ECharts.Label newLabel() {
        ECharts.Label label = Js.cast(new Object());
        return label;
    }

    public ECharts.Toolbox newToolbox() {
        ECharts.Toolbox toolbox = Js.cast(new Object());
        return toolbox;
    }

    public ECharts.DataView newDataView() {
        ECharts.DataView dataView = Js.cast(new Object());
        return dataView;
    }

    public ECharts.SaveAsImage newSaveAsImage() {
        ECharts.SaveAsImage saveAsImage = Js.cast(new Object());
        return saveAsImage;
    }

    public ECharts.ToolBoxFeature newToolBoxFeature() {
        ECharts.ToolBoxFeature toolBoxFeature = Js.cast(new Object());
        return toolBoxFeature;
    }

    public ECharts.Encode newEncode() {
        ECharts.Encode encode = Js.cast(new Object());
        return encode;
    }

    public ECharts.Dataset newDataset() {
        ECharts.Dataset dataset = Js.cast(new Object());
        return dataset;
    }

    public ECharts.ItemStyle newItemStyle() {
        ECharts.ItemStyle itemStyle = Js.cast(new Object());
        return itemStyle;
    }

    public ECharts.Data newData() {
        ECharts.Data data = Js.cast(new Object());
        return data;
    }

    public ECharts.VisualMap newVisualMap() {
        ECharts.VisualMap visualMap = Js.cast(new Object());
        return visualMap;
    }

    public ECharts.Piece newPiece(long min, long max, String color) {
        ECharts.Piece piece = newPiece();
        piece.setMin(min);
        piece.setMax(max);
        piece.setColor(color);
        return piece;
    }
    
    public ECharts.Piece newPiece() {
        ECharts.Piece piece = Js.cast(new Object());
        return piece;
    }

    public ECharts.Pointer newPointer() {
        ECharts.Pointer visualMap = Js.cast(new Object());
        return visualMap;
    }

    public ECharts.Progress newProgress() {
        ECharts.Progress progress = Js.cast(new Object());
        return progress;
    }

    public ECharts.AxisLine newAxisLine() {
        ECharts.AxisLine axisLine = Js.cast(new Object());
        return axisLine;
    }
    
    public ECharts.AxisTick newAxisTick() {
        ECharts.AxisTick axisTick = Js.cast(new Object());
        return axisTick;
    }

    public ECharts.LineStyle newLineStyle() {
        ECharts.LineStyle lineStyle = Js.cast(new Object());
        return lineStyle;
    }

    public InRange newInRange() {
        ECharts.InRange inRange = Js.cast(new Object());
        return inRange;
    }

    public ECharts.SeriesType convertDisplayerType(DisplayerType displayerType) {

        switch (displayerType) {
            case BARCHART:
                return SeriesType.bar;
            case AREACHART:
            case LINECHART:
                return SeriesType.line;
            case PIECHART:                
                return SeriesType.pie;
                
            case METERCHART:
                return SeriesType.gauge;
            case SCATTERCHART:
            case BUBBLECHART:
                return SeriesType.scatter;
            case MAP:
            case METRIC:
            case SELECTOR:
            case TABLE:
            default:
                return SeriesType.bar;
        }

    }

}

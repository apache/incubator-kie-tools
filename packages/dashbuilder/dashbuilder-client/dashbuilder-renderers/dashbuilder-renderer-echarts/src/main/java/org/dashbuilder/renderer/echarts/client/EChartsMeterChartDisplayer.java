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
package org.dashbuilder.renderer.echarts.client;

import java.util.stream.IntStream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.renderer.echarts.client.js.ECharts;
import org.dashbuilder.renderer.echarts.client.js.ECharts.VisualMapType;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

@Dependent
public class EChartsMeterChartDisplayer extends EChartsAbstractDisplayer<EChartsDisplayerView<?>> {

    private static final int LEGEND_TITLE_DISTANCE = 20;
    private static final int LEGEND_ITEM_MIN_POS_Y = 20;
    private static final int LEGEND_ITEM_MIN_POS_X = -100;
    private static final int LEGEND_ITEM_MAX_POS = 100;
    private static final int LEGEND_ITEM_Y_GAP = 50;
    private static final int LEGEND_ITEM_X_GAP = 50;

    @Inject
    public EChartsMeterChartDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
        super(view, echartsFactory);
    }

    @Override
    void chartSetup() {
        var nColumns = dataSet.getColumns().size();
        if (nColumns < 1) {
            return;
        }
        var series = echartsFactory.newSeries();
        var visualMap = echartsFactory.newVisualMap();
        var valuesColumn = dataSet.getColumnByIndex(nColumns - 1);

        var min = displayerSettings.getMeterStart();        
        var max = displayerSettings.getMeterEnd();

        var showLegend = displayerSettings.isChartShowLegend();

        var inRange = echartsFactory.newInRange();
        var pointer = echartsFactory.newPointer();
        var progress = echartsFactory.newProgress();
        var axisLabel = echartsFactory.newAxisLabel();
        var axisLine = echartsFactory.newAxisLine();
        var lineStyle = echartsFactory.newLineStyle();
        var detail = echartsFactory.newItemStyle();
        var seriesTitle = echartsFactory.newTitle();
        var legend = echartsFactory.newLegend();

        legend.setShow(false);
        pointer.setShow(false);
        lineStyle.setWidth(50);
        axisLine.setLineStyle(lineStyle);
        axisLabel.setDistance(50);
        progress.setShow(true);
        progress.setOverlap(false);

        detail.setValueAnimation(true);
        detail.setShow(showLegend);
        detail.setWidth(40);
        detail.setHeight(14);
        detail.setFontSize(14);
        detail.setColor("#fff");
        detail.setBackgroundColor("auto");
        detail.setBorderRadius(3);
        
        visualMap.setShow(false);
        visualMap.setType(VisualMapType.piecewise.name());
        visualMap.setMin(min);
        visualMap.setMax(max);
        visualMap.setInRange(inRange);
        visualMap.setPieces(new ECharts.Piece[] {
            echartsFactory.newPiece(displayerSettings.getMeterStart(), displayerSettings.getMeterWarning(), "green"),
            echartsFactory.newPiece(displayerSettings.getMeterWarning(), displayerSettings.getMeterCritical(),  "orange"),
            echartsFactory.newPiece(displayerSettings.getMeterCritical(), displayerSettings.getMeterEnd(), "red")
        });

        var names = getNames(nColumns);
        var values = getNumberValues(valuesColumn);

        int legendBasePosX = LEGEND_ITEM_MIN_POS_X;
        int legendBasePosY = LEGEND_ITEM_MIN_POS_Y;
        var seriesData = new ECharts.Data[names.length];
        for (int i = 0; i < values.length; i++) {
            var data = echartsFactory.newData();
            var dataTitle = echartsFactory.newItemStyle();
            var dataDetail = echartsFactory.newItemStyle();

            data.setValue(values[i]);
            data.setName(names[i]);

            var titleXPos = legendBasePosX + "%";
            var titleYPos = legendBasePosY + "%";

            var detailXPos = titleXPos;
            var detailYPos = legendBasePosY + LEGEND_TITLE_DISTANCE + "%";
            dataTitle.setOffsetCenter(new String[]{titleXPos, titleYPos});
            dataDetail.setOffsetCenter(new String[]{detailXPos, detailYPos});

            legendBasePosX += LEGEND_ITEM_X_GAP; 
            if (legendBasePosX > LEGEND_ITEM_MAX_POS) {
                legendBasePosX = LEGEND_ITEM_MIN_POS_X;
                legendBasePosY += LEGEND_ITEM_Y_GAP;
            }

            data.setDetail(dataDetail);
            data.setTitle(dataTitle);
            seriesData[i] = data;
        }

        series.setData(seriesData);
        series.setDetail(detail);
        series.setStartAngle(180);
        series.setEndAngle(0);
        series.setMin(min);
        series.setMax(max);
        series.setTitle(seriesTitle);
        series.setAxisLine(axisLine);
        series.setAxisLabel(axisLabel);
        series.setProgress(progress);
        series.setPointer(pointer);
        series.setName("Meter");
        series.setType(this.echartsType);

        option.setLegend(legend);
        option.setVisualMap(visualMap);        
        option.setSeries(series);

    }

    private String[] getNames(int n) {
        return n == 1
                ? IntStream.rangeClosed(0, n)
                        .mapToObj(i -> "Series " + i)
                        .toArray(String[]::new)
                : dataSet.getColumnByIndex(0)
                        .getValues()
                        .stream()
                        .map(o -> o.toString())
                        .toArray(String[]::new);
    }

    @Override
    DataSetLookupConstraints getDataSetLookupConstraints() {
        return new DataSetLookupConstraints()
                .setMaxColumns(2)
                .setMinColumns(1)
                .setExtraColumnsAllowed(false)
                .setColumnTypes(new ColumnType[]{
                                                 ColumnType.LABEL,
                                                 ColumnType.NUMBER});
    }

}

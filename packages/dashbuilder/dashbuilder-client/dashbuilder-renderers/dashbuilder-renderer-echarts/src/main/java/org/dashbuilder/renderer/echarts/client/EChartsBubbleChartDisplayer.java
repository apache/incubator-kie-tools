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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Series;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

@Dependent
public class EChartsBubbleChartDisplayer extends EChartsXYDisplayer {

    private static final int VALUE_INDEX = 1;
    private static final int RADIUS_INDEX = 2;
    private static final int LABEL_INDEX = 3;

    @Inject
    public EChartsBubbleChartDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
        super(view, echartsFactory);
    }

    @Override
    protected Series[] buildSeries() {
        var bubbleMinRadius = displayerSettings.getBubbleMinRadius();
        var bubbleMaxRadius = displayerSettings.getBubbleMaxRadius();
        if (dataSet.getColumns().size() > VALUE_INDEX) {
            var series = echartsFactory.newSeries();
            var encode = echartsFactory.newEncode();
            var column = dataSet.getColumnByIndex(VALUE_INDEX);
            var settings = displayerSettings.getColumnSettings(column);
            var seriesColumn = settings.getColumnName();
            var catColumn = displayerSettings.getColumnSettings(dataSet.getColumnByIndex(0)).getColumnName();

            if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.BUBBLE_COLOR)) {
                this.option.setColor(displayerSettings.getBubbleColor());
            }

            encode.setX(catColumn);
            encode.setY(seriesColumn);

            series.setName(seriesColumn);
            series.setEncode(encode);
            series.setType(this.echartsType);

            if (dataSet.getColumns().size() > RADIUS_INDEX) {
                var radiusColumn = dataSet.getColumnByIndex(RADIUS_INDEX);
                var min = min(radiusColumn).orElse(bubbleMinRadius);
                var max = max(radiusColumn).orElse(bubbleMaxRadius);
                series.setSymbolSize((v, params) -> {
                    var radiusValueObj = dataSet.getValueAt(params.getDataIndex(), RADIUS_INDEX);
                    if (radiusValueObj != null) {
                        var radiusValue = Double.valueOf(radiusValueObj.toString());
                        return map(radiusValue, min, max, bubbleMinRadius, bubbleMaxRadius);
                    }
                    return bubbleMinRadius;
                });
            }

            if (dataSet.getColumns().size() > LABEL_INDEX) {
                var label = echartsFactory.newLabel();
                label.setShow(true);
                label.setFormatter(params -> dataSet.getValueAt(params.getDataIndex(), LABEL_INDEX));
                series.setLabel(label);
            }

            return new Series[]{series};
        }
        return new Series[]{};
    }

    @Override
    DataSetLookupConstraints getDataSetLookupConstraints() {
        return new DataSetLookupConstraints()
                .setMaxColumns(4)
                .setMinColumns(2)
                .setExtraColumnsAllowed(false);
    }

}

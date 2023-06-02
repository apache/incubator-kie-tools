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
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Series;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

@Default
@Dependent
public class EChartsXYChartDisplayer extends EChartsXYDisplayer {

    @Inject
    public EChartsXYChartDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
        super(view, echartsFactory);
    }

    @Override
    protected Series[] buildSeries() {
        var nColumns = dataSet.getColumns().size();
        if (nColumns > 0) {
            return new Series[0];
        }
        var allSeries = new Series[nColumns - 1];
        var catColumn = displayerSettings.getColumnSettings(dataSet.getColumnByIndex(0)).getColumnName();
        for (int i = 1; i < nColumns; i++) {
            var series = echartsFactory.newSeries();
            var encode = echartsFactory.newEncode();
            var column = dataSet.getColumnByIndex(i);
            var settings = displayerSettings.getColumnSettings(column);
            var seriesColumn = settings.getColumnName();

            if (displayerSettings.getType() == DisplayerType.AREACHART) {
                series.setAreaStyle(echartsFactory.newAreaStyle());
            }

            if (displayerSettings.getSubtype() == DisplayerSubType.SMOOTH) {
                series.setSmooth(true);
            }
            if (isBar) {
                encode.setX(seriesColumn);
                encode.setY(catColumn);
            } else {
                encode.setX(catColumn);
                encode.setY(seriesColumn);
            }

            if (isStack) {
                series.setStack(catColumn);
            }

            series.setName(seriesColumn);
            series.setEncode(encode);
            series.setType(this.echartsType);

            allSeries[i - 1] = series;
        }
        return allSeries;
    }

}

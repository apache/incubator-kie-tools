/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Dataset;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Series;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

@Default
@Dependent
public class EChartsTimeseriesDisplayer extends EChartsXYDisplayer {

    @Inject
    public EChartsTimeseriesDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
        super(view, echartsFactory);
    }

    @Override
    DataSetLookupConstraints getDataSetLookupConstraints() {
        return new DataSetLookupConstraints()
                .setMaxColumns(3)
                .setMinColumns(3);
    }

    @Override
    protected Series[] buildSeries() {
        var columns = dataSet.getColumns();
        var nColumns = columns.size();
        if (nColumns < 3) {
            return new Series[0];
        }
        var seriesMap = new HashMap<String, List<Object[]>>();
        var seriesColumn = columns.get(0);
        var timestampColumn = columns.get(1);
        var valuesColumn = columns.get(2);

        for (var i = 0; i < dataSet.getRowCount(); i++) {
            var serieName = getValue(seriesColumn, i).toString();
            var data = seriesMap.getOrDefault(serieName, new ArrayList<>());
            data.add(new Object[]{getValue(timestampColumn, i), getValue(valuesColumn, i)});
            seriesMap.put(serieName, data);
        }

        var seriesList = new ArrayList<Series>();
        seriesMap.forEach((k, v) -> {
            var data = v.stream().toArray(String[]::new);
            var series = echartsFactory.newSeries();

            series.setName(k);
            series.setType("line");
            series.setSmooth(false);
            series.setSymbol("none");

            var areaStyle = echartsFactory.newAreaStyle();
            areaStyle.setOpacity(0.2);
            series.setAreaStyle(areaStyle);

            series.setData(data);
            seriesList.add(series);
        });
        return seriesList.stream().toArray(Series[]::new);
    }

    @Override
    public Dataset buildDataSet() {
        return null;
    }

    private Object getValue(DataColumn column, int i) {
        return column.getValues().get(i);
    }

}

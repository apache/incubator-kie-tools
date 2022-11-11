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

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.renderer.echarts.client.js.EChartsTypeFactory;

@Dependent
public class EChartsPieChartDisplayer extends EChartsAbstractDisplayer<EChartsDisplayerView<?>> {

    @Inject
    public EChartsPieChartDisplayer(EChartsDisplayerView<?> view, EChartsTypeFactory echartsFactory) {
        super(view, echartsFactory);
    }

    @Override
    void chartSetup() {
        var series = echartsFactory.newSeries();
        

        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_BOTTOM)) {
            series.setBottom(displayerSettings.getChartMarginBottom());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_TOP)) {
            series.setTop(displayerSettings.getChartMarginTop());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_LEFT)) {
            series.setLeft(displayerSettings.getChartMarginLeft());
        }
        if (displayerSettings.isAttributeDefinedByUser(DisplayerAttributeDef.CHART_MARGIN_RIGHT)) {
            series.setRight(displayerSettings.getChartMarginRight());
        }

        if (displayerSettings.getSubtype() == DisplayerSubType.DONUT) {
            var radius = new String[]{"50%", "70%"};
            option.getTitle().setTop("center");            
            series.setRadius(radius);
        }
        
        series.setType(this.echartsType);
        option.setSeries(series);
    }

    @Override
    DataSetLookupConstraints getDataSetLookupConstraints() {
        return new DataSetLookupConstraints()
                .setMaxColumns(2)
                .setMinColumns(2)
                .setExtraColumnsAllowed(false)
                .setColumnTypes(new ColumnType[]{
                        ColumnType.LABEL,
                        ColumnType.NUMBER});
    }

}

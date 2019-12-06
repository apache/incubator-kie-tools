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
package org.dashbuilder.renderer.c3.client.charts.pie;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.renderer.c3.client.C3Displayer;
import org.dashbuilder.renderer.c3.client.charts.CommonC3DisplayerConstants;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisX;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Donut;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;

@Dependent
public class C3PieChartDisplayer extends C3Displayer<C3PieChartDisplayer.View> {
    
    public interface View extends C3Displayer.View<C3PieChartDisplayer> {
        
        void setShowAsDonut(boolean showAsDonut);
        
    }
    
    private View view;

    @Override
    protected C3ChartConf buildConfiguration() {
        C3ChartConf conf = super.buildConfiguration();
        if (DisplayerSubType.DONUT == displayerSettings.getSubtype() &&
                !StringUtils.isBlank(displayerSettings.getDonutHoleTitle())) {
            C3Donut donutConf = createDonut();
            conf.setDonut(donutConf);
        }
        return conf;
    }

    @Inject
    public C3PieChartDisplayer(View view, FilterLabelSet filterLabelSet, C3JsTypesFactory builder) {
        super(filterLabelSet, builder);
        this.view = view;
        this.view.init(this);
    }
    
    @Override
    public View getView() {
        return view;
    }
    
    public C3PieChartDisplayer donut() {
        getView().setShowAsDonut(true);
        return this;
    }
    
    // In C3 we only need the series for PieCharts, categories are not needed
    @Override
    protected String[][] createSeries() {
        List<DataColumn> columns = dataSet.getColumns();
        String[][] data  = null;
        // first columns hold the pie series name
        DataColumn categoriesColumn = columns.get(0);
        List<?> values = categoriesColumn.getValues();
        data = new String[values.size()][];
        // next columns hold the values for each series
        for (int i = 0; i < values.size(); i++) {
            String[] seriesValues = new String[columns.size()];
            seriesValues[0] = super.formatValue(values.get(i), categoriesColumn);
            for (int j = 1; j < columns.size(); j++) {
                DataColumn dataColumn = columns.get(j);
                seriesValues[j] = dataColumn.getValues().get(i).toString();
            }
            data[i] = seriesValues;
        }
        return data;
    }
    
    @Override
    protected C3AxisX createAxisX() {
        return null;
     }

    @Override
    protected String[] createCategories() {
        return null;
    }
    
    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxColumns(2)
                .setMinColumns(2)
                .setExtraColumnsAllowed(false)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle())
                .setColumnTypes(new ColumnType[]{
                        ColumnType.LABEL,
                        ColumnType.NUMBER});

        return new CommonC3DisplayerConstants(lookupConstraints).create()
                .supportsAttribute(DisplayerAttributeDef.SUBTYPE);
    }

    protected C3Donut createDonut() {
        String donutHoleTitle = displayerSettings.getDonutHoleTitle();
        return factory.c3Donut(donutHoleTitle);
    }
}
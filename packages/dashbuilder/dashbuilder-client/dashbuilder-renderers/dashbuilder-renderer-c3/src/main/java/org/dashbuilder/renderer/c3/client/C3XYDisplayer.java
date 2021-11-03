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
package org.dashbuilder.renderer.c3.client;

import java.util.List;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisLabel;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Tick;

public abstract class C3XYDisplayer<V extends C3Displayer.View> extends C3Displayer {
    
    private static final String DEFAULT_LABEL_POS = "outer-center";
    
    private boolean stacked;

    public C3XYDisplayer(FilterLabelSet filterLabelSet, C3JsTypesFactory builder) {
        super(filterLabelSet, builder);
    }

    @Override
    protected C3ChartConf buildConfiguration() {
         C3ChartConf conf = super.buildConfiguration();
         applyPropertiesToAxes(conf.getAxis());
         return conf;
    }
    
    protected C3Tick createTickY() {
        return factory.createC3Tick(f -> {
            List<DataColumn> columns = dataSet.getColumns();
            if (columns.size() > 1) {
                DataColumn dataColumn = columns.get(1);
                f = super.formatValue(f, dataColumn);
            }
            return f;
        });
    }
    
    private void applyPropertiesToAxes(C3AxisInfo axis) {
        axis.getX().getTick().setRotate(displayerSettings.getXAxisLabelsAngle());
        if (displayerSettings.isXAxisShowLabels()) {
            C3AxisLabel xLabel = factory.createC3Label(displayerSettings.getXAxisTitle(), 
                                                       DEFAULT_LABEL_POS);
            axis.getX().setLabel(xLabel);
        }
        if (displayerSettings.isYAxisShowLabels()) {
            C3AxisLabel yLabel = factory.createC3Label(displayerSettings.getYAxisTitle(), 
                                                       DEFAULT_LABEL_POS);
            axis.getY().setLabel(yLabel);
        }
    }
    
    protected String[][] stackedGroups() {
        String[][] groups;
        groups = new String[1][];
        groups[0] = dataSet.getColumns()
                            .stream().skip(1)
                            .map(displayerSettings::getColumnSettings)
                            .map(ColumnSettings::getColumnName)
                            .toArray(String[]::new);
        return groups;
    }
    
    @Override
    protected String[][] createGroups() {
        String[][] groups = new String[0][0];
        if (isStacked()) {
            groups = stackedGroups();
            
        }
        return groups;
    }

    public boolean isStacked() {
        return stacked;
    }
    
    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }    

}
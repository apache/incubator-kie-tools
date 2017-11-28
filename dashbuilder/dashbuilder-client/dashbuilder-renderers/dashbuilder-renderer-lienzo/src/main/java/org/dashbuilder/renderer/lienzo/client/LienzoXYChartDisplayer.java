/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.lienzo.client;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;

public abstract class LienzoXYChartDisplayer<V extends LienzoXYChartDisplayer.View> extends LienzoDisplayer<V> {

    public interface View<P extends LienzoXYChartDisplayer> extends LienzoDisplayer.View<P> {

        void setShowXLabels(boolean enabled);

        void setShowYLabels(boolean enabled);

        void setXAxisTitle(String xAxisTitle);

        void setYAxisTitle(String yAxisTitle);

        void setHorizontal(boolean horizontal);
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxColumns(10)
                .setMinColumns(2)
                .setExtraColumnsAllowed(true)
                .setExtraColumnsType(ColumnType.NUMBER)
                .setGroupsTitle(getView().getGroupsTitle())
                .setColumnsTitle(getView().getColumnsTitle())
                .setColumnTypes(new ColumnType[]{
                        ColumnType.LABEL,
                        ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute( DisplayerAttributeDef.TYPE )
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                .supportsAttribute(DisplayerAttributeDef.CHART_RESIZABLE)
                .supportsAttribute(DisplayerAttributeDef.CHART_MAX_WIDTH)
                .supportsAttribute(DisplayerAttributeDef.CHART_MAX_HEIGHT)
                .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_LEGEND_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.AXIS_GROUP);
    }

    @Override
    protected void createVisualization() {
        getView().setShowXLabels(false);
        getView().setShowYLabels(false);
        getView().setXAxisTitle(displayerSettings.getXAxisTitle());
        getView().setYAxisTitle(displayerSettings.getYAxisTitle());

        super.createVisualization();
    }
}

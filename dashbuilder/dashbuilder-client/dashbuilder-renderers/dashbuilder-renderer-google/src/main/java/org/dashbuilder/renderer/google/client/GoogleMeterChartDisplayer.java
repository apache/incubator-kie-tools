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
package org.dashbuilder.renderer.google.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;

@Dependent
public class GoogleMeterChartDisplayer extends GoogleCategoriesDisplayer<GoogleMeterChartDisplayer.View> {

    public interface View extends GoogleCategoriesDisplayer.View<GoogleMeterChartDisplayer> {

        void setMeterStart(long meterStart);

        void setMeterWarning(long meterWarning);

        void setMeterCritical(long meterCritical);

        void setMeterEnd(long meterEnd);
    }

    private View view;

    @Inject
    public GoogleMeterChartDisplayer(View view, FilterLabelSet filterLabelSet) {
        super(filterLabelSet);
        this.view = view;
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public DisplayerConstraints createDisplayerConstraints() {

        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(false)
                .setGroupAllowed(true)
                .setGroupColumn(true)
                .setMaxColumns(2)
                .setMinColumns(1)
                .setExtraColumnsAllowed(false)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle())
                .setFunctionRequired(true)
                .setColumnTypes(new ColumnType[] {ColumnType.NUMBER},
                        new ColumnType[] {ColumnType.LABEL, ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                   .supportsAttribute(DisplayerAttributeDef.TYPE)
                   .supportsAttribute(DisplayerAttributeDef.RENDERER)
                   .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                   .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                   .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                   .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.METER_GROUP);
    }

    @Override
    protected void createVisualization() {
        view.setMeterStart(displayerSettings.getMeterStart());
        view.setMeterWarning(displayerSettings.getMeterWarning());
        view.setMeterCritical(displayerSettings.getMeterCritical());
        view.setMeterEnd(displayerSettings.getMeterEnd());

        super.createVisualization();
    }
}

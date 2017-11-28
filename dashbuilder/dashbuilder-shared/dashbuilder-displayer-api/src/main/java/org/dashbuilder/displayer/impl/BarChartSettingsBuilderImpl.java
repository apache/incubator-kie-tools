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
package org.dashbuilder.displayer.impl;

import org.dashbuilder.displayer.BarChartSettingsBuilder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;

public class BarChartSettingsBuilderImpl extends AbstractXAxisChartSettingsBuilder<BarChartSettingsBuilderImpl> implements BarChartSettingsBuilder<BarChartSettingsBuilderImpl> {

    protected DisplayerSettings createDisplayerSettings() {
        return new DisplayerSettings( DisplayerType.BARCHART );
    }

    public BarChartSettingsBuilderImpl set3d( boolean b ) {
        displayerSettings.setChart3D(b);
        return this;
    }

    @Override
    public BarChartSettingsBuilderImpl subType_Bar() {
        displayerSettings.setSubtype(DisplayerSubType.BAR);
        return this;
    }

    @Override
    public BarChartSettingsBuilderImpl subType_StackedBar() {
        displayerSettings.setSubtype(DisplayerSubType.BAR_STACKED);
        return this;
    }

    @Override
    public BarChartSettingsBuilderImpl subType_Column() {
        displayerSettings.setSubtype(DisplayerSubType.COLUMN);
        return this;
    }

    @Override
    public BarChartSettingsBuilderImpl subType_StackedColumn() {
        displayerSettings.setSubtype(DisplayerSubType.COLUMN_STACKED);
        return this;
    }
}

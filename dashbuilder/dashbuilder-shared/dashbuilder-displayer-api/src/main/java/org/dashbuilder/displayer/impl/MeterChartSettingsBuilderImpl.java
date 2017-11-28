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

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.MeterChartSettingsBuilder;

public class MeterChartSettingsBuilderImpl extends AbstractChartSettingsBuilder<MeterChartSettingsBuilderImpl> implements MeterChartSettingsBuilder<MeterChartSettingsBuilderImpl> {

    protected DisplayerSettings createDisplayerSettings() {
        return new DisplayerSettings( DisplayerType.METERCHART );
    }

    public MeterChartSettingsBuilderImpl meter(long start, long warning, long critical, long end) {
        displayerSettings.setMeterStart(start);
        displayerSettings.setMeterWarning(warning);
        displayerSettings.setMeterCritical(critical);
        displayerSettings.setMeterEnd(end);
        return this;
    }
}

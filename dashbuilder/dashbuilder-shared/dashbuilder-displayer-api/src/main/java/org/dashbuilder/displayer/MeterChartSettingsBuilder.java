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
package org.dashbuilder.displayer;

/**
 * A displayer settings builder for meter charts
 *
 * <pre>
 *   DisplayerSettingsFactory.newMeterChartSettings()
 *   .title("Sales goal")
 *   .titleVisible(true)
 *   .width(250).height(250)
 *   .meter(0, 15000000, 25000000, 35000000)
 *   .column("Total amount")
 *   .buildSettings());
 * </pre>
 */
public interface MeterChartSettingsBuilder<T extends MeterChartSettingsBuilder> extends ChartSettingsBuilder<T> {

    /**
     * Set specific the configuration parameters for this meter chart.
     * @param start The start of the meter scale
     * @param warning The warning level, any value above this level will be shown in orange on the meter chart.
     * Values below this level are shown in green.
     * @param critical The critical level, any value above this level will be shown in red on the meter chart.
     * @param end The end of the meter scale.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Meter data displayer.
     */
    T meter(long start, long warning, long critical, long end);
}

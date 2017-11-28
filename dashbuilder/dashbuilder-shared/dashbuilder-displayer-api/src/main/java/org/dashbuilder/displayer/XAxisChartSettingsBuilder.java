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
 * A common base interface for al charts that are axis-based.
 */
public interface XAxisChartSettingsBuilder<T extends XAxisChartSettingsBuilder> extends ChartSettingsBuilder<T> {

    /**
     * Set the X axis title.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T xAxisTitle(String title);

    /**
     * Set the X axis labels display angle (from 0 to 360).
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T xAxisAngle(int angle);

    /**
     * Set the Y axis title.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T yAxisTitle(String title);
}

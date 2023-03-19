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
 * A displayer settings builder for bar charts
 *
 * <pre>
 *   DisplayerSettingsFactory.newBarChartSettings()
 *   .title("By Product")
 *   .column("Product")
 *   .column("Total amount")
 *   .buildSettings()
 * </pre>
 */
public interface BarChartSettingsBuilder<T extends BarChartSettingsBuilder> extends XAxisChartSettingsBuilder<T> {

    /**
     * @param b True if the bars of this bar chart are to be shown in 3D, false if they are to be shown flat.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Bar chart data displayer.
     */
    T set3d(boolean b);

    /**
     * Set this bar chart's subtype to a simple bar chart (i.e. horizontal non-stacked bars)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Bar chart data displayer.
     */
    T subType_Bar();

    /**
     * Set this bar chart's subtype to a stacked bar chart (i.e. horizontal stacked bars)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Bar chart data displayer.
     */
    T subType_StackedBar();

    /**
     * Set this bar chart's subtype to a simple column chart (i.e. vertical non-stacked bars)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Bar chart data displayer.
     */
    T subType_Column();

    /**
     * Set this bar chart's subtype to a stacked column chart (i.e. vertical stacked bars)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Bar chart data displayer.
     */
    T subType_StackedColumn();
}

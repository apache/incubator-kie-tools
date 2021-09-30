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
 * A displayer settings builder for pie charts
 *
 * <pre>
 *   DisplayerSettingsFactory.newPieChartSettings()
 *   .title("By Product")
 *   .column("Product")
 *   .column("Total amount")
 * </pre>
 */
public interface PieChartSettingsBuilder<T extends PieChartSettingsBuilder> extends XAxisChartSettingsBuilder<T> {

    /**
     * Set this pie chart's subtype to a simple pie chart
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Pie chart data displayer.
     */
    T subType_Pie();

    /**
     * Set this pie chart's subtype to a 3D pie chart
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Pie chart data displayer.
     */
    T subType_Pie_3d();

    /**
     * Set this pie chart's subtype to a donut-like simple pie chart (i.e. a pie chart with a hole in its center)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Pie chart data displayer.
     */
    T subType_Donut();

    /**
     * Set this pie chart's subtype to a donut-like simple pie chart (i.e. a pie chart with a hole in its center)
     * and, in addition, with the label to assign inside the donut graph hole.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Pie chart data displayer.
     */
    T subType_Donut(String holeLabel);
}

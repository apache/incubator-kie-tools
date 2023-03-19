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
 * A displayer settings builder for map charts
 *
 * <pre>
 *   DisplayerSettingsFactory.newMapChartSettings()
 *   .title("By Country")
 *   .column("Country")
 *   .column("Total amount")
 * </pre>
 */
public interface MapChartSettingsBuilder<T extends MapChartSettingsBuilder> extends XAxisChartSettingsBuilder<T> {

    /**
     * Set this map chart's subtype to a region map chart (i.e. a Map chart that highlights certain regions)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Map chart data displayer.
     */
    T subType_Region_Map();

    /**
     * Set this map chart's subtype to a marker map chart (i.e. a Map chart that shows a magnitude indication for certain regions)
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Map chart data displayer.
     */
    T subType_Marker_Map();

    /**
     * Set this map color scheme.
     * @param colorScheme
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Map chart data displayer.
     */
    T colorScheme(MapColorScheme colorScheme);
}

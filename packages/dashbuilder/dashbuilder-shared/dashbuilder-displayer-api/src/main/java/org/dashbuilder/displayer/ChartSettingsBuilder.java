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
 * A displayer settings builder for the assembly of Chart based data displayer instances.
 */
public interface ChartSettingsBuilder<T extends ChartSettingsBuilder> extends DisplayerSettingsBuilder<T> {

    /**
     * Sets the width of the chart.
     * @param width The width of the chart.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T width(int width);

    /**
     * Sets the height of the chart.
     * @param height The height of the chart.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T height(int height);

    /**
     * Set the margins for this chart.
     * @param top The top margin.
     * @param bottom The bottom margin.
     * @param left The left margin.
     * @param right The right margin.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T margins(int top, int bottom, int left, int right);

    /**
     * Hides off the char legend.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T legendOff();

    /**
     * Turns on the char legend display.
     * @param position The display position.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T legendOn(String position);
    T legendOn(Position position);

    /**
     * Set the chart as resizable, it can change its size from the original one, defined by <code>width</code> and <code>height</code> attributes.
     * @param maxWidth The maximum width value.
     * @param maxHeight The maximum height value.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T resizableOn(int maxWidth, int maxHeight);

    /**
     * Set the chart as no resizable, it cannot change its size from the original one, defined by <code>width</code> and <code>height</code> attributes. 
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Chart data displayer.
     */
    T resizableOff();
}

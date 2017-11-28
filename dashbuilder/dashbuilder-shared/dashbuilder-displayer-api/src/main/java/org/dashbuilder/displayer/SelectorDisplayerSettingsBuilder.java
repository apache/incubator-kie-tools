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
 * A displayer settings builder for selectors
 *
 * <pre>
 *   DisplayerSettingsFactory.newSelectorSettings()
 *   .dataset("products")
 *   .group("product")
 *   .count("items")
 *   .title("Product selector")
 *   .column("Product")
 *   .column("Number of items")
 * </pre>
 */
public interface SelectorDisplayerSettingsBuilder<T extends SelectorDisplayerSettingsBuilder> extends DisplayerSettingsBuilder<T> {

    /**
     * Sets the width of the selector.
     * @param width The width of selector.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a selector.
     */
    T width(int width);

    /**
     * Set the margins for this selector.
     * @param top The top margin.
     * @param bottom The bottom margin.
     * @param left The left margin.
     * @param right The right margin.
     * @return The DisplayerSettingsBuilder instance that is being used to configure the selector.
     */
    T margins(int top, int bottom, int left, int right);

    /**
     * Enables or disables the selection of multiple entries
     * @param multiple The multiple selection flag.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a selector.
     */
    T multiple(boolean multiple);
}

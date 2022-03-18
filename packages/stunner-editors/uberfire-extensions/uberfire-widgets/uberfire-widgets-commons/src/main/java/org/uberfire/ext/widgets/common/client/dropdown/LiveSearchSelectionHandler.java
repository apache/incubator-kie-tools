/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.dropdown;

import org.uberfire.mvp.Command;

/**
 * Handles the selection of a {@link LiveSearchDropDown}
 * @param <TYPE> The data type handled by the component
 */
public interface LiveSearchSelectionHandler<TYPE> {

    /**
     * Retrieves the text for the {@link LiveSearchDropDown} header based on the selected values
     * @return The {@link LiveSearchDropDown} header
     */
    String getDropDownMenuHeader();

    /**
     * Registers and initializes the given {@link LiveSearchSelectorItem}
     * @param item
     */
    void registerItem(LiveSearchSelectorItem<TYPE> item);

    /**
     * Sets the given {@link LiveSearchSelectorItem} as a selected value
     * @param item
     */
    void selectItem(LiveSearchSelectorItem<TYPE> item);

    /**
     * Selects the {@link LiveSearchSelectorItem} that has the given key
     * @param key
     */
    void selectKey(TYPE key);

    /**
     * Clears the selection
     */
    void clearSelection();

    /**
     * Sets the {@link Command} callback to notify the {@link LiveSearchDropDown} when there's a selection change.
     * @param command
     */
    void setLiveSearchSelectionCallback(Command command);

    /**
     * Determines if the handler supports multiple selection or not.
     * @return True if supports multiple selection, false if not.
     */
    boolean isMultipleSelection();
}

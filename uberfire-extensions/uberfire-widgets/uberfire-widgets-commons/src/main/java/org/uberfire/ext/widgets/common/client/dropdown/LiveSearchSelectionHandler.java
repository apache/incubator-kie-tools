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

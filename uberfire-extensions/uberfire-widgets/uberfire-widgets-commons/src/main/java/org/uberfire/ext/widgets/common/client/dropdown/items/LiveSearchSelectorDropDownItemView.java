package org.uberfire.ext.widgets.common.client.dropdown.items;

import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchSelectorItemView;

public interface LiveSearchSelectorDropDownItemView<TYPE> extends LiveSearchSelectorItemView<LiveSearchSelectorDropDownItem<TYPE>, TYPE> {

    void setSelectionIconVisible(boolean visible);

    void setMultiSelect(boolean multiSelect);
}

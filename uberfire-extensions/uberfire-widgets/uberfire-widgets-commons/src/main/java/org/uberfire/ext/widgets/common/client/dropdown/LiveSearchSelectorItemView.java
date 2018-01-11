package org.uberfire.ext.widgets.common.client.dropdown;

import org.uberfire.client.mvp.UberElement;

public interface LiveSearchSelectorItemView<PRESENTER extends LiveSearchSelectorItem<TYPE>, TYPE> extends UberElement<PRESENTER> {

    void render(String value);

    void select();

    void reset();
}

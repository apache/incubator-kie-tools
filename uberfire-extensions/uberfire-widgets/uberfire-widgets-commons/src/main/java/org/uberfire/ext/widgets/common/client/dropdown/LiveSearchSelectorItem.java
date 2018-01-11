package org.uberfire.ext.widgets.common.client.dropdown;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.mvp.Command;

public interface LiveSearchSelectorItem<TYPE> extends IsElement{

    void init(TYPE key, String value);

    TYPE getKey();

    String getValue();

    void reset();

    void setMultipleSelection(boolean enable);

    void onItemClick();

    void select();

    void setSelectionCallback(Command selectionCallback);
}

package org.uberfire.ext.layout.editor.client.components.columns;

import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public interface Column<T> {
    void setColumnPosition( Position columnPosition );

    UberView<T> getView();

    int getParentHashCode();

    Integer getSize();

    void incrementSize();

    void reduzeSize();

    void setSize( Integer size );

    LayoutComponent getLayoutComponent();

    boolean hasInnerRows();

    enum Position {
        FIRST_COLUMN, MIDDLE;
    }
}

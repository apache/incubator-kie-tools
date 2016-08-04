package org.uberfire.ext.layout.editor.client.components.columns;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public interface Column<T> {

    UberElement<T> getView();

    String getParentId();

    Integer getSize();

    void incrementSize();

    void reduzeSize();

    void setupResize( boolean canResizeLeft, boolean canResizeRight );

    void setSize( Integer size );

    LayoutComponent getLayoutComponent();

    boolean hasInnerRows();

    void calculateSize();

    String getId();
}

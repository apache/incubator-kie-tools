/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DynamicColumn;

/**
 * Represents a column resize event.
 */
public class ColumnResizeEvent extends GwtEvent<ColumnResizeEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onColumnResize( ColumnResizeEvent event );

    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private final DynamicColumn<?> column;
    private final int              width;

    /**
     * Creates a value change event.
     * @param column The column on which the resize event was triggered
     * @param width The new width of the column
     */
    public ColumnResizeEvent( DynamicColumn<?> column,
                              int width ) {
        if ( column == null ) {
            throw new IllegalArgumentException( "column cannot be null" );
        }
        this.column = column;
        this.width = width;
    }

    /**
     * Gets the column to which the resize event relates.
     * @return the column
     */
    public DynamicColumn<?> getColumn() {
        return this.column;
    }

    /**
     * Gets the width of the column
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( ColumnResizeEvent.Handler handler ) {
        handler.onColumnResize( this );
    }

}

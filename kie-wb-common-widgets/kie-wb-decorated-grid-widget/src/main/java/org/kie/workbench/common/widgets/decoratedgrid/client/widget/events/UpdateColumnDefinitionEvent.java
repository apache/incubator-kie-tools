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
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DecoratedGridCellValueAdaptor;

/**
 * An event to change a Column definition
 */
public class UpdateColumnDefinitionEvent extends GwtEvent<UpdateColumnDefinitionEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onUpdateColumnDefinition( UpdateColumnDefinitionEvent event );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell;

    private boolean isSystemControlled;

    private boolean isSortable;

    private int columnIndex;

    public UpdateColumnDefinitionEvent( DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell,
                                        int columnIndex ) {
        this( cell,
              false,
              true,
              columnIndex );
    }

    public UpdateColumnDefinitionEvent( DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell,
                                        boolean isSystemControlled,
                                        boolean isSortable,
                                        int columnIndex ) {
        this.cell = cell;
        this.isSystemControlled = isSystemControlled;
        this.isSortable = isSortable;
        this.columnIndex = columnIndex;
    }

    public DecoratedGridCellValueAdaptor<? extends Comparable<?>> getCell() {
        return this.cell;
    }

    public boolean isSystemControlled() {
        return this.isSystemControlled;
    }

    public boolean isSortable() {
        return this.isSortable;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( UpdateColumnDefinitionEvent.Handler handler ) {
        handler.onUpdateColumnDefinition( this );
    }

}

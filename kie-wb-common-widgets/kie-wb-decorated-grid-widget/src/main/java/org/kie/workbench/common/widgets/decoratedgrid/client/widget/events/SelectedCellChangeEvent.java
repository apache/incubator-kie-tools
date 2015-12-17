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
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractMergableGridWidget;

/**
 * Represents a change in the selected cell.
 */
public class SelectedCellChangeEvent extends GwtEvent<SelectedCellChangeEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onSelectedCellChange( SelectedCellChangeEvent event );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private AbstractMergableGridWidget.CellSelectionDetail cellDetails;

    /**
     * Creates a cell selection event for no selection
     */
    public SelectedCellChangeEvent() {
    }

    /**
     * Creates a cell selection event
     * @param cellExtents details of selected cell
     */
    public SelectedCellChangeEvent( AbstractMergableGridWidget.CellSelectionDetail cellDetails ) {
        if ( cellDetails == null ) {
            throw new IllegalArgumentException( "cellDetails cannot be null" );
        }
        this.cellDetails = cellDetails;
    }

    /**
     * Gets the details of the selected cell
     * @return the details
     */
    public AbstractMergableGridWidget.CellSelectionDetail getCellSelectionDetail() {
        return this.cellDetails;
    }

    @Override
    public final Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( SelectedCellChangeEvent.Handler handler ) {
        handler.onSelectedCellChange( this );
    }
}

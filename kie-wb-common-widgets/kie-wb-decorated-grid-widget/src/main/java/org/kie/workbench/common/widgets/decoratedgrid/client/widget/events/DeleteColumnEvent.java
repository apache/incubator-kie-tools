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

/**
 * An event to delete a column
 */
public class DeleteColumnEvent
        extends GwtEvent<DeleteColumnEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onDeleteColumn( DeleteColumnEvent event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private int firstColumnIndex;
    private int     numberOfColumns = 1;
    private boolean redraw          = true;

    public DeleteColumnEvent( int firstColumnIndex ) {
        this.firstColumnIndex = firstColumnIndex;
    }

    public DeleteColumnEvent( int firstColumnIndex,
                              boolean redraw ) {
        this.firstColumnIndex = firstColumnIndex;
        this.redraw = redraw;
    }

    public DeleteColumnEvent( int firstColumnIndex,
                              int numberOfColumns,
                              boolean redraw ) {
        this.firstColumnIndex = firstColumnIndex;
        this.numberOfColumns = numberOfColumns;
        this.redraw = redraw;
    }

    public int getFirstColumnIndex() {
        return this.firstColumnIndex;
    }

    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    public boolean redraw() {
        return this.redraw;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( DeleteColumnEvent.Handler handler ) {
        handler.onDeleteColumn( this );
    }

}

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

import java.util.ArrayList;
import java.util.List;

/**
 * An event to insert a column
 */
public abstract class InsertColumnEvent<T, C> extends GwtEvent<InsertColumnEvent.Handler<T, C>> {

    public static interface Handler<T, C>
            extends
            EventHandler {

        void onInsertColumn( InsertColumnEvent<T, C> event );
    }

    private int index;

    private boolean redraw = true;

    private List<T> columns;

    private List<List<C>> columnsData;

    public InsertColumnEvent( List<T> columns,
                              List<List<C>> columnsData,
                              int index,
                              boolean redraw ) {
        this( columns,
              columnsData,
              index );
        this.redraw = redraw;
    }

    public InsertColumnEvent( List<T> columns,
                              List<List<C>> columnsData,
                              int index ) {
        this.columns = columns;
        this.columnsData = columnsData;
        this.index = index;
    }

    public InsertColumnEvent( T column,
                              List<C> columnData,
                              int index,
                              boolean redraw ) {
        this( column,
              columnData,
              index );
        this.redraw = redraw;
    }

    public InsertColumnEvent( T column,
                              List<C> columnData,
                              int index ) {
        this.columns = new ArrayList<T>();
        this.columnsData = new ArrayList<List<C>>();
        this.columns.add( column );
        this.columnsData.add( columnData );
        this.index = index;
    }

    public List<T> getColumns() {
        return this.columns;
    }

    public List<List<C>> getColumnsData() {
        return this.columnsData;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean redraw() {
        return this.redraw;
    }

    @Override
    protected void dispatch( InsertColumnEvent.Handler<T, C> handler ) {
        handler.onInsertColumn( this );
    }

}

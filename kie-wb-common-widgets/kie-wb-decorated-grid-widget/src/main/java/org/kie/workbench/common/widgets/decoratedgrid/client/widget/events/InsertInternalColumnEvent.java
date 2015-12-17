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
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DynamicColumn;

import java.util.List;

/**
 * An event to insert a column in the table
 */
public abstract class InsertInternalColumnEvent<T> extends GwtEvent<InsertInternalColumnEvent.Handler<T>> {

    public static interface Handler<T>
            extends
            EventHandler {

        void onInsertInternalColumn( InsertInternalColumnEvent<T> event );
    }

    private List<DynamicColumn<T>>                         columns;
    private List<List<CellValue<? extends Comparable<?>>>> columnsData;
    private int                                            index;
    private boolean                                        redraw;

    public InsertInternalColumnEvent( List<DynamicColumn<T>> columns,
                                      List<List<CellValue<? extends Comparable<?>>>> columnsData,
                                      int index,
                                      boolean redraw ) {
        this.columns = columns;
        this.columnsData = columnsData;
        this.index = index;
        this.redraw = redraw;
    }

    public List<DynamicColumn<T>> getColumns() {
        return this.columns;
    }

    public List<List<CellValue<? extends Comparable<?>>>> getColumnsData() {
        return this.columnsData;
    }

    public int getIndex() {
        return index;
    }

    public boolean redraw() {
        return redraw;
    }

    @Override
    protected void dispatch( InsertInternalColumnEvent.Handler<T> handler ) {
        handler.onInsertInternalColumn( this );
    }

}

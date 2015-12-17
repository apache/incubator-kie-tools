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
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;

import java.util.List;

/**
 * An event to set the model used within the table
 */
public abstract class SetInternalModelEvent<M, T> extends GwtEvent<SetInternalModelEvent.Handler<M, T>> {

    public static interface Handler<M, T>
            extends
            EventHandler {

        void onSetInternalModel( SetInternalModelEvent<M, T> event );
    }

    private M                      model;
    private DynamicData data;
    private List<DynamicColumn<T>> columns;

    public SetInternalModelEvent( M model,
                                  DynamicData data,
                                  List<DynamicColumn<T>> columns ) {
        this.model = model;
        this.data = data;
        this.columns = columns;
    }

    public M getModel() {
        return this.model;
    }

    public DynamicData getData() {
        return this.data;
    }

    public List<DynamicColumn<T>> getColumns() {
        return this.columns;
    }

    @Override
    protected void dispatch( SetInternalModelEvent.Handler<M, T> handler ) {
        handler.onSetInternalModel( this );
    }

}

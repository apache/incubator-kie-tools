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
 * An event to set the underlying model in the table
 */
public abstract class SetModelEvent<T> extends GwtEvent<SetModelEvent.Handler<T>> {

    public static interface Handler<T>
            extends
            EventHandler {

        void onSetModel( SetModelEvent<T> event );
    }

    private T model;

    public SetModelEvent( T model ) {
        this.model = model;
    }

    public T getModel() {
        return this.model;
    }

    @Override
    protected void dispatch( SetModelEvent.Handler<T> handler ) {
        handler.onSetModel( this );
    }

}

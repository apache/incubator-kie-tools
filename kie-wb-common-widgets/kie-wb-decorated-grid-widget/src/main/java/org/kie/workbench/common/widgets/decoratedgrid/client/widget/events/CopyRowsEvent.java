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

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

/**
 * An event to copy row(s)
 */
public class CopyRowsEvent extends GwtEvent<CopyRowsEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onCopyRows( CopyRowsEvent event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private Set<Integer> rowIndexes;

    public CopyRowsEvent() {
        this.rowIndexes = Collections.emptySet();
    }

    public CopyRowsEvent( SortedSet<Integer> rowIndexes ) {
        this.rowIndexes = rowIndexes;
    }

    public Set<Integer> getRowIndexes() {
        return this.rowIndexes;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( CopyRowsEvent.Handler handler ) {
        handler.onCopyRows( this );
    }

}

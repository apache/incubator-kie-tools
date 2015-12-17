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
 * Represents a change in the grouping of rows. The number of rows may change
 * when the grouping changes, for example the Sidebar needs to be redrawn when
 * grouping is changed
 */
public class RowGroupingChangeEvent extends GwtEvent<RowGroupingChangeEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onRowGroupingChange( RowGroupingChangeEvent event );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    @Override
    public final Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( RowGroupingChangeEvent.Handler handler ) {
        handler.onRowGroupingChange( this );
    }
}

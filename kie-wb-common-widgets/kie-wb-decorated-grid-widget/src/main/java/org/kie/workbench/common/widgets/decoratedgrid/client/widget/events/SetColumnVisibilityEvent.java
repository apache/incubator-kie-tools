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
 * An event to set the visibility of a column
 */
public class SetColumnVisibilityEvent extends GwtEvent<SetColumnVisibilityEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onSetColumnVisibility( SetColumnVisibilityEvent event );

    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final int     index;
    private       boolean isVisible;

    public SetColumnVisibilityEvent( int index,
                                     boolean isVisible ) {
        this.index = index;
        this.isVisible = isVisible;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( SetColumnVisibilityEvent.Handler handler ) {
        handler.onSetColumnVisibility( this );
    }

}

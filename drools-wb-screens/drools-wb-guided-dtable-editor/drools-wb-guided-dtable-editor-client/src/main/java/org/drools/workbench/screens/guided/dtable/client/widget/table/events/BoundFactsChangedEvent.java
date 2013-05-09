/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * An event representing a change in Patterns (added, deleted, edited)
 */
public class BoundFactsChangedEvent extends GwtEvent<BoundFactsChangedEvent.Handler> {

    public static interface Handler
        extends
        EventHandler {

        void onBoundFactsChanged( BoundFactsChangedEvent event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private final List<String>                               factBindings;

    public BoundFactsChangedEvent(List<String> factBindings) {
        this.factBindings = factBindings;
    }

    public List<String> getFactBindings() {
        return factBindings;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onBoundFactsChanged( this );
    }

}

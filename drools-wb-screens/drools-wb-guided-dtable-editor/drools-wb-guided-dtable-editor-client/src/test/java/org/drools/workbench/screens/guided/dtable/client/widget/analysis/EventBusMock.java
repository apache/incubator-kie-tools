/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;

public class EventBusMock
        extends EventBus {

    private UpdateColumnDataEvent updateColumnDataEvent;

    public UpdateColumnDataEvent getUpdateColumnDataEvent() {
        return updateColumnDataEvent;
    }

    @Override
    public <H extends EventHandler> HandlerRegistration addHandler( GwtEvent.Type<H> type,
                                                                    H h ) {
        return null;
    }

    @Override
    public <H extends EventHandler> HandlerRegistration addHandlerToSource( GwtEvent.Type<H> type,
                                                                            Object o,
                                                                            H h ) {
        return null;
    }

    @Override
    public void fireEvent( GwtEvent<?> gwtEvent ) {
        if ( gwtEvent instanceof UpdateColumnDataEvent ) {
            updateColumnDataEvent = (UpdateColumnDataEvent) gwtEvent;

        }
    }

    @Override
    public void fireEventFromSource( GwtEvent<?> gwtEvent,
                                     Object o ) {

    }
}

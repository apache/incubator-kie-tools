/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.events;

import com.google.gwt.event.dom.client.DomEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.CloseCompositeEventHandler;

/**
 * <code>GwtEvent</code> to <b>close</b> a <code>Composite</code>
 */
public class CloseCompositeEvent extends DomEvent<CloseCompositeEventHandler> {

    /**
     * Event type for closeComposite events. Represents the meta-data associated with this
     * event.
     */
    private static final Type<CloseCompositeEventHandler> TYPE = new Type<>("close_composite", new CloseCompositeEvent());

    /**
     * Gets the event type associated with closeComposite events.
     * @return the handler type
     */
    public static Type<CloseCompositeEventHandler> getType() {
        return TYPE;
    }

    public CloseCompositeEvent() {
    }

    @Override
    public Type<CloseCompositeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CloseCompositeEventHandler handler) {
        handler.onEvent(this);
    }
}

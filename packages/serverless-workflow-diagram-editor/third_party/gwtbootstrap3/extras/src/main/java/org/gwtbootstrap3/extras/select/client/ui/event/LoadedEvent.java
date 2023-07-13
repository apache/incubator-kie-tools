package org.gwtbootstrap3.extras.select.client.ui.event;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2016 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.event.shared.GwtEvent;

/**
 * The loaded event is fired immediately when the Loaded instance method is called.
 */
public class LoadedEvent extends GwtEvent<LoadedHandler> {

    private static Type<LoadedHandler> TYPE;

    /**
     * Fires a loaded event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param source the source of the handlers
     */
    public static void fire(final HasLoadedHandlers source) {
        if (TYPE != null) {
            LoadedEvent event = new LoadedEvent();
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<LoadedHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<LoadedHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<LoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final LoadedHandler handler) {
        handler.onLoaded(this);
    }

    /**
     * Creates a loaded event.
     */
    protected LoadedEvent() {}

}

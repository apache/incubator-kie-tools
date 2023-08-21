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
 * The hide event is fired immediately when the hide instance method has been called.
 */
public class HideEvent extends GwtEvent<HideHandler> {

    private static Type<HideHandler> TYPE;

    /**
     * Fires a hide event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param source the source of the handlers
     */
    public static void fire(final HasHideHandlers source) {
        if (TYPE != null) {
            HideEvent event = new HideEvent();
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<HideHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<HideHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<HideHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final HideHandler handler) {
        handler.onHide(this);
    }

    /**
     * Creates a hide event.
     */
    protected HideEvent() {}

}

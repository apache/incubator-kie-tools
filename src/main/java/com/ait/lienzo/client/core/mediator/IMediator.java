/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.mediator;

import elemental2.dom.UIEvent;

import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;

/**
 * IMediator is used in the {@link Mediators} list of a {@link Viewport}.
 * See {@link Mediators} for more information.
 * 
 * @since 1.1
 */
public interface IMediator
{
    /**
     * Acts on the event if needed, and returns true if it did.
     * 
     * @param event One of the Lienzo Node events. (Note that these are not the raw GWT events.)
     * @return Whether it acted on the event. If so, no further {@link Mediators} will be invoked.
     */
    public <H extends EventHandler> boolean handleEvent(Type<H> type, final UIEvent event, int x, int y);

    /**
     * Terminates the current operation and 
     * resets the internal state of the mediator for future use.
     */
    public void cancel();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public String getName();

    public void setName(String name);
}

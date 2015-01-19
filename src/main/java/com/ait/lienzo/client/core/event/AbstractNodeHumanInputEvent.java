/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.event;

import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractNodeHumanInputEvent<T extends HumanInputEvent<?>, H extends EventHandler> extends GwtEvent<H>
{
    private final T m_event;

    protected AbstractNodeHumanInputEvent(T event)
    {
        m_event = event;
    }

    public final T getHumanInputEvent()
    {
        return m_event;
    }

    public final boolean isControlKeyDown()
    {
        return isControlKeyDown(getHumanInputEvent());
    }

    public static final boolean isControlKeyDown(final HumanInputEvent<?> event)
    {
        return ((null != event) && (event.isControlKeyDown()));
    }

    public final boolean isShiftKeyDown()
    {
        return isShiftKeyDown(getHumanInputEvent());
    }

    public static final boolean isShiftKeyDown(final HumanInputEvent<?> event)
    {
        return ((null != event) && (event.isShiftKeyDown()));
    }

    public final boolean isAltKeyDown()
    {
        return isAltKeyDown(getHumanInputEvent());
    }

    public static final boolean isAltKeyDown(final HumanInputEvent<?> event)
    {
        return ((null != event) && (event.isAltKeyDown()));
    }

    public final boolean isMetaKeyDown()
    {
        return isMetaKeyDown(getHumanInputEvent());
    }

    public static final boolean isMetaKeyDown(final HumanInputEvent<?> event)
    {
        return ((null != event) && (event.isMetaKeyDown()));
    }
}

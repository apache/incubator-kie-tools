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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractNodeTouchEvent<T extends TouchEvent<?>, H extends EventHandler> extends AbstractNodeHumanInputEvent<T, H> implements INodeXYEvent
{
    private final List<TouchPoint> m_touches;

    public static class Type<H> extends GwtEvent.Type<H>
    {
    }

    protected AbstractNodeTouchEvent(T event, List<TouchPoint> touches)
    {
        super(event);

        if (null == touches)
        {
            m_touches = new ArrayList<TouchPoint>();
        }
        else
        {
            m_touches = touches;
        }
    }

    public List<TouchPoint> getTouches()
    {
        return Collections.unmodifiableList(m_touches);
    }

    @Override
    public int getX()
    {
        if (m_touches.size() > 0)
        {
            TouchPoint touch = m_touches.get(0);

            if (null != touch)
            {
                return touch.getX();
            }
        }
        return 0;
    }

    @Override
    public int getY()
    {
        if (m_touches.size() > 0)
        {
            TouchPoint touch = m_touches.get(0);

            if (null != touch)
            {
                return touch.getY();
            }
        }
        return 0;
    }

    @Override
    public GwtEvent<?> getNodeEvent()
    {
        return this;
    }

    public final T getTouchEvent()
    {
        return getHumanInputEvent();
    }
}

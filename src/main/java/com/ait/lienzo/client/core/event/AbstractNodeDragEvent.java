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

import com.ait.lienzo.client.widget.DragContext;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

abstract class AbstractNodeDragEvent<H extends EventHandler> extends GwtEvent<H> implements INodeXYEvent
{
    private final DragContext m_drag;

    public static class Type<H> extends GwtEvent.Type<H>
    {
    }

    public AbstractNodeDragEvent(DragContext drag)
    {
        m_drag = drag;
    }

    @Override
    public int getX()
    {
        return m_drag.getEventX();
    }

    public final DragContext getDragContext()
    {
        return m_drag;
    }

    @Override
    public int getY()
    {
        return m_drag.getEventY();
    }

    @Override
    public GwtEvent<?> getNodeEvent()
    {
        return this;
    }
}

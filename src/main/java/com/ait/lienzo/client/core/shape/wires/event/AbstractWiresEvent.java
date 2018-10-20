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

package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.INodeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractWiresEvent<S, H extends EventHandler> extends GwtEvent<H> implements INodeEvent
{
    private final S shape;

    private boolean m_dead = false;

    public AbstractWiresEvent(S shape)
    {
        this.shape = shape;
    }

    public S getShape()
    {
        return shape;
    }

    @Override
    public final boolean isAlive()
    {
        return (false == m_dead);
    }

    @Override
    public final void preventDefault()
    {
        m_dead = true;
    }

    @Override
    public final void stopPropagation()
    {
        m_dead = true;
    }

    @Override
    public final GwtEvent<?> getNodeEvent()
    {
        return this;
    }
}

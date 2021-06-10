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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.tools.common.api.types.Activatable;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

public abstract class AbstractControlHandle extends Activatable implements IControlHandle
{
    private final HandlerRegistrationManager m_manage = new HandlerRegistrationManager();

    protected AbstractControlHandle()
    {
        super(true);
    }

    protected HandlerRegistration register(final HandlerRegistration handler)
    {
        return m_manage.register(handler);
    }

    protected void deregister(final HandlerRegistrationManager manager)
    {
        if (null != manager)
        {
            manager.deregister(m_manage);
        }
    }

    protected void deregister(final HandlerRegistration handler)
    {
        m_manage.deregister(handler);
    }

    @Override
    public void destroy()
    {
        IPrimitive<?> prim = getControl();

        if (null != prim)
        {
            prim.removeFromParent();
        }
        m_manage.destroy();
    }

    @Override
    public final HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_manage;
    }
}

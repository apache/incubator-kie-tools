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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.HandlerRegistrationManager;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.shared.java.util.Activatible;
import com.google.gwt.event.shared.HandlerRegistration;

public abstract class AbstractControlHandle extends Activatible implements IControlHandle
{
    private final HandlerRegistrationManager m_manage = new HandlerRegistrationManager();

    protected HandlerRegistration register(final HandlerRegistration handler)
    {
        return m_manage.register(handler);
    }

    protected void delete(final HandlerRegistrationManager manager)
    {
        if (null != manager)
        {
            manager.delete(m_manage);
        }
    }

    protected void delete(final HandlerRegistration handler)
    {
        m_manage.delete(handler);
    }

    @Override
    public void destroy()
    {
        IPrimitive<?> prim = getControl();

        if (null != prim)
        {
            prim.removeFromParent();
        }
        m_manage.delete();
    }

    @Override
    public final HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_manage;
    }
}

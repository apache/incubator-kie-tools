/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class WiresContainer
{
    private NFastArrayList<WiresShape>   m_childShapes;

    private IContainer<?, IPrimitive<?>> m_container;

    private WiresContainer               m_parent;

    private IContainmentAcceptor         m_containmentAcceptor = IContainmentAcceptor.ALL;;
    private IDockingAcceptor             m_dockingAcceptor     = IDockingAcceptor.ALL;

    private WiresContainer               dockedTo;

    public WiresContainer(IContainer<?, IPrimitive<?>> container)
    {
        m_container = container;
    }

    public IContainer<?, IPrimitive<?>> getContainer()
    {
        return m_container;
    }

    public void setContainer(IContainer<?, IPrimitive<?>> container)
    {
        m_container = container;
    }

    public WiresContainer getParent()
    {
        return m_parent;
    }

    public void setParent(WiresContainer parent)
    {
        m_parent = parent;
    }

    public NFastArrayList<WiresShape> getChildShapes()
    {
        return m_childShapes;
    }

    public IContainmentAcceptor getContainmentAcceptor()
    {
        return m_containmentAcceptor;
    }

    public void setContainmentAcceptor(IContainmentAcceptor containmentAcceptor)
    {
        m_containmentAcceptor = containmentAcceptor;
    }

    public IDockingAcceptor getDockingAcceptor() {
        return m_dockingAcceptor;
    }

    public void setDockingAcceptor(IDockingAcceptor dockingAcceptor)
    {
        m_dockingAcceptor = dockingAcceptor;
    }

    public void add(WiresShape shape)
    {
        if (shape.getParent() == this)
        {
            return;
        }
        if (shape.getParent() != null)
        {
            shape.removeFromParent();
        }

        if (m_childShapes == null)
        {
            m_childShapes = new NFastArrayList<WiresShape>();
        }
        m_childShapes.add(shape);

        m_container.add(shape.getGroup());

        shape.setParent(this);

        if (shape.getMagnets() != null)
        {
            shape.getMagnets().shapeMoved();
        }
    }

    public void remove(WiresShape shape)
    {
        if (m_childShapes != null)
        {
            m_childShapes.remove(shape);

            m_container.remove(shape.getGroup());

            shape.setParent(null);
        }
    }

    public void setDockedTo(WiresContainer dockedTo)
    {
        this.dockedTo = dockedTo;
    }

    public WiresContainer getDockedTo()
    {
        return dockedTo;
    }
}

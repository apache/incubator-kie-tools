package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class WiresContainer
{
    private NFastArrayList<WiresShape> m_childShapes;

    private IContainer                 m_container;

    private WiresContainer             m_parent;

    public WiresContainer(IContainer container)
    {
        m_container = container;
    }

    public IContainer getContainer()
    {
        return m_container;
    }

    public void setContainer(IContainer container)
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

    public void add(WiresShape shape)
    {
        if ( shape.getParent() == this )
        {
            return;
        }

        if ( shape.getParent() != null )
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
}

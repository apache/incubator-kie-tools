package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.util.Console;

public class WiresShape extends WiresContainer
{
    private WiresManager               m_manager;

    private MultiPath                  m_path;

    private IMagnets                   m_magnets;

    private boolean                    m_dragTarget;

    public WiresShape(MultiPath path, Group group, WiresManager manager)
    {
        super(group);
        m_path = path;
        m_manager = manager;
    }

    public Group getGroup()
    {
        return (Group) getContainer();
    }

    public MultiPath getPath()
    {
        return m_path;
    }

    public IMagnets getMagnets()
    {
        return m_magnets;
    }

    public void setMagnets(IMagnets magnets)
    {
        m_magnets = magnets;
    }

    public boolean isDragTarget()
    {
        return m_dragTarget;
    }

    public void setDragTarget(boolean dragTarget)
    {
        m_dragTarget = dragTarget;
    }

    public void removeFromParent()
    {
        if ( getParent() != null )
        {
            getParent().remove(this);
        }
    }

    public WiresLayer getWiresLayer()
    {
        WiresContainer current = this;
        while ( current.getParent() != null )
        {
            current = current.getParent();
        }
        return (WiresLayer) current;
    }

}

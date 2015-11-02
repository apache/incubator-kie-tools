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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;

public class WiresShape extends WiresContainer
{
    private MultiPath    m_path;

    private IMagnets     m_magnets;

    private boolean      m_dragTarget;

    public WiresShape(MultiPath path, Group group, WiresManager manager)
    {
        super(group);
        
        m_path = path;
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
        if (getParent() != null)
        {
            getParent().remove(this);
        }
    }

    public WiresLayer getWiresLayer()
    {
        WiresContainer current = this;

        while (current.getParent() != null)
        {
            current = current.getParent();
        }
        return (WiresLayer) current;
    }
}

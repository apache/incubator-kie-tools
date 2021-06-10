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

package com.ait.lienzo.client.core.event;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.tools.client.event.INodeXYEvent;
import com.ait.lienzo.tools.client.event.MouseEventUtil;

import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;

public abstract class AbstractNodeDragEvent<H> extends AbstractNodeEvent<H, Node> implements INodeXYEvent<H, Node>
{
    private DragContext m_drag;

//    public AbstractNodeDragEvent(final DragContext drag)
//    {
//        m_drag = drag;
//    }

    public AbstractNodeDragEvent(final HTMLElement relativeElement)
    {
        super(relativeElement);
    }


    public void reviveMouseEvent(Node sourceNode, final DragContext drag)
    {
        this.m_drag  = m_drag;
        setSource(sourceNode);

//        m_x = MouseEventUtil.getRelativeX(event.clientX, getRelativeElement());
//
//        m_y = MouseEventUtil.getRelativeY(event.clientY, getRelativeElement());
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
}

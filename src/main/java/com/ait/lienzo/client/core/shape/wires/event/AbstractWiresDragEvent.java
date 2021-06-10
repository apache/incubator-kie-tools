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

import com.ait.lienzo.client.core.event.AbstractNodeEvent;
import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.tools.client.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;

import elemental2.dom.Event;
import elemental2.dom.HTMLElement;

/**
 * <p>Base event that is fired when a wires container is being drag.</p>
 * <ul>
 *     <li>shape = the wires container</li>
 *     <li>x = the X coordinate of the shape's container</li>
 *     <li>y = the Y coordinate of the shape's container</li>
 *     <li>nodeDragEvent = the drag event on the node.</li>
 * </ul>
 */
public abstract class AbstractWiresDragEvent<H extends EventHandler, H2 extends EventHandler> extends AbstractNodeEvent<H, WiresContainer> implements INodeXYEvent<H, WiresContainer>
{
    private AbstractNodeHumanInputEvent<H2, Node> nodeDragEvent;

    public AbstractWiresDragEvent(final HTMLElement relativeElement)
    {
        super(relativeElement);
    }

    public void kill()
    {
        setSource(null);
        setDead(true);
        nodeDragEvent = null;
    }

    public void revive()
    {
        setSource(null);
        setDead(false);
        nodeDragEvent = null;
    }

    public void override(final WiresContainer shape, final AbstractNodeHumanInputEvent<H2, Node> nodeDragEvent)
    {
        setSource(shape);
        this.nodeDragEvent = nodeDragEvent;
    }

    @Override
    public int getX()
    {
        return nodeDragEvent.getX();
    }

    @Override
    public int getY()
    {
        return nodeDragEvent.getY();
    }

    public AbstractNodeHumanInputEvent<H2, Node> getNodeDragEvent()
    {
        return nodeDragEvent;
    }

    public Event getNativeEvent()
    {
        throw new UnsupportedOperationException();
    }
}

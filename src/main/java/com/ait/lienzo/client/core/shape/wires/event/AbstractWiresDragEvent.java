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

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

/**
 * <p>Base event that is fired when a wires container is being drag.</p>
 * <ul>
 *     <li>shape = the wires container</li>
 *     <li>x = the X coordinate of the shape's container</li>
 *     <li>y = the Y coordinate of the shape's container</li>
 *     <li>nodeDragEvent = the drag event on the node.</li>
 * </ul>
 */
public abstract class AbstractWiresDragEvent<H extends WiresEventHandler>extends AbstractWiresEvent<WiresContainer, H> implements INodeXYEvent
{
    private final AbstractNodeDragEvent<?> nodeDragEvent;

    public AbstractWiresDragEvent(final WiresContainer shape, final AbstractNodeDragEvent<?> nodeDragEvent)
    {
        super(shape);
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

    public AbstractNodeDragEvent<?> getNodeDragEvent()
    {
        return nodeDragEvent;
    }
}

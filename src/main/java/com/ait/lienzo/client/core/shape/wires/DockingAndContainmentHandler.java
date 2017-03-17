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

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDragControlContext;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;

public class DockingAndContainmentHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragEndHandler, DragConstraintEnforcer
{

    private WiresDockingAndContainmentControl dockingAndContainmentControl;

    public DockingAndContainmentHandler(WiresShape shape, WiresManager wiresManager)
    {

        this.dockingAndContainmentControl = wiresManager.getControlFactory().newDockingAndContainmentControl(shape, wiresManager);
    }

    public WiresDockingAndContainmentControl getDockingAndContainmentControl()
    {
        return dockingAndContainmentControl;
    }

    @Override
    public void startDrag(DragContext dragContext)
    {
        this.dockingAndContainmentControl.dragStart(new WiresDragControlContext(dragContext.getDragStartX(), dragContext.getDragStartY(), dragContext.getNode()));
    }

    @Override
    public boolean adjust(final Point2D dxy)
    {
        return this.dockingAndContainmentControl.dragAdjust(dxy);
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event)
    {
        this.dockingAndContainmentControl.dragEnd(new WiresDragControlContext(event.getX(), event.getY(), event.getSource()));
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        this.dockingAndContainmentControl.onNodeMouseDown();
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event)
    {
        this.dockingAndContainmentControl.onNodeMouseUp();
    }

}

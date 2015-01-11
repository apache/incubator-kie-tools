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

package com.ait.lienzo.client.core.event;

import com.ait.lienzo.client.widget.DragContext;

public class NodeDragMoveEvent extends AbstractNodeDragEvent<NodeDragMoveHandler>
{
    private static final Type<NodeDragMoveHandler> TYPE = new Type<NodeDragMoveHandler>();

    public static Type<NodeDragMoveHandler> getType()
    {
        return TYPE;
    }

    public NodeDragMoveEvent(DragContext drag)
    {
        super(drag);
    }

    @Override
    public final Type<NodeDragMoveHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeDragMoveHandler handler)
    {
        handler.onNodeDragMove(this);
    }
}

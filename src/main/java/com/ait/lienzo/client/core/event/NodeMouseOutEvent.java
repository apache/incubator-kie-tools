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

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;

public class NodeMouseOutEvent extends AbstractNodeMouseEvent<MouseEvent<?>, NodeMouseOutHandler>
{
    private static final Type<NodeMouseOutHandler> TYPE = new Type<NodeMouseOutHandler>();

    public static Type<NodeMouseOutHandler> getType()
    {
        return TYPE;
    }

    public NodeMouseOutEvent(MouseOutEvent event)
    {
        super(event);
    }

    @Override
    public final Type<NodeMouseOutHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeMouseOutHandler handler)
    {
        handler.onNodeMouseOut(this);
    }
}

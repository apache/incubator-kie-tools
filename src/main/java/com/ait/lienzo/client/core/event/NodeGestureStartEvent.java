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

public class NodeGestureStartEvent extends AbstractNodeGestureEvent<NodeGestureStartHandler>
{
    private static final Type<NodeGestureStartHandler> TYPE = new Type<NodeGestureStartHandler>();

    public static Type<NodeGestureStartHandler> getType()
    {
        return TYPE;
    }

    public NodeGestureStartEvent(double scale, double rotation)
    {
        super(scale, rotation);
    }

    @Override
    public final Type<NodeGestureStartHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeGestureStartHandler handler)
    {
        handler.onNodeGestureStart(this);
    }
}

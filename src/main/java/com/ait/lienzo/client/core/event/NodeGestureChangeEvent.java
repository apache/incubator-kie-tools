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

public class NodeGestureChangeEvent extends AbstractNodeGestureEvent<NodeGestureChangeHandler>
{
    private static final Type<NodeGestureChangeHandler> TYPE = new Type<NodeGestureChangeHandler>();

    public static Type<NodeGestureChangeHandler> getType()
    {
        return TYPE;
    }

    public NodeGestureChangeEvent(double scale, double rotation)
    {
        super(scale, rotation);
    }

    @Override
    public final Type<NodeGestureChangeHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeGestureChangeHandler handler)
    {
        handler.onNodeGestureChange(this);
    }
}

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

import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.WheelEvent;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class NodeMouseWheelEvent extends AbstractNodeHumanInputEvent<NodeMouseWheelHandler, Node>
{
    private static final Type<NodeMouseWheelHandler> TYPE = new Type<>();

    public static final Type<NodeMouseWheelHandler> getType()
    {
        return TYPE;
    }

    public NodeMouseWheelEvent(final HTMLElement relativeElement)
    {
        super(relativeElement);
    }

    /**
     * Returns the normalized delta Y of the mouse wheel.
     * 
     * Typical values are 1 for North and -1 for South.
     * <p>
     * Note that the values returned by the GWT MouseWheelEvent are
     * inconsistent. They can still be accessed via getOriginalEvent().getDeltaY().
     * 
     * @return double
     */
    public double getDeltaY()
    {
        return getNormalizedDeltaY(getOriginalEvent());
    }

    public double getNormalizedDeltaY()
    {
        return getOriginalEvent().deltaY;
    }

    public boolean isNorth()
    {
        return getDeltaY() > 0;
    }

    public boolean isSouth()
    {
        return getDeltaY() < 0;
    }

    public WheelEvent getOriginalEvent()
    {
        return (WheelEvent) getNativeEvent();
    }

    @Override
    public final Type<NodeMouseWheelHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    public void dispatch(final NodeMouseWheelHandler handler)
    {
        handler.onNodeMouseWheel(this);
    }

    /**
     * Returns the change in the mouse wheel position along the Y-axis; positive if
     * the mouse wheel is moving north (toward the top of the screen) or negative
     * if the mouse wheel is moving south (toward the bottom of the screen).
     * 
     * Note that delta values *are* normalized across browsers or OSes.
     * 
     * @return the delta of the mouse wheel along the y axis
     */
    public static double getNormalizedDeltaY(MouseEvent event)
    {

        WheelEvent    wheelEvent = Js.uncheckedCast(event);
        JsPropertyMap<Object> wheelventMap  = Js.uncheckedCast(wheelEvent);

        double delta = 0;
        if (wheelventMap.has("wheelDelta")) {
            // IE/Opera.
            delta = (int)wheelventMap.get("wheelDelta") / 120;
        } else if (wheelventMap.has("detail")) {
            // Mozilla case.
            // In Mozilla, sign of delta is different than in IE.
            // Also, delta is multiple of 3.
            delta = -wheelEvent.detail / 3;
        }
        return delta;
    }
}

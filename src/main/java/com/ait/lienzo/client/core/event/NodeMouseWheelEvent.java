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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

public class NodeMouseWheelEvent extends AbstractNodeMouseEvent<MouseEvent<?>, NodeMouseWheelHandler> // extends GwtEvent<NodeMouseWheelHandler>
{
    private static final Type<NodeMouseWheelHandler> TYPE = new Type<NodeMouseWheelHandler>();

    public static Type<NodeMouseWheelHandler> getType()
    {
        return TYPE;
    }

    public NodeMouseWheelEvent(MouseWheelEvent event)
    {
        super(event);
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
        return getOriginalEvent().getDeltaY();
    }

    public boolean isNorth()
    {
        return getDeltaY() > 0;
    }

    public boolean isSouth()
    {
        return getDeltaY() < 0;
    }

    public MouseWheelEvent getOriginalEvent()
    {
        return (MouseWheelEvent) getMouseEvent();
    }

    @Override
    public final Type<NodeMouseWheelHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeMouseWheelHandler handler)
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
    public static double getNormalizedDeltaY(MouseWheelEvent e)
    {
        return getNativeNormalizedDeltaY(e.getNativeEvent());
    }

    /**
     * Returns the change in the mouse wheel position along the Y-axis; positive if
     * the mouse wheel is moving north (toward the top of the screen) or negative
     * if the mouse wheel is moving south (toward the bottom of the screen).
     * 
     * Note that delta values *are* normalized across browsers or OSes.
     * 
     * @return the delta of the mouse wheel along the y axis
     * 
     * @see http://stackoverflow.com/questions/6775168/zooming-with-canvas        
     * @see http://www.adomas.org/javascript-mouse-wheel/
     */
    private static native final double getNativeNormalizedDeltaY(NativeEvent event)
    /*-{
		var delta = 0;
		if (event.wheelDelta) {
			// IE/Opera.
			delta = event.wheelDelta / 120;
		} else if (event.detail) {
			// Mozilla case. 
			// In Mozilla, sign of delta is different than in IE.
			// Also, delta is multiple of 3.
			delta = -event.detail / 3;
		}
		return delta;
    }-*/;
}

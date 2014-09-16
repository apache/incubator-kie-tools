/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.types;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public final class BoundingBox
{
    private final BoundingBoxJSO m_jso;

    public BoundingBox(double x, double y, double width, double height)
    {
        this(BoundingBoxJSO.make(x, y, width, height));
    }

    public BoundingBox(BoundingBoxJSO jso)
    {
        m_jso = jso;
    }

    public final double getX()
    {
        return m_jso.getX();
    }

    public final double getY()
    {
        return m_jso.getY();
    }

    public final double getWidth()
    {
        return m_jso.getWidth();
    }

    public final double getHeight()
    {
        return m_jso.getHeight();
    }

    public final BoundingBoxJSO getJSO()
    {
        return m_jso;
    }

    public final String toJSONString()
    {
        return new JSONObject(m_jso).toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    public final static class BoundingBoxJSO extends JavaScriptObject
    {
        protected BoundingBoxJSO()
        {
        }

        final static native BoundingBoxJSO make(double x, double y, double width, double height)
        /*-{
            return {x: x, y: y, width: width, height: height};
        }-*/;

        final native double getX()
        /*-{
            return this.x;
        }-*/;

        final native double getY()
        /*-{
            return this.y;
        }-*/;

        final native double getWidth()
        /*-{
            return this.width;
        }-*/;

        final native double getHeight()
        /*-{
            return this.height;
        }-*/;
    }
}

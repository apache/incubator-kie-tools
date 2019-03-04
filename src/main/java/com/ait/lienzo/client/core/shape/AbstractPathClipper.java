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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.shared.core.types.PathClipperType;
import com.google.gwt.core.client.JavaScriptObject;

public abstract class AbstractPathClipper implements IPathClipper
{
    private final PathClipperJSO m_jso;

    protected AbstractPathClipper(final PathClipperJSO jso)
    {
        m_jso = jso;
    }

    public final PathClipperJSO getJSO()
    {
        return m_jso;
    }

    @Override
    public boolean isActive()
    {
        return m_jso.isActive_0();
    }

    @Override
    public boolean setActive(boolean active)
    {
        if (active == isActive())
        {
            return false;
        }
        m_jso.setActive_0(active);

        return true;
    }

    public final PathClipperType getType()
    {
        return m_jso.getType();
    }

    public final double getX()
    {
        return m_jso.getX_0();
    }

    public final void setX(final double x)
    {
        m_jso.setX_0(x);
    }

    public final double getY()
    {
        return m_jso.getY_0();
    }

    public final void setY(final double y)
    {
        m_jso.setY_0(y);
    }

    @Override
    public boolean clip(final Context2D context)
    {
        if (isActive() && (null != getValue()))
        {
            final double x = getX();

            final double y = getY();

            context.translate(x, y);

            final boolean good = apply(context);

            context.translate(-x, -y);

            return good;
        }
        return false;
    }

    protected final JavaScriptObject getValue()
    {
        return m_jso.getValue_0();
    }

    abstract protected boolean apply(Context2D context);

    public static final class PathClipperJSO extends JavaScriptObject
    {
        public static final PathPartList deep(final PathPartList path)
        {
            if (null == path)
            {
                return null;
            }
            if (path.size() < 2)
            {
                return null;
            }
            final PathPartList copy = path.deep();

            if (null == copy)
            {
                return null;
            }
            if (copy.size() < 2)
            {
                return null;
            }
            if (false == copy.isClosed())
            {
                copy.Z();
            }
            return copy;
        }

        static final PathClipperJSO make(final BoundingBox bbox)
        {
            final PathClipperJSO jso = JavaScriptObject.createObject().cast();

            jso.setType_0(PathClipperType.BOUNDING_BOX.getValue());

            jso.setValue_0(bbox.getJSO());

            jso.setX_0(0);

            jso.setY_0(0);

            return jso;
        }

        static final PathClipperJSO make(final PathPartList path)
        {
            final PathClipperJSO jso = JavaScriptObject.createObject().cast();

            jso.setType_0(PathClipperType.PATH_PART_LIST.getValue());

            if (null == path)
            {
                jso.setValue_0(null);
            }
            else
            {
                PathPartList list = deep(path);
                if (list != null)
                {
                    jso.setValue_0(list.getJSO());
                }
            }
            jso.setX_0(0);

            jso.setY_0(0);

            return jso;
        }

        protected PathClipperJSO()
        {
        }

        public final PathClipperType getType()
        {
            return PathClipperType.lookup(getType_0());
        }

        final native void setType_0(String type)
        /*-{
			this.type = type;
        }-*/;

        final native void setX_0(double x)
        /*-{
			this.x = x;
        }-*/;

        final native double getX_0()
        /*-{
			return this.x;
        }-*/;

        final native void setY_0(double y)
        /*-{
			this.y = y;
        }-*/;

        final native double getY_0()
        /*-{
			return this.y;
        }-*/;

        final native void setValue_0(JavaScriptObject value)
        /*-{
			this.value = value;
        }-*/;

        final native JavaScriptObject getValue_0()
        /*-{
			return this.value;
        }-*/;

        final native String getType_0()
        /*-{
			return this.type;
        }-*/;

        final native void setActive_0(boolean active)
        /*-{
			this.active = active;
        }-*/;

        final native boolean isActive_0()
        /*-{
			return (!!this.active);
        }-*/;
    }
}

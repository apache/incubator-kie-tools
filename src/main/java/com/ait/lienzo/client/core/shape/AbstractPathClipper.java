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

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

// @TODO This class and it's children need more checking (mdp)
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
        return m_jso.isActive();
    }

    @Override
    public boolean setActive(boolean active)
    {
        if (active == isActive())
        {
            return false;
        }
        m_jso.setActive(active);

        return true;
    }

    public final PathClipperType getType()
    {
        return m_jso.getType();
    }

    public final double getX()
    {
        return m_jso.getX();
    }

    public final void setX(final double x)
    {
        m_jso.setX(x);
    }

    public final double getY()
    {
        return m_jso.getY();
    }

    public final void setY(final double y)
    {
        m_jso.setY(y);
    }

    @Override
    public boolean clip(final Context2D context)
    {
        if (isActive() && (null != getBoundBox() || null != getPathPartList()))
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

    protected final PathPartList getPathPartList()
    {
        return m_jso.getPathPartList();
    }

    protected final BoundingBox getBoundBox()
    {
        return m_jso.getBoundingBox();
    }

    abstract protected boolean apply(Context2D context);


    @JsType
    public static final class PathClipperJSO
    {
        private String type;

        private double x;

        private double y;

        private boolean active;

        private BoundingBox bbox;

        private PathPartList plist;

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
            final PathClipperJSO jso = new PathClipperJSO();

            jso.setTypeString(PathClipperType.BOUNDING_BOX.getValue());

            jso.setBoundingBox(bbox);

            jso.setX(0);

            jso.setY(0);

            return jso;
        }

        static final PathClipperJSO make(final PathPartList path)
        {
            final PathClipperJSO jso = new PathClipperJSO();

            jso.setTypeString(PathClipperType.PATH_PART_LIST.getValue());

            if (null == path)
            {
                jso.setPathPartList(null);
            }
            else
            {
                PathPartList list = deep(path);
                if (list != null)
                {
                    jso.setPathPartList(deep(path));
                }
            }
            jso.setX(0);

            jso.setY(0);

            return jso;
        }

        protected PathClipperJSO()
        {
        }

        public final PathClipperType getType()
        {
            return PathClipperType.lookup(getTypeString());
        }

        @JsProperty
        final  void setTypeString(String type)
        {
			this.type = type;
        };

        @JsProperty
        final void setX(double x)
        {
			this.x = x;
        };

        @JsProperty
        final double getX()
        {
			return this.x;
        };

        @JsProperty
        final void setY(double y)
        {
			this.y = y;
        };

        @JsProperty
        final double getY()
        {
			return this.y;
        };

        @JsProperty
        final void setBoundingBox(BoundingBox bbox)
        {
			this.bbox = bbox;
        };

        @JsProperty
        final BoundingBox getBoundingBox()
        {
			return this.bbox;
        };

        @JsProperty
        final void setPathPartList(PathPartList plist)
        {
            this.plist = plist;
        };

        @JsProperty
        final PathPartList getPathPartList()
        {
            return this.plist;
        };

        @JsProperty
        final String getTypeString()
        {
			return this.type;
        };

        @JsProperty
        final void setActive(boolean active)
        {
			this.active = active;
        };

        @JsProperty
        final boolean isActive()
        {
			return (!!this.active);
        };
    }
}

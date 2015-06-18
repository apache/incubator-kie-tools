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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PathPartListArray;
import com.ait.tooling.common.api.types.Activatable;

@SuppressWarnings("serial")
public abstract class AbstractPathClipper extends Activatable implements IPathClipper
{
    protected AbstractPathClipper()
    {
    }

    @Override
    public boolean clip(final Context2D context)
    {
        if (isActive())
        {
            return apply(context);
        }
        return false;
    }

    abstract protected boolean apply(Context2D context);

    public static final IPathClipper make(final BoundingBox bbox)
    {
        if (null == bbox)
        {
            return null;
        }
        if (false == bbox.isValid())
        {
            return null;
        }
        if ((bbox.getWidth() == 0) || (bbox.getHeight() == 0))
        {
            return null;
        }
        return new BoundingBoxPathClipper(new BoundingBox(bbox));
    }

    private static final PathPartList deep(final PathPartList path)
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

    public static final IPathClipper make(final PathPartList path)
    {
        final PathPartList copy = deep(path);

        if (null == copy)
        {
            return null;
        }
        return new PathPartListPathClipper(copy);
    }

    public static final IPathClipper make(final PathPartListArray path)
    {
        if (null == path)
        {
            return null;
        }
        final int size = path.size();

        if (size < 1)
        {
            return null;
        }
        if (size == 1)
        {
            final PathPartList copy = deep(path.get(0));

            if (null == copy)
            {
                return null;
            }
            return new PathPartListPathClipper(copy);
        }
        final PathPartListArray list = new PathPartListArray();

        for (int i = 0; i < size; i++)
        {
            final PathPartList copy = deep(path.get(i));

            if (null != copy)
            {
                list.add(copy);
            }
        }
        if (list.size() == 1)
        {
            final PathPartList copy = list.get(0);

            if (null == copy)
            {
                return null;
            }
            return new PathPartListPathClipper(copy);
        }
        return new PathPartListArrayPathClipper(list);
    }

    private static final class BoundingBoxPathClipper extends AbstractPathClipper
    {
        private static final long serialVersionUID = 7860410970267151015L;

        private final double      m_x;

        private final double      m_y;

        private final double      m_w;

        private final double      m_h;

        private BoundingBoxPathClipper(final BoundingBox bbox)
        {
            m_x = bbox.getX();

            m_y = bbox.getY();

            m_w = bbox.getWidth();

            m_h = bbox.getHeight();

            setActive(true);
        }

        @Override
        protected boolean apply(final Context2D context)
        {
            context.rect(m_x, m_y, m_w, m_h);

            context.clip();

            return true;
        }
    }

    private static final class PathPartListPathClipper extends AbstractPathClipper
    {
        private static final long  serialVersionUID = -8566776415376567100L;

        private final PathPartList m_path;

        private PathPartListPathClipper(final PathPartList path)
        {
            m_path = path;

            setActive(true);
        }

        @Override
        protected boolean apply(final Context2D context)
        {
            final boolean fill = context.path(m_path);

            if (fill)
            {
                context.clip();
            }
            return fill;
        }
    }

    private static final class PathPartListArrayPathClipper extends AbstractPathClipper
    {
        private static final long       serialVersionUID = -688994989495752351L;

        private final PathPartListArray m_list;

        private PathPartListArrayPathClipper(final PathPartListArray list)
        {
            m_list = list;

            setActive(true);
        }

        @Override
        protected boolean apply(final Context2D context)
        {
            boolean fill = true;

            final int size = m_list.size();

            for (int i = 0; i < size; i++)
            {
                fill = (fill && context.path(m_list.get(i)));
            }
            if (fill)
            {
                context.clip();
            }
            return fill;
        }
    }
}

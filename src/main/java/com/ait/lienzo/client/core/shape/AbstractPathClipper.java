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
        super(true);
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
        return new BoundingBoxPathClipper(bbox);
    }

    public static final IPathClipper make(final PathPartList path)
    {
        return new PathPartListPathClipper(path);
    }

    public static final IPathClipper make(final PathPartListArray path)
    {
        return new PathPartListArrayPathClipper(path);
    }

    private static final class BoundingBoxPathClipper extends AbstractPathClipper
    {
        private static final long serialVersionUID = 7860410970267151015L;

        private BoundingBoxPathClipper(final BoundingBox bbox)
        {
        }

        @Override
        protected boolean apply(final Context2D context)
        {
            return false;
        }
    }

    private static final class PathPartListPathClipper extends AbstractPathClipper
    {
        private static final long serialVersionUID = -8566776415376567100L;

        private PathPartListPathClipper(final PathPartList path)
        {
        }

        @Override
        protected boolean apply(final Context2D context)
        {
            return false;
        }
    }

    private static final class PathPartListArrayPathClipper extends AbstractPathClipper
    {
        private static final long serialVersionUID = -688994989495752351L;

        private PathPartListArrayPathClipper(final PathPartListArray path)
        {
        }

        @Override
        protected boolean apply(final Context2D context)
        {
            return false;
        }
    }
}

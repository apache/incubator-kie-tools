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

public final class BoundingBoxPathClipper extends AbstractPathClipper
{
    public static final String TYPE = "BoundingBoxPathClipper";

    private final BoundingBox  m_bbox;

    public BoundingBoxPathClipper(final PathClipperJSO clip)
    {
        super(clip);

        m_bbox = BoundingBox.fromBoundingBox(getBoundBox());
    }

    public BoundingBoxPathClipper(final BoundingBox bbox)
    {
        this(PathClipperJSO.make(bbox));
    }

    @Override
    protected final boolean apply(final Context2D context)
    {
        context.beginPath();

        context.rect(m_bbox.getX(), m_bbox.getY(), m_bbox.getWidth(), m_bbox.getHeight());

        context.clip();

        return true;
    }
}

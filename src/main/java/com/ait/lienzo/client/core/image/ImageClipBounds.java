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

package com.ait.lienzo.client.core.image;

public final class ImageClipBounds
{
    private final int m_clip_xpos;

    private final int m_clip_ypos;

    private final int m_clip_wide;

    private final int m_clip_high;

    private final int m_dest_wide;

    private final int m_dest_high;

    public ImageClipBounds(final int cx, final int cy, final int cw, final int ch, final int dw, final int dh)
    {
        m_clip_xpos = cx;

        m_clip_ypos = cy;

        m_clip_wide = cw;

        m_clip_high = ch;

        m_dest_wide = dw;

        m_dest_high = dh;
    }

    public final int getClipXPos()
    {
        return m_clip_xpos;
    }

    public final int getClipYPos()
    {
        return m_clip_ypos;
    }

    public final int getClipWide()
    {
        return m_clip_wide;
    }

    public final int getClipHigh()
    {
        return m_clip_high;
    }

    public final int getDestWide()
    {
        return m_dest_wide;
    }

    public final int getDestHigh()
    {
        return m_dest_high;
    }

    public final boolean isSame(final ImageClipBounds that)
    {
        if (null == that)
        {
            return false;
        }
        if (this == that)
        {
            return true;
        }
        return ((that.m_clip_xpos == m_clip_xpos) && (that.m_clip_ypos == m_clip_ypos) && (that.m_clip_wide == m_clip_wide) && (that.m_clip_high == m_clip_high) && (that.m_dest_wide == m_dest_wide) && (that.m_dest_high == m_dest_high));
    }

    public final boolean isDifferent(final ImageClipBounds that)
    {
        return (!isSame(that));
    }
}

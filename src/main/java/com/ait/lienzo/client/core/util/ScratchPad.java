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

package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.shared.core.types.DataURLType;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;

public final class ScratchPad
{
    private       int               m_wide;

    private       int               m_high;

    private final HTMLCanvasElement m_element;

    private final Context2D         m_context;

    public ScratchPad(final int wide, final int high)
    {
        m_wide = wide;

        m_high = high;

        if (LienzoCore.IS_CANVAS_SUPPORTED)
        {
            m_element = (HTMLCanvasElement) DomGlobal.document.createElement("canvas"); // Document.get().createCanvasElement();

            m_element.width = wide;

            m_element.height = high;

            m_context = new Context2D(m_element);
        }
        else
        {
            m_element = null;

            m_context = null;
        }
    }

    public final void clear()
    {
        Context2D context = getContext();

        if (null != context)
        {
            context.clearRect(0, 0, m_wide, m_high);
        }
    }

    public final void setPixelSize(final int wide, final int high)
    {
        m_element.width = m_wide = wide;

        m_element.height = m_high = high;
    }

    public final HTMLCanvasElement getElement()
    {
        return m_element;
    }

    public final int getWidth()
    {
        return m_wide;
    }

    public final int getHeight()
    {
        return m_high;
    }

    public final Context2D getContext()
    {
        return m_context;
    }

    public final String toDataURL()
    {
        if (null != m_element)
        {
            return toDataURL(m_element);
        }
        else
        {
            return "data:,";
        }
    }

    public final String toDataURL(DataURLType mimetype, final double quality)
    {
        if (null != m_element)
        {
            if (null == mimetype)
            {
                mimetype = DataURLType.PNG;
            }
            return toDataURL(m_element, mimetype.getValue(), quality);
        }
        else
        {
            return "data:,";
        }
    }

    public static final String toDataURL(final HTMLImageElement element, final double quality)
    {
        return toDataURL(element, DataURLType.PNG, quality);
    }

    public static final String toDataURL(final HTMLImageElement element, DataURLType mimetype, final double quality)
    {
        if (null == mimetype)
        {
            mimetype = DataURLType.PNG;
        }
        ScratchPad canvas = new ScratchPad(element.width, element.height);

        canvas.getContext().drawImage(element, 0, 0, element.width, element.height);

        return canvas.toDataURL(mimetype, quality);
    }

    public static final String toDataURL(final HTMLImageElement element)
    {
        final ScratchPad canvas = new ScratchPad(element.width, element.height);

        canvas.getContext().drawImage(element, 0, 0);

        return canvas.toDataURL();
    }

    private static final String toDataURL(final HTMLCanvasElement element)
    {
		return element.toDataURL(null); // @FIXME Make sure this accepts null (mdp)
    }

    // TODO other arguments, e.g. for image/jpeg The second argument, if it is a number in the range 0.0 to 1.0 inclusive, must be treated as the desired quality level. If it is not a number or is outside that range, the user agent must use its default value, as if the argument had been omitted.

    private static final String toDataURL(HTMLCanvasElement element, String mimetype, double quality)
    {
		return element.toDataURL(mimetype, quality);
    }
}

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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.LienzoGlobals;
import com.ait.lienzo.client.core.NativeContext2D;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;

public class LayerElementProxy
{
    private CanvasElement m_element = null;

    private Context2D     m_context = null;

    public LayerElementProxy(int wide, int high)
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Return the {@link CanvasElement}.
     * 
     * @return CanvasElement
     */
    public CanvasElement getCanvasElement()
    {
        if (LienzoGlobals.get().isCanvasSupported())
        {
            if (null == m_element)
            {
                m_element = Document.get().createCanvasElement();
            }
            if (null == m_context)
            {
                m_context = new Context2D(getNativeContext2D(m_element));
            }
        }
        return m_element;
    }

    /**
     * Returns the {@link Context2D} this layer is operating on.
     * 
     * @return Context2D
     */
    public Context2D getContext()
    {
        return m_context;
    }

    protected static final native NativeContext2D getNativeContext2D(CanvasElement element)
    /*-{
		return element.getContext("2d");
    }-*/;
}

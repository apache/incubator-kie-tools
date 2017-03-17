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

package com.ait.lienzo.client.core.event;

import com.ait.lienzo.client.core.shape.Viewport;

public class ViewportTransformChangedEvent extends AbstractNodeEvent<ViewportTransformChangedHandler>
{
    private static final Type<ViewportTransformChangedHandler> TYPE = new Type<ViewportTransformChangedHandler>();
    
    private final Viewport m_viewport;

    public static final Type<ViewportTransformChangedHandler> getType()
    {
        return TYPE;
    }

    public ViewportTransformChangedEvent(final Viewport viewport)
    {
        m_viewport = viewport;
    }

    /**
     * Returns the Viewport whose Transform was changed
     * (probably due to a zoom operation.)
     * 
     * @return Viewport
     */
    public final Viewport getViewport()
    {
        return m_viewport;
    }

    @Override
    public final Type<ViewportTransformChangedHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(final ViewportTransformChangedHandler handler)
    {
        handler.onViewportTransformChanged(this);
    }
}

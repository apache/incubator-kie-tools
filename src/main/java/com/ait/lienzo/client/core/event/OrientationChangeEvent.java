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

import com.ait.lienzo.shared.core.types.ScreenOrientation;

public class OrientationChangeEvent extends AbstractNodeEvent<OrientationChangeHandler>
{
    private final int                                   m_width;

    private final int                                   m_height;

    private final ScreenOrientation                     m_orientation;

    private static final Type<OrientationChangeHandler> TYPE = new Type<OrientationChangeHandler>();

    public static final Type<OrientationChangeHandler> getType()
    {
        return TYPE;
    }

    public OrientationChangeEvent(final ScreenOrientation orientation, final int width, final int height)
    {
        m_width = width;

        m_height = height;

        m_orientation = orientation;
    }

    @Override
    public final Type<OrientationChangeHandler> getAssociatedType()
    {
        return TYPE;
    }

    public int getWidth()
    {
        return m_width;
    }

    public int getHeight()
    {
        return m_height;
    }

    public ScreenOrientation getOrientation()
    {
        return m_orientation;
    }

    @Override
    protected void dispatch(final OrientationChangeHandler handler)
    {
        handler.onOrientationChange(this);
    }
}
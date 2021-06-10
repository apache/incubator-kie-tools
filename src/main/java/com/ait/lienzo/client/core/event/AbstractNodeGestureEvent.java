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

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;

import elemental2.dom.HTMLElement;

public abstract class AbstractNodeGestureEvent<H extends EventHandler> extends AbstractNodeHumanInputEvent<H, Node>
{
    private double m_scale;

    private double m_rotation;

    public AbstractNodeGestureEvent(final HTMLElement relativeElement)
    {
        super(relativeElement);
    }

    public void override(final double scale, final double rotation)
    {
        m_scale = scale;

        m_rotation = rotation;
    }

    public double getRotation()
    {
        return m_rotation;
    }

    public double getScale()
    {
        return m_scale;
    }
}
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
import com.ait.lienzo.tools.client.event.INodeEvent;

import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.Touch;
import elemental2.dom.TouchEvent;
import elemental2.dom.UIEvent;

// @FIXME preventDefault and stopPropagation do very little, maybe a GWTEvent porting error.
// @TODO RENAME, this is no longer just for nodes (mdp)
public abstract class AbstractNodeEvent<H, S> implements INodeEvent<H, S>
{
    private boolean m_dead = false;

    private S    source;

    private HTMLElement relativeElement;

    public AbstractNodeEvent(final HTMLElement relativeElement)
    {
        this.relativeElement = relativeElement;
    }

    @Override
    public final boolean isAlive()
    {
        return (false == m_dead);
    }

    protected void setDead(boolean dead)
    {
        m_dead = dead;
    }

    @Override
    public final void preventDefault()
    {
        m_dead = true;
    }

    @Override
    public final void stopPropagation()
    {
        m_dead = true;
    }

    @Override
    public S getSource()
    {
        return source;
    }

    public HTMLElement getRelativeElement()
    {
        return this.relativeElement;
    }

    public void setSource(final S source)
    {
        this.source = source;
    }

    public static final boolean isShiftKeyDown(final UIEvent event)
    {
        return (null != event) && (event instanceof MouseEvent) ? ((MouseEvent) event).shiftKey : ((TouchEvent) event).shiftKey;
    }

    public static final boolean isAltKeyDown(final UIEvent event)
    {
        return (null != event) && (event instanceof MouseEvent) ? ((MouseEvent) event).altKey : ((TouchEvent) event).altKey;
    }

    public static final boolean isMetaKeyDown(final UIEvent event)
    {
        return (null != event) && (event instanceof MouseEvent) ?  ((MouseEvent) event).metaKey : ((TouchEvent) event).metaKey;
    }

    public static final boolean isCtrlKeyDown(final UIEvent event)
    {
        return (null != event) && (event instanceof MouseEvent) ?  ((MouseEvent) event).ctrlKey : ((TouchEvent) event).ctrlKey;
    }


}

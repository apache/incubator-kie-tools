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

package com.ait.lienzo.client.core.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.shape.Viewport;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Mediators maintains a list (or stack) of {@link IMediator} instances.
 * 
 * These mediators can be used to intercept the events in the 
 * {@link Viewport} of a {@link LienzoPanel}.
 * Mediators are typically used for zooming or rubberbanding operations.
 * <p>
 * The mediators are processed in the order of the (internal) list.
 * To insert a new mediator into the start of the list (at position 0), 
 * use the {@link #push(IMediator) push} method.
 * To remove the first one, use the {@link #pop() pop} method.
 * The {@link #add(int, IMediator) add} and {@link #remove(IMediator) remove} methods can be used for more finer grained control.
 * <p>
 * Each IMediator must implement two methods:
 * <ul>
 * <li>{@link IMediator#handleEvent(GwtEvent) handleEvent(GwtEvent)} - acts on the event if needed, and returns true if it did
 * <li>{@link IMediator#cancel() cancel()} - terminates the current operation and resets the internal state of the mediator for future use
 * </ul>
 * 
 * See the built-in mediators:
 * <ul>
 * <li>{@link MouseBoxZoomMediator}
 * <li>{@link MouseWheelZoomMediator} 
 * <li>{@link MouseSwipeZoomMediator}
 * </ul>
 * 
 * @see LienzoHandlerManager
 * @see IMediator
 * @see Viewport#pushMediator(IMediator)
 * @see Viewport#getMediators()
 * 
 * @since 1.1
 */
public class Mediators implements Iterable<IMediator>
{
    private final Viewport             m_viewport;

    private boolean                    m_enabled   = true;

    private final ArrayList<IMediator> m_mediators = new ArrayList<IMediator>();

    public Mediators(Viewport viewport)
    {
        m_viewport = viewport;
    }

    public void push(IMediator mediator)
    {
        add(0, mediator);
    }

    public IMediator pop()
    {
        if (m_mediators.size() == 0)
        {
            return null;
        }
        return m_mediators.remove(0);
    }

    public void add(int index, IMediator mediator)
    {
        if (mediator instanceof AbstractMediator)
        {
            ((AbstractMediator) mediator).setViewport(m_viewport);
        }
        m_mediators.add(index, mediator);
    }

    public boolean remove(IMediator mediator)
    {
        return m_mediators.remove(mediator);
    }

    public boolean handleEvent(GwtEvent<?> event)
    {
        if (m_enabled)
        {
            for (int i = 0, n = m_mediators.size(); i < n; i++)
            {
                IMediator m = m_mediators.get(i);

                if ((null != m) && (m.isEnabled()) && (m.handleEvent(event)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEnabled()
    {
        return m_enabled;
    }

    public void setEnabled(boolean enabled)
    {
        m_enabled = enabled;
    }

    @Override
    public Iterator<IMediator> iterator()
    {
        return Collections.unmodifiableList(m_mediators).iterator();
    }
}

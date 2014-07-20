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

import com.ait.lienzo.client.core.shape.Viewport;

/**
 * AbstractMediator provides common functionality for {@link IMediator}s.
 * See {@link Mediators} for details.
 * 
 * @see Mediators
 * @see EventFilter
 * @see IEventFilter
 * 
 * @since 1.1
 */
public abstract class AbstractMediator implements IMediator
{
    private static long    s_name_counter = 0;

    protected IEventFilter m_eventFilter  = EventFilter.ANY;

    protected Viewport     m_viewport;

    private boolean        m_enabled      = true;

    private String         m_name         = getClass().getName() + "_" + (s_name_counter++); // Create a default unique name, we don't create alot of Mediators, so... good'nuf

    /**
     * Returns the event filter for this mediator.
     * This can be used to restrict which events the mediator should act upon.
     * 
     * The default value is EventFilter.ANY, which allows all events.
     * 
     * @return {@link IEventFilter}
     * 
     * @see EventFilter
     */
    public IEventFilter getEventFilter()
    {
        return m_eventFilter;
    }

    /**
     * Sets the event filter for this mediator.
     * This can be used to restrict which events the mediator should act upon.
     * 
     * The default value is EventFilter.ANY, which allows all events.
     * 
     * @param eventFilter {@link IEventFilter}
     * 
     * @see EventFilter
     */
    public void setEventFilter(IEventFilter eventFilter)
    {
        m_eventFilter = eventFilter;
    }

    /**
     * Sets the Viewport that this mediator belongs to.
     * This is set by the framework when the mediator is added to the 
     * {@link Mediators} of a {@link Viewport}.
     * 
     * @param viewport
     */
    public void setViewport(Viewport viewport)
    {
        m_viewport = viewport;
    }

    /**
     * Returns the Viewport that this mediator belongs to.
     * This is set by the framework when the mediator is added to the 
     * {@link Mediators} of a {@link Viewport}.
     * 
     * @return {@link Viewport}
     */
    public Viewport getViewport()
    {
        return m_viewport;
    }

    @Override
    public boolean isEnabled()
    {
        return m_enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        m_enabled = enabled;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public void setName(String name)
    {
        m_name = name;
    }
}

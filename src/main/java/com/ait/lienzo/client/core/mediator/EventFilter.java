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

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * EventFilter provides basic implementations of {@link IEventFilter}s.
 * Multiple event filters can be combined with {@link #and(IEventFilter...) and},
 * and {@link #or(IEventFilter...) or} and {@link #not(IEventFilter) not} operations.
 * To write a custom implementation, simply implement the {@link IEventFilter}
 * interface.
 * <p>
 * The following event filters are provided by the Lienzo toolkit:
 * <p>
 * <table cellpadding="4" cellspacing="1" style="background-color: #000000;">
 * <tr style="background: #CCCCCC;"><th>Filter</th><th>Event Type</th><th>Description</th></tr>
 * <tr style="background: #EEEEEE;"><td>ANY</td><td>any event</td><td>accepts all events</td></tr>
 * <tr style="background: #EEEEEE;"><td>BUTTON_LEFT</td><td>mouse event</td><td>whether the left mouse button is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>BUTTON_MIDDLE</td><td>mouse event</td><td>whether the middle mouse button is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>BUTTON_RIGHT</td><td>mouse event</td><td>whether the right mouse button is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>CONTROL</td><td>mouse event</td><td>whether the Control key is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>SHIFT</td><td>mouse event</td><td>whether the Shift key is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>ALT</td><td>mouse event</td><td>whether the Alt key is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>META</td><td>mouse event</td><td>whether the Meta key is pressed</td></tr>
 * </table>
 * 
 * @since 1.1
 */
public class EventFilter
{
    public static final IEventFilter ANY = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            return true;
        }        
    };
    
    public static final IEventFilter BUTTON_LEFT = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isButtonLeft();
            else
                return false;
        }        
    };
    
    public static final IEventFilter BUTTON_MIDDLE = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isButtonMiddle();
            else
                return false;
        }        
    };
    
    public static final IEventFilter BUTTON_RIGHT = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isButtonRight();
            else
                return false;
        }        
    };
    
    public static final IEventFilter CONTROL = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isControlKeyDown();
            else
                return false;
        }        
    };
    
    public static final IEventFilter META = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isMetaKeyDown();
            else
                return false;
        }        
    };
    
    public static final IEventFilter SHIFT = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isShiftKeyDown();
            else
                return false;
        }        
    };
    
    public static final IEventFilter ALT = new IEventFilter() 
    {
        @Override
        public final boolean matches(GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?,?>)
                return ((AbstractNodeMouseEvent<?,?>) event).isAltKeyDown();
            else
                return false;
        }        
    };
    
    /**
     * Chains several filters together. 
     * The resulting filter will return true, if at least one filter returns true.
     * 
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter or(final IEventFilter... filters)
    {
        if (filters.length == 1)
            return filters[0];
        
        return new IEventFilter() 
        {
            @Override
            public final boolean matches(GwtEvent<?> event)
            {
                for (int i = 0; i < filters.length; i++)
                {
                    if (filters[i].matches(event))
                        return true;
                }
                return false;
            }        
        };
    }
    
    /**
     * Chains several filters together. 
     * The resulting filter will return false, if at least one filter returns false.
     * 
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter and(final IEventFilter... filters)
    {
        if (filters.length == 1)
            return filters[0];
        
        return new IEventFilter() 
        {
            @Override
            public final boolean matches(GwtEvent<?> event)
            {
                for (int i = 0; i < filters.length; i++)
                {
                    if (!filters[i].matches(event))
                        return false;
                }
                return true;
            }        
        };
    }
    
    /**
     * The resulting filter will return false, if the specified filter returns true.
     * 
     * @param filter IEventFilter.
     * @return IEventFilter
     */
    public static final IEventFilter not(final IEventFilter filter)
    {
        return new IEventFilter() 
        {
            @Override
            public final boolean matches(GwtEvent<?> event)
            {
                return !filter.matches(event);
            }        
        };
    }
}

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

package com.ait.lienzo.client.core.mediator;

import java.util.List;

import com.ait.lienzo.client.core.event.AbstractNodeEvent;
import com.ait.lienzo.tools.client.event.MouseEventUtil;

import elemental2.dom.MouseEvent;
import elemental2.dom.UIEvent;

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
public final class EventFilter
{
    private static final IEventFilter[] FOR_TO_ARRAY  = new IEventFilter[0];

    public static final IEventFilter    ANY           = new AnyEventFilterOp();

    public static final IEventFilter    BUTTON_LEFT   = new ButtonLeftEventFilter();

    public static final IEventFilter    BUTTON_MIDDLE = new ButtonMiddleEventFilter();

    public static final IEventFilter    BUTTON_RIGHT  = new ButtonRightEventFilter();

    public static final IEventFilter    CONTROL       = new CtrlKeyEventFilter();

    public static final IEventFilter    META          = new MetaKeyEventFilter();

    public static final IEventFilter    SHIFT         = new ShiftKeyEventFilter();

    public static final IEventFilter    ALT           = new AltKeyEventFilter();

    private EventFilter()
    {
    }

    /**
     * Chains several filters together. 
     * The resulting filter will return true, if at least one filter returns true.
     * 
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter or(final IEventFilter... filters)
    {
        return new OrOpEventFilter(filters);
    }

    /**
     * Chains several filters together. 
     * The resulting filter will return true, if at least one filter returns true.
     * 
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter or(final List<IEventFilter> filters)
    {
        return new OrOpEventFilter(filters.toArray(FOR_TO_ARRAY));
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
        return new AndOpEventFilter(filters);
    }

    /**
     * Chains several filters together. 
     * The resulting filter will return false, if at least one filter returns false.
     * 
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter and(final List<IEventFilter> filters)
    {
        return new AndOpEventFilter(filters.toArray(FOR_TO_ARRAY));
    }

    /**
     * The resulting filter will return false, if the specified filter returns true.
     * 
     * @param filter IEventFilter.
     * @return IEventFilter
     */
    public static final IEventFilter not(final IEventFilter filter)
    {
        return new AbstractEventFilter()
        {
            @Override
            public final boolean test(final UIEvent event)
            {
                return (!filter.test(event));
            }
        };
    }

    private static final class AnyEventFilterOp implements IEventFilter
    {
        @Override
        public final boolean test(final UIEvent event)
        {
            return true;
        }

        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(boolean enabled)
        {
        }
    }

    public static class ButtonLeftEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            if (event instanceof MouseEvent)
            {
                return MouseEventUtil.isButtonLeft((MouseEvent) event);

            }
            else
            {
                return false;
            }
        }
    }

    public static class ButtonMiddleEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            if (event instanceof MouseEvent)
            {
                return MouseEventUtil.isButtonMiddle((MouseEvent) event);
            }
            else
            {
                return false;
            }
        }
    }

    public static class ButtonRightEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            if (event instanceof MouseEvent)
            {
                return MouseEventUtil.isButtonRight((MouseEvent) event);
            }
            else
            {
                return false;
            }
        }
    }

    public static class ShiftKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            return AbstractNodeEvent.isShiftKeyDown(event);
        }
    }

    public static class CtrlKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            return AbstractNodeEvent.isCtrlKeyDown(event);
        }
    }

    public static class MetaKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            return AbstractNodeEvent.isMetaKeyDown(event);
        }
    }

    public static class AltKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final UIEvent event)
        {
            return AbstractNodeEvent.isAltKeyDown(event);
        }
    }

    private static final class AndOpEventFilter extends AbstractEventFilter
    {
        private final int            m_size;

        private final IEventFilter[] m_list;

        public AndOpEventFilter(IEventFilter[] filters)
        {
            m_list = filters;

            m_size = filters.length;
        }

        @Override
        public final boolean test(final UIEvent event)
        {
            for (int i = 0; i < m_size; i++)
            {
                final IEventFilter filter = m_list[i];

                if (filter.isEnabled())
                {
                    if (!filter.test(event))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static final class OrOpEventFilter extends AbstractEventFilter
    {
        private final int            m_size;

        private final IEventFilter[] m_list;

        public OrOpEventFilter(IEventFilter[] filters)
        {
            m_list = filters;

            m_size = filters.length;
        }

        @Override
        public final boolean test(final UIEvent event)
        {
            for (int i = 0; i < m_size; i++)
            {
                final IEventFilter filter = m_list[i];

                if (filter.isEnabled())
                {
                    if (filter.test(event))
                    {
                        return true;
                    }
                }
            }
            return true;
        }
    }
}

/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.HandlerRegistrationManager;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.shared.java.util.IActivatable;

public interface IControlHandle extends IActivatable
{
    public IPrimitive<?> getControl();

    public ControlHandleType getType();

    public void destroy();

    public HandlerRegistrationManager getHandlerRegistrationManager();

    public abstract static class ControlHandleType
    {
        private final int    m_value;

        private final String m_label;

        private static int   s_value;

        protected ControlHandleType(final String label)
        {
            m_label = label;

            m_value = ++s_value;
        }

        public final int getValue()
        {
            return m_value;
        }

        public final String getLabel()
        {
            return m_label;
        }

        @Override
        public final int hashCode()
        {
            return m_value;
        }

        @Override
        public String toString()
        {
            return "ControlHandleType(" + m_label + "," + m_value + ")";
        }
    }

    public static final class ControlHandleStandardType extends ControlHandleType
    {
        public static final ControlHandleType POINT     = new ControlHandleStandardType("POINT");

        public static final ControlHandleType ROTATE    = new ControlHandleStandardType("ROTATE");

        public static final ControlHandleType RESIZE    = new ControlHandleStandardType("RESIZE");

        public static final ControlHandleType CONNECTOR = new ControlHandleStandardType("CONNECTOR");

        public static final ControlHandleType HANDLE    = new ControlHandleStandardType("HANDLE");

        public static final ControlHandleType MAGNET    = new ControlHandleStandardType("MAGNET");

        public static final ControlHandleType CONFIGURE = new ControlHandleStandardType("CONFIGURE");

        private ControlHandleStandardType(final String label)
        {
            super(label);
        }
    }
}

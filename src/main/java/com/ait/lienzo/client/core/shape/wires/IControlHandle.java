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

package com.ait.lienzo.client.core.shape.wires;

import java.util.Objects;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.tools.common.api.types.IActivatable;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

public interface IControlHandle extends IActivatable
{
    IPrimitive<?> getControl();

    ControlHandleType getType();

    void destroy();

    HandlerRegistrationManager getHandlerRegistrationManager();

    abstract class ControlHandleType
    {
        private final int    m_value;

        private final String m_label;

        private static int   s_value;

        protected ControlHandleType(final String label)
        {
            m_label = Objects.requireNonNull(label);

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

        @Override
        public boolean equals(final Object other)
        {
            if ((other == null) || (!(other instanceof ControlHandleType)))
            {
                return false;
            }
            if (this == other)
            {
                return true;
            }
            ControlHandleType that = ((ControlHandleType) other);

            return ((that.getValue() == getValue()) && (getLabel().equals(that.getLabel())));
        }
    }

    final class ControlHandleStandardType extends ControlHandleType
    {
        public static final ControlHandleType POINT     = new ControlHandleStandardType("POINT");

        public static final ControlHandleType ROTATE    = new ControlHandleStandardType("ROTATE");

        public static final ControlHandleType RESIZE    = new ControlHandleStandardType("RESIZE");

        public static final ControlHandleType SCALE     = new ControlHandleStandardType("SCALE");

        public static final ControlHandleType SHEAR     = new ControlHandleStandardType("SHEAR");

        public static final ControlHandleType HANDLE    = new ControlHandleStandardType("HANDLE");

        public static final ControlHandleType MAGNET    = new ControlHandleStandardType("MAGNET");

        public static final ControlHandleType CONNECTOR = new ControlHandleStandardType("CONNECTOR");

        private ControlHandleStandardType(final String label)
        {
            super(label);
        }
    }
}

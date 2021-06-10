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

public interface IDockingAcceptor
{
    int              HOTSPOT_SIZE = 40;

    IDockingAcceptor ALL          = new DefaultDockingAcceptor(true);

    IDockingAcceptor NONE         = new DefaultDockingAcceptor(false);

    boolean dockingAllowed(WiresContainer parent, WiresShape child);

    boolean acceptDocking(WiresContainer parent, WiresShape child);

    int getHotspotSize();

    class DefaultDockingAcceptor implements IDockingAcceptor
    {
        private final boolean m_defaultValue;

        public DefaultDockingAcceptor(boolean defaultValue)
        {
            m_defaultValue = defaultValue;
        }

        @Override
        public boolean dockingAllowed(WiresContainer parent, WiresShape child)
        {
            return m_defaultValue;
        }

        @Override
        public boolean acceptDocking(WiresContainer parent, WiresShape child)
        {
            return m_defaultValue;
        }

        @Override
        public int getHotspotSize()
        {
            return HOTSPOT_SIZE;
        }
    }
}

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

public interface IConnectionAcceptor
{
    IConnectionAcceptor ALL  = new DefaultConnectionAcceptor(true);

    IConnectionAcceptor NONE = new DefaultConnectionAcceptor(false);

    boolean headConnectionAllowed(WiresConnection head, WiresShape shape);

    boolean tailConnectionAllowed(WiresConnection tail, WiresShape shape);

    boolean acceptHead(WiresConnection head, WiresMagnet magnet);

    boolean acceptTail(WiresConnection tail, WiresMagnet magnet);

    class DefaultConnectionAcceptor implements IConnectionAcceptor
    {
        final private boolean m_defaultValue;

        private DefaultConnectionAcceptor(final boolean defaultValue)
        {
            m_defaultValue = defaultValue;
        }

        @Override
        public boolean tailConnectionAllowed(WiresConnection connection, WiresShape shape)
        {
            return m_defaultValue;
        }

        @Override
        public boolean headConnectionAllowed(WiresConnection connection, WiresShape shape)
        {
            return m_defaultValue;
        }

        @Override
        public boolean acceptHead(WiresConnection connection, WiresMagnet magnet)
        {
            return m_defaultValue;
        }

        @Override
        public boolean acceptTail(WiresConnection connection, WiresMagnet magnet)
        {
            return m_defaultValue;
        }

    }
}

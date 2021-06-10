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

import com.ait.lienzo.client.core.types.Point2D;

public interface ILocationAcceptor {

    ILocationAcceptor ALL = new DefaultLocationAcceptor(true);

    ILocationAcceptor NONE = new DefaultLocationAcceptor(false);

    boolean allow(WiresContainer[] shapes,
                         Point2D[] locations);

    boolean accept(WiresContainer[] shapes,
                          Point2D[] locations);

    class DefaultLocationAcceptor implements ILocationAcceptor {

        private final boolean m_defaultValue;

        private DefaultLocationAcceptor(final boolean defaultValue) {
            m_defaultValue = defaultValue;
        }

        @Override
        public boolean allow(WiresContainer[] shapes,
                             Point2D[] locations) {
            return m_defaultValue;
        }

        @Override
        public boolean accept(WiresContainer[] shapes,
                              Point2D[] locations) {
            return m_defaultValue;
        }
    }
}

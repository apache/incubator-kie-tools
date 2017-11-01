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

public interface ILayoutHandler {

    ILayoutHandler NONE = new DefaultLayoutHandler();

    void add( final WiresShape shape,
              final WiresContainer container,
              final Point2D mouseRelativeLoc );

    void remove( final WiresShape shape,
                 final WiresContainer container );

    void requestLayout( final WiresContainer container );

    void layout( final WiresContainer container );

    class DefaultLayoutHandler implements ILayoutHandler {

        @Override
        public void add( final WiresShape shape,
                         final WiresContainer container,
                         final Point2D mouseRelativeLoc ) {
            //Add the shape to the container at the specified position. No layout.
            shape.setLocation( mouseRelativeLoc );
            container.add( shape );
        }

        @Override
        public void remove( final WiresShape shape,
                            final WiresContainer container ) {
            //Remove the shape from its container. No layout.
            container.remove( shape );
        }

        @Override
        public void requestLayout( final WiresContainer container ) {
            //No automated layout by default
        }

        @Override
        public void layout( final WiresContainer container ) {
            //No automated layout by default
        }

    }

}

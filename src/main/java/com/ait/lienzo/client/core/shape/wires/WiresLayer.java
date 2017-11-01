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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresLayer extends WiresContainer
{
    public WiresLayer(Layer layer)
    {
        super(layer);
    }

    public Layer getLayer()
    {
        return (Layer) getContainer();
    }

    @Override
    public Point2D getLocation() {
        return getLayer().getAbsoluteLocation();
    }

    @Override
    public Point2D getComputedLocation() {
        return getLayer().getComputedLocation();
    }

    @Override
    public double getX() {
        return getLocation().getX();
    }

    @Override
    public double getY() {
        return getLocation().getY();
    }
}

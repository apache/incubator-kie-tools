/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

public class PickerPart
{
    private final WiresShape shape;

    private final ShapePart  part;

    public WiresShape getShape()
    {
        return shape;
    }

    public ShapePart getShapePart()
    {
        return part;
    }

    public enum ShapePart
    {
        BORDER, BORDER_HOTSPOT, BODY
    }

    public PickerPart(WiresShape shape, ShapePart part)
    {
        this.shape = shape;
        this.part = part;
    }

    @Override
    public String toString()
    {
        return this.part.toString() + " for " + this.shape.toString();
    }
}

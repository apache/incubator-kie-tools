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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;

public interface LayoutContainer
{
    enum Layout
    {
        CENTER, LEFT, TOP, RIGHT, BOTTOM;
    }

    LayoutContainer setX(double x);

    LayoutContainer setY(double y);

    LayoutContainer setHeight(double height);

    LayoutContainer setWidth(double width);

    LayoutContainer add(IPrimitive<?> child);

    LayoutContainer add(IPrimitive<?> child, Layout layout);

    LayoutContainer add(IPrimitive<?> child, Layout layout, double x, double y);

    LayoutContainer move(IPrimitive<?> child, double dx, double dy);

    LayoutContainer remove(IPrimitive<?> child);

    LayoutContainer clear();

    Group getGroup();
}

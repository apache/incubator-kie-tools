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
    public enum Layout
    {
        CENTER, LEFT, TOP, RIGHT, BOTTOM;
    }

    public LayoutContainer setX(double x);

    public LayoutContainer setY(double y);

    public LayoutContainer setHeight(double height);

    public LayoutContainer setWidth(double width);

    public LayoutContainer add(IPrimitive<?> child);

    public LayoutContainer add(IPrimitive<?> child, Layout layout);

    public LayoutContainer add(IPrimitive<?> child, Layout layout, double x, double y);

    public LayoutContainer move(IPrimitive<?> child, double dx, double dy);

    public LayoutContainer remove(IPrimitive<?> child);

    public LayoutContainer clear();

    public Group getGroup();

    public void deregister();
}

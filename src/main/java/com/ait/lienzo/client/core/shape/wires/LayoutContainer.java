package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;

public interface LayoutContainer {

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

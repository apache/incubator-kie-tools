
package com.ait.lienzo.client.core.shape.wires;

import static com.ait.lienzo.client.core.AttributeOp.any;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.event.AnimationFrameAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.tooling.common.api.flow.Flows;
import com.ait.tooling.common.api.java.util.UUID;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class WiresLayoutContainer
{

    public enum Layout
    {
        CENTER, LEFT, TOP, RIGHT, BOTTOM;
    }

    private static final Flows.BooleanOp XYWH_OP                  = any(Attribute.X, Attribute.Y, Attribute.WIDTH, Attribute.HEIGHT);

    private IAttributesChangedBatcher    attributesChangedBatcher = new AnimationFrameAttributesChangedBatcher();

    private final LayoutBuilder          CENTER_LAYOUT            = new CenterLayoutBuilder();

    private final LayoutBuilder          TOP_LAYOUT               = new TopLayoutBuilder();

    private final LayoutBuilder          BOTTOM_LAYOUT            = new BottomLayoutBuilder();

    private final LayoutBuilder          LEFT_LAYOUT              = new LeftLayoutBuilder();

    private final LayoutBuilder          RIGHT_LAYOUT             = new RightLayoutBuilder();

    private Group                        group;

    private NFastArrayList<String>       layout_keys;

    private NFastArrayList<Double>       layout_x;

    private NFastArrayList<Double>       layout_y;

    private NFastArrayList<Layout>       layout_values;

    public WiresLayoutContainer()
    {
        this.group = new Group().setDraggable(false);
        this.layout_keys = new NFastArrayList<String>();
        this.layout_values = new NFastArrayList<Layout>();
        this.layout_x = new NFastArrayList<Double>();
        this.layout_y = new NFastArrayList<Double>();
        init();
    }

    public void setX(final double x)
    {
        group.getAttributes().setX(x);
    }

    public void setY(final double y)
    {
        group.getAttributes().setY(y);
    }

    public void setHeight(final double height)
    {
        group.getAttributes().setHeight(height);
    }

    public void setWidth(final double width)
    {
        group.getAttributes().setWidth(width);
    }

    public double getX()
    {
        return group.getAttributes().getX();
    }

    public double getY()
    {
        return group.getAttributes().getY();
    }

    public double getWidth()
    {
        return group.getAttributes().getWidth();
    }

    public double getHeight()
    {
        return group.getAttributes().getHeight();
    }

    private void init()
    {
        group.setAttributesChangedBatcher(attributesChangedBatcher);

        final AttributesChangedHandler handler = new AttributesChangedHandler()
        {
            @Override
            public void onAttributesChanged(AttributesChangedEvent event)
            {
                if (event.evaluate(XYWH_OP))
                {
                    doPositionChildren();
                    getGroup().getLayer().batch();
                }
            }
        };

        // Attribute change handlers.
        group.addAttributesChangedHandler(Attribute.X, handler);
        group.addAttributesChangedHandler(Attribute.Y, handler);
        group.addAttributesChangedHandler(Attribute.WIDTH, handler);
        group.addAttributesChangedHandler(Attribute.HEIGHT, handler);
    }

    public Group add(IPrimitive<?> child, Layout layout, double dx, double dy)
    {
        if (null == child.getID())
        {
            child.setID(UUID.uuid());
        }

        layout_keys.add(child.getID());
        layout_values.add(layout);
        layout_x.add(dx);
        layout_y.add(dy);

        Group result = group.add(child);
        result.moveToTop();

        doPositionChild(result);

        return result;
    }

    public WiresLayoutContainer move(String id, double dx, double dy)
    {
        final int index = layout_keys.toList().indexOf(id);
        layout_x.set(index, dx);
        layout_y.set(index, dy);
        return this;
    }

    public Group remove(IPrimitive<?> child)
    {
        Group result = group.remove(child);

        final Layout layout = getLayout(child.getID());
        if (null != layout)
        {
            layout_keys.remove(child.getID());
            layout_values.remove(layout);
        }
        return result;
    }

    public Group removeAll()
    {
        Group result = group.removeAll();
        layout_keys.clear();
        layout_values.clear();
        return result;
    }

    Group getGroup()
    {
        return group;
    }

    private Layout getLayout(final String key)
    {
        final int index = layout_keys.toList().indexOf(key);
        return index > -1 ? layout_values.get(index) : null;
    }

    private void doPositionChildren()
    {
        for (IPrimitive<?> child : group.getChildNodes())
        {
            doPositionChild(child);
        }
    }

    private void doPositionChild(final IPrimitive<?> child)
    {
        final int index = layout_keys.toList().indexOf(child.getID());
        final Layout childLayout = index > -1 ? layout_values.get(index) : null;

        if (null != childLayout)
        {
            LayoutBuilder builder = null;
            switch (childLayout)
            {
                case CENTER:
                    builder = CENTER_LAYOUT;
                    break;
                case TOP:
                    builder = TOP_LAYOUT;
                    break;
                case LEFT:
                    builder = LEFT_LAYOUT;
                    break;
                case BOTTOM:
                    builder = BOTTOM_LAYOUT;
                    break;
                case RIGHT:
                    builder = RIGHT_LAYOUT;
                    break;
            }

            if (null != builder)
            {
                builder.layoutIt(child);

                // Obtain the original shape's position that made it applicable to the given layout and 
                // apply the increment.
                child.setX(child.getX() + layout_x.get(index));
                child.setY(child.getY() + layout_y.get(index));
            }
        }
    }

    /* *******************************************************************
                                LAYOUTS 
     ******************************************************************* */

    interface LayoutBuilder
    {
        void layoutIt(IPrimitive<?> child);
    }

    private class CenterLayoutBuilder implements LayoutBuilder
    {
        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double x = getWidth() / 2;
            final double y = getHeight() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbw = bb.getWidth();
            final double bbh = bb.getHeight();
            child.setX(x - (bbw / 2));
            child.setY(y - (bbh / 2));
        }
    }

    private class TopLayoutBuilder implements LayoutBuilder
    {
        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double x = getWidth() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbw = bb.getWidth();
            child.setX(x - (bbw / 2));
            child.setY(0);
        }
    }

    private class BottomLayoutBuilder implements LayoutBuilder
    {
        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double x = getWidth() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbw = bb.getWidth();
            final double bbh = bb.getHeight();
            child.setX(x - (bbw / 2));
            child.setY(getHeight() - bbh);
        }
    }

    private class LeftLayoutBuilder implements LayoutBuilder
    {

        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double y = getHeight() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbh = bb.getHeight();
            child.setX(0);
            child.setY(y - (bbh / 2));
        }
    }

    private class RightLayoutBuilder implements LayoutBuilder
    {

        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double y = getHeight() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbw = bb.getWidth();
            final double bbh = bb.getHeight();
            child.setX(getWidth() - bbw);
            child.setY(y - (bbh / 2));
        }
    }
}

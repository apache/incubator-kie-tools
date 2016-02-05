
package com.ait.lienzo.client.core.shape.wires;

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

import static com.ait.lienzo.client.core.AttributeOp.any;

/**
 * A container that allows adding children in given a layout position. 
 * The given child position is ensured to be consistent after either moving or resizing the container. 
 * 
 * Currently the supported layout positions are: CENTER, TOP, BOTTOM, RIGHT and LEFT.
 * Eg: <code>addChild( new Circle(25), CENTER );</code>
 * 
 * In order to place the child into the given layout position, this implementations uses the 
 * child bounding box in order to obtain its size. Once bounding box is calculated, so once discovered child size, 
 * the layout positions are relative to the bounding box coordinates at bounding box center points.
 * 
 * As an example, consider the <i>asterisks</i> on next diagram as the bounding box child added in the CENTER position, the
 * resulting layout looks like:
 * 
 *     =====================
 *     |                   |
 *     |                   |
 *     |      *******      |
 *     |      *     *      |
 *     |      *     *      |
 *     |      *******      |
 *     |                   |
 *     |                   |
 *     =====================
 * 
 * You can also specify x and y coordinates in order to move the child inside the container but relative to a 
 * given layout position or for applying some padding.
 * Eg: <code>addChild( new Circle(25), CENTER, 50d, 50d );</code> * 
 * As this example, the child will be added on the center position and moved 50 / 50 from the center.
 * If you resize or move the container, those differential coordinates are keep by the container and applied as well.
 * 
 *
 */
public class WiresLayoutContainer implements LayoutContainer
{

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

    private NFastArrayList<LayoutContainer.Layout>       layout_values;

    public WiresLayoutContainer()
    {
        this.group = new Group().setDraggable(false);
        this.layout_keys = new NFastArrayList<String>();
        this.layout_values = new NFastArrayList<LayoutContainer.Layout>();
        this.layout_x = new NFastArrayList<Double>();
        this.layout_y = new NFastArrayList<Double>();
        init();
    }

    public WiresLayoutContainer setX(final double x)
    {
        group.getAttributes().setX(x);
        return this;
    }

    public WiresLayoutContainer setY(final double y)
    {
        group.getAttributes().setY(y);
        return this;
    }

    public WiresLayoutContainer setHeight(final double height)
    {
        group.getAttributes().setHeight(height);
        return this;
    }

    public WiresLayoutContainer setWidth(final double width)
    {
        group.getAttributes().setWidth(width);
        return this;
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

    public WiresLayoutContainer add(final IPrimitive<?> child)
    {
        if (null == child.getID())
        {
            child.setID(UUID.uuid());
        }

        group.add(child).moveToTop();
        
        return this;
    }

    public WiresLayoutContainer add(final IPrimitive<?> child, final LayoutContainer.Layout layout)
    {
        return this.add( child, layout, 0d, 0d);
    }

    public WiresLayoutContainer add(final IPrimitive<?> child, final LayoutContainer.Layout layout,
                                    final double dx, final double dy)
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

        return this;
    }

    public WiresLayoutContainer move(final IPrimitive<?> child, 
                                     final double dx, final double dy)
    {
        final int index = layout_keys.toList().indexOf(child.getID());
        layout_x.set(index, dx);
        layout_y.set(index, dy);
        return this;
    }

    public WiresLayoutContainer remove(final IPrimitive<?> child)
    {
        final LayoutContainer.Layout layout = getLayout(child.getID());
        if (null != layout)
        {
            layout_keys.remove(child.getID());
            layout_values.remove(layout);
        }

        group.remove(child);

        return this;
    }

    public WiresLayoutContainer clear()
    {
        layout_keys.clear();
        layout_values.clear();
        group.removeAll();
        
        return this;
    }

    public Group getGroup()
    {
        return group;
    }

    private LayoutContainer.Layout getLayout(final String key)
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
        final LayoutContainer.Layout childLayout = index > -1 ? layout_values.get(index) : null;

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
            child.setX(x);
            child.setY(y);
        }
    }

    private class TopLayoutBuilder implements LayoutBuilder
    {
        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double x = getWidth() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbh = bb.getHeight();
            child.setX(x);
            child.setY(0 + ( bbh / 2 ) );
        }
    }

    private class BottomLayoutBuilder implements LayoutBuilder
    {
        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double x = getWidth() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbh = bb.getHeight();
            child.setX(x);
            child.setY(getHeight() - ( bbh / 2  ) );
        }
    }

    private class LeftLayoutBuilder implements LayoutBuilder
    {

        @Override
        public void layoutIt(final IPrimitive<?> child)
        {
            final double y = getHeight() / 2;
            final BoundingBox bb = child.getBoundingBox();
            final double bbw = bb.getWidth();
            child.setX(0 + ( bbw / 2 ) );
            child.setY(y);
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
            child.setX(getWidth() - ( bbw / 2 ) );
            child.setY(y);
        }
    }
}

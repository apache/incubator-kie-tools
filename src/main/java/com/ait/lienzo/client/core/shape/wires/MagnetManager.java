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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.shape.wires.decorator.MagnetDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

import elemental2.dom.ImageData;

public class MagnetManager
{
    public static final Direction[]  FOUR_CARDINALS           = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static final int[]        FOUR_CARDINALS_MAPPING   = new int[] { 0, 1, 1, 2, 3, 3, 3, 4, 1};

    public static final Direction[]  EIGHT_CARDINALS          = new Direction[] { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    public static final int[]        EIGHT_CARDINALS_MAPPING  = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8};

    private static final int          CONTROL_RADIUS          = 7;

    public static final ColorKeyRotor m_c_rotor               = new ColorKeyRotor();

    private NFastStringMap<Magnets>   m_magnetRegistry        = new NFastStringMap<>();

    private int                       m_ctrlSize              = CONTROL_RADIUS;

    private MagnetDecorator m_magnetDecorator                 = new MagnetDecorator();

    public MagnetManager()
    {
    }

    public ImageData drawMagnetsToBack(Magnets magnets, NFastStringMap<WiresShape> shapeColors, NFastStringMap<WiresMagnet> magnetColors, ScratchPad scratch)
    {
        scratch.clear();
        Context2D ctx = scratch.getContext();

        drawShapeToBacking(magnets, shapeColors, ctx);

        magnetColors.clear();
        for (int i = 0; i < magnets.size(); i++)
        {
            drawMagnet(magnetColors, ctx, magnets.getMagnet(i));
        }
        return ctx.getImageData(0, 0, scratch.getWidth(), scratch.getHeight());
    }

    protected void drawShapeToBacking(Magnets magnets, NFastStringMap<WiresShape> shapeColorMap, Context2D ctx)
    {
        // the Shape doesn't need recording, we just need to know the mouse is over something
        BackingColorMapUtils.drawShapeToBacking(ctx, magnets.getWiresShape(), m_c_rotor.next(), shapeColorMap);
    }

    protected void drawMagnet(NFastStringMap<WiresMagnet> magnetColorMap, Context2D ctx, WiresMagnet m)
    {
        String c = m_c_rotor.next();
        magnetColorMap.put(c, m);
        ctx.beginPath();
        ctx.setStrokeWidth(m_ctrlSize);
        ctx.setStrokeColor(c);
        ctx.setFillColor(c);
        ctx.arc(m.getControl().getX(), m.getControl().getY(), m_ctrlSize, 0, 2 * Math.PI, false);
        ctx.stroke();
        ctx.fill();
    }

    public Magnets createMagnets(final WiresShape wiresShape)
    {
        return createMagnets(wiresShape, EIGHT_CARDINALS);
    }

    /**
     * Right now it only works with provided FOUR or EIGHT cardinals, anything else will break WiresConnector autoconnection
     *
     * @param wiresShape
     * @param requestedCardinals
     * @return
     */
    public Magnets createMagnets(final WiresShape wiresShape, Direction[] requestedCardinals)
    {
        final IPrimitive<?> primTarget = wiresShape.getGroup();
        final Point2DArray points = getWiresIntersectionPoints(wiresShape, requestedCardinals);
        final ControlHandleList list = new ControlHandleList(primTarget);
        final BoundingBox box = wiresShape.getPath().getBoundingBox();

        final Point2D primLoc = primTarget.getComputedLocation();
        final Magnets magnets = new Magnets(this, list, wiresShape);

        int i    = 0;
        for (Point2D p : points.asArray())
        {
            final double mx = primLoc.getX() + p.getX();
            final double my = primLoc.getY() + p.getY();
            final Shape<?> primitive = m_magnetDecorator.decorate(getControlPrimitive(mx, my), IShapeDecorator.ShapeState.VALID);
            WiresMagnet m = new WiresMagnet(magnets, null, i++, p.getX(), p.getY(), primitive, true);
            Direction d = getDirection(p, box);
            m.setDirection(d);
            list.add(m);
        }

        final String uuid = wiresShape.uuid();
        m_magnetRegistry.put(uuid, magnets);

        wiresShape.setMagnets(magnets);

        return magnets;
    }

    public Magnets getMagnets(IPrimitive<?> shape)
    {
        return m_magnetRegistry.get(shape.uuid());
    }

    public static Direction getDirection(Point2D point, BoundingBox box)
    {
        double left   = box.getMinX();
        double right  = box.getMaxX();
        double top    = box.getMinY();
        double bottom = box.getMaxY();

        double x = point.getX();
        double y = point.getY();

        double leftDist = Math.abs(x - left);
        double rightDist = Math.abs(x - right);

        double topDist = Math.abs(y - top);
        double bottomDist = Math.abs(y - bottom);

        boolean moreLeft = leftDist < rightDist;
        boolean moreTop = topDist < bottomDist;

        if (leftDist == rightDist && topDist == bottomDist)
        {
            // this is the center, so return NONE
            return Direction.NONE;
        }
        if (moreLeft)
        {
            if (moreTop)
            {
                if (topDist < leftDist)
                {
                    return Direction.NORTH;
                }
                else if (topDist > leftDist)
                {
                    return Direction.WEST;
                }
                else
                {
                    return Direction.WEST;//Direction.NORTH_WEST;
                }
            }
            else
            {
                if (bottomDist < leftDist)
                {
                    return Direction.SOUTH;
                }
                else if (bottomDist > leftDist)
                {
                    return Direction.WEST;
                }
                else
                {
                    return Direction.WEST;//Direction.SOUTH_WEST;
                }
            }
        }
        else
        {
            if (moreTop)
            {
                if (topDist < rightDist)
                {
                    return Direction.NORTH;
                }
                else if (topDist > rightDist)
                {
                    return Direction.EAST;
                }
                else
                {
                    return Direction.EAST;//Direction.NORTH_EAST;
                }
            }
            else
            {
                if (bottomDist < rightDist)
                {
                    return Direction.SOUTH;
                }
                else if (bottomDist > rightDist)
                {
                    return Direction.EAST;
                }
                else
                {
                    return Direction.EAST;//Direction.SOUTH_EAST;
                }
            }
        }
    }

    public void setHotspotSize(int m_ctrlSize)
    {
        this.m_ctrlSize = m_ctrlSize;
    }

    public void setMagnetDecorator(MagnetDecorator magnetDecorator)
    {
        this.m_magnetDecorator = magnetDecorator;
    }

    public static Point2DArray getWiresIntersectionPoints(final WiresShape wiresShape, Direction[] requestedCardinals)
    {
        return Geometry.getCardinalIntersects(wiresShape.getPath(), requestedCardinals);
    }

    private Circle getControlPrimitive(double x, double y)
    {
        return new Circle(m_ctrlSize)
                .setX(x)
                .setY(y)
                .setDraggable(false)
                .setDragMode(DragMode.SAME_LAYER);
    }

    public static class Magnets implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private final IControlHandleList m_list;

        private final MagnetManager m_magnetManager;

        private final WiresShape m_wiresShape;

        private boolean m_isDragging;

        private final HandlerRegistrationManager m_registrationManager = new HandlerRegistrationManager();

        public Magnets(MagnetManager magnetManager, IControlHandleList list, WiresShape wiresShape)
        {
            m_list = list;
            m_magnetManager = magnetManager;
            m_wiresShape = wiresShape;

            Group shapeGroup = wiresShape.getGroup();
            m_registrationManager.register(shapeGroup.addNodeDragStartHandler(this));
            m_registrationManager.register(shapeGroup.addNodeDragMoveHandler(this));
            m_registrationManager.register(shapeGroup.addNodeDragEndHandler(this));
        }

        public boolean isEmpty()
        {
            return m_list.isEmpty();
        }

        public WiresShape getWiresShape()
        {
            return m_wiresShape;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_isDragging = true;
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_isDragging = false;
            getControl().shapeMoved();
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            getControl().shapeMoved();
        }

        public void show()
        {
            m_list.show();
            batch();
        }

        public void hide()
        {
            m_list.hide();
            batch();
        }

        public void destroy()
        {
            m_list.destroy();

            m_registrationManager.removeHandler();

            m_magnetManager.m_magnetRegistry.remove(m_wiresShape.uuid());

        }

        public void destroy(WiresMagnet magnet)
        {
            m_list.remove(magnet);
        }

        public IControlHandleList getMagnets()
        {
            return m_list;
        }

        public int size()
        {
            return m_list.size();
        }

        public Shape<?> getShape()
        {
            return m_wiresShape.getPath();
        }

        public Group getGroup()
        {
            return m_wiresShape.getGroup();
        }

        public WiresMagnet getMagnet(int index)
        {
            return (WiresMagnet) m_list.getHandle(index);
        }

        private WiresMagnetsControl getControl() {
            return getWiresShape().getControl().getMagnetsControl();
        }

        private void batch()
        {
            if (null != m_wiresShape.getPath().getLayer())
            {
                m_wiresShape.getPath().getLayer().batch();
            }
        }
    }
}

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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
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
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

public class MagnetManager
{
    private static final int          CONTROL_RADIUS       = 7;

    public static final ColorKeyRotor m_c_rotor            = new ColorKeyRotor();

    private NFastStringMap<Magnets>   m_magnetRegistry     = new NFastStringMap<Magnets>();

    private int                       m_ctrlSize           = CONTROL_RADIUS;

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
        final IPrimitive<?> primTarget = wiresShape.getGroup();
        final Point2DArray points = getWiresIntersectionPoints(wiresShape);
        final ControlHandleList list = new ControlHandleList(primTarget);
        final BoundingBox box = wiresShape.getPath().getBoundingBox();

        double left = box.getX();
        double right = left + box.getWidth();
        double top = box.getY();
        double bottom = top + box.getHeight();

        final Point2D primLoc = WiresUtils.getLocation(primTarget);
        final Magnets magnets = new Magnets(this, list, wiresShape);

        for (Point2D p : points)
        {
            final double mx = primLoc.getX() + p.getX();
            final double my = primLoc.getY() + p.getY();
            WiresMagnet m = new WiresMagnet(magnets, null, 0, p.getX(), p.getY(), getControlPrimitive(mx, my), true);
            Direction d = getDirection(p, left, right, top, bottom);
            m.setDirection(d);
            list.add(m);
        }

        String uuid = primTarget.uuid();
        m_magnetRegistry.put(uuid, magnets);

        wiresShape.setMagnets(magnets);

        return magnets;
    }

    public Magnets getMagnets(IPrimitive<?> shape)
    {
        return m_magnetRegistry.get(shape.uuid());
    }

    public Direction getDirection(Point2D point, double left, double right, double top, double bottom)
    {
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
                    return Direction.NORTH_WEST;
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
                    return Direction.SOUTH_WEST;
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
                    return Direction.NORTH_EAST;
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
                    return Direction.SOUTH_EAST;
                }
            }
        }
    }

    public void setHotspotSize(int m_ctrlSize)
    {
        this.m_ctrlSize = m_ctrlSize;
    }

    private static Point2DArray getWiresIntersectionPoints(final WiresShape wiresShape)
    {
        return Geometry.getCardinalIntersects(wiresShape.getPath());
    }

    private Circle getControlPrimitive(double x, double y)
    {
        return new Circle(m_ctrlSize)
                .setX(x)
                .setY(y)
                .setFillColor(ColorName.DARKRED)
                .setFillAlpha(0.8)
                .setStrokeAlpha(0)
                .setDraggable(true)
                .setDragMode(DragMode.SAME_LAYER);
    }

    public static class Magnets implements AttributesChangedHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private IControlHandleList m_list;

        private MagnetManager m_magnetManager;

        private WiresShape m_wiresShape;

        private boolean m_isDragging;

        private final HandlerRegistrationManager m_registrationManager = new HandlerRegistrationManager();

        public Magnets(MagnetManager magnetManager, IControlHandleList list, WiresShape wiresShape)
        {
            m_list = list;
            m_magnetManager = magnetManager;
            m_wiresShape = wiresShape;

            Group shapeGroup = wiresShape.getGroup();
            m_registrationManager.register(shapeGroup.addAttributesChangedHandler(Attribute.X, this));
            m_registrationManager.register(shapeGroup.addAttributesChangedHandler(Attribute.Y, this));
            m_registrationManager.register(shapeGroup.addNodeDragStartHandler(this));
            m_registrationManager.register(shapeGroup.addNodeDragMoveHandler(this));
            m_registrationManager.register(shapeGroup.addNodeDragEndHandler(this));
        }

        public WiresShape getWiresShape()
        {
            return m_wiresShape;
        }

        public IPrimitive<?> getPrimTarget()
        {
            return getGroup();
        }

        @Override
        public void onAttributesChanged(AttributesChangedEvent event)
        {
            if (!m_isDragging && event.any(Attribute.X, Attribute.Y))
            {
                shapeMoved();
            }
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
            shapeMoved();
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            shapeMoved();
        }

        public void shapeMoved()
        {
            IPrimitive<?> prim = getPrimTarget();
            Point2D absLoc = WiresUtils.getLocation(prim);
            double x = absLoc.getX();
            double y = absLoc.getY();
            shapeMoved(x, y);
        }

        private void shapeMoved(final double x, final double y)
        {
            for (int i = 0; i < m_list.size(); i++)
            {
                WiresMagnet m = (WiresMagnet) m_list.getHandle(i);
                m.shapeMoved(x, y);
            }

            if (m_wiresShape.getChildShapes() != null && !m_wiresShape.getChildShapes().isEmpty())
            {
                for (WiresShape child : m_wiresShape.getChildShapes())
                {
                    child.getMagnets().shapeMoved();
                }
            }

            batch();
        }

        public void shapeChanged()
        {
            final Point2DArray points = MagnetManager.getWiresIntersectionPoints(m_wiresShape);

            if (points.size() == m_list.size())
            {
                for (int i = 0; i < m_list.size(); i++)
                {
                    Point2D p = points.get(i);
                    WiresMagnet m = (WiresMagnet) m_list.getHandle(i);
                    m.setRx(p.getX()).setRy(p.getY());
                }
                this.shapeMoved();
            }
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

            m_magnetManager.m_magnetRegistry.remove(m_wiresShape.getPath().uuid());
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

        private void batch()
        {
            if (null != m_wiresShape.getPath().getLayer())
            {
                m_wiresShape.getPath().getLayer().batch();
            }
        }
    }
}

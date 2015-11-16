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
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class MagnetManager
{
    public static final double        CONTROL_RADIUS       = 5;

    public static final double        CONTROL_STROKE_WIDTH = 2;

    public static final ColorKeyRotor m_c_rotor            = new ColorKeyRotor();

    private NFastStringMap<Magnets>  m_magnetRegistry     = new NFastStringMap<Magnets>();

    public ImageData drawMagnetsToBack(Magnets magnets, NFastStringMap<WiresShape> shape_color_map, NFastStringMap<WiresMagnet> magnet_color_map, ScratchPad scratch)
    {
        scratch.clear();
        Context2D ctx = scratch.getContext();

        // the Shape doesn't need recording, we just need to know the mouse is over something
        drawShapeToBacking(ctx, magnets.getWiresShape(), m_c_rotor.next(), shape_color_map);

        magnet_color_map.clear();
        for (int i = 0; i < magnets.size(); i++)
        {
            WiresMagnet m = magnets.getMagnet(i);

            String c = m_c_rotor.next();
            magnet_color_map.put(c, m);
            ctx.beginPath();
            ctx.setStrokeWidth(CONTROL_STROKE_WIDTH);
            ctx.setStrokeColor(c);
            ctx.setFillColor(c);
            ctx.arc(m.getControl().getX(), m.getControl().getY(), CONTROL_RADIUS, 0, 2 * Math.PI, false);
            ctx.stroke();
            ctx.fill();
        }
        return ctx.getImageData(0, 0, scratch.getHeight(), scratch.getHeight());
    }

    public ImageData drawShapesToBacking(NFastArrayList<WiresShape> prims, ScratchPad scratch, WiresContainer skip, NFastStringMap<WiresShape> shape_color_map)
    {
        scratch.clear();
        Context2D ctx = scratch.getContext();

        shape_color_map.clear();
        drawShapesToBacking(prims, ctx, skip, shape_color_map);

        return ctx.getImageData(0, 0, scratch.getWidth(), scratch.getHeight());
    }

    public void drawShapesToBacking(NFastArrayList<WiresShape> prims, Context2D ctx, WiresContainer skip, NFastStringMap<WiresShape> shape_color_map)
    {
        for (int j = 0; j < prims.size(); j++)
        {
            WiresShape prim = prims.get(j);
            if (prim == skip)
            {
                continue;
            }
            drawShapeToBacking(ctx, prim, m_c_rotor.next(), shape_color_map);

            if (prim.getChildShapes() != null)
            {
                drawShapesToBacking(prim.getChildShapes(), ctx, skip, shape_color_map);
            }
        }
    }

    public void drawShapeToBacking(Context2D ctx, WiresShape shape, String color, NFastStringMap<WiresShape> m_shape_color_map)
    {
        m_shape_color_map.put(color, shape);
        MultiPath multiPath = shape.getPath();
        NFastArrayList<PathPartList> listOfPaths = multiPath.getPathPartListArray();

        for (int k = 0; k < listOfPaths.size(); k++)
        {
            PathPartList path = listOfPaths.get(k);

            ctx.setStrokeWidth(multiPath.getStrokeWidth());
            ctx.setStrokeColor(color);
            ctx.setFillColor(color);
            ctx.beginPath();

            Point2D absLoc = multiPath.getAbsoluteLocation();
            double offsetX = absLoc.getX();
            double offsetY = absLoc.getY();

            ctx.moveTo(offsetX, offsetY);

            boolean closed = false;
            for (int i = 0; i < path.size(); i++)
            {
                PathPartEntryJSO entry = path.get(i);
                NFastDoubleArrayJSO points = entry.getPoints();

                switch (entry.getCommand())
                {
                    case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    {
                        ctx.moveTo(points.get(0) + offsetX, points.get(1) + offsetY);
                        break;
                    }
                    case PathPartEntryJSO.LINETO_ABSOLUTE:
                    {
                        points = entry.getPoints();
                        double x0 = points.get(0) + offsetX;
                        double y0 = points.get(1) + offsetY;
                        ctx.lineTo(x0, y0);
                        break;
                    }
                    case PathPartEntryJSO.CLOSE_PATH_PART:
                    {
                        ctx.closePath();
                        closed = true;
                        break;
                    }
                    case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE:
                    {
                        points = entry.getPoints();

                        double x0 = points.get(0) + offsetX;
                        double y0 = points.get(1) + offsetY;

                        double x1 = points.get(2) + offsetX;
                        double y1 = points.get(3) + offsetY;
                        double r = points.get(4);
                        ctx.arcTo(x0, y0, x1, y1, r);

                    }
                        break;
                }
            }

            if (!closed)
            {
                ctx.closePath();
            }
            ctx.fill();
            ctx.stroke();
        }
    }

    public String findColorAtPoint(final ImageData imageData, final int x, final int y)
    {
        int red = imageData.getRedAt(x, y);
        int green = imageData.getGreenAt(x, y);
        int blue = imageData.getBlueAt(x, y);
        int alpha = imageData.getAlphaAt(x, y);

        if (alpha != 255)
        {
            return null;
        }
        String color = Color.rgbToBrowserHexColor(red, green, blue);

        return color;
    }

    public Magnets createMagnets(Shape<?> shape, Point2DArray points, WiresShape wiresShape)
    {
        return createMagnets(shape, null, points, wiresShape);
    }

    public NFastStringMap<Magnets> getMagnetRegistry()
    {
        return m_magnetRegistry;
    }

    public Magnets createMagnets(Shape<?> shape, IPrimitive<?> primTarget, Point2DArray points, WiresShape wiresShape)
    {
        ControlHandleList list = new ControlHandleList(primTarget);
        BoundingBox box = shape.getBoundingBox();

        double left = box.getX();
        double right = left + box.getWidth();
        double top = box.getY();
        double bottom = top + box.getHeight();

        Magnets magnets = new Magnets(this, list, shape, primTarget, wiresShape);

        Point2D absLoc = primTarget.getAbsoluteLocation();
        double offsetX = absLoc.getX();
        double offsetY = absLoc.getY();

        for (Point2D p : points)
        {
            double x = offsetX + p.getX();
            double y = offsetY + p.getY();
            WiresMagnet m = new WiresMagnet(magnets, null, 0, p.getX(), p.getY(), getControlPrimitive(x, y), true);
            Direction d = getDirection(p, left, right, top, bottom);
            m.setDirection(d);
            list.add(m);
        }

        String uuid = primTarget.uuid();
        m_magnetRegistry.put(uuid, magnets);

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

    private static Circle getControlPrimitive(double x, double y)
    {
        return new Circle(CONTROL_RADIUS).setFillColor(ColorName.RED).setFillAlpha(0.4).setX(x).setY(y).setDraggable(true).setDragMode(DragMode.SAME_LAYER).setStrokeColor(ColorName.BLACK).setStrokeWidth(CONTROL_STROKE_WIDTH);
    }

    public static class Magnets implements AttributesChangedHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private IControlHandleList m_list;

        private MagnetManager      m_magnetManager;

        private Shape<?>           m_shape;

        private IPrimitive<?>      m_primTarget;

        private boolean            m_isDragging;

        private WiresShape         m_wiresShape;

        public Magnets(MagnetManager magnetManager, IControlHandleList list, Shape<?> shape, IPrimitive<?> primTarget, WiresShape wiresShape)
        {
            m_list = list;
            m_magnetManager = magnetManager;
            m_shape = shape;
            m_primTarget = primTarget;
            m_wiresShape = wiresShape;

            IPrimitive<?> prim = getPrimTarget();
            prim.addAttributesChangedHandler(Attribute.X, this);
            prim.addAttributesChangedHandler(Attribute.Y, this);
            prim.addNodeDragMoveHandler(this);
        }

        public WiresShape getWiresShape()
        {
            return m_wiresShape;
        }

        public IPrimitive<?> getPrimTarget()
        {
            return m_primTarget;
        }

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
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            shapeMoved();
        }

        public void shapeMoved()
        {
            IPrimitive<?> prim = getPrimTarget();

            Point2D absLoc = prim.getAbsoluteLocation();
            double x = absLoc.getX();
            double y = absLoc.getY();
            for (int i = 0; i < m_list.size(); i++)
            {
                WiresMagnet m = (WiresMagnet) m_list.getHandle(i);
                m.shapeMoved(x, y);
            }

            if (m_wiresShape.getChildShapes() != null)
            {
                for (WiresShape child : m_wiresShape.getChildShapes())
                {
                    child.getMagnets().shapeMoved();
                }
            }
            if (m_list.getLayer() != null)
            {
                // it can be null, if the magnets are not currently displayed
                m_list.getLayer().batch();
            }
        }

        public void show()
        {
            m_list.show();
        }

        public void hide()
        {
            m_list.hide();
        }

        public void destroy()
        {
            m_list.destroy();

            m_magnetManager.m_magnetRegistry.remove(m_shape.uuid());
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
            return m_shape;
        }

        public Group getGroup()
        {
            return (Group) m_primTarget;
        }

        public WiresMagnet getMagnet(int index)
        {
            return (WiresMagnet) m_list.getHandle(index);
        }
    }
}

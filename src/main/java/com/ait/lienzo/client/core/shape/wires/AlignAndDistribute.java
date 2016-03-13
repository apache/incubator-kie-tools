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

import static com.ait.lienzo.client.core.AttributeOp.any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.tooling.common.api.flow.Flows.BooleanOp;
import com.ait.tooling.nativetools.client.collection.NFastStringSet;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * This class indexes related classes for alignment and distribution.
 *
 * an index is maintained for each edge and center for alignment and distribution.
 *
 * All indexing is done by rounding the double value to int - using Math.round.
 *
 * It then uses this information to optional show guidelines or perform snapping. These can be turned on and off using the setter methods of this class
 *
 * It's possible to control the style of the guideline when drawn. By using the style setter methods of this class.
 *
 * The circa property controls the number of pixes to search from the current position. For instance a circle of 4, will search 4 pixels
 * above and 4 pixels below the current y position, as well as 4 pixels to the left and 4 pixels to the right. As soon as the first index has a match, the search stops and snapping is done to that offset.
 *
 * The implementation is fairly generic and uses shape.getBoundingPoints().getBoundingBox() to do it's work.
 * The reason for getBoundPoints, is that the x/y of a Circle is at the center, where as other shapes are top left - getBoundingPoints normalises this to top left.
 * There is only one bit that is shape specific, which is the attribute listener, so the engine can determine if a shape has been moved or resized. For example in the case of a rectangle
 * this is the x, y, w and h attributes - this would be different for other shapes. This information is provided by getBoundingBoxAttributes().
 *
 * Be aware that nested indexed shapes are removed on drag, so that if they extend beyond the parent shape, they do not impact it's bounding box used for indexing. One the new boundingbox is assigned.
 * the children are added back.
 */
public class AlignAndDistribute
{
    private Map<Double, LinkedList<AlignAndDistributeHandler>> m_leftIndex;

    private Map<Double, LinkedList<AlignAndDistributeHandler>> m_hCenterIndex;

    private Map<Double, LinkedList<AlignAndDistributeHandler>> m_rightIndex;

    private Map<Double, LinkedList<AlignAndDistributeHandler>> m_topIndex;

    private Map<Double, LinkedList<AlignAndDistributeHandler>> m_vCenterIndex;

    private Map<Double, LinkedList<AlignAndDistributeHandler>> m_bottomIndex;

    private Map<Double, LinkedList<DistributionEntry>>         m_leftDistIndex;

    private Map<Double, LinkedList<DistributionEntry>>         m_hCenterDistIndex;

    private Map<Double, LinkedList<DistributionEntry>>         m_rightDistIndex;

    private Map<Double, LinkedList<DistributionEntry>>         m_topDistIndex;

    private Map<Double, LinkedList<DistributionEntry>>         m_vCenterDistIndex;

    private Map<Double, LinkedList<DistributionEntry>>         m_bottomDistIndex;

    private DefaultAlignAndDistributeMatchesCallback           m_alignmentCallback;

    private Map<String, AlignAndDistributeHandler>             m_shapes         = new HashMap<String, AlignAndDistributeHandler>();

    private int                                                m_circa          = 4;

    protected boolean                                          m_snap           = true;

    protected boolean                                          m_drawGuideLines = true;

    public AlignAndDistribute(Layer layer)
    {
        m_leftIndex = new HashMap<Double, LinkedList<AlignAndDistributeHandler>>();
        m_hCenterIndex = new HashMap<Double, LinkedList<AlignAndDistributeHandler>>();
        m_rightIndex = new HashMap<Double, LinkedList<AlignAndDistributeHandler>>();

        m_topIndex = new HashMap<Double, LinkedList<AlignAndDistributeHandler>>();
        m_vCenterIndex = new HashMap<Double, LinkedList<AlignAndDistributeHandler>>();
        m_bottomIndex = new HashMap<Double, LinkedList<AlignAndDistributeHandler>>();

        m_alignmentCallback = new DefaultAlignAndDistributeMatchesCallback(layer);

        m_leftDistIndex = new HashMap<Double, LinkedList<DistributionEntry>>();
        m_hCenterDistIndex = new HashMap<Double, LinkedList<DistributionEntry>>();
        m_rightDistIndex = new HashMap<Double, LinkedList<DistributionEntry>>();

        m_topDistIndex = new HashMap<Double, LinkedList<DistributionEntry>>();
        m_vCenterDistIndex = new HashMap<Double, LinkedList<DistributionEntry>>();
        m_bottomDistIndex = new HashMap<Double, LinkedList<DistributionEntry>>();
    }

    public static BoundingBox getBoundingBox(IDrawable<?> prim)
    {
        return prim.getBoundingPoints().getBoundingBox();
    }

    public static Attributes getAttributes(IPrimitive<?> prim)
    {
        return prim.getAttributes();
    }

    public double getStrokeWidth()
    {
        return m_alignmentCallback.getStrokeWidth();
    }

    public void setStrokeWidth(double strokeWidth)
    {
        m_alignmentCallback.setStrokeWidth(strokeWidth);
    }

    public String getStrokeColor()
    {
        return m_alignmentCallback.getStrokeColor();
    }

    public void setStrokeColor(String strokeColor)
    {
        m_alignmentCallback.setStrokeColor(strokeColor);
    }

    public DashArray getDashArray()
    {
        return m_alignmentCallback.getDashArray();
    }

    public void setDashArray(DashArray dashArray)
    {
        m_alignmentCallback.setDashArray(dashArray);
    }

    public int getSnapCirca()
    {
        return m_circa;
    }

    public void setSnapCirca(int circa)
    {
        m_circa = circa;
    }

    public boolean isSnap()
    {
        return m_snap;
    }

    public void setSnap(boolean snap)
    {
        m_snap = snap;
    }

    public boolean isDrawGuideLines()
    {
        return m_drawGuideLines;
    }

    public void setDrawGuideLines(boolean drawGuideLines)
    {
        m_drawGuideLines = drawGuideLines;
    }

    public AlignAndDistributeHandler getShapeHandler(IPrimitive<?> prim)
    {
        return m_shapes.get(prim.uuid());
    }

    public AlignAndDistributeHandler addShape(IDrawable<?> shape)
    {
        final String uuid = shape.uuid();

        AlignAndDistributeHandler handler = m_shapes.get(uuid);

        if (null == handler)
        {
            // only add if the shape has not already been added
            if (shape instanceof Group)
            {
                handler = new AlignAndDistributeHandler((IPrimitive<?>) shape, this, m_alignmentCallback, ((Group) shape).getBoundingBoxAttributes());
            }
            else
            {
                handler = new AlignAndDistributeHandler((IPrimitive<?>) shape, this, m_alignmentCallback, ((IPrimitive<?>) shape).getBoundingBoxAttributes());
            }
            m_shapes.put(uuid, handler);
        }

        return handler;
    }

    public void removeShape(IDrawable<?> shape)
    {
        AlignAndDistributeHandler handler = m_shapes.get(shape.uuid());

        if (null != handler)
        {
            indexOff(handler);

            m_shapes.remove(shape.uuid());

            handler.removeHandlerRegistrations();
        }
    }

    public void addAlignIndexEntry(Map<Double, LinkedList<AlignAndDistributeHandler>> index, AlignAndDistributeHandler handler, double pos)
    {
        double rounded = round(pos);
        LinkedList<AlignAndDistributeHandler> bucket = index.get(rounded);
        if (bucket == null)
        {
            bucket = new LinkedList<AlignAndDistributeHandler>();
            index.put(rounded, bucket);
        }
        bucket.add(handler);
    }

    public void removeAlignIndexEntry(Map<Double, LinkedList<AlignAndDistributeHandler>> index, AlignAndDistributeHandler handler, double pos)
    {
        double rounded = round(pos);
        LinkedList<AlignAndDistributeHandler> bucket = index.get(rounded);
        bucket.remove(handler);
        if (bucket.isEmpty())
        {
            index.remove(rounded);
        }
    }

    public void addDistIndexEntry(Map<Double, LinkedList<DistributionEntry>> index, DistributionEntry dist)
    {
        LinkedList<DistributionEntry> bucket = index.get(dist.getPoint());
        if (bucket == null)
        {
            bucket = new LinkedList<DistributionEntry>();
            index.put(dist.getPoint(), bucket);
        }
        bucket.add(dist);
    }

    public void removeDistIndexEntry(Map<Double, LinkedList<DistributionEntry>> index, DistributionEntry dist)
    {
        LinkedList<DistributionEntry> bucket = index.get(dist.getPoint());
        bucket.remove(dist);
        if (bucket.isEmpty())
        {
            index.remove(dist.getPoint());
        }
    }

    public void removeDistIndex(AlignAndDistributeHandler handler)
    {
        removeHorizontalDistIndex(handler);
        removeVerticalDistIndex(handler);
    }

    public void removeHorizontalDistIndex(AlignAndDistributeHandler handler)
    {
        for (DistributionEntry dist : handler.getHorizontalDistributionEntries())
        {
            AlignAndDistributeHandler h1 = dist.getShape1();
            AlignAndDistributeHandler h2 = dist.getShape2();

            // make sure we don't remove from handler, or it will remove from the collection currently being iterated.
            if (handler == h1)
            {
                h2.getHorizontalDistributionEntries().remove(dist);
            }
            else
            {
                h1.getHorizontalDistributionEntries().remove(dist);
            }
            switch (dist.getDistributionType())
            {
                case DistributionEntry.LEFT_DIST:
                    removeDistIndexEntry(m_leftDistIndex, dist);
                    break;
                case DistributionEntry.H_CENTER_DIST:
                    removeDistIndexEntry(m_hCenterDistIndex, dist);
                    break;
                case DistributionEntry.RIGHT_DIST:
                    removeDistIndexEntry(m_rightDistIndex, dist);
                    break;
            }
        }
        handler.getHorizontalDistributionEntries().clear();
    }

    public void removeVerticalDistIndex(AlignAndDistributeHandler handler)
    {
        for (DistributionEntry dist : handler.getVerticalDistributionEntries())
        {
            AlignAndDistributeHandler h1 = dist.getShape1();
            AlignAndDistributeHandler h2 = dist.getShape2();

            // make sure we don't remove from handler, or it will remove from the collection currently being iterated.
            if (handler == h1)
            {
                h2.getVerticalDistributionEntries().remove(dist);
            }
            else
            {
                h1.getVerticalDistributionEntries().remove(dist);
            }

            switch (dist.getDistributionType())
            {
                case DistributionEntry.TOP_DIST:
                    removeDistIndexEntry(m_topDistIndex, dist);
                    break;
                case DistributionEntry.V_CENTER_DIST:
                    removeDistIndexEntry(m_vCenterDistIndex, dist);
                    break;
                case DistributionEntry.BOTTOM_DIST:
                    removeDistIndexEntry(m_bottomDistIndex, dist);
                    break;
            }
        }
        handler.getVerticalDistributionEntries().clear();
    }

    public void buildDistIndex(AlignAndDistributeHandler handler)
    {
        buildHorizontalDistIndex(handler);
        buildVerticalDistIndex(handler);
    }

    public void buildHorizontalDistIndex(AlignAndDistributeHandler handler)
    {
        double left = round(handler.getLeft());
        double right = round(handler.getRight());

        for (AlignAndDistributeHandler otherH : m_shapes.values())
        {
            if (skipShape(handler, otherH))
            {
                continue;
            }

            double otherLeft = round(otherH.getLeft());
            double otherRight = round(otherH.getRight());

            DistributionEntry leftDist = null;
            DistributionEntry hCenterDist = null;
            DistributionEntry rightDist = null;
            if (otherRight < left)
            {
                double dx = left - otherRight;
                double leftPoint = otherLeft - dx;
                double rightPoint = right + dx;
                double centerPoint = round(otherRight + ((left - otherRight) / 2));
                leftDist = new DistributionEntry(otherH, handler, leftPoint, DistributionEntry.LEFT_DIST);
                hCenterDist = new DistributionEntry(otherH, handler, centerPoint, DistributionEntry.H_CENTER_DIST);
                rightDist = new DistributionEntry(otherH, handler, rightPoint, DistributionEntry.RIGHT_DIST);
            }
            else if (otherLeft > right)
            {
                double dx = otherLeft - right;
                double leftPoint = left - dx;
                double rightPoint = otherRight + dx;
                double centerPoint = round(otherLeft + ((right - otherLeft) / 2));
                leftDist = new DistributionEntry(handler, otherH, leftPoint, DistributionEntry.LEFT_DIST);
                hCenterDist = new DistributionEntry(handler, otherH, centerPoint, DistributionEntry.H_CENTER_DIST);
                rightDist = new DistributionEntry(handler, otherH, rightPoint, DistributionEntry.RIGHT_DIST);
            }

            if (leftDist != null)
            {
                addDistIndexEntry(m_leftDistIndex, leftDist);
                addDistIndexEntry(m_hCenterDistIndex, hCenterDist);
                addDistIndexEntry(m_rightDistIndex, rightDist);
            }
        }
    }

    private boolean skipShape(AlignAndDistributeHandler handler, AlignAndDistributeHandler otherH)
    {
        if (otherH == handler || !otherH.isIndexed())
        {
            // don't index against yourself or shapes not indexed
            return true;
        }
        return false;
    }

    public void buildVerticalDistIndex(AlignAndDistributeHandler handler)
    {
        double top = round(handler.getTop());
        double bottom = round(handler.getBottom());

        for (AlignAndDistributeHandler otherH : m_shapes.values())
        {
            if (skipShape(handler, otherH))
            {
                continue;
            }

            double otherTop = round(otherH.getTop());
            double otherBottom = round(otherH.getBottom());

            DistributionEntry topDist = null;
            DistributionEntry vCenterDist = null;
            DistributionEntry bottomDist = null;
            if (otherBottom < top)
            {
                double dx = top - otherBottom;
                double topPoint = otherTop - dx;
                double bottomPoint = bottom + dx;
                double centerPoint = round(otherBottom + ((top - otherBottom) / 2));
                topDist = new DistributionEntry(otherH, handler, topPoint, DistributionEntry.TOP_DIST);
                vCenterDist = new DistributionEntry(otherH, handler, centerPoint, DistributionEntry.V_CENTER_DIST);
                bottomDist = new DistributionEntry(otherH, handler, bottomPoint, DistributionEntry.BOTTOM_DIST);
            }
            else if (otherTop > bottom)
            {
                double dx = otherTop - bottom;
                double topPoint = top - dx;
                double bottomPoint = otherBottom + dx;
                double centerPoint = round(bottom + ((otherTop - bottom) / 2));
                topDist = new DistributionEntry(handler, otherH, topPoint, DistributionEntry.TOP_DIST);
                vCenterDist = new DistributionEntry(handler, otherH, centerPoint, DistributionEntry.V_CENTER_DIST);
                bottomDist = new DistributionEntry(handler, otherH, bottomPoint, DistributionEntry.BOTTOM_DIST);
            }

            if (topDist != null)
            {
                addDistIndexEntry(m_topDistIndex, topDist);
                addDistIndexEntry(m_vCenterDistIndex, vCenterDist);
                addDistIndexEntry(m_bottomDistIndex, bottomDist);
            }
        }
    }

    public static class DistributionEntry
    {
        private static final int          LEFT_DIST     = 0;

        private static final int          H_CENTER_DIST = 1;

        private static final int          RIGHT_DIST    = 2;

        private static final int          TOP_DIST      = 3;

        private static final int          V_CENTER_DIST = 4;

        private static final int          BOTTOM_DIST   = 5;

        private AlignAndDistributeHandler m_shape1;

        private AlignAndDistributeHandler m_shape2;

        private double                    m_point;

        private int                       m_distType;

        public DistributionEntry(AlignAndDistributeHandler shape1, AlignAndDistributeHandler shape2, double point, int distType)
        {
            m_shape1 = shape1;
            m_shape2 = shape2;
            m_point = point;
            m_distType = distType;
            if (distType <= 2)
            {
                shape1.getHorizontalDistributionEntries().add(this);
                shape2.getHorizontalDistributionEntries().add(this);
            }
            else
            {
                shape1.getVerticalDistributionEntries().add(this);
                shape2.getVerticalDistributionEntries().add(this);
            }

        }

        public AlignAndDistributeHandler getShape1()
        {
            return m_shape1;
        }

        public AlignAndDistributeHandler getShape2()
        {
            return m_shape2;
        }

        public double getPoint()
        {
            return m_point;
        }

        public int getDistributionType()
        {
            return m_distType;
        }
    }

    public AlignAndDistributeMatches findNearestMatches(AlignAndDistributeHandler handler, double left, double hCenter, double right, double top, double vCenter, double bottom)
    {
        LinkedList<AlignAndDistributeHandler> leftList = null;
        LinkedList<AlignAndDistributeHandler> hCenterList = null;
        LinkedList<AlignAndDistributeHandler> rightList = null;

        LinkedList<AlignAndDistributeHandler> topList = null;
        LinkedList<AlignAndDistributeHandler> vCenterList = null;
        LinkedList<AlignAndDistributeHandler> bottomList = null;

        LinkedList<DistributionEntry> leftDistList = null;
        LinkedList<DistributionEntry> hCenterDistList = null;
        LinkedList<DistributionEntry> rightDistList = null;

        LinkedList<DistributionEntry> topDistList = null;
        LinkedList<DistributionEntry> vCenterDistList = null;
        LinkedList<DistributionEntry> bottomDistList = null;

        int hOffset = 0;
        while (hOffset <= m_circa)
        {
            leftList = findNearestAlignIndexEntry(m_leftIndex, left + hOffset);
            hCenterList = findNearestAlignIndexEntry(m_hCenterIndex, hCenter + hOffset);
            rightList = findNearestAlignIndexEntry(m_rightIndex, right + hOffset);

            leftDistList = findNearestDistIndexEntry(m_leftDistIndex, right + hOffset);
            hCenterDistList = findNearestDistIndexEntry(m_hCenterDistIndex, hCenter + hOffset);
            rightDistList = findNearestDistIndexEntry(m_rightDistIndex, left + hOffset);

            if (matchFound(leftList, hCenterList, rightList, leftDistList, hCenterDistList, rightDistList))
            {
                break;
            }

            leftList = findNearestAlignIndexEntry(m_leftIndex, left - hOffset);
            hCenterList = findNearestAlignIndexEntry(m_hCenterIndex, hCenter - hOffset);
            rightList = findNearestAlignIndexEntry(m_rightIndex, right - hOffset);

            leftDistList = findNearestDistIndexEntry(m_leftDistIndex, right - hOffset);
            hCenterDistList = findNearestDistIndexEntry(m_hCenterDistIndex, hCenter - hOffset);
            rightDistList = findNearestDistIndexEntry(m_rightDistIndex, left - hOffset);
            if (matchFound(leftList, hCenterList, rightList, leftDistList, hCenterDistList, rightDistList))
            {
                hOffset = -hOffset;
                break;
            }

            hOffset++;
        }

        int vOffset = 0;
        while (vOffset <= m_circa)
        {
            topList = findNearestAlignIndexEntry(m_topIndex, top + vOffset);
            vCenterList = findNearestAlignIndexEntry(m_vCenterIndex, vCenter + vOffset);
            bottomList = findNearestAlignIndexEntry(m_bottomIndex, bottom + vOffset);

            topDistList = findNearestDistIndexEntry(m_topDistIndex, bottom + vOffset);
            vCenterDistList = findNearestDistIndexEntry(m_vCenterDistIndex, vCenter + vOffset);
            bottomDistList = findNearestDistIndexEntry(m_bottomDistIndex, top + vOffset);

            if (matchFound(topList, vCenterList, bottomList, topDistList, vCenterDistList, bottomDistList))
            {
                break;
            }

            topList = findNearestAlignIndexEntry(m_topIndex, top - vOffset);
            vCenterList = findNearestAlignIndexEntry(m_vCenterIndex, vCenter - vOffset);
            bottomList = findNearestAlignIndexEntry(m_bottomIndex, bottom - vOffset);

            topDistList = findNearestDistIndexEntry(m_topDistIndex, bottom - vOffset);
            vCenterDistList = findNearestDistIndexEntry(m_vCenterDistIndex, vCenter - vOffset);
            bottomDistList = findNearestDistIndexEntry(m_bottomDistIndex, top - vOffset);

            if (matchFound(topList, vCenterList, bottomList, topDistList, vCenterDistList, bottomDistList))
            {
                vOffset = -vOffset;
                break;
            }
            vOffset++;
        }

        AlignAndDistributeMatches matches;
        if (matchFound(leftList, hCenterList, rightList, leftDistList, hCenterDistList, rightDistList) || matchFound(topList, vCenterList, bottomList, topDistList, vCenterDistList, bottomDistList))
        {
            matches = new AlignAndDistributeMatches(handler, left + hOffset, leftList, hCenter + hOffset, hCenterList, right + hOffset, rightList, top + vOffset, topList, vCenter + vOffset, vCenterList, bottom + vOffset, bottomList, leftDistList, hCenterDistList, rightDistList, topDistList, vCenterDistList, bottomDistList);
        }
        else
        {
            matches = emptyAlignedMatches;
        }

        return matches;
    }

    private boolean matchFound(LinkedList<AlignAndDistributeHandler> l1, LinkedList<AlignAndDistributeHandler> l2, LinkedList<AlignAndDistributeHandler> l3, LinkedList<DistributionEntry> l4, LinkedList<DistributionEntry> l5, LinkedList<DistributionEntry> l6)
    {
        if (l1 != null || l2 != null || l3 != null || l4 != null || l5 != null || l6 != null)
        {
            return true;
        }
        return false;
    }

    private static LinkedList<AlignAndDistributeHandler> findNearestAlignIndexEntry(Map<Double, LinkedList<AlignAndDistributeHandler>> map, double pos)
    {
        double rounded = Math.round(pos);
        LinkedList<AlignAndDistributeHandler> indexEntries = map.get(rounded);
        return indexEntries;
    }

    private static LinkedList<DistributionEntry> findNearestDistIndexEntry(Map<Double, LinkedList<DistributionEntry>> map, double pos)
    {
        double rounded = Math.round(pos);
        LinkedList<DistributionEntry> indexEntries = map.get(rounded);
        return indexEntries;
    }

    private static final EmptyAlignAndDistributeMatches emptyAlignedMatches = new EmptyAlignAndDistributeMatches();

    public static class EmptyAlignAndDistributeMatches extends AlignAndDistributeMatches
    {
        public EmptyAlignAndDistributeMatches()
        {
            m_hasMatch = false;
        }
    }

    public void indexOff(AlignAndDistributeHandler handler)
    {
        indexOffWithoutChangingStatus(handler);
        handler.setIndexed(false);
    }

    private void indexOffWithoutChangingStatus(AlignAndDistributeHandler handler)
    {
        removeAlignIndex(handler, handler.getLeft(), handler.getHorizontalCenter(), handler.getRight(), handler.getTop(), handler.getVerticalCenter(), handler.getBottom());
        removeDistIndex(handler);
    }

    public void indexOn(AlignAndDistributeHandler handler)
    {
        indexOnWithoutChangingStatus(handler);
        handler.setIndexed(true);
    }

    private void indexOnWithoutChangingStatus(AlignAndDistributeHandler handler)
    {
        buildAlignIndex(handler, handler.getLeft(), handler.getHorizontalCenter(), handler.getRight(), handler.getTop(), handler.getVerticalCenter(), handler.getBottom());
        buildDistIndex(handler);
    }

    public void buildAlignIndex(AlignAndDistributeHandler handler, double left, double hCenter, double right, double top, double vCenter, double bottom)
    {
        addAlignIndexEntry(m_leftIndex, handler, left);
        addAlignIndexEntry(m_hCenterIndex, handler, hCenter);
        addAlignIndexEntry(m_rightIndex, handler, right);

        addAlignIndexEntry(m_topIndex, handler, top);
        addAlignIndexEntry(m_vCenterIndex, handler, vCenter);
        addAlignIndexEntry(m_bottomIndex, handler, bottom);
    }

    public void removeAlignIndex(AlignAndDistributeHandler handler, double left, double hCenter, double right, double top, double vCenter, double bottom)
    {
        removeAlignIndexEntry(m_leftIndex, handler, left);
        removeAlignIndexEntry(m_hCenterIndex, handler, hCenter);
        removeAlignIndexEntry(m_rightIndex, handler, right);

        removeAlignIndexEntry(m_topIndex, handler, top);
        removeAlignIndexEntry(m_vCenterIndex, handler, vCenter);
        removeAlignIndexEntry(m_bottomIndex, handler, bottom);
    }

    public void addLeftAlignIndexEntry(AlignAndDistributeHandler shape, double left)
    {
        addAlignIndexEntry(m_leftIndex, shape, left);
    }

    public void addHCenterAlignIndexEntry(AlignAndDistributeHandler shape, double hCenter)
    {
        addAlignIndexEntry(m_hCenterIndex, shape, hCenter);
    }

    public void addRightAlignIndexEntry(AlignAndDistributeHandler shape, double right)
    {
        addAlignIndexEntry(m_rightIndex, shape, right);
    }

    public void addTopAlignIndexEntry(AlignAndDistributeHandler shape, double top)
    {
        addAlignIndexEntry(m_topIndex, shape, top);
    }

    public void addVCenterAlignIndexEntry(AlignAndDistributeHandler shape, double vCenter)
    {
        addAlignIndexEntry(m_vCenterIndex, shape, vCenter);
    }

    public void addBottomAlignIndexEntry(AlignAndDistributeHandler shape, double bottom)
    {
        addAlignIndexEntry(m_bottomIndex, shape, bottom);
    }

    public void removeLeftAlignIndexEntry(AlignAndDistributeHandler shape, double left)
    {
        removeAlignIndexEntry(m_leftIndex, shape, left);
    }

    public void removeHCenterAlignIndexEntry(AlignAndDistributeHandler shape, double hCenter)
    {
        removeAlignIndexEntry(m_hCenterIndex, shape, hCenter);
    }

    public void removeRightAlignIndexEntry(AlignAndDistributeHandler shape, double right)
    {
        removeAlignIndexEntry(m_rightIndex, shape, right);
    }

    public void removeTopAlignIndexEntry(AlignAndDistributeHandler shape, double top)
    {
        removeAlignIndexEntry(m_topIndex, shape, top);
    }

    public void removeVCenterAlignIndexEntry(AlignAndDistributeHandler shape, double vCenter)
    {
        removeAlignIndexEntry(m_vCenterIndex, shape, vCenter);
    }

    public void removeBottomAlignIndexEntry(AlignAndDistributeHandler shape, double bottom)
    {
        removeAlignIndexEntry(m_bottomIndex, shape, bottom);
    }

    public static class AlignAndDistributeMatches
    {
        private AlignAndDistributeHandler             m_handler;

        private double                                m_leftPos;

        private LinkedList<AlignAndDistributeHandler> m_leftList;

        private double                                m_hCenterPos;

        private LinkedList<AlignAndDistributeHandler> m_hCenterList;

        private double                                m_rightPos;

        private LinkedList<AlignAndDistributeHandler> m_rightList;

        private double                                m_topPos;

        private LinkedList<AlignAndDistributeHandler> m_topList;

        private double                                m_vCenterPos;

        private LinkedList<AlignAndDistributeHandler> m_vCenterList;

        private double                                m_bottomPos;

        private LinkedList<AlignAndDistributeHandler> m_bottomList;

        private LinkedList<DistributionEntry>         m_leftDistList;

        private LinkedList<DistributionEntry>         m_hCenterDistList;

        private LinkedList<DistributionEntry>         m_rightDistList;

        private LinkedList<DistributionEntry>         m_topDistList;

        private LinkedList<DistributionEntry>         m_vCenterDistList;

        private LinkedList<DistributionEntry>         m_bottomDistList;

        protected boolean                             m_hasMatch;

        public AlignAndDistributeMatches()
        {

        }

        public AlignAndDistributeMatches(AlignAndDistributeHandler handler, double leftPos, LinkedList<AlignAndDistributeHandler> leftList, double hCenterPos, LinkedList<AlignAndDistributeHandler> hCenterList, double rightPos, LinkedList<AlignAndDistributeHandler> rightList, double topPos, LinkedList<AlignAndDistributeHandler> topList, double vCenterPos, LinkedList<AlignAndDistributeHandler> vCenterList, double bottomPos, LinkedList<AlignAndDistributeHandler> bottomList, LinkedList<DistributionEntry> leftDistList, LinkedList<DistributionEntry> hCenterDistList, LinkedList<DistributionEntry> rightDistList, LinkedList<DistributionEntry> topDistList, LinkedList<DistributionEntry> vCenterDistList, LinkedList<DistributionEntry> bottomDistList)
        {
            m_handler = handler;
            m_leftPos = leftPos;
            m_leftList = leftList;
            m_hCenterPos = hCenterPos;
            m_hCenterList = hCenterList;
            m_rightPos = rightPos;
            m_rightList = rightList;
            m_topPos = topPos;
            m_topList = topList;
            m_vCenterPos = vCenterPos;
            m_vCenterList = vCenterList;
            m_bottomPos = bottomPos;
            m_bottomList = bottomList;

            m_leftDistList = leftDistList;
            m_hCenterDistList = hCenterDistList;
            m_rightDistList = rightDistList;

            m_topDistList = topDistList;
            m_vCenterDistList = vCenterDistList;
            m_bottomDistList = bottomDistList;

            m_hasMatch = true;
        }

        public AlignAndDistributeHandler getHandler()
        {
            return m_handler;
        }

        public boolean hashMatch()
        {
            return m_hasMatch;
        }

        public LinkedList<AlignAndDistributeHandler> getLeftList()
        {
            return m_leftList;
        }

        public LinkedList<AlignAndDistributeHandler> getHorizontalCenterList()
        {
            return m_hCenterList;
        }

        public LinkedList<AlignAndDistributeHandler> getRightList()
        {
            return m_rightList;
        }

        public LinkedList<AlignAndDistributeHandler> getTopList()
        {
            return m_topList;
        }

        public LinkedList<AlignAndDistributeHandler> getVerticalCenterList()
        {
            return m_vCenterList;
        }

        public LinkedList<AlignAndDistributeHandler> getBottomList()
        {
            return m_bottomList;
        }

        public double getLeftPos()
        {
            return m_leftPos;
        }

        public double getHorizontalCenterPos()
        {
            return m_hCenterPos;
        }

        public double getRightPos()
        {
            return m_rightPos;
        }

        public double getTopPos()
        {
            return m_topPos;
        }

        public double getVerticalCenterPos()
        {
            return m_vCenterPos;
        }

        public double getBottomPos()
        {
            return m_bottomPos;
        }

        public LinkedList<DistributionEntry> getLeftDistList()
        {
            return m_leftDistList;
        }

        public LinkedList<DistributionEntry> getHorizontalCenterDistList()
        {
            return m_hCenterDistList;
        }

        public LinkedList<DistributionEntry> getRightDistList()
        {
            return m_rightDistList;
        }

        public LinkedList<DistributionEntry> getTopDistList()
        {
            return m_topDistList;
        }

        public LinkedList<DistributionEntry> getVerticalCenterDistList()
        {
            return m_vCenterDistList;
        }

        public LinkedList<DistributionEntry> getBottomDistList()
        {
            return m_bottomDistList;
        }
    }

    public static double round(double value)
    {
        return Math.round(value);
    }

    public static class AlignAndDistributeHandler implements AttributesChangedHandler, DragConstraintEnforcer, NodeDragEndHandler
    {
        protected AlignAndDistribute                m_alignAndDistribute;

        protected IPrimitive<?>                     m_shape;

        protected BoundingBox                       m_box;

        protected boolean                           m_isDraggable;

        protected boolean                           m_isDragging;

        protected HandlerRegistrationManager        m_attrHandlerRegs;

        protected HandlerRegistration               m_dragEndHandlerReg;

        protected AlignAndDistributeMatchesCallback m_alignAndDistributeMatchesCallback;

        protected double                            m_startLeft;

        protected double                            m_startTop;

        protected double                            m_left;

        protected double                            m_hCenter;

        protected double                            m_right;

        protected double                            m_top;

        protected double                            m_vCenter;

        protected double                            m_bottom;

        protected Set<DistributionEntry>            m_horizontalDistEntries;

        protected Set<DistributionEntry>            m_verticalDistEntries;

        private boolean                             indexed;

        private final BooleanOp                     m_bboxOp;

        private final BooleanOp                     m_tranOp;

        private double                              m_leftOffset;
        private double                              m_topOffset;


        public AlignAndDistributeHandler(IPrimitive<?> shape, AlignAndDistribute alignAndDistribute, AlignAndDistributeMatchesCallback alignAndDistributeMatchesCallback, List<Attribute> attributes)
        {
            m_shape = shape;

            m_alignAndDistribute = alignAndDistribute;

            m_alignAndDistributeMatchesCallback = alignAndDistributeMatchesCallback;

            // circles xy are in centre, where as others are top left.
            // For this reason we must use getBoundingBox, which uses BoundingPoints underneath, when ensures the shape x/y is now top left.
            // use this to determine an offset used for later get x/y
            m_box = AlignAndDistribute.getBoundingBox(shape);
            m_leftOffset = shape.getX() - m_box.getX();
            m_topOffset = shape.getY() - m_box.getY();

            Point2D absLoc = shape.getAbsoluteLocation();

            double left = absLoc.getX() + m_leftOffset;
            double right = left + m_box.getWidth();
            double top = absLoc.getY() + m_topOffset;
            double bottom = top + m_box.getHeight();

            captureHorizontalPositions(left, right);
            captureVerticalPositions(top, bottom);

            m_alignAndDistribute.indexOn(this);

            if (m_shape.isDraggable())
            {
                dragOn();
            }
            m_attrHandlerRegs = new HandlerRegistrationManager();

            final ArrayList<Attribute> temp = new ArrayList<Attribute>(attributes);

            temp.add(Attribute.X);

            temp.add(Attribute.Y);

            final NFastStringSet seen = new NFastStringSet();

            final ArrayList<Attribute> list = new ArrayList<Attribute>();

            for (Attribute attribute : temp)
            {
                if (null != attribute)
                {
                    if (false == seen.contains(attribute.getProperty()))
                    {
                        list.add(attribute);

                        seen.add(attribute.getProperty());
                    }
                }
            }
            m_bboxOp = any(list);

            addHandlers(m_shape, list);

            m_tranOp = any(Attribute.ROTATION, Attribute.SCALE, Attribute.SHEAR);
        }

        public void addHandlers(IDrawable<?> drawable, ArrayList<Attribute> list)
        {
            for (Attribute attribute : list)
            {
                m_attrHandlerRegs.register(drawable.addAttributesChangedHandler(attribute, this));
            }
            m_attrHandlerRegs.register(drawable.addAttributesChangedHandler(Attribute.ROTATION, this));
            m_attrHandlerRegs.register(drawable.addAttributesChangedHandler(Attribute.SCALE, this));
            m_attrHandlerRegs.register(drawable.addAttributesChangedHandler(Attribute.SHEAR, this));
        }

        public boolean isIndexed()
        {
            return indexed;
        }

        public void setIndexed(boolean indexed)
        {
            this.indexed = indexed;
        }

        public Set<DistributionEntry> getHorizontalDistributionEntries()
        {
            if (m_horizontalDistEntries == null)
            {
                m_horizontalDistEntries = new HashSet<DistributionEntry>();
            }
            return m_horizontalDistEntries;
        }

        public Set<DistributionEntry> getVerticalDistributionEntries()
        {
            if (m_verticalDistEntries == null)
            {
                m_verticalDistEntries = new HashSet<DistributionEntry>();
            }
            return m_verticalDistEntries;
        }

        public IPrimitive<?> getShape()
        {
            return m_shape;
        }

        /**
         * This is a cached BoundingBox
         * @return
         */
        public BoundingBox getBoundingBox()
        {
            return m_box;
        }

        public double getLeft()
        {
            return m_left;
        }

        public double getHorizontalCenter()
        {
            return m_hCenter;
        }

        public double getRight()
        {
            return m_right;
        }

        public double getTop()
        {
            return m_top;
        }

        public double getVerticalCenter()
        {
            return m_vCenter;
        }

        public double getBottom()
        {
            return m_bottom;
        }

        public void capturePositions(double left, double right, double top, double bottom)
        {
            if (left != m_left || right != m_right)
            {
                captureHorizontalPositions(left, right);
            }

            if (top != m_top || bottom != m_bottom)
            {
                captureVerticalPositions(top, bottom);
            }
        }

        public void captureHorizontalPositions(double left, double right)
        {
            double width = m_box.getWidth();
            m_left = left;
            m_hCenter = m_left + (width / 2);
            m_right = right;
        }

        public void captureVerticalPositions(double top, double bottom)
        {
            double height = m_box.getHeight();
            m_top = top;
            m_vCenter = (m_top + (height / 2));
            m_bottom = bottom;
        }

        public void updateIndex()
        {


            // circles xy are in centre, where as others are top left.
            // For this reason we must use getBoundingBox, which uses BoundingPoints underneath, when ensures the shape x/y is now top left.
            // However getBoundingBox here is still relative to parent, so must offset against parent absolute xy
            Point2D absLoc = m_shape.getAbsoluteLocation();

            double left = absLoc.getX() + m_leftOffset;
            double right = left + m_box.getWidth();
            double top = absLoc.getY() + m_topOffset;
            double bottom = top + m_box.getHeight();


            boolean leftChanged = left != m_left;
            boolean rightChanged = right != m_right;
            boolean topChanged = top != m_top;
            boolean bottomChanged = bottom != m_bottom;

            if (!leftChanged && !rightChanged && !topChanged && !bottomChanged)
            {
                // this can happen when the event batching triggers after a drag has stopped, but the event change was due to the dragging.
                // @dean REVIEW
                return;
            }

            //BoundingBox box = AlignAndDistribute.getBoundingBox(m_shape);
            updateIndex(leftChanged, rightChanged, topChanged, bottomChanged, left, right, top, bottom);
        }

        public void updateIndex(boolean leftChanged, boolean rightChanged, boolean topChanged, boolean bottomChanged, double left, double right, double top, double bottom)
        {
            if (leftChanged || rightChanged)
            {
                m_alignAndDistribute.removeHorizontalDistIndex(this);

                boolean hCenterChanged = (left + (m_box.getWidth() / 2) != m_hCenter);

                if (leftChanged)
                {
                    m_alignAndDistribute.removeLeftAlignIndexEntry(this, m_left);
                }

                if (hCenterChanged)
                {
                    m_alignAndDistribute.removeHCenterAlignIndexEntry(this, m_hCenter);
                }

                if (rightChanged)
                {
                    m_alignAndDistribute.removeRightAlignIndexEntry(this, m_right);
                }

                captureHorizontalPositions(left, right);
                if (leftChanged)
                {
                    m_alignAndDistribute.addLeftAlignIndexEntry(this, m_left);
                }

                if (hCenterChanged)
                {
                    m_alignAndDistribute.addHCenterAlignIndexEntry(this, m_hCenter);
                }

                if (rightChanged)
                {
                    m_alignAndDistribute.addRightAlignIndexEntry(this, m_right);
                }

                m_alignAndDistribute.buildHorizontalDistIndex(this);
            }

            if (topChanged || bottomChanged)
            {
                m_alignAndDistribute.removeVerticalDistIndex(this);

                boolean vCenterChanged = (top + (m_box.getHeight() / 2) != m_vCenter);

                if (topChanged)
                {
                    m_alignAndDistribute.removeTopAlignIndexEntry(this, m_top);
                }

                if (vCenterChanged)
                {
                    m_alignAndDistribute.removeVCenterAlignIndexEntry(this, m_vCenter);
                }

                if (bottomChanged)
                {
                    m_alignAndDistribute.removeBottomAlignIndexEntry(this, m_bottom);
                }

                captureVerticalPositions(top, bottom);
                if (topChanged)
                {
                    m_alignAndDistribute.addTopAlignIndexEntry(this, m_top);
                }

                if (vCenterChanged)
                {
                    m_alignAndDistribute.addVCenterAlignIndexEntry(this, m_vCenter);
                }

                if (bottomChanged)
                {
                    m_alignAndDistribute.addBottomAlignIndexEntry(this, m_bottom);
                }

                m_alignAndDistribute.buildVerticalDistIndex(this);
            }
        }

        public void dragOn()
        {
            m_isDraggable = true;
        }

        public void draggOff()
        {
            m_isDraggable = false;
        }

        public boolean isDraggable() {
            return m_isDraggable;
        }

        private final boolean hasComplexTransformAttributes()
        {
            final Attributes attr = getAttributes(m_shape);

            if (attr.hasComplexTransformAttributes())
            {
                final double r = attr.getRotation();

                if (r != 0)
                {
                    return true;
                }
                final Point2D scale = attr.getScale();

                if (null != scale)
                {
                    if ((scale.getX() != 1) || (scale.getY() != 1))
                    {
                        return true;
                    }
                }
                final Point2D shear = attr.getShear();

                if (null != shear)
                {
                    if ((shear.getX() != 0) || (shear.getY() != 0))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onAttributesChanged(final AttributesChangedEvent event)
        {
            if (m_isDragging)
            {
                // ignore attribute changes while dragging
                return;
            }
            if (event.evaluate(m_tranOp))
            {
                boolean hasTransformations = hasComplexTransformAttributes();

                if (indexed && hasTransformations)
                {
                    // Indexing cannot be done on transformed shapes
                    // it's cheaper to just check if the attributes exist on the shape, than it is to test for attributes on the event
                    m_alignAndDistribute.indexOff(this);
                }
                else if (!indexed && !hasTransformations)
                {
                    // Indexing was turned off, but there are no more transformations, so turn it back on again
                    m_alignAndDistribute.indexOn(this);
                }
            }
            boolean isDraggable = m_shape.isDraggable();

            if (!m_isDraggable && isDraggable)
            {
                // was off, now on
                dragOn();
            }
            else if (m_isDraggable && !isDraggable)
            {
                // was on, now on off
                draggOff();
            }
            if (indexed && event.evaluate(m_bboxOp))
            {
                updateIndex();
            }
        }

        @Override
        public void startDrag(DragContext dragContext)
        {
            // shapes being dragged must be removed from the index, so that they don't snap to themselves
            // Also removes all nested shapes.
            m_startLeft = m_left;
            m_startTop = m_top;

            m_isDragging = true;
            iterateAndRemoveIndex(m_shape);
        }

        public void iterateAndRemoveIndex(IPrimitive<?> prim)
        {
            indexOff(prim);
            if (prim instanceof Group)
            {
                for (IPrimitive<?> child : prim.asGroup().getChildNodes())
                {
                    if (child instanceof Group)
                    {
                        iterateAndRemoveIndex(child.asGroup());
                    }
                    else
                    {
                        indexOff(child);
                    }
                }
            }
        }

        public void indexOff(IPrimitive<?> child)
        {
            AlignAndDistributeHandler handler = m_alignAndDistribute.m_shapes.get(child.uuid());
            if (handler != null && handler.isIndexed())
            {
                m_alignAndDistribute.indexOffWithoutChangingStatus(handler);
            }
        }

        public static class ShapePair {
            private Group parent;
            private IPrimitive<?> child;
            AlignAndDistributeHandler handler;

            public ShapePair(Group group, IPrimitive<?> child, AlignAndDistributeHandler handler) {
                this.parent = group;
                this.child = child;
                this.handler = handler;
            }
        }

        public void removeChildrenIfIndexed(IPrimitive<?> prim, List<ShapePair> pairs) {
            for (IPrimitive<?> child : prim.asGroup().getChildNodes())
            {
                AlignAndDistributeHandler handler = m_alignAndDistribute.m_shapes.get(child.uuid());
                if ( handler != null ) {
                    ShapePair pair = new ShapePair(prim.asGroup(), child, handler);
                    pairs.add( pair );
                    prim.asGroup().remove(child);
                }
                if (child instanceof Group)
                {
                    removeChildrenIfIndexed(child.asGroup(), pairs);
                }
            }
        }

        private void indexOn(IPrimitive<?> shape)
        {
            AlignAndDistributeHandler handler = m_alignAndDistribute.m_shapes.get(shape.uuid());
            indexOn(handler);
        }

        private void indexOn(AlignAndDistributeHandler handler)
        {
            if (handler != null && handler.isIndexed())
            {
                m_alignAndDistribute.indexOnWithoutChangingStatus(handler);
                handler.updateIndex();
            }
        }

        @Override
        public boolean adjust(Point2D dxy)
        {
            if (!indexed)
            {
                // ignore adjustment if indexing is off
                return false;
            }

            double left = m_startLeft + dxy.getX();
            double top = m_startTop + dxy.getY();
            double width = m_box.getWidth();
            double height = m_box.getHeight();
            capturePositions(left, left + width, top, top + height);

            AlignAndDistributeMatches matches = m_alignAndDistribute.findNearestMatches(this, m_left, m_hCenter, m_right, m_top, m_vCenter, m_bottom);

            if (m_alignAndDistribute.isSnap())
            {
                boolean recapture = false;

                double xOffset = m_startLeft;
                double yOffset = m_startTop;

                // Adjust horizontal
                if (matches.getLeftList() != null)
                {
                    dxy.setX(matches.getLeftPos() - xOffset);
                    recapture = true;
                }
                else if (matches.getHorizontalCenterList() != null)
                {
                    dxy.setX((matches.getHorizontalCenterPos() - (width / 2)) - xOffset);
                    recapture = true;
                }
                else if (matches.getRightList() != null)
                {
                    dxy.setX((matches.getRightPos() - width) - xOffset);
                    recapture = true;
                }

                // Adjust Vertical
                if (matches.getTopList() != null)
                {
                    dxy.setY(matches.getTopPos() - yOffset);
                    recapture = true;
                }
                else if (matches.getVerticalCenterList() != null)
                {
                    dxy.setY((matches.getVerticalCenterPos() - (height / 2)) - yOffset);
                    recapture = true;
                }
                else if (matches.getBottomList() != null)
                {
                    dxy.setY((matches.getBottomPos() - height) - yOffset);
                    recapture = true;
                }

                // Adjust horizontal distribution
                if (matches.getLeftDistList() != null)
                {
                    dxy.setX(matches.getLeftDistList().getFirst().getPoint() - width - xOffset);
                    recapture = true;
                }
                else if (matches.getRightDistList() != null)
                {
                    dxy.setX(matches.getRightDistList().getFirst().getPoint() - xOffset);
                    recapture = true;
                }
                else if (matches.getHorizontalCenterDistList() != null)
                {
                    dxy.setX(matches.getHorizontalCenterDistList().getFirst().getPoint() - (width / 2) - xOffset);
                    recapture = true;
                }

                // Adjust vertical distribution
                if (matches.getTopDistList() != null)
                {
                    dxy.setY(matches.getTopDistList().getFirst().getPoint() - height - yOffset);
                    recapture = true;
                }
                else if (matches.getBottomDistList() != null)
                {
                    dxy.setY(matches.getBottomDistList().getFirst().getPoint() - yOffset);
                    recapture = true;
                }
                else if (matches.getVerticalCenterDistList() != null)
                {
                    dxy.setY(matches.getVerticalCenterDistList().getFirst().getPoint() - (height / 2) - yOffset);
                    recapture = true;
                }

                // it was adjusted, so recapture points
                if (recapture)
                {
                    // can't use the original left and top vars, as they are before adjustment snap
                    left = m_startLeft + dxy.getX();
                    top = m_startTop + dxy.getY();
                    width = m_box.getWidth();
                    height = m_box.getHeight();
                    capturePositions(left, left + width, top, top + height);
                }
            }

            if (m_alignAndDistribute.isDrawGuideLines())
            {
                m_alignAndDistributeMatchesCallback.call(matches);
            }

            return true;
        }

        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_isDragging = false;

            m_alignAndDistributeMatchesCallback.dragEnd();

            // We do not want the nested indexed shapes to impact the bounding box
            // so remove them, they will be added once the index has been made.
            List<ShapePair> pairs = new ArrayList<ShapePair>();
            removeChildrenIfIndexed(m_shape, pairs);

            indexOn(m_shape);

            // re-add the children, index before it adds the next nested child
            for ( ShapePair pair : pairs )
            {
                pair.parent.add(pair.child);
                indexOn(pair.handler);
            }
        }

        private void removeDragHandlerRegistrations()
        {
            if (null != m_dragEndHandlerReg)
            {
                m_dragEndHandlerReg.removeHandler();

                m_dragEndHandlerReg = null;
            }
        }

        public void removeHandlerRegistrations()
        {
            if (null != m_attrHandlerRegs)
            {
                m_attrHandlerRegs.destroy();

                m_attrHandlerRegs = null;
            }
            removeDragHandlerRegistrations();
        }
    }

    public static interface AlignAndDistributeMatchesCallback
    {
        void call(AlignAndDistributeMatches matches);

        void dragEnd();
    }

    public static class DefaultAlignAndDistributeMatchesCallback implements AlignAndDistributeMatchesCallback
    {
        private final Shape<?>[] m_lines       = new Shape<?>[18];

        private Layer            m_layer;

        private Layer            m_overs;

        private double           m_strokeWidth = 0.5;

        private String           m_strokeColor = "#000000";

        private DashArray        m_dashArray   = new DashArray(10, 10);

        public DefaultAlignAndDistributeMatchesCallback(Layer layer)
        {
            m_layer = layer;
        }

        public DefaultAlignAndDistributeMatchesCallback(Layer layer, double strokeWidth, String strokeColor, DashArray dashArray)
        {
            this(layer);
            m_strokeWidth = strokeWidth;
            m_strokeColor = strokeColor;
            m_dashArray = dashArray;
        }

        public double getStrokeWidth()
        {
            return m_strokeWidth;
        }

        public void setStrokeWidth(double strokeWidth)
        {
            m_strokeWidth = strokeWidth;
        }

        public String getStrokeColor()
        {
            return m_strokeColor;
        }

        public void setStrokeColor(String strokeColor)
        {
            m_strokeColor = strokeColor;
        }

        public DashArray getDashArray()
        {
            return m_dashArray;
        }

        public void setDashArray(DashArray dashArray)
        {
            m_dashArray = dashArray;
        }

        private final Layer getOverLayer()
        {
            if (null == m_overs)
            {
                m_overs = m_layer.getViewport().getOverLayer();
            }
            return m_overs;
        }

        @Override
        public void dragEnd()
        {
            final Layer layer = getOverLayer();

            for (int i = 0; i < m_lines.length; i++)
            {
                if (m_lines[i] != null)
                {
                    layer.remove(m_lines[i]);

                    m_lines[i] = null;
                }
            }
            layer.draw();
        }

        @Override
        public void call(AlignAndDistributeMatches matches)
        {
            AlignAndDistributeHandler handler = matches.getHandler();

            drawAlignIfMatches(handler, matches.getLeftList(), matches.getLeftPos(), 0, true);
            drawAlignIfMatches(handler, matches.getHorizontalCenterList(), matches.getHorizontalCenterPos(), 1, true);
            drawAlignIfMatches(handler, matches.getRightList(), matches.getRightPos(), 2, true);

            drawAlignIfMatches(handler, matches.getTopList(), matches.getTopPos(), 3, false);
            drawAlignIfMatches(handler, matches.getVerticalCenterList(), matches.getVerticalCenterPos(), 4, false);
            drawAlignIfMatches(handler, matches.getBottomList(), matches.getBottomPos(), 5, false);

            drawDistIfMatches(handler, matches.getLeftDistList(), 6, false);
            drawDistIfMatches(handler, matches.getHorizontalCenterDistList(), 8, false);
            drawDistIfMatches(handler, matches.getRightDistList(), 10, false);

            drawDistIfMatches(handler, matches.getTopDistList(), 12, true);
            drawDistIfMatches(handler, matches.getVerticalCenterDistList(), 14, true);
            drawDistIfMatches(handler, matches.getBottomDistList(), 16, true);
        }

        private void drawAlignIfMatches(AlignAndDistributeHandler handler, LinkedList<AlignAndDistributeHandler> shapes, double pos, int index, boolean vertical)
        {
            final Layer layer = getOverLayer();

            if (shapes != null)
            {
                if (vertical)
                {
                    drawVerticalLine(handler, pos, shapes, index);
                }
                else
                {
                    drawHorizontalLine(handler, pos, shapes, index);
                }
                layer.draw();
            }
            else if (m_lines[index] != null)
            {
                removeLine(index, m_lines[index]);

                layer.draw();
            }
        }

        private void drawDistIfMatches(AlignAndDistributeHandler h, LinkedList<DistributionEntry> shapes, int index, boolean vertical)
        {
            final Layer layer = getOverLayer();

            if (shapes != null)
            {
                for (DistributionEntry dist : shapes)
                {
                    AlignAndDistributeHandler h1 = dist.getShape1();

                    AlignAndDistributeHandler h2 = dist.getShape2();

                    if (!vertical)
                    {
                        double bottom = h.getBottom();

                        if (h1.getBottom() > bottom)
                        {
                            bottom = h1.getBottom();
                        }
                        if (h2.getBottom() > bottom)
                        {
                            bottom = h2.getBottom();
                        }
                        bottom = bottom + 20;

                        double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
                        double x2 = 0, y2 = 0, x3 = 0, y3 = 0;

                        switch (dist.getDistributionType())
                        {
                            case DistributionEntry.LEFT_DIST:
                                x0 = h.getRight();
                                y0 = h.getBottom() + 5;
                                x1 = h1.getLeft();
                                y1 = h1.getBottom() + 5;

                                x2 = h1.getRight();
                                y2 = h1.getBottom() + 5;
                                x3 = h2.getLeft();
                                y3 = h2.getBottom() + 5;
                                break;
                            case DistributionEntry.H_CENTER_DIST:
                                x0 = h1.getRight();
                                y0 = h1.getBottom() + 5;
                                x1 = h.getLeft();
                                y1 = h.getBottom() + 5;

                                x2 = h.getRight();
                                y2 = h.getBottom() + 5;
                                x3 = h2.getLeft();
                                y3 = h2.getBottom() + 5;
                                break;
                            case DistributionEntry.RIGHT_DIST:
                                x0 = h1.getRight();
                                y0 = h1.getBottom() + 5;
                                x1 = h2.getLeft();
                                y1 = h2.getBottom() + 5;

                                x2 = h2.getRight();
                                y2 = h2.getBottom() + 5;
                                x3 = h.getLeft();
                                y3 = h.getBottom() + 5;
                                break;
                        }
                        drawPolyLine(index, bottom, x0, y0, x1, y1, false);
                        drawPolyLine(index + 1, bottom, x2, y2, x3, y3, false);
                    }
                    else
                    {
                        double right = h.getRight();

                        if (h1.getRight() > right)
                        {
                            right = h1.getRight();
                        }
                        if (h2.getRight() > right)
                        {
                            right = h2.getRight();
                        }
                        right = right + 20;

                        double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
                        double x2 = 0, y2 = 0, x3 = 0, y3 = 0;

                        switch (dist.getDistributionType())
                        {
                            case DistributionEntry.TOP_DIST:
                                x0 = h.getRight() + 5;
                                y0 = h.getBottom();
                                x1 = h1.getRight() + 5;
                                y1 = h1.getTop();

                                x2 = h1.getRight() + 5;
                                y2 = h1.getBottom();
                                x3 = h2.getRight() + 5;
                                y3 = h2.getTop();
                                break;
                            case DistributionEntry.V_CENTER_DIST:
                                x0 = h1.getRight() + 5;
                                y0 = h1.getBottom();
                                x1 = h.getRight() + 5;
                                y1 = h.getTop();

                                x2 = h.getRight() + 5;
                                y2 = h.getBottom();
                                x3 = h2.getRight() + 5;
                                y3 = h2.getTop();
                                break;
                            case DistributionEntry.BOTTOM_DIST:
                                x0 = h1.getRight() + 5;
                                y0 = h1.getBottom();
                                x1 = h2.getRight();
                                y1 = h2.getTop();

                                x2 = h2.getRight() + 5;
                                y2 = h2.getBottom();
                                x3 = h.getRight() + 5;
                                y3 = h.getTop();
                                break;
                        }
                        drawPolyLine(index, right, x0, y0, x1, y1, true);
                        drawPolyLine(index + 1, right, x2, y2, x3, y3, true);
                    }
                }
                layer.draw();
            }
            else if (m_lines[index] != null)
            {
                removeLine(index, m_lines[index]);
                removeLine(index + 1, m_lines[index + 1]);
                layer.draw();
            }
        }

        private void removeLine(int index, Shape<?> line)
        {
            getOverLayer().remove(line);

            m_lines[index] = null;
        }

        private void drawPolyLine(int index, double edge, double x0, double y0, double x1, double y1, boolean vertical)
        {
            Point2DArray points;

            if (vertical)
            {
                points = new Point2DArray(new Point2D(x0, y0), new Point2D(edge, y0), new Point2D(edge, y1), new Point2D(x1, y1));
            }
            else
            {
                points = new Point2DArray(new Point2D(x0, y0), new Point2D(x0, edge), new Point2D(x1, edge), new Point2D(x1, y1));
            }
            PolyLine pline = (PolyLine) m_lines[index];

            if (pline == null)
            {
                pline = new PolyLine(points);
                pline.setStrokeWidth(m_strokeWidth);
                pline.setStrokeColor(m_strokeColor);
                pline.setDashArray(m_dashArray);
                m_lines[index] = pline;
                getOverLayer().add(pline);
            }
            else
            {
                pline.setPoints(points);
            }
        }

        private void drawHorizontalLine(AlignAndDistributeHandler handler, double pos, LinkedList<AlignAndDistributeHandler> shapes, int index)
        {
            double left = handler.getLeft();
            double right = handler.getRight();

            for (AlignAndDistributeHandler otherHandler : shapes)
            {
                double newLeft = otherHandler.getLeft();
                double newRight = otherHandler.getRight();

                if (newLeft < left)
                {
                    left = newLeft;
                }

                if (newRight > right)
                {
                    right = newRight;
                }
            }
            drawHorizontalLine(pos, left, right, index);
        }

        private void drawHorizontalLine(double pos, double left, double right, int index)
        {
            Line line = (Line) m_lines[index];
            if (line == null)
            {
                line = new Line(left, pos, right, pos);
                line.setStrokeWidth(m_strokeWidth);
                line.setStrokeColor(m_strokeColor);
                line.setDashArray(m_dashArray);
                getOverLayer().add(line);
                m_lines[index] = line;
            }
            else
            {
                line.setPoints(new Point2DArray(new Point2D(left, pos), new Point2D(right, pos)));
            }
        }

        private void drawVerticalLine(AlignAndDistributeHandler handler, double pos, LinkedList<AlignAndDistributeHandler> shapes, int index)
        {
            double top = handler.getTop();
            double bottom = handler.getBottom();

            for (AlignAndDistributeHandler otherHandler : shapes)
            {
                double newTop = otherHandler.getTop();
                double newBottom = otherHandler.getBottom();

                if (newTop < top)
                {
                    top = newTop;
                }

                if (newBottom > bottom)
                {
                    bottom = newBottom;
                }
            }
            drawVerticalLine(pos, top, bottom, index);
        }

        private void drawVerticalLine(double pos, double top, double bottom, int index)
        {
            Line line = (Line) m_lines[index];
            if (line == null)
            {
                line = new Line(pos, top, pos, bottom);
                line.setStrokeWidth(m_strokeWidth);
                line.setStrokeColor(m_strokeColor);
                line.setDashArray(m_dashArray);
                getOverLayer().add(line);
                m_lines[index] = line;
            }
            else
            {
                line.setPoints(new Point2DArray(new Point2D(pos, top), new Point2D(pos, bottom)));
            }
        }
    }
}

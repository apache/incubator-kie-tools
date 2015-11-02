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

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class DragAndDropManager
{
    private NFastStringMap<WiresShape> m_shape_color_map;

    private ImageData                  m_shapesBacking;

    public DragAndDropManager()
    {
    }

    public static class WiresShapeDragHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private DragAndDropManager m_dndManager;

        private WiresShape         m_shape;

        private WiresContainer     m_parent;

        private WiresLayer         m_layer;

        private WiresManager       m_wiresManager;

        private String             m_priorFill;

        private double             m_priorAlpha;

        public WiresShapeDragHandler(DragAndDropManager dndManager, WiresShape shape, WiresManager wiresManager)
        {
            m_dndManager = dndManager;
            m_shape = shape;
            m_wiresManager = wiresManager;
            m_layer = m_wiresManager.getLayer();
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_dndManager.m_shape_color_map = new NFastStringMap<WiresShape>();
            m_dndManager.m_shapesBacking = m_wiresManager.getMagnetManager().drawShapesToBacking(m_layer.getChildShapes(), m_layer.getLayer().getScratchPad(), m_shape, m_dndManager.m_shape_color_map);
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            String color = m_wiresManager.getMagnetManager().findColorAtPoint(m_dndManager.m_shapesBacking, event.getX(), event.getY());
            WiresContainer parent = null;
            if (color != null)
            {
                parent = m_dndManager.m_shape_color_map.get(color);
            }
            if (parent != m_parent)
            {
                if (m_parent != null && m_parent instanceof WiresShape)
                {

                    ((WiresShape) m_parent).getPath().setFillColor(m_priorFill);
                    ((WiresShape) m_parent).getPath().setAlpha(m_priorAlpha);
                }
                if (parent != null && parent instanceof WiresShape)
                {
                    m_priorFill = ((WiresShape) parent).getPath().getFillColor();
                    m_priorAlpha = ((WiresShape) parent).getPath().getAlpha();
                    ((WiresShape) parent).getPath().setFillColor("#CCCCCC");
                    ((WiresShape) parent).getPath().setAlpha(0.5);
                }
            }
            m_parent = parent;
            m_layer.getLayer().batch();
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            addShapeToParent();
        }

        @Override
        public void onNodeMouseDown(NodeMouseDownEvent event)
        {
            m_parent = m_shape.getParent();
        }

        @Override
        public void onNodeMouseUp(NodeMouseUpEvent event)
        {
            if (m_parent != m_shape.getParent())
            {
                addShapeToParent();
            }
        }

        private void addShapeToParent()
        {
            Point2D absLoc = m_shape.getGroup().getAbsoluteLocation();
            m_shape.removeFromParent();

            if (m_parent != null && m_parent instanceof WiresShape)
            {
                ((WiresShape) m_parent).getPath().setFillColor(m_priorFill);
                ((WiresShape) m_parent).getPath().setAlpha(m_priorAlpha);
            }
            if (m_parent == null)
            {
                m_parent = m_layer;
            }
            if (m_parent == m_layer)
            {
                m_shape.getGroup().setLocation(absLoc);
            }
            else
            {
                Point2D trgAbsOffset = m_parent.getContainer().getAbsoluteLocation();
                
                m_shape.getGroup().setX(absLoc.getX() - trgAbsOffset.getX()).setY(absLoc.getY() - trgAbsOffset.getY());
            }
            m_parent.add(m_shape);
            
            m_layer.getLayer().batch();

            m_parent = null;
            m_dndManager.m_shapesBacking = null;
            m_dndManager.m_shape_color_map = null;
        }
    }
}

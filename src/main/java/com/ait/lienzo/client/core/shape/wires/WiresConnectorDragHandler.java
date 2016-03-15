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
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArray;
import com.ait.tooling.nativetools.client.util.Console;

public class WiresConnectorDragHandler implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
{
    private WiresConnector m_connector;

    private WiresLayer     m_layer;

    private WiresManager   m_wiresManager;

    private NFastDoubleArray m_startPoints;

    public WiresConnectorDragHandler(WiresConnector shape, WiresManager wiresManager)
    {
        m_connector = shape;
        m_wiresManager = wiresManager;
        m_layer = m_wiresManager.getLayer();
    }

    @Override
    public void onNodeDragStart(NodeDragStartEvent event)
    {
        IControlHandleList handles = m_connector.getPointHandles();

        m_startPoints = new NFastDoubleArray();
        for (int i = 0; i < handles.size(); i++)
        {
            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            m_startPoints.push(prim.getX());
            m_startPoints.push(prim.getY());
        }
    }

    @Override
    public void onNodeDragMove(NodeDragMoveEvent event)
    {
        IControlHandleList handles = m_connector.getPointHandles();

        int dx = event.getDragContext().getDx();
        int dy = event.getDragContext().getDy();

        for (int i = 0, j = 0; i < handles.size(); i++, j += 2)
        {
            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            prim.setX( m_startPoints.get(j) + dx);
            prim.setY( m_startPoints.get(j+1) + dy);
        }

        m_layer.getLayer().batch();
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event)
    {

        m_connector.getDecoratableLine().setX(0);
        m_connector.getDecoratableLine().setY(0);

        int dx = event.getDragContext().getDx();
        int dy = event.getDragContext().getDy();

        Point2DArray points = m_connector.getDecoratableLine().getLine().getPoint2DArray();
        IControlHandleList handles = m_connector.getPointHandles();

        for (int i = 0, j = 0; i < handles.size(); i++, j += 2)
        {
            Point2D p = points.get(i);
            p.setX( p.getX() + dx );
            p.setY( p.getY() + dy );

            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            prim.setX( m_startPoints.get(j) + dx);
            prim.setY( m_startPoints.get(j+1) + dy);
        }

        m_connector.getDecoratableLine().refresh();

        m_layer.getLayer().batch();

        m_startPoints = null;
    }


}

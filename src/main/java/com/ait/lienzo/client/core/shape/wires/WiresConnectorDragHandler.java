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

public class WiresConnectorDragHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
{
    private WiresConnector m_connector;

    // private WiresLayer     m_layer;

    // private WiresManager   m_wiresManager;

    private int            start_x;

    private int            start_y;

    public WiresConnectorDragHandler(WiresConnector shape, WiresManager wiresManager)
    {
        m_connector = shape;
        // m_wiresManager = wiresManager;
        //m_layer = m_wiresManager.getLayer();
    }

    @Override
    public void onNodeDragStart(NodeDragStartEvent event)
    {

        start_x = event.getX();
        start_y = event.getY();

    }

    @Override
    public void onNodeDragMove(NodeDragMoveEvent event)
    {

    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event)
    {

        final int xDiff = event.getX() - start_x;
        final int yDiff = event.getY() - start_y;

        WiresConnection headConnection = m_connector.getHeadConnection();
        final double hx = headConnection.m_point.getX() + xDiff;
        final double hy = headConnection.m_point.getY() + yDiff;
        headConnection.move(hx, hy);

        WiresConnection tailConnection = m_connector.getTailConnection();
        final double tx = tailConnection.m_point.getX() + xDiff;
        final double ty = tailConnection.m_point.getY() + yDiff;
        tailConnection.move(tx, ty);

        m_connector.getDecoratableLine().setX(m_connector.getDecoratableLine().getX() - xDiff);
        m_connector.getDecoratableLine().setY(m_connector.getDecoratableLine().getY() - yDiff);

    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event)
    {
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event)
    {

    }

}

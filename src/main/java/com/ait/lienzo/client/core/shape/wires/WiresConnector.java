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

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.shared.core.types.ArrowEnd;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.POINT;

public class WiresConnector
{
    interface WiresConnectorHandler extends NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler, NodeMouseClickHandler, NodeMouseDoubleClickHandler
    {

        WiresConnectorControl getControl();

    }

    private WiresConnection                       m_headConnection;

    private WiresConnection                       m_tailConnection;

    private IControlHandleList                    m_pointHandles;

    private HandlerRegistrationManager            m_HandlerRegistrationManager;

    private AbstractDirectionalMultiPointShape<?> m_line;

    private MultiPathDecorator                    m_headDecorator;

    private MultiPathDecorator                    m_tailDecorator;

    private Group                                 m_group;

    private IConnectionAcceptor                   m_connectionAcceptor = IConnectionAcceptor.ALL;

    public WiresConnector(AbstractDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator)
    {
        m_line = line;
        m_headDecorator = headDecorator;
        m_tailDecorator = tailDecorator;

        setHeadConnection(new WiresConnection(this, m_headDecorator.getPath(), ArrowEnd.HEAD));
        setTailConnection(new WiresConnection(this, m_tailDecorator.getPath(), ArrowEnd.TAIL));

        m_line.setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        m_group = new Group();
        m_group.add(m_line);
        m_group.add(m_headDecorator.getPath());
        m_group.add(m_tailDecorator.getPath());

        // these are not draggable, only the group may or may not be draggable, depending if the line is connected or not
        m_line.setDraggable(false);
        m_headDecorator.getPath().setDraggable(false);
        m_tailDecorator.getPath().setDraggable(false);

        // The Line is only draggable if both Connections are unconnected
        setDraggable();
    }

    public WiresConnector(WiresMagnet headMagnet, WiresMagnet tailMagnet, AbstractDirectionalMultiPointShape<?> line, MultiPathDecorator headDecorator, MultiPathDecorator tailDecorator)
    {
        this(line, headDecorator, tailDecorator);
        setHeadMagnet(headMagnet);
        setTailMagnet(tailMagnet);
    }

    public WiresConnector setHeadMagnet(WiresMagnet headMagnet)
    {
        if (null != headMagnet)
        {
            m_headConnection.setMagnet(headMagnet);
        }
        return this;
    }

    public WiresConnector setTailMagnet(WiresMagnet tailMagnet)
    {
        if (null != tailMagnet)
        {
            m_tailConnection.setMagnet(tailMagnet);
        }
        return this;
    }

    public IConnectionAcceptor getConnectionAcceptor()
    {
        return m_connectionAcceptor;
    }

    public void setConnectionAcceptor(IConnectionAcceptor connectionAcceptor)
    {
        m_connectionAcceptor = connectionAcceptor;
    }

    public void setWiresConnectorHandler( final HandlerRegistrationManager m_registrationManager,
                                          final WiresConnectorHandler handler ) {

        final Group group = getGroup();

        m_registrationManager.register(group.addNodeDragStartHandler(handler));

        m_registrationManager.register(group.addNodeDragMoveHandler(handler));

        m_registrationManager.register(group.addNodeDragEndHandler(handler));
    }

    public void destroy()
    {
        removeHandlers();
        removeFromLayer();
    }

    private void removeHandlers()
    {

        if (null != m_HandlerRegistrationManager)
        {
            m_HandlerRegistrationManager.removeHandler();
        }

    }

    public void addToLayer(Layer layer)
    {
        layer.add(m_group);
    }

    public void removeFromLayer()
    {
        m_group.removeFromParent();
    }

    public WiresConnection getHeadConnection()
    {
        return m_headConnection;
    }

    public void setHeadConnection(WiresConnection headConnection)
    {
        m_headConnection = headConnection;

    }

    public void setDraggable()
    {
        // The line can only be dragged if both Magnets are null
        m_group.setDraggable(isDraggable());
    }

    private boolean isDraggable()
    {
        return getHeadConnection().getMagnet() == null && getTailConnection().getMagnet() == null;
    }

    public WiresConnection getTailConnection()
    {
        return m_tailConnection;
    }

    public void setTailConnection(WiresConnection tailConnection)
    {
        m_tailConnection = tailConnection;
    }

    public void setPointHandles(IControlHandleList pointHandles)
    {
        m_pointHandles = pointHandles;
    }

    public AbstractDirectionalMultiPointShape<?> getLine()
    {
        return m_line;
    }

    public MultiPathDecorator getHeadDecorator()
    {
        return m_headDecorator;
    }

    public MultiPathDecorator getTailDecorator()
    {
        return m_tailDecorator;
    }

    public MultiPath getHead()
    {
        return m_headDecorator.getPath();
    }

    public MultiPath getTail()
    {
        return m_tailDecorator.getPath();
    }

    public Group getGroup()
    {
        return m_group;
    }

    public String uuid()
    {
        return getGroup().uuid();
    }

    public void destroyPointHandles()
    {
        m_pointHandles.destroy();
        m_pointHandles = null;
    }

    public IControlHandleList getPointHandles()
    {
        if (m_pointHandles == null)
        {
            m_pointHandles = m_line.getControlHandles(POINT).get(POINT);
        }
        return m_pointHandles;
    }



    static class WiresConnectorHandlerImpl implements WiresConnectorHandler
    {
        private final WiresConnectorControl m_control;

        private final WiresConnector        m_connector;

        WiresConnectorHandlerImpl(WiresConnector connector, WiresManager wiresManager)
        {
            this.m_control = wiresManager.getControlFactory().newConnectorControl(connector, wiresManager);
            this.m_connector = connector;
            init();
        }

        private void init()
        {
            if (m_connector.m_HandlerRegistrationManager != null)
            {
                m_connector.m_HandlerRegistrationManager.removeHandler();
            }

            m_connector.m_HandlerRegistrationManager = new HandlerRegistrationManager();

            m_connector.m_HandlerRegistrationManager.register(m_connector.getLine().addNodeMouseClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getLine().addNodeMouseDoubleClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getHead().addNodeMouseClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getHead().addNodeMouseDoubleClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getTail().addNodeMouseClickHandler(this));
            m_connector.m_HandlerRegistrationManager.register(m_connector.getTail().addNodeMouseDoubleClickHandler(this));
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            this.m_control.dragStart(event.getDragContext());
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event)
        {
            this.m_control.dragMove(event.getDragContext());
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            this.m_control.dragEnd(event.getDragContext());
        }


        @Override
        public void onNodeMouseClick(NodeMouseClickEvent event)
        {

            if (m_connector.getPointHandles().isVisible())
            {
                this.m_control.hideControlPoints();
            }
            else if (((Node<?> ) event.getSource()).getParent() == m_connector.getGroup() )
            {
                this.m_control.showControlPoints();
            }
        }

        @Override
        public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event)
        {

            if (m_connector.getPointHandles().isVisible())
            {
                this.m_control.addControlPoint(event.getX(), event.getY());
            }
        }

        public WiresConnectorControl getControl()
        {
            return m_control;
        }

    }

}

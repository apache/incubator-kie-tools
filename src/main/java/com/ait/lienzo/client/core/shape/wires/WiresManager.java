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

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresControlFactoryImpl;
import com.ait.lienzo.client.core.types.OnLayerBeforeDraw;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

public final class WiresManager
{
    private static final NFastStringMap<WiresManager>        MANAGER_MAP           = new NFastStringMap<WiresManager>();

    private final MagnetManager                              m_magnetManager       = new MagnetManager();

    private final SelectionManager                           m_selectionManager    = new SelectionManager();

    private final AlignAndDistribute                         m_index;

    private final NFastStringMap<WiresShape>                 m_shapesMap           = new NFastStringMap<WiresShape>();

    private final NFastStringMap<HandlerRegistrationManager> m_shapeHandlersMap    = new NFastStringMap<HandlerRegistrationManager>();

    private final NFastArrayList<WiresConnector>             m_connectorList       = new NFastArrayList<WiresConnector>();

    private final WiresLayer                                 m_layer;

    private WiresControlFactory                              m_controlFactory;

    private IConnectionAcceptor                              m_connectionAcceptor  = IConnectionAcceptor.ALL;

    private IContainmentAcceptor                             m_containmentAcceptor = IContainmentAcceptor.ALL;

    private IDockingAcceptor                                 m_dockingAcceptor     = IDockingAcceptor.NONE;

    public static final WiresManager get(Layer layer)
    {
        String uuid = layer.uuid();

        WiresManager manager = MANAGER_MAP.get(uuid);

        if (null != manager)
        {
            return manager;
        }
        manager = new WiresManager(layer);

        MANAGER_MAP.put(uuid, manager);

        return manager;
    }

    private WiresManager(final Layer layer)
    {
        m_layer = new WiresLayer(layer);
        layer.setOnLayerBeforeDraw(new LinePreparer(this));

        m_index = new AlignAndDistribute(layer);
    }

    public static class LinePreparer implements OnLayerBeforeDraw
    {
        private WiresManager m_wiresManager;

        public LinePreparer(WiresManager wiresManager)
        {
            m_wiresManager = wiresManager;
        }

        @Override
        public boolean onLayerBeforeDraw(Layer layer)
        {
            // this is necessary as the line decorator cannot be determined until line parse has been attempted
            // as this is expensive it's delayed until the last minute before draw. As drawing order is not guaranteed
            // this method is used to force a parse on any line that has been refreshed. Refreshed means it's points where
            // changed and thus will be reparsed.
            for (WiresConnector c : m_wiresManager.getConnectorList())
            {
                // Iterate each refreshed line and get the new points for the decorators
                if (c.getLine().getPathPartList().size() < 1)
                {
                    // only do this for lines that have had refresh called
                    AbstractDirectionalMultiPointShape<?> line = c.getLine();

                    final boolean prepared = line.isPathPartListPrepared(c.getLine().getAttributes());

                    if (!prepared)
                    {
                        return false;
                    }

                    Point2DArray points = line.getPoint2DArray();
                    Point2D p0 = points.get(0);
                    Point2D p1 = line.getHeadOffsetPoint();
                    Point2DArray headPoints = new Point2DArray(p1, p0);
                    c.getHeadDecorator().draw(headPoints);

                    p0 = points.get(points.size() - 1);
                    p1 = line.getTailOffsetPoint();
                    Point2DArray tailPoints = new Point2DArray(p1, p0);
                    c.getTailDecorator().draw(tailPoints);

                }
            }

            return true;
        }

    }

    public MagnetManager getMagnetManager()
    {
        return m_magnetManager;
    }

    public SelectionManager getSelectionManager()
    {
        return m_selectionManager;
    }

    public WiresShapeControl register(final WiresShape shape)
    {
        return register(shape, true);
    }

    public WiresShapeControl register(final WiresShape shape, final boolean addIntoIndex)
    {
        shape.setContainmentAcceptor(m_containmentAcceptor);

        shape.setDockingAcceptor(m_dockingAcceptor);

        final WiresShape.WiresShapeHandler handler = new WiresShape.WiresShapeHandlerImpl(shape, this);

        final WiresDockingAndContainmentControl dockingAndContainmentControl = getControlFactory().newDockingAndContainmentControl(shape, this);
        handler.setDockingAndContainmentControl(dockingAndContainmentControl);

        if (addIntoIndex)
        {
            // Shapes added to the align and distribute index.
            final AlignAndDistributeControl alignAndDistrControl = addToIndex(shape);
            handler.setAlignAndDistributeControl(alignAndDistrControl);

            shape.addWiresResizeEndHandler(new WiresResizeEndHandler()
            {
                @Override
                public void onShapeResizeEnd(WiresResizeEndEvent event)
                {
                    removeFromIndex(shape);
                    final AlignAndDistributeControl controls = addToIndex(shape);
                    handler.setAlignAndDistributeControl(controls);
                }
            });
        }

        final HandlerRegistrationManager registrationManager = createHandlerRegistrationManager();

        shape.addWiresShapeHandler( registrationManager, handler );

        // Shapes added to the canvas layer by default.
        getLayer().add(shape);

        final String uuid = shape.uuid();
        m_shapesMap.put(uuid, shape);
        m_shapeHandlersMap.put(uuid, registrationManager);

        return handler.getControl();
    }

    public void deregister(final WiresShape shape)
    {
        final String uuid = shape.uuid();
        removeHandlers(uuid);
        removeFromIndex(shape);
        shape.destroy();
        getLayer().remove(shape);
        m_shapesMap.remove(uuid);
    }

    public WiresConnectorControl register(final WiresConnector connector)
    {
        connector.setConnectionAcceptor(m_connectionAcceptor);

        final String uuid = connector.uuid();

        final HandlerRegistrationManager m_registrationManager = createHandlerRegistrationManager();

        final WiresConnector.WiresConnectorHandler handler = new WiresConnector.WiresConnectorHandlerImpl(connector, this);

        connector.setWiresConnectorHandler( m_registrationManager, handler );

        getConnectorList().add(connector);
        m_shapeHandlersMap.put(uuid, m_registrationManager);

        connector.addToLayer(getLayer().getLayer());

        return handler.getControl();
    }

    public void deregister(final WiresConnector connector)
    {
        connector.removeFromLayer();
        final String uuid = connector.uuid();
        removeHandlers(uuid);
        connector.destroy();
        getConnectorList().remove(connector);
    }

    public WiresLayer getLayer()
    {
        return m_layer;
    }

    public WiresShape getShape(final String uuid)
    {
        return m_shapesMap.get(uuid);
    }

    private AlignAndDistributeControl addToIndex(final WiresShape shape)
    {
        return m_index.addShape(shape.getGroup());
    }

    private void removeFromIndex(final WiresShape shape)
    {
        m_index.removeShape(shape.getGroup());
    }

    public AlignAndDistribute getAlignAndDistribute()
    {
        return m_index;
    }

    public void setWiresControlFactory(final WiresControlFactory factory)
    {
        this.m_controlFactory = factory;
    }

    public WiresControlFactory getControlFactory()
    {
        if (null == m_controlFactory)
        {
            m_controlFactory = new WiresControlFactoryImpl();
        }
        return m_controlFactory;
    }

    public IConnectionAcceptor getConnectionAcceptor()
    {
        return m_connectionAcceptor;
    }

    public void setConnectionAcceptor(IConnectionAcceptor connectionAcceptor)
    {
        m_connectionAcceptor = connectionAcceptor;
    }

    public IContainmentAcceptor getContainmentAcceptor()
    {
        return m_containmentAcceptor;
    }

    public void setContainmentAcceptor(IContainmentAcceptor containmentAcceptor)
    {
        if (containmentAcceptor == null)
        {
            throw new IllegalArgumentException("ContainmentAcceptor cannot be null");
        }
        m_containmentAcceptor = containmentAcceptor;
    }

    public void setDockingAcceptor(IDockingAcceptor dockingAcceptor)
    {
        if (dockingAcceptor == null)
        {
            throw new IllegalArgumentException("DockingAcceptor cannot be null");
        }
        this.m_dockingAcceptor = dockingAcceptor;
    }

    private void removeHandlers(final String uuid)
    {
        final HandlerRegistrationManager m_registrationManager = m_shapeHandlersMap.get(uuid);
        if (null != m_registrationManager)
        {
            m_registrationManager.removeHandler();
        }
    }

    public NFastArrayList<WiresConnector> getConnectorList()
    {
        return m_connectorList;
    }

    HandlerRegistrationManager createHandlerRegistrationManager()
    {
        return new HandlerRegistrationManager();
    }

}

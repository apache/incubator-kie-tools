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

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Decorator;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.lienzo.client.core.shape.wires.AlignAndDistribute.AlignAndDistributeHandler;

public final class WiresManager
{
    private static final NFastStringMap<WiresManager> MANAGER_MAP           = new NFastStringMap<WiresManager>();

    private final MagnetManager                       m_magnetManager       = new MagnetManager();

    private final AlignAndDistribute                  m_index;

    private final NFastStringMap<WiresShape>          m_shapesMap           = new NFastStringMap<WiresShape>();

    private final NFastArrayList<WiresShape>          m_shapesList          = new NFastArrayList<WiresShape>();

    private final WiresLayer                          m_layer;

    private IConnectionAcceptor                       m_connectionAcceptor  = IConnectionAcceptor.ALL;

    private IContainmentAcceptor                      m_containmentAcceptor = IContainmentAcceptor.ALL;

    private IDockingAcceptor                          m_dockingAcceptor     = IDockingAcceptor.ALL;

    public static final WiresManager get(final Layer layer)
    {
        final String uuid = layer.uuid();

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

        m_index = new AlignAndDistribute(layer);
    }

    public MagnetManager getMagnetManager()
    {
        return m_magnetManager;
    }

    public WiresShape createShape(final MultiPath path)
    {
        WiresShape shape = new WiresShape(path, new WiresLayoutContainer(), this);

        return registerShape(shape);
    }

    public WiresShape registerShape(final WiresShape shape) {
        final Group group = shape.getGroup();
        //final MultiPath path = shape.getPath();

        shape.setContainmentAcceptor(m_containmentAcceptor);

        shape.setDockingAcceptor(m_dockingAcceptor);
        m_shapesMap.put(shape.getGroup().uuid(), shape);

        WiresShapeDragHandler handler = new WiresShapeDragHandler(shape, this);

        group.addNodeMouseDownHandler(handler);

        group.addNodeMouseUpHandler(handler);

        group.setDragConstraints(handler);

        group.addNodeDragEndHandler(handler);

        // Shapes added to the canvas layer by default.
        getLayer().add(shape);

        // Shapes added to the align and distribute index by default.
        AlignAndDistributeHandler alignAndDistrHandler = addToIndex(shape);

        handler.setAlignAndDistributeHandler(alignAndDistrHandler);
        handler.setDockingAndContainmentHandler(new DockingAndContainmentHandler(shape, this));

        return shape;
    }

    public WiresManager deregisterShape(final WiresShape shape) {
        removeFromIndex(shape);
        shape.removeHandlers();
        getShapes().remove(shape);
        getLayer().remove(shape);
        return this;
    }

    public WiresConnector createConnector(AbstractDirectionalMultiPointShape<?> line, Decorator<?> head, Decorator<?> tail)
    {
        WiresConnector connector = new WiresConnector(line, head, tail, this);
        registerConnector(connector);
        return connector;
    }

    public WiresConnector createConnector(WiresMagnet headMagnet, WiresMagnet tailMagnet, AbstractDirectionalMultiPointShape<?> line, Decorator<?> head, Decorator<?> tail)
    {
        WiresConnector connector = new WiresConnector(headMagnet, tailMagnet, line, head, tail, this);
        registerConnector(connector);
        return connector;
    }

    public WiresConnector createConnector(WiresMagnet headMagnet, WiresMagnet tailMagnet, AbstractDirectionalMultiPointShape<?> line)
    {
        WiresConnector connector = createConnector(headMagnet, tailMagnet, line, null, null);
        registerConnector(connector);
        return connector;
    }

    public WiresManager registerConnector(WiresConnector connector) {
        connector.setConnectionAcceptor(m_connectionAcceptor);

        WiresConnectorDragHandler handler = new WiresConnectorDragHandler(connector, this);

        connector.getDecoratableLine().addNodeDragStartHandler(handler);

        connector.getDecoratableLine().addNodeDragMoveHandler(handler);

        connector.getDecoratableLine().addNodeDragEndHandler(handler);

        getLayer().getLayer().add(connector.getDecoratableLine());

        return this;
    }

    public WiresManager deregisterConnector(WiresConnector connector) {
        removeFromIndex(connector);
        connector.removeHandlers();
        getLayer().getLayer().remove(connector.getDecoratableLine());
        return this;
    }

    public WiresLayer getLayer()
    {
        return m_layer;
    }

    public WiresShape getShape(final String uuid)
    {
        return m_shapesMap.get(uuid);
    }

    public NFastArrayList<WiresShape> getShapes()
    {
        return m_shapesList;
    }

    protected AlignAndDistributeHandler addToIndex(final WiresShape shape)
    {
        return m_index.addShape(shape.getGroup());
    }

    protected void removeFromIndex(final WiresShape shape)
    {
        m_index.removeShape(shape.getGroup());
    }

    protected void addToIndex(final WiresConnector connector)
    {
        m_index.addShape(connector.getLine());
    }

    protected void removeFromIndex(final WiresConnector connector)
    {
        m_index.removeShape(connector.getLine());
    }

    public void createMagnets(final WiresShape shape)
    {
        shape.setMagnets(m_magnetManager.createMagnets(shape.getPath(), shape.getGroup(), Geometry.getCardinalIntersects(shape.getPath()), shape));
    }

    public AlignAndDistribute getAlignAndDistribute()
    {
        return m_index;
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
        m_containmentAcceptor = containmentAcceptor;
    }

    public void setDockingAcceptor(IDockingAcceptor dockingAcceptor)
    {
        this.m_dockingAcceptor = dockingAcceptor;
    }
}

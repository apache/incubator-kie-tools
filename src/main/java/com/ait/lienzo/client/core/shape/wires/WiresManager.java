/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.client.core.shape.EndDecorator;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.lienzo.client.core.shape.wires.DragAndDropManager.WiresShapeDragHandler;

public class WiresManager
{
    private static final WiresManager m_instance = new WiresManager();

    public static final WiresManager getInstance()
    {
        return m_instance;
    }

    private final MagnetManager        m_magnetManager = MagnetManager.getInstance();

    private AlignAndDistribute         m_index;

    private NFastStringMap<WiresShape> m_shapesMap     = new NFastStringMap<WiresShape>();

    private NFastArrayList<WiresShape> m_shapesList    = new NFastArrayList<WiresShape>();

    private WiresLayer                 m_layer;

    protected WiresManager()
    {
    }

    public void init(final Layer layer)
    {
        m_layer = new WiresLayer(layer);

        m_index = new AlignAndDistribute(layer);
    }

    public WiresShape createShape(final MultiPath path)
    {
        path.setDraggable(false);

        Group group = new Group();

        group.add(path);

        group.setDraggable(true);

        group.setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        WiresShape shape = new WiresShape(path, group, this);

        m_shapesMap.put(shape.getGroup().uuid(), shape);

        WiresShapeDragHandler handler = new WiresShapeDragHandler(DragAndDropManager.getInstance(), shape, this);

        group.addNodeMouseDownHandler(handler);

        group.addNodeMouseUpHandler(handler);

        group.addNodeDragStartHandler(handler);

        group.addNodeDragMoveHandler(handler);

        group.addNodeDragEndHandler(handler);

        return shape;
    }

    public Connector createConnector(Magnet headMagnet, Magnet tailMagnet,  AbstractDirectionalMultiPointShape<?> line, EndDecorator<?> head, EndDecorator<?> tail)
    {
        return new Connector(headMagnet, tailMagnet, line, head, tail, this);
    }

    public Connector createConnector(Magnet headMagnet, Magnet tailMagnet, AbstractDirectionalMultiPointShape<?> line)
    {
        return createConnector(headMagnet, tailMagnet, line, null, null);
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

    public void addToIndex(final WiresShape shape)
    {
        m_index.addShape(shape.getGroup());
    }

    public void removeFromIndex(final WiresShape shape)
    {
        m_index.removeShape(shape.getGroup());
    }

    public void createMagnets(final WiresShape shape)
    {
        shape.setMagnets(m_magnetManager.createMagnets(shape.getPath(), shape.getGroup(), Geometry.getCardinalIntersects(shape.getPath()), shape));
    }
}

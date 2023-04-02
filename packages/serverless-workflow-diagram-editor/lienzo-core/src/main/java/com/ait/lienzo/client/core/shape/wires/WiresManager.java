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

package com.ait.lienzo.client.core.shape.wires;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresControlFactoryImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresHandlerFactoryImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import com.ait.lienzo.client.core.types.OnLayerBeforeDraw;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.collection.NFastStringMap;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import elemental2.core.JsArray;
import jsinterop.base.Js;

import static com.ait.lienzo.client.core.util.JsInteropUtils.toValuesJsArray;

public final class WiresManager {

    private static final Map<String, WiresManager> MANAGER_MAP = new HashMap<>();

    private final MagnetManager m_magnetManager = new MagnetManager();

    private final AlignAndDistribute m_index;

    private final NFastStringMap<WiresShape> m_shapesMap = new NFastStringMap<>();

    private final NFastStringMap<HandlerRegistrationManager> m_shapeHandlersMap = new NFastStringMap<>();

    private final NFastArrayList<WiresConnector> m_connectorList = new NFastArrayList<>();

    private final WiresLayer m_layer;

    private WiresControlFactory m_controlFactory;

    private WiresHandlerFactory m_wiresHandlerFactory;

    private ILocationAcceptor m_locationAcceptor = ILocationAcceptor.ALL;

    private IConnectionAcceptor m_connectionAcceptor = IConnectionAcceptor.ALL;

    private IContainmentAcceptor m_containmentAcceptor = IContainmentAcceptor.ALL;

    private IControlPointsAcceptor m_controlPointsAcceptor = IControlPointsAcceptor.ALL;

    private IDockingAcceptor m_dockingAcceptor = IDockingAcceptor.NONE;

    private ILineSpliceAcceptor m_lineSpliceAcceptor = ILineSpliceAcceptor.ALL;

    private SelectionManager m_selectionManager;

    private WiresDragHandler m_handler;

    private boolean m_spliceEnabled;

    private WiresEventHandlers m_wiresEventHandlers;

    public static final WiresManager get(Layer layer) {
        final String uuid = layer.uuid();

        WiresManager manager = MANAGER_MAP.get(layer.uuid());

        if (null != manager) {
            return manager;
        }
        manager = new WiresManager(layer);

        MANAGER_MAP.put(uuid, manager);

        return manager;
    }

    public static void remove(Layer layer) {
        remove(get(layer));
    }

    public static void remove(WiresManager manager) {
        final String uuid = manager.getLayer().getLayer().uuid();
        manager.destroy();
        MANAGER_MAP.remove(uuid);
    }

    private WiresManager(final Layer layer) {
        m_layer = new WiresLayer(layer);
        m_layer.setWiresManager(this);
        layer.setOnLayerBeforeDraw(new LinePreparer(this));

        m_index = new AlignAndDistribute(layer);
        m_handler = null;
        m_controlFactory = new WiresControlFactoryImpl();
        m_wiresHandlerFactory = new WiresHandlerFactoryImpl();

        m_wiresEventHandlers = new WiresEventHandlers(layer.getViewport().getElement());
    }

    public SelectionManager enableSelectionManager() {
        if (m_selectionManager == null) {
            m_selectionManager = new SelectionManager(this);
        }
        return m_selectionManager;
    }

    public WiresEventHandlers getWiresEventHandlers() {
        return m_wiresEventHandlers;
    }

    public boolean isSpliceEnabled() {
        return m_spliceEnabled;
    }

    public void setSpliceEnabled(boolean spliceEnabled) {
        m_spliceEnabled = spliceEnabled;
    }

    public static class LinePreparer implements OnLayerBeforeDraw {

        private WiresManager m_wiresManager;

        public LinePreparer(WiresManager wiresManager) {
            m_wiresManager = wiresManager;
        }

        @Override
        public boolean onLayerBeforeDraw(Layer layer) {
            // this is necessary as the line decorator cannot be determined until line parse has been attempted
            // as this is expensive it's delayed until the last minute before draw. As drawing order is not guaranteed
            // this method is used to force a parse on any line that has been refreshed. Refreshed means it's points where
            // changed and thus will be reparsed.
            //for (WiresConnector c : )
            NFastArrayList<WiresConnector> list = m_wiresManager.getConnectorList();
            for (int i = 0, size = list.size(); i < size; i++) {
                WiresConnector c = list.get(i);
                if (WiresConnector.updateHeadTailForRefreshedConnector(c)) {
                    return false;
                }
            }

            return true;
        }
    }

    public MagnetManager getMagnetManager() {
        return m_magnetManager;
    }

    public SelectionManager getSelectionManager() {
        return m_selectionManager;
    }

    public WiresShapeControl register(final WiresShape shape) {
        return register(shape, true);
    }

    public WiresShapeControl register(final WiresShape shape,
                                      final boolean addIntoIndex) {
        return register(shape, addIntoIndex, true);
    }

    public WiresShapeControl register(final WiresShape shape,
                                      final boolean addIntoIndex,
                                      final boolean addHandlers) {
        shape.setWiresManager(this);

        final WiresShapeControl control = getControlFactory().newShapeControl(shape, this);
        shape.setControl(control);

        final HandlerRegistrationManager registrationManager = createHandlerRegistrationManager();

        if (addHandlers) {
            final WiresShapeHandler handler =
                    getWiresHandlerFactory()
                            .newShapeHandler(shape,
                                             getControlFactory().newShapeHighlight(this),
                                             this);
            addWiresShapeHandler(shape, registrationManager, handler);
        }

        if (addIntoIndex) {
            addAlignAndDistributeHandlers(shape, registrationManager);
        }

        // Shapes added to the canvas layer by default.
        getLayer().add(shape);

        final String uuid = shape.uuid();
        m_shapesMap.put(uuid, shape);
        m_shapeHandlersMap.put(uuid, registrationManager);

        return control;
    }

    private void addAlignAndDistributeHandlers(final WiresShape shape,
                                               final HandlerRegistrationManager registrationManager) {
        // Shapes added to the align and distribute index.
        // Treat a resize like a drag.
        final AlignAndDistributeControl alignAndDistrControl = addToIndex(shape);
        shape.getControl().setAlignAndDistributeControl(alignAndDistrControl);

        registrationManager.register(shape.addWiresResizeStartHandler(event -> alignAndDistrControl.dragStart()));

        registrationManager.register(shape.addWiresMoveHandler(event -> alignAndDistrControl.refresh(true, true)));

        registrationManager.register(shape.addWiresResizeEndHandler(event -> alignAndDistrControl.dragEnd()));

        registrationManager.register(shape.addWiresResizeStepHandler(event -> {
            Point2D point = new Point2D(event.getX(), event.getY());
            alignAndDistrControl.dragAdjust(point);
        }));
    }

    public static void addWiresShapeHandler(final WiresShape shape,
                                            final HandlerRegistrationManager registrationManager,
                                            final WiresShapeHandler handler) {
        registrationManager.register(shape.getPath().addNodeMouseClickHandler(handler));
        registrationManager.register(shape.getPath().addNodeMouseDownHandler(handler));
        registrationManager.register(shape.getPath().addNodeMouseUpHandler(handler));
        registrationManager.register(shape.getGroup().addNodeDragEndHandler(handler));
        shape.getGroup().setDragConstraints(handler);
    }

    public void deregister(final WiresShape shape) {
        final String uuid = shape.uuid();
        deselect(shape);
        removeHandlers(uuid);
        shape.destroy();
        removeFromIndex(shape);
        getLayer().remove(shape);
        m_shapesMap.remove(uuid);
    }

    public WiresConnectorControl register(final WiresConnector connector) {
        return registerConnector(connector);
    }

    public WiresConnectorControl registerConnector(final WiresConnector connector) {
        final WiresConnectorControl control = getControlFactory().newConnectorControl(connector, this);
        connector.setControl(control);

        getConnectorList().add(connector);

        connector.addToLayer(getLayer().getLayer());

        return control;
    }

    public void addHandlers(final WiresConnector connector) {
        final String uuid = connector.uuid();
        final HandlerRegistrationManager m_registrationManager = createHandlerRegistrationManager();

        final WiresConnectorHandler handler = getWiresHandlerFactory().newConnectorHandler(connector, this);

        m_registrationManager.register(connector.getHead().addNodeMouseClickHandler(handler));
        m_registrationManager.register(connector.getHead().addNodeMouseEnterHandler(handler));
        m_registrationManager.register(connector.getHead().addNodeMouseMoveHandler(handler));
        m_registrationManager.register(connector.getHead().addNodeMouseExitHandler(handler));
        m_registrationManager.register(connector.getTail().addNodeMouseClickHandler(handler));

        m_registrationManager.register(connector.getTail().addNodeMouseEnterHandler(handler));
        m_registrationManager.register(connector.getTail().addNodeMouseMoveHandler(handler));
        m_registrationManager.register(connector.getTail().addNodeMouseExitHandler(handler));

        m_registrationManager.register(connector.getGroup().addNodeDragStartHandler(handler));
        m_registrationManager.register(connector.getGroup().addNodeDragMoveHandler(handler));
        m_registrationManager.register(connector.getGroup().addNodeDragEndHandler(handler));

        m_registrationManager.register(connector.getLine().addNodeMouseClickHandler(handler));
        m_registrationManager.register(connector.getLine().addNodeMouseDownHandler(handler));
        m_registrationManager.register(connector.getLine().addNodeMouseMoveHandler(handler));
        m_registrationManager.register(connector.getLine().addNodeMouseEnterHandler(handler));
        m_registrationManager.register(connector.getLine().addNodeMouseExitHandler(handler));

        m_shapeHandlersMap.put(uuid, m_registrationManager);
    }

    public void deregister(final WiresConnector connector) {
        final String uuid = connector.uuid();
        deselect(connector);
        removeHandlers(uuid);
        connector.destroy();
        getConnectorList().remove(connector);
    }

    public void resetContext() {
        if (null != m_handler) {
            m_handler.reset();
            m_handler = null;
        }
    }

    private void destroy() {
        if (!m_shapesMap.isEmpty()) {
            final WiresShape[] shapes = Js.uncheckedCast(JsArray.from(m_shapesMap.values()));
            for (WiresShape shape : shapes) {
                deregister(shape);
            }
            m_shapesMap.clear();
        }
        if (!m_connectorList.isEmpty()) {
            final NFastArrayList<WiresConnector> connectors = m_connectorList.copy();
            for (WiresConnector connector : connectors.asList()) {
                deregister(connector);
            }
            m_connectorList.clear();
        }
        if (null != m_selectionManager) {
            m_selectionManager.destroy();
            m_selectionManager = null;
        }
        if (null != m_handler) {
            m_handler.reset();
            m_handler = null;
        }
        m_shapeHandlersMap.clear();
        m_controlFactory = null;
        m_wiresHandlerFactory = null;
        m_locationAcceptor = null;
        m_connectionAcceptor = null;
        m_containmentAcceptor = null;
        m_controlPointsAcceptor = null;
        m_dockingAcceptor = null;
        m_lineSpliceAcceptor = null;
    }

    public WiresLayer getLayer() {
        return m_layer;
    }

    public WiresShape getShape(final String uuid) {
        return m_shapesMap.get(uuid);
    }

    private AlignAndDistributeControl addToIndex(final WiresShape shape) {
        return m_index.addShape(shape.getGroup());
    }

    private void removeFromIndex(final WiresShape shape) {
        m_index.removeShape(shape.getGroup());
    }

    public AlignAndDistribute getAlignAndDistribute() {
        return m_index;
    }

    public void setWiresControlFactory(final WiresControlFactory factory) {
        this.m_controlFactory = factory;
    }

    public void setWiresHandlerFactory(WiresHandlerFactory wiresHandlerFactory) {
        this.m_wiresHandlerFactory = wiresHandlerFactory;
    }

    public WiresControlFactory getControlFactory() {
        return m_controlFactory;
    }

    public WiresHandlerFactory getWiresHandlerFactory() {
        return m_wiresHandlerFactory;
    }

    public IConnectionAcceptor getConnectionAcceptor() {
        return m_connectionAcceptor;
    }

    public IControlPointsAcceptor getControlPointsAcceptor() {
        return m_controlPointsAcceptor;
    }

    public IContainmentAcceptor getContainmentAcceptor() {
        return m_containmentAcceptor;
    }

    public IDockingAcceptor getDockingAcceptor() {
        return m_dockingAcceptor;
    }

    public ILineSpliceAcceptor getLineSpliceAcceptor() {
        return m_lineSpliceAcceptor;
    }

    public void setConnectionAcceptor(IConnectionAcceptor connectionAcceptor) {
        m_connectionAcceptor = connectionAcceptor;
    }

    public void setControlPointsAcceptor(IControlPointsAcceptor controlPointsAcceptor) {
        if (controlPointsAcceptor == null) {
            throw new IllegalArgumentException("ControlPointsAcceptor cannot be null");
        }
        this.m_controlPointsAcceptor = controlPointsAcceptor;
    }

    public void setContainmentAcceptor(IContainmentAcceptor containmentAcceptor) {
        if (containmentAcceptor == null) {
            throw new IllegalArgumentException("ContainmentAcceptor cannot be null");
        }
        m_containmentAcceptor = containmentAcceptor;
    }

    public void setDockingAcceptor(IDockingAcceptor dockingAcceptor) {
        if (dockingAcceptor == null) {
            throw new IllegalArgumentException("DockingAcceptor cannot be null");
        }
        this.m_dockingAcceptor = dockingAcceptor;
    }

    public void setLocationAcceptor(ILocationAcceptor m_locationAcceptor) {
        if (m_locationAcceptor == null) {
            throw new IllegalArgumentException("LocationAcceptor cannot be null");
        }
        this.m_locationAcceptor = m_locationAcceptor;
    }

    public void setLineSpliceAcceptor(ILineSpliceAcceptor lineSpliceAcceptor) {
        if (lineSpliceAcceptor == null) {
            throw new IllegalArgumentException("LineSpliceAcceptor cannot be null");
        }
        this.m_lineSpliceAcceptor = lineSpliceAcceptor;
    }

    public ILocationAcceptor getLocationAcceptor() {
        return m_locationAcceptor;
    }

    private void removeHandlers(final String uuid) {
        final HandlerRegistrationManager m_registrationManager = m_shapeHandlersMap.get(uuid);
        if (null != m_registrationManager) {
            m_registrationManager.removeHandler();
            m_shapeHandlersMap.remove(uuid);
        }
    }

    public NFastArrayList<WiresConnector> getConnectorList() {
        return m_connectorList;
    }

    public NFastStringMap<WiresShape> getShapesMap() {
        return m_shapesMap;
    }

    public WiresShape[] getShapes() {
        JsArray<WiresShape> array = toValuesJsArray(m_shapesMap);
        final WiresShape[] shapes = Js.uncheckedCast(array);
        return shapes;
    }

    HandlerRegistrationManager createHandlerRegistrationManager() {
        return new HandlerRegistrationManager();
    }

    public static abstract class WiresDragHandler implements DragConstraintEnforcer,
                                                             NodeDragEndHandler {

        private final WiresManager wiresManager;
        private DragContext dragContext;

        protected WiresDragHandler(final WiresManager wiresManager) {
            this.wiresManager = wiresManager;
        }

        public abstract WiresControl getControl();

        protected abstract boolean doAdjust(Point2D dxy);

        protected abstract void doOnNodeDragEnd(NodeDragEndEvent event);

        @Override
        public void startDrag(DragContext dragContext) {
            this.dragContext = dragContext;
            wiresManager.m_handler = this;
        }

        @Override
        public boolean adjust(Point2D dxy) {
            if (null == dragContext) {
                dxy.setX(0);
                dxy.setY(0);
                return true;
            }
            return doAdjust(dxy);
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            if (null != dragContext) {
                doOnNodeDragEnd(event);
                this.dragContext = null;
                wiresManager.m_handler = null;
            }
        }

        public void reset() {
            if (null != dragContext) {
                doReset();
            }
        }

        protected void doReset() {
            dragContext.reset();
            dragContext = null;
            getControl().reset();
        }

        protected WiresManager getWiresManager() {
            return wiresManager;
        }
    }

    private void deselect(final WiresShape shape) {
        if (null != getSelectionManager() &&
                getSelectionManager().getSelectedItems().isShapeSelected(shape)) {
            getSelectionManager().getSelectedItems().remove(shape);
        }
    }

    private void deselect(final WiresConnector connector) {
        if (null != getSelectionManager() &&
                getSelectionManager().getSelectedItems().isConnectorSelected(connector)) {
            getSelectionManager().getSelectedItems().remove(connector);
        }
    }
}

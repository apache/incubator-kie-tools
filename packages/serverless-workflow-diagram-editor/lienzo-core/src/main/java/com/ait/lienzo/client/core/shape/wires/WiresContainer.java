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

import java.util.Objects;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerManager;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

public class WiresContainer {

    private final NFastArrayList<WiresShape> m_childShapes;

    private final IContainer<?, IPrimitive<?>> m_container;

    private final HandlerManager m_events;

    private final HandlerRegistrationManager m_registrationManager;

    private WiresContainer m_parent;

    private WiresContainer dockedTo;

    private WiresManager m_wiresManager;

    private boolean m_drag_initialized;

    private boolean m_dragging;

    private ILayoutHandler m_layoutHandler = ILayoutHandler.NONE;

    public WiresContainer(final IContainer<?, IPrimitive<?>> container) {
        this(container, null, new HandlerRegistrationManager());
    }

    WiresContainer(final IContainer<?, IPrimitive<?>> container, final HandlerManager m_events, final HandlerRegistrationManager m_registrationManager) {
        this.m_container = container;
        this.m_events = null != m_events ? m_events : new HandlerManager(this);
        this.m_dragging = false;
        this.m_drag_initialized = false;
        this.m_childShapes = new NFastArrayList<>();
        this.m_registrationManager = m_registrationManager;
    }

    public String getID() {
        return getContainer().getID();
    }

    public WiresContainer setID(String id) {
        getContainer().setID(id);
        return this;
    }

    public WiresManager getWiresManager() {
        return m_wiresManager;
    }

    public void setWiresManager(WiresManager wiresManager) {
        m_wiresManager = wiresManager;
    }

    public IContainer<?, IPrimitive<?>> getContainer() {
        return m_container;
    }

    public Group getGroup() {
        return getContainer().asGroup();
    }

    public double getX() {
        return getGroup().getX();
    }

    public double getY() {
        return getGroup().getY();
    }

    public WiresContainer listen(final boolean listen) {
        getGroup().setListening(listen);
        return this;
    }

    public boolean isListening() {
        return getGroup().isListening();
    }

    public WiresContainer setLocation(final Point2D p) {
        getGroup().setLocation(p);
        shapeMoved();
        return this;
    }

    public Point2D getLocation() {
        return getGroup().getLocation();
    }

    public Point2D getComputedLocation() {
        return getGroup().getComputedLocation();
    }

    public WiresContainer getParent() {
        return m_parent;
    }

    public void setParent(WiresContainer parent) {
        m_parent = parent;
    }

    public NFastArrayList<WiresShape> getChildShapes() {
        return m_childShapes;
    }

    public ILayoutHandler getLayoutHandler() {
        return m_layoutHandler;
    }

    public void setLayoutHandler(ILayoutHandler layoutHandler) {
        this.m_layoutHandler = layoutHandler;
    }

    public void add(WiresShape shape) {
        if (shape.getParent() == this) {
            return;
        }
        if (shape.getParent() != null) {
            shape.removeFromParent();
        }

        m_childShapes.add(shape);

        m_container.add(shape.getGroup());

        shape.setParent(this);

        shape.shapeMoved();

        if (null != m_wiresManager && m_wiresManager.getAlignAndDistribute().isShapeIndexed(shape.uuid())) {
            m_wiresManager.getAlignAndDistribute().getControlForShape(shape.uuid()).updateIndex();
        }

        getLayoutHandler().requestLayout(this);
    }

    public void shapeMoved() {
        if (m_container.getLayer() == null) {
            // no layer yet, cannot issue any events. Avoids errors from events needing a layer, to be instantiated
            return;
        }

        // Delegate to children.
        if (getChildShapes() != null && !getChildShapes().isEmpty()) {
            NFastArrayList<WiresShape> shapes = getChildShapes();
            for (int i = 0, size = shapes.size(); i < size; i++) {
                WiresShape child = shapes.get(i);
                child.shapeMoved();
            }
        }
        // Notify the update..
        fireMove();
    }

    public void remove(WiresShape shape) {
        if (m_childShapes != null) {
            m_childShapes.remove(shape);

            m_container.remove(shape.getGroup());

            shape.setParent(null);
        }

        getLayoutHandler().requestLayout(this);
    }

    public void setDockedTo(WiresContainer dockedTo) {
        this.dockedTo = dockedTo;
    }

    public WiresContainer getDockedTo() {
        return dockedTo;
    }

    public WiresContainer setDraggable(final boolean draggable) {
        ensureHandlers();

        getGroup().setDraggable(draggable);

        return this;
    }

    private void ensureHandlers() {
        if (!m_drag_initialized && null != m_container) {
            m_registrationManager.register(m_container.addNodeDragStartHandler(new NodeDragStartHandler() {
                @Override
                public void onNodeDragStart(final NodeDragStartEvent event) {
                    WiresEventHandlers wiresEventHandlers = m_wiresManager.getWiresEventHandlers();
                    WiresContainer.this.m_dragging = true;
                    wiresEventHandlers.dragStartEvent.revive();
                    wiresEventHandlers.dragStartEvent.override(WiresContainer.this, event);
                    m_events.fireEvent(wiresEventHandlers.dragStartEvent);
                    wiresEventHandlers.dragStartEvent.kill();
                }
            }));

            m_registrationManager.register(m_container.addNodeDragMoveHandler(new NodeDragMoveHandler() {
                @Override
                public void onNodeDragMove(final NodeDragMoveEvent event) {
                    WiresEventHandlers wiresEventHandlers = m_wiresManager.getWiresEventHandlers();
                    WiresContainer.this.m_dragging = true;
                    wiresEventHandlers.dragMoveEvent.revive();
                    wiresEventHandlers.dragMoveEvent.override(WiresContainer.this, event);
                    m_events.fireEvent(wiresEventHandlers.dragMoveEvent);
                    wiresEventHandlers.dragMoveEvent.kill();
                }
            }));

            m_registrationManager.register(m_container.addNodeDragEndHandler(new NodeDragEndHandler() {
                @Override
                public void onNodeDragEnd(final NodeDragEndEvent event) {
                    WiresEventHandlers wiresEventHandlers = m_wiresManager.getWiresEventHandlers();
                    WiresContainer.this.m_dragging = false;
                    wiresEventHandlers.dragEndEvent.revive();
                    wiresEventHandlers.dragEndEvent.override(WiresContainer.this, event);
                    m_events.fireEvent(wiresEventHandlers.dragEndEvent);
                    wiresEventHandlers.dragEndEvent.kill();
                }
            }));

            m_drag_initialized = true;
        }
    }

    private void fireMove() {
        WiresEventHandlers wiresEventHandlers = m_wiresManager.getWiresEventHandlers();
        wiresEventHandlers.wiresMoveEvent.revive();
        wiresEventHandlers.wiresMoveEvent.override(WiresContainer.this, (int) getLocation().getX(), (int) getLocation().getY());
        m_events.fireEvent(wiresEventHandlers.wiresMoveEvent);
        wiresEventHandlers.wiresMoveEvent.kill();
    }

    public final HandlerRegistration addWiresMoveHandler(final WiresMoveHandler handler) {
        Objects.requireNonNull(handler);

        return m_events.addHandler(WiresMoveEvent.TYPE, handler);
    }

    public final HandlerRegistration addWiresDragStartHandler(final WiresDragStartHandler dragHandler) {
        Objects.requireNonNull(dragHandler);

        return m_events.addHandler(WiresDragStartEvent.TYPE, dragHandler);
    }

    public final HandlerRegistration addWiresDragMoveHandler(final WiresDragMoveHandler dragHandler) {
        Objects.requireNonNull(dragHandler);

        return m_events.addHandler(WiresDragMoveEvent.TYPE, dragHandler);
    }

    public final HandlerRegistration addWiresDragEndHandler(final WiresDragEndHandler dragHandler) {
        Objects.requireNonNull(dragHandler);

        return m_events.addHandler(WiresDragEndEvent.TYPE, dragHandler);
    }

    public void destroy() {
        for (WiresShape shape : m_childShapes.asList()) {
            remove(shape);
        }
        NFastArrayList<WiresShape> shapes = m_childShapes;
        for (int i = 0, size = shapes.size(); i < size; i++) {
            WiresShape child = shapes.get(i);
            child.shapeMoved();
        }

        m_childShapes.clear();
        m_registrationManager.removeHandler();
        m_container.removeFromParent();
        m_parent = null;
        dockedTo = null;
        m_wiresManager = null;
        m_layoutHandler = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WiresShape that = (WiresShape) o;

        return getGroup().uuid().equals(that.getGroup().uuid());
    }

    @Override
    public int hashCode() {
        return getGroup().uuid().hashCode();
    }

    protected HandlerManager getHandlerManager() {
        return m_events;
    }
}

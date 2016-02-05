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

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.event.AbstractWiresEvent;
import com.ait.lienzo.client.core.shape.wires.event.DragEvent;
import com.ait.lienzo.client.core.shape.wires.event.ResizeEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresEventHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.Map;
import java.util.Objects;

public class WiresShape extends WiresContainer
{
    private MultiPath m_path;

    private Magnets   m_magnets;

    private boolean   m_dragTarget;
    
    private LayoutContainer m_layout_container;

    private IControlHandleList m_ctrls;
    
    private final HandlerManager m_manager = new HandlerManager(this);
    
    private final WiresManager manager;

    private final HandlerRegistrationManager drag_manage         = new HandlerRegistrationManager();

    private final HandlerRegistrationManager resize_manage         = new HandlerRegistrationManager();

    public WiresShape(MultiPath path, LayoutContainer m_layout_container, WiresManager manager)
    {
        super(m_layout_container.getGroup());
        
        this.m_layout_container = m_layout_container;

        this.m_layout_container.getGroup().setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        this.m_layout_container.add(path);
        
        this.manager = manager;
        
        m_path = path;

        init();        

    }

    public WiresShape setX(final double x) {
        getGroup().setX(x);
        return this;
    }

    public WiresShape setY(final double y) {
        getGroup().setY(y);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child, final LayoutContainer.Layout layout) {
        m_layout_container.add(child, layout, 0, 0);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child, final LayoutContainer.Layout layout,
                         final double dx, final double dy) {
        m_layout_container.add(child, layout, dx, dy);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child) {
        m_layout_container.add(child);
        return this;
    }

    public WiresShape moveChild(final IPrimitive<?> child, final double dx, final double dy) {
        m_layout_container.move(child, dx, dy);
        return this;
    }

    public WiresShape removeChild(final IPrimitive<?> child) {
        m_layout_container.remove(child);
        return this;
    }

    public WiresShape setDraggable(final boolean draggable) {
        
        m_layout_container.getGroup().setDraggable(draggable);
        
        drag_manage.removeHandler();
        
        if (draggable) {
            
            drag_manage.register(getGroup().addNodeDragStartHandler(new NodeDragStartHandler() {
                @Override
                public void onNodeDragStart(NodeDragStartEvent event) {
                    removeControls();
                    m_manager.fireEvent(new DragEvent(WiresShape.this, event.getX(), event.getY(), DragEvent.Type.START));
                }
            }));

            drag_manage.register(getGroup().addNodeDragMoveHandler(new NodeDragMoveHandler() {
                @Override
                public void onNodeDragMove(NodeDragMoveEvent event) {
                    removeControls();
                    m_manager.fireEvent(new DragEvent(WiresShape.this, event.getX(), event.getY(), DragEvent.Type.STEP));
                }
            }));

            drag_manage.register(getGroup().addNodeDragEndHandler(new NodeDragEndHandler() {
                @Override
                public void onNodeDragEnd(NodeDragEndEvent event) {
                    removeControls();
                    m_manager.fireEvent(new DragEvent(WiresShape.this, event.getX(), event.getY(), DragEvent.Type.END));
                }
            }));
            
        }
        
        return this;
        
    }
    
    public WiresShape setResizable(final boolean resizable) {
        
        this.setResizable(getPath(), resizable);
        
        return this;
        
    }

    // Resize based on listening events for a given child, as probably the wires shape's MultiPath is not on top, so cannot receive some mouse events.
    // Do not consider putting the wires shape's MultiPath on top, as for example, if some text is added, it must receive the different mouse events as well.
    public WiresShape setResizable(final Shape<?> shapeToListenForEvent, final boolean resizable) {

        resize_manage.removeHandler();

        if (resizable) {
            resize_manage.register(shapeToListenForEvent.addNodeMouseClickHandler(new NodeMouseClickHandler() {
                @Override
                public void onNodeMouseClick(NodeMouseClickEvent event) {
                    if (event.isShiftKeyDown())
                    {
                        if (!removeControls()) {
                            Map<IControlHandle.ControlHandleType, IControlHandleList> hmap = getPath().getControlHandles(IControlHandle.ControlHandleStandardType.RESIZE);

                            if (null != hmap)
                            {
                                m_ctrls = hmap.get(IControlHandle.ControlHandleStandardType.RESIZE);

                                if ((null != m_ctrls) && (m_ctrls.isActive()))
                                {
                                    addResizeEventHandlers(0, 1, 2, 3);
                                    m_ctrls.show(getGroup());
                                }
                            }
                        }
                    }
                }
            }));
        }

        return this;

    }

    public MultiPath getPath()
    {
        return m_path;
    }

    public IControlHandleList getControls() {
        return m_ctrls;
    }

    public Magnets getMagnets()
    {
        return m_magnets;
    }

    public void setMagnets(Magnets magnets)
    {
        m_magnets = magnets;
    }

    public boolean isDragTarget()
    {
        return m_dragTarget;
    }

    public void setDragTarget(boolean dragTarget)
    {
        m_dragTarget = dragTarget;
    }

    public void removeFromParent()
    {
        if (getParent() != null)
        {
            getParent().remove(this);
        }
    }

    public WiresLayer getWiresLayer()
    {
        WiresContainer current = this;

        while (current.getParent() != null)
        {
            current = current.getParent();
        }
        return (WiresLayer) current;
    }
    
    public final <H extends WiresEventHandler> HandlerRegistration addWiresHandler(final GwtEvent.Type<H> type, final H handler)
    {
        Objects.requireNonNull(type);

        Objects.requireNonNull(handler);

        return m_manager.addHandler(type, handler);

    }

    public Group getGroup()
    {
        return getContainer().asGroup();
    }

    private void init() {
        final BoundingBox box = getPath().refresh().getBoundingBox();
        m_layout_container.setWidth(box.getWidth());
        m_layout_container.setHeight(box.getHeight());
        m_layout_container.getGroup().moveToTop();
    }

    private void addResizeEventHandlers(final int... indexes) {

        for (final int index : indexes) {

            final IControlHandle handle = m_ctrls.getHandle(index);
            final IPrimitive<?> control = handle.getControl();

            if ( null != control ) {
                control.addNodeDragStartHandler(new NodeDragStartHandler() {
                    @Override
                    public void onNodeDragStart(final NodeDragStartEvent event) {
                        doResize(index, event.getX(), event.getY(), AbstractWiresEvent.Type.START);
                    }
                });
                control.addNodeDragMoveHandler(new NodeDragMoveHandler() {
                    @Override
                    public void onNodeDragMove(final NodeDragMoveEvent event) {
                        doResize(index, event.getX(), event.getY(), AbstractWiresEvent.Type.STEP);
                    }
                });
                control.addNodeDragEndHandler(new NodeDragEndHandler() {
                    @Override
                    public void onNodeDragEnd(final NodeDragEndEvent event) {
                        doResize(index, event.getX(), event.getY(), AbstractWiresEvent.Type.END);
                        resizeMagnets();
                    }
                });
            }
        }
    }

    private void doResize(final int index,
                          final int x,
                          final int y,
                          final AbstractWiresEvent.Type type) {
        final double[] size = resize(0, 1, 2, 3);;
        m_manager.fireEvent(new ResizeEvent(WiresShape.this, index, x, y, size[2], size[3], type));
    }

    private double[] resize(final int... indexes) {
        double minx = m_ctrls.getHandle(0).getControl().getX();
        double miny = m_ctrls.getHandle(0).getControl().getY();
        double maxx = m_ctrls.getHandle(0).getControl().getX();
        double maxy = m_ctrls.getHandle(0).getControl().getY();

        for (final int pos : indexes) {
            final IControlHandle handle = m_ctrls.getHandle(pos);
            final IPrimitive<?> control = handle.getControl();
            if (control.getX() < minx) {
                minx = control.getX();
            }
            if (control.getX() > maxx) {
                maxx = control.getX();
            }
            if (control.getY() < miny) {
                miny = control.getY();
            }
            if (control.getY() > maxy) {
                maxy = control.getY();
            }
        }

        // Resize the primitives container.
        final double w = maxx - minx;
        final double h = maxy - miny;
        m_layout_container.setX(minx);
        m_layout_container.setY(miny);
        m_layout_container.setWidth(w);
        m_layout_container.setHeight(h);

        return new double[] {minx, miny, w, h};

    }

    // Improve by do not destroying the magnets, just move the points to fit new size.
    private void resizeMagnets() {
        getMagnets().destroy();
        manager.createMagnets(this);
    }

    void removeHandlers() {

        drag_manage.removeHandler();
        resize_manage.removeHandler();

    }

    private boolean removeControls() {
        final boolean removed =  null != m_ctrls;

        if (removed)
        {
            m_ctrls.destroy();

            m_ctrls = null;
        }

        return removed;
    }


}

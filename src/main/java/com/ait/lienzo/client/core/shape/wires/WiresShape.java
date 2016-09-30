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

import java.util.Map;
import java.util.Objects;

import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDragControlContext;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class WiresShape extends WiresContainer
{

    private MultiPath                   m_path;

    private Magnets                     m_magnets;

    private LayoutContainer             m_layout_container;

    private WiresShapeControlHandleList m_ctrls;

    private boolean                     m_resizable;

    public WiresShape(final MultiPath path)
    {
        this(path, new WiresLayoutContainer());
    }

    public WiresShape(final MultiPath path, final LayoutContainer m_layout_container)
    {
        super(m_layout_container.getGroup());
        this.m_path = path;
        this.m_layout_container = m_layout_container;
        this.m_ctrls = null;
        init();

    }

    WiresShape(final MultiPath path, final LayoutContainer m_layout_container, final HandlerManager m_manager, final HandlerRegistrationManager m_registrationManager, final IAttributesChangedBatcher attributesChangedBatcher)
    {
        super(m_layout_container.getGroup(), m_manager, m_registrationManager, attributesChangedBatcher);
        this.m_path = path;
        this.m_ctrls = null;
        this.m_layout_container = m_layout_container;
        init();
    }

    public WiresShape setX(final double x)
    {
        getGroup().setX(x);
        return this;
    }

    public WiresShape setY(final double y)
    {
        getGroup().setY(y);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child)
    {
        m_layout_container.add(child);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child, final LayoutContainer.Layout layout)
    {
        m_layout_container.add(child, layout);
        return this;
    }

    public WiresShape removeChild(final IPrimitive<?> child)
    {
        m_layout_container.remove(child);
        return this;
    }

    public WiresShapeControlHandleList getControls()
    {
        return m_ctrls;
    }

    public IControlHandleList loadControls(final IControlHandle.ControlHandleType type)
    {
        _loadControls(type);
        return getControls();
    }

    @Override
    public WiresShape setDraggable(final boolean draggable)
    {
        super.setDraggable(draggable);
        return this;
    }

    public WiresShape setResizable(final boolean resizable)
    {
        this.m_resizable = resizable;
        return this;
    }

    public boolean isResizable()
    {
        return this.m_resizable;
    }

    /**
     * If the shape's path parts/points have been updated programatically (not via human events interactions),
     * you can call this method to update the children layouts, controls and magnets.
     * The WiresResizeEvent event is not fired as this method is suposed to be called by the developer.
     */
    public void refresh()
    {

        _loadControls(IControlHandle.ControlHandleStandardType.RESIZE);

        if (null != getControls())
        {
            getControls().refresh();
        }

    }

    public MultiPath getPath()
    {
        return m_path;
    }

    public Magnets getMagnets()
    {
        return m_magnets;
    }

    public void setMagnets(Magnets magnets)
    {
        m_magnets = magnets;
    }

    public void removeFromParent()
    {
        if (getParent() != null)
        {
            getParent().remove(this);
        }
    }

    public final HandlerRegistration addWiresResizeStartHandler(final WiresResizeStartHandler handler)
    {

        Objects.requireNonNull(handler);

        return getHandlerManager().addHandler(WiresResizeStartEvent.TYPE, handler);

    }

    public final HandlerRegistration addWiresResizeStepHandler(final WiresResizeStepHandler handler)
    {

        Objects.requireNonNull(handler);

        return getHandlerManager().addHandler(WiresResizeStepEvent.TYPE, handler);

    }

    public final HandlerRegistration addWiresResizeEndHandler(final WiresResizeEndHandler handler)
    {

        Objects.requireNonNull(handler);

        return getHandlerManager().addHandler(WiresResizeEndEvent.TYPE, handler);

    }

    private void init()
    {

        this.m_resizable = true;

        this.m_layout_container.getGroup().setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        this.m_layout_container.add(getPath());

        final BoundingBox box = getPath().refresh().getBoundingBox();

        m_layout_container.setOffset(new Point2D(box.getX(), box.getY())).setSize(box.getWidth(), box.getHeight()).execute();
    }

    private void _loadControls(final IControlHandle.ControlHandleType type)
    {

        if (null != getControls())
        {

            this.getControls().destroy();

            this.m_ctrls = null;

        }

        Map<IControlHandle.ControlHandleType, IControlHandleList> hmap = getPath().getControlHandles(type);

        if (null != hmap)
        {

            IControlHandleList o_ctrls = hmap.get(type);

            if ((null != o_ctrls) && (o_ctrls.isActive()))
            {

                this.m_ctrls = new WiresShapeControlHandleList(this, type, (ControlHandleList) o_ctrls);

            }

        }

    }

    protected void preDestroy()
    {
        super.preDestroy();
        m_layout_container.destroy();
        removeHandlers();
        removeFromParent();
    }

    private void removeHandlers()
    {
        if (null != getControls())
        {
            getControls().destroy();
        }
    }

    LayoutContainer getLayoutContainer()
    {
        return m_layout_container;
    }

    static class WiresShapeHandler implements NodeMouseDownHandler, NodeMouseUpHandler, NodeDragEndHandler, DragConstraintEnforcer
    {
        private final WiresShapeControl shapeControl;

        WiresShapeHandler(WiresShape shape, WiresManager wiresManager)
        {
            this.shapeControl = wiresManager.getControlFactory().newShapeControl(shape, wiresManager);
        }

        void setAlignAndDistributeControl(AlignAndDistributeControl alignAndDistributeHandler)
        {
            this.shapeControl.setAlignAndDistributeControl(alignAndDistributeHandler);
        }

        void setDockingAndContainmentControl(WiresDockingAndContainmentControl m_dockingAndContainmentControl)
        {
            this.shapeControl.setDockingAndContainmentControl(m_dockingAndContainmentControl);
        }

        @Override
        public void startDrag(DragContext dragContext)
        {
            this.shapeControl.dragStart(new WiresDragControlContext(dragContext.getDragStartX(), dragContext.getDragStartY(), dragContext.getNode()));

        }

        @Override
        public boolean adjust(final Point2D dxy)
        {
            return this.shapeControl.dragAdjust(dxy);
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            this.shapeControl.dragEnd(new WiresDragControlContext(event.getX(), event.getY(), event.getSource()));
        }

        @Override
        public void onNodeMouseDown(NodeMouseDownEvent event)
        {
            this.shapeControl.onNodeMouseDown();
        }

        @Override
        public void onNodeMouseUp(NodeMouseUpEvent event)
        {
            this.shapeControl.onNodeMouseUp();
        }

        WiresShapeControl getControl()
        {
            return shapeControl;
        }
    }

}

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
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.ait.tooling.nativetools.client.util.Console;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.Map;
import java.util.Objects;

public class WiresShape extends WiresContainer
{
    interface WiresShapeHandler extends NodeMouseDownHandler, NodeMouseUpHandler, NodeMouseClickHandler, NodeDragEndHandler, DragConstraintEnforcer
    {

        void setAlignAndDistributeControl(AlignAndDistributeControl alignAndDistributeHandler);

        void setDockingAndContainmentControl(WiresDockingAndContainmentControl m_dockingAndContainmentControl);

        WiresShapeControl getControl();

    }

    private MultiPath                   m_drawnObject;

    private Magnets                     m_magnets;

    private LayoutContainer             m_innerLayoutContainer;

    private WiresShapeControlHandleList m_ctrls;

    private boolean                     m_resizable;

    private WiresManager                m_wiresManager;

    private WiresShapeHandler           m_handler;

    public WiresShape(final MultiPath path)
    {
        this(path, new WiresLayoutContainer());
    }

    public WiresShape(final MultiPath path, final LayoutContainer layoutContainer)
    {
        super(layoutContainer.getGroup());
        this.m_drawnObject = path;
        this.m_innerLayoutContainer = layoutContainer;
        this.m_ctrls = null;
        init();

    }

    WiresShape(final MultiPath path, final LayoutContainer layoutContainer, final HandlerManager manager, final HandlerRegistrationManager registrationManager, final IAttributesChangedBatcher attributesChangedBatcher)
    {
        super(layoutContainer.getGroup(), manager, registrationManager, attributesChangedBatcher);
        this.m_drawnObject = path;
        this.m_ctrls = null;
        this.m_innerLayoutContainer = layoutContainer;
        init();
    }

    public double getX()
    {
        return getGroup().getX();
    }

    public WiresShape setX(final double x)
    {
        getGroup().setX(x);
        return this;
    }

    public double getY()
    {
        return getGroup().getY();
    }

    public WiresShape setY(final double y)
    {
        getGroup().setY(y);
        return this;
    }

    public WiresManager getWiresManager()
    {
        return m_wiresManager;
    }

    public void setWiresManager(WiresManager wiresManager)
    {
        m_wiresManager = wiresManager;
    }

    public WiresShape addChild(final IPrimitive<?> child)
    {
        m_innerLayoutContainer.add(child);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child, final LayoutContainer.Layout layout)
    {
        m_innerLayoutContainer.add(child, layout);
        return this;
    }

    public WiresShape removeChild(final IPrimitive<?> child)
    {
        m_innerLayoutContainer.remove(child);
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
        return m_resizable;
    }

    /**
     * If the shape's path parts/points have been updated programmatically (not via human events interactions),
     * you can call this method to update the children layouts, controls and m_magnets.
     * The WiresResizeEvent event is not fired as this method is supposed to be called by the developer.
     */
    public void refresh()
    {
        _loadControls(IControlHandle.ControlHandleStandardType.RESIZE);

        if (null != getControls())
        {
            getControls().refresh();
        }
    }

    public void addWiresShapeHandler( final HandlerRegistrationManager registrationManager,
                                      final WiresShapeHandler handler )
    {
        registrationManager.register(getGroup().addNodeMouseClickHandler(handler));
        registrationManager.register(getGroup().addNodeMouseDownHandler(handler));
        registrationManager.register(getGroup().addNodeMouseUpHandler(handler));
        registrationManager.register(getGroup().addNodeDragEndHandler(handler));
        getGroup().setDragConstraints(handler);
        m_handler = handler;
    }

    public WiresShapeHandler getHandler()
    {
        return m_handler;
    }

    public MultiPath getPath()
    {
        return m_drawnObject;
    }

    public Magnets getMagnets()
    {
        return m_magnets;
    }

    public void setMagnets(Magnets magnets)
    {
        this.m_magnets = magnets;
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

    public String uuid()
    {
        return getGroup().uuid();
    }

    private void init()
    {
        m_resizable = true;

        m_innerLayoutContainer.getGroup().setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        m_innerLayoutContainer.add(getPath());

        BoundingBox box = getPath().refresh().getBoundingBox();

        m_innerLayoutContainer.setOffset(new Point2D(box.getX(), box.getY())).setSize(box.getWidth(), box.getHeight()).execute();
    }

    private void _loadControls(final IControlHandle.ControlHandleType type)
    {
        if (null != getControls())
        {
            this.getControls().destroy();

            this.m_ctrls = null;
        }

        Map<IControlHandle.ControlHandleType, IControlHandleList> handles = getPath().getControlHandles(type);

        if (null != handles)
        {
            IControlHandleList controls = handles.get(type);

            if ((null != controls) && (controls.isActive()))
            {
                this.m_ctrls = createControlHandles(type, (ControlHandleList) controls);
            }
        }
    }

    protected WiresShapeControlHandleList createControlHandles(IControlHandle.ControlHandleType type, ControlHandleList controls)
    {
        return new WiresShapeControlHandleList(this, type, controls);
    }

    protected void preDestroy()
    {
        super.preDestroy();
        m_innerLayoutContainer.destroy();
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
        return m_innerLayoutContainer;
    }

    static class WiresShapeHandlerImpl implements WiresShape.WiresShapeHandler
    {
        private final WiresShapeControl shapeControl;

        private WiresManager m_wiresManager;

        WiresShapeHandlerImpl(WiresShape shape, WiresManager wiresManager )
        {
            this.shapeControl = wiresManager.getControlFactory().newShapeControl(shape, wiresManager);
            m_wiresManager = wiresManager;
        }

        public void setAlignAndDistributeControl(AlignAndDistributeControl alignAndDistributeHandler)
        {
            this.shapeControl.setAlignAndDistributeControl(alignAndDistributeHandler);
        }

        public void setDockingAndContainmentControl(WiresDockingAndContainmentControl m_dockingAndContainmentControl)
        {
            this.shapeControl.setDockingAndContainmentControl(m_dockingAndContainmentControl);
        }

        @Override
        public void startDrag(DragContext dragContext)
        {
            this.shapeControl.dragStart(dragContext);
        }

        @Override
        public boolean adjust(final Point2D dxy)
        {
            return this.shapeControl.dragAdjust(dxy);
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            this.shapeControl.dragEnd(event.getDragContext());
        }

        @Override
        public void onNodeMouseDown(NodeMouseDownEvent event)
        {
            this.shapeControl.onNodeMouseDown(event);
        }

        @Override
        public void onNodeMouseUp(NodeMouseUpEvent event)
        {
            this.shapeControl.onNodeMouseUp(event);
        }

        @Override public void onNodeMouseClick(NodeMouseClickEvent event)
        {
            this.shapeControl.onNodeClick(event);
        }

        public WiresShapeControl getControl()
        {
            return shapeControl;
        }
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        WiresShape that = (WiresShape) o;

        return getGroup().uuid() == that.getGroup().uuid();
    }

    @Override public int hashCode()
    {
        return getGroup().uuid().hashCode();
    }
}

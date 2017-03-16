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

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.event.*;
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

import java.util.Map;
import java.util.Objects;

public class WiresShape extends WiresContainer
{
    interface WiresShapeHandler extends NodeMouseDownHandler, NodeMouseUpHandler, NodeDragEndHandler, DragConstraintEnforcer
    {

        void setAlignAndDistributeControl(AlignAndDistributeControl alignAndDistributeHandler);

        void setDockingAndContainmentControl(WiresDockingAndContainmentControl m_dockingAndContainmentControl);

        WiresShapeControl getControl();

    }

    private MultiPath drawnObject;

    private Magnets magnets;

    private LayoutContainer innerLayoutContainer;

    private WiresShapeControlHandleList m_ctrls;

    private boolean resizable;

    public WiresShape(final MultiPath path)
    {
        this(path, new WiresLayoutContainer());
    }

    public WiresShape(final MultiPath path, final LayoutContainer layoutContainer)
    {
        super(layoutContainer.getGroup());
        this.drawnObject = path;
        this.innerLayoutContainer = layoutContainer;
        this.m_ctrls = null;
        init();

    }

    WiresShape(final MultiPath path, final LayoutContainer layoutContainer, final HandlerManager manager, final HandlerRegistrationManager registrationManager, final IAttributesChangedBatcher attributesChangedBatcher)
    {
        super(layoutContainer.getGroup(), manager, registrationManager, attributesChangedBatcher);
        this.drawnObject = path;
        this.m_ctrls = null;
        this.innerLayoutContainer = layoutContainer;
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

    public WiresShape addChild(final IPrimitive<?> child)
    {
        innerLayoutContainer.add(child);
        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child, final LayoutContainer.Layout layout)
    {
        innerLayoutContainer.add(child, layout);
        return this;
    }

    public WiresShape removeChild(final IPrimitive<?> child)
    {
        innerLayoutContainer.remove(child);
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
        this.resizable = resizable;
        return this;
    }

    public boolean isResizable()
    {
        return resizable;
    }

    /**
     * If the shape's path parts/points have been updated programmatically (not via human events interactions),
     * you can call this method to update the children layouts, controls and magnets.
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
        registrationManager.register(getGroup().addNodeMouseDownHandler(handler));
        registrationManager.register(getGroup().addNodeMouseUpHandler(handler));
        registrationManager.register(getGroup().addNodeDragEndHandler(handler));
        getGroup().setDragConstraints(handler);
    }

    public MultiPath getPath()
    {
        return drawnObject;
    }

    public Magnets getMagnets()
    {
        return magnets;
    }

    public void setMagnets(Magnets magnets)
    {
        this.magnets = magnets;
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
        resizable = true;

        innerLayoutContainer.getGroup().setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        innerLayoutContainer.add(getPath());

        BoundingBox box = getPath().refresh().getBoundingBox();

        innerLayoutContainer.setOffset(new Point2D(box.getX(), box.getY())).setSize(box.getWidth(), box.getHeight()).execute();
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
        innerLayoutContainer.destroy();
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
        return innerLayoutContainer;
    }

    static class WiresShapeHandlerImpl implements WiresShape.WiresShapeHandler
    {
        private final WiresShapeControl shapeControl;

        WiresShapeHandlerImpl(WiresShape shape, WiresManager wiresManager )
        {
            this.shapeControl = wiresManager.getControlFactory().newShapeControl(shape, wiresManager);
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
            final boolean accepts = this.shapeControl.dragEnd(new WiresDragControlContext(event.getX(), event.getY(), event.getSource()));
            if (!accepts) {
                event.getDragContext().reset();
            }
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

        public WiresShapeControl getControl()
        {
            return shapeControl;
        }
    }

}

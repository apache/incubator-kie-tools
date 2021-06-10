/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.client.core.shape.wires;

import java.util.Iterator;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

import elemental2.dom.HTMLDivElement;

/**
 * This class handles the Wires Shape controls to provide additional features.
 * As the shape's MultiPath, when resizing using the resize control points, does not updates any attribute, the way
 * the resize is captured when user drags a resize control point is by adding drag handlers to the resize controls. That
 * way it can be calculated the bounding box location and size for the multipath.
 *
 * Future thoughts: if the different parts of the multipath are stored as attributes, another approach based on attributes
 * changed batcher could be used.
 */
public class WiresShapeControlHandleList implements IControlHandleList
{
    private static final int POINTS_SIZE = 4;

    private final WiresShape                       m_wires_shape;

    private final ControlHandleList                m_ctrls;

    private final IControlHandle.ControlHandleType m_ctrls_type;

    private final HandlerRegistrationManager       m_registrationManager;

    private Group                                  parent;

    private WiresResizeStartEvent wiresResizeStartEvent;

    private WiresResizeStepEvent wiresResizeStepEvent;

    private WiresResizeEndEvent wiresResizeEndEvent;

    public WiresShapeControlHandleList(final WiresShape wiresShape, final IControlHandle.ControlHandleType controlsType, final ControlHandleList controls)
    {
        this(wiresShape, controlsType, controls, new HandlerRegistrationManager());
    }

    WiresShapeControlHandleList(final WiresShape wiresShape, final IControlHandle.ControlHandleType controlsType, final ControlHandleList controls, final HandlerRegistrationManager registrationManager)
    {
        this.m_wires_shape = wiresShape;
        this.m_ctrls = controls;
        this.m_ctrls_type = controlsType;
        this.m_registrationManager = registrationManager;
        this.parent = null;

        // TODO: lienzo-to-native
        /*HTMLElement relativeDiv = parent.getLayer().getViewport().getElement();
        wiresResizeStartEvent = new WiresResizeStartEvent(relativeDiv);
        wiresResizeStepEvent = new WiresResizeStepEvent(relativeDiv);
        wiresResizeEndEvent = new WiresResizeEndEvent(relativeDiv);*/

        updateParentLocation();
        initControlsListeners();
        setupEvents(parent);
    }

    protected void setupEvents(Group parent) {
        if(parent != null && parent.getLayer().getViewport() != null) {
            HTMLDivElement div = parent.getLayer().getViewport().getElement();
            wiresResizeStartEvent = new WiresResizeStartEvent(div);
            wiresResizeStepEvent = new WiresResizeStepEvent(div);
            wiresResizeEndEvent = new WiresResizeEndEvent(div);
        }
    }

    @Override
    public void show()
    {
        switchVisibility(true);
    }

    public void refresh()
    {
        final BoundingBox bbox = getPath().getBoundingBox();
        resize(bbox.getWidth(), bbox.getHeight(), true);
    }

    @Override
    public void destroy()
    {
        m_ctrls.destroy();
        m_registrationManager.removeHandler();
        if (null != parent)
        {
            parent.removeFromParent();
        }
    }

    @Override
    public int size()
    {
        return m_ctrls.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_ctrls.isEmpty();
    }

    @Override
    public IControlHandle getHandle(int index)
    {
        return m_ctrls.getHandle(index);
    }

    @Override
    public void add(IControlHandle handle)
    {
        m_ctrls.add(handle);
    }

    @Override
    public void remove(IControlHandle handle)
    {
        m_ctrls.remove(handle);
    }

    @Override
    public boolean contains(IControlHandle handle)
    {
        return m_ctrls.contains(handle);
    }

    @Override
    public void hide()
    {
        switchVisibility(false);
    }

    @Override
    public boolean isVisible()
    {
        return m_ctrls.isVisible();
    }

    public WiresShape getWiresShape()
    {
        return m_wires_shape;
    }

    @Override
    public HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_ctrls.getHandlerRegistrationManager();
    }

    @Override
    public boolean isActive()
    {
        return m_ctrls.isActive();
    }

    @Override
    public boolean setActive(boolean b)
    {
        return m_ctrls.setActive(b);
    }

    @Override
    public Iterator<IControlHandle> iterator()
    {
        return m_ctrls.iterator();
    }

    private void initControlsListeners()
    {
        // Control points - to provide the resize support.
        if (IControlHandle.ControlHandleStandardType.RESIZE.equals(m_ctrls_type))
        {
            for (int i = 0; i < POINTS_SIZE; i++)
            {
                final IControlHandle handle = m_ctrls.getHandle(i);
                final IPrimitive<?> control = handle.getControl();
                control.setUserData(this); // TODO (mdp) this is hack (and not robust, if something else re-uses this field) but for now it allows a fix in resize code that shifts the canvas location

                m_registrationManager.register(control.addNodeDragStartHandler(event -> WiresShapeControlHandleList.this.resizeStart(event)));

                m_registrationManager.register(control.addNodeDragMoveHandler(event -> WiresShapeControlHandleList.this.resizeMove(event)));

                m_registrationManager.register(control.addNodeDragEndHandler(event -> WiresShapeControlHandleList.this.resizeEnd(event)));
            }
        }

        // Shape container's drag.
        m_registrationManager.register(m_wires_shape.addWiresDragStartHandler(event -> updateParentLocation()));

        m_registrationManager.register(m_wires_shape.addWiresDragMoveHandler(event -> updateParentLocation()));

        m_registrationManager.register(m_wires_shape.addWiresDragEndHandler(event -> updateParentLocation()));

        // Shape container's position.
        m_registrationManager.register(m_wires_shape.addWiresMoveHandler(event -> updateParentLocation()));
    }

    protected void resizeStart(final AbstractNodeHumanInputEvent<NodeDragStartHandler, Node> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            // Ensure magnets hidden while resizing.
            if (null != m_wires_shape.getMagnets())
            {
                m_wires_shape.getMagnets().hide();
            }

            final double[] r = this.resizeWhileDrag();
            wiresResizeStartEvent.revive();
            wiresResizeStartEvent.override(m_wires_shape, dragEvent, (int) r[0], (int) r[1], r[2], r[3]);
            m_wires_shape.getHandlerManager().fireEvent(wiresResizeStartEvent);
            wiresResizeStartEvent.kill();
        }
    }

    protected void resizeMove(final AbstractNodeHumanInputEvent<NodeDragMoveHandler, Node> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            final double[] r = this.resizeWhileDrag();
            wiresResizeStepEvent.revive();
            wiresResizeStepEvent.override(m_wires_shape, dragEvent, (int) r[0], (int) r[1], r[2], r[3]);
            m_wires_shape.getHandlerManager().fireEvent(wiresResizeStepEvent);
            wiresResizeStepEvent.kill();
        }
    }

    protected void resizeEnd(final AbstractNodeHumanInputEvent<NodeDragEndHandler, Node> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            final double[] r = this.resizeWhileDrag();
            wiresResizeEndEvent.revive();
            wiresResizeEndEvent.override(m_wires_shape, dragEvent, (int) r[0], (int) r[1], r[2], r[3]);
            m_wires_shape.getHandlerManager().fireEvent(wiresResizeEndEvent);
            wiresResizeEndEvent.kill();
        }
    }

    private double[] resizeWhileDrag()
    {
        if (m_wires_shape.isResizable())
        {
            final Point2DArray points = getControlPointsArray();

            final double[] attrs = getBBAttributes(points);

            this.resize(attrs[0], attrs[1], attrs[2], attrs[3], false);

            return attrs;
        }

        return null;
    }

    protected void resize(final Double x, final Double y, final double width, final double height, final boolean refresh)
    {
        m_wires_shape.getLayoutContainer().setOffset(new Point2D(x, y));
        resize(width, height, refresh);
    }

    protected void resize(final double width, final double height, final boolean refresh)
    {
        m_wires_shape.getLayoutContainer().setSize(width, height);

        if (refresh)
        {
            m_wires_shape.getLayoutContainer().refresh();
        }

        m_wires_shape.getLayoutContainer().execute();

        if (null != m_wires_shape.getControl()) {
            m_wires_shape.getControl().getMagnetsControl().shapeChanged();
        }

        // Layout content whilst resizing
        m_wires_shape.getLayoutHandler().requestLayout( m_wires_shape );
    }


    private Point2DArray getControlPointsArray()
    {
        Point2DArray result = new Point2DArray();

        for (int i = 0; i < POINTS_SIZE; i++)
        {
            final IControlHandle handle = m_ctrls.getHandle(i);
            final IPrimitive<?> control = handle.getControl();
            final Point2D p = new Point2D(control.getX(), control.getY());
            result.push(p);
        }

        return result;
    }

    public void updateParentLocation()
    {
        if (null == parent && null != getGroup().getLayer())
        {
            this.parent = new Group();
            getGroup().getLayer().add(parent);
        }

        if (null == parent)
        {
            return;
        }

        final double[] ap = getAbsolute(getGroup());
        parent.setX(ap[0]);
        parent.setY(ap[1]);
        parent.moveToTop();

        for (final WiresShape child : m_wires_shape.getChildShapes().asList())
        {
            if (null != child.getControls())
            {
                child.getControls().updateParentLocation();
            }
        }
    }

    private double[] getBBAttributes(final Point2DArray controlPoints)
    {
        double minx = controlPoints.get(0).getX();
        double miny = controlPoints.get(0).getY();
        double maxx = controlPoints.get(0).getX();
        double maxy = controlPoints.get(0).getY();

        for (Point2D control : controlPoints.asArray())
        {
            if (control.getX() < minx)
            {
                minx = control.getX();
            }
            if (control.getX() > maxx)
            {
                maxx = control.getX();
            }
            if (control.getY() < miny)
            {
                miny = control.getY();
            }
            if (control.getY() > maxy)
            {
                maxy = control.getY();
            }
        }

        // Resize the primitives container.
        final double w = maxx - minx;
        final double h = maxy - miny;

        return new double[] { minx, miny, w, h };
    }

    private void switchVisibility(final boolean visible)
    {
        if (null == parent)
        {
            return;
        }

        // For usability goals let's ensure children shape's controls are hidden as
        // with the parent one.
        if (!visible)
        {
            for (WiresShape shape : m_wires_shape.getChildShapes().asList())
            {
                if (shape.getControls() == null)
                {
                    continue;
                }

                shape.getControls().hide();
            }
        }

        if (visible)
        {
            m_ctrls.showOn(parent);
        }
        else
        {
            m_ctrls.hide();
        }
    }

    private static double[] getAbsolute(final Group group)
    {
        final Point2D p = group.getComputedLocation();
        return new double[] { p.getX(), p.getY() };
    }

    private MultiPath getPath()
    {
        return m_wires_shape.getPath();
    }

    private Group getGroup()
    {
        return m_wires_shape.getGroup();
    }
}

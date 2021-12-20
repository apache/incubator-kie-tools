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

package com.ait.lienzo.client.core.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.core.types.ProxyType;

public abstract class CompositeProxy<C extends CompositeProxy<C, P>, P extends IPrimitive<?>> extends Node<C> implements IPrimitive<C> {

    private ProxyType m_type = null;

    private IControlHandleFactory m_controlHandleFactory = null;

    private DragConstraintEnforcer m_dragConstraintEnforcer = null;

    protected CompositeProxy(final ProxyType type) {
        super(NodeType.PROXY);

        m_type = type;
    }

    protected abstract P getProxy();

    protected void setProxyType(final ProxyType type) {
        m_type = type;
    }

    public ProxyType getProxyType() {
        return m_type;
    }

    @Override
    public boolean removeFromParent() {
        Node<?> parent = getParent();

        if (null != parent) {
            Layer layer = parent.asLayer();

            if (null != layer) {
                layer.remove(this);

                return true;
            }
            GroupOf<IPrimitive<?>, ?> group = parent.asGroupOf();

            if (null != group) {
                group.remove(this);

                return true;
            }
        }
        return false;
    }

    /**
     * Moves this shape one layer up.
     *
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveUp() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container) {
                container.moveUp(this);
            }
        }
        return cast();
    }

    /**
     * Moves this shape one layer down.
     *
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveDown() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container) {
                container.moveDown(this);
            }
        }
        return cast();
    }

    /**
     * Moves this shape to the top of the layers stack.
     *
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveToTop() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container) {
                container.moveToTop(this);
            }
        }
        return cast();
    }

    /**
     * Moves this shape to the bottomw of the layers stack.
     *
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveToBottom() {
        final Node<?> parent = getParent();

        if (null != parent) {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container) {
                container.moveToBottom(this);
            }
        }
        return cast();
    }

    @Override
    public Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types) {
        return getControlHandles(Arrays.asList(types));
    }

    @Override
    public Map<ControlHandleType, IControlHandleList> getControlHandles(List<ControlHandleType> types) {
        if ((null == types) || (types.isEmpty())) {
            return null;
        }
        if (types.size() > 1) {
            types = new ArrayList<ControlHandleType>(new HashSet<ControlHandleType>(types));
        }
        IControlHandleFactory factory = getControlHandleFactory();

        if (null == factory) {
            return null;
        }
        return factory.getControlHandles(types);
    }

    @Override
    public IControlHandleFactory getControlHandleFactory() {
        return m_controlHandleFactory;
    }

    @Override
    public C setControlHandleFactory(IControlHandleFactory factory) {
        m_controlHandleFactory = factory;

        return cast();
    }

    @Override
    public DragConstraintEnforcer getDragConstraints() {
        if (null == m_dragConstraintEnforcer) {
            return new DefaultDragConstraintEnforcer();
        } else {
            return m_dragConstraintEnforcer;
        }
    }

    @Override
    public C setDragConstraints(final DragConstraintEnforcer enforcer) {
        m_dragConstraintEnforcer = enforcer;

        return cast();
    }

    @Override
    public void attachToLayerColorMap() {
        getProxy().attachToLayerColorMap();
    }

    @Override
    public void detachFromLayerColorMap() {
        getProxy().detachFromLayerColorMap();
    }

    @Override
    public C refresh() {
        getProxy().refresh();

        return cast();
    }

    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, final BoundingBox bounds) {
        if ((context.isSelection()) && (!isListening())) {
            return;
        }
        alpha = alpha * getAlpha();

        if (alpha <= 0) {
            return;
        }
        getProxy().drawWithTransforms(context, alpha, bounds);
    }
}

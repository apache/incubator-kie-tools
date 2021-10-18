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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * A Container capable of holding a collection of T objects
 */
public abstract class GroupOf<T extends IPrimitive<?>, C extends GroupOf<T, C>> extends ContainerNode<T, C> implements IPrimitive<C>,
                                                                                                                       IDestroyable {

    private GroupType m_type = null;

    private final OptionalGroupOfFields m_opts = OptionalGroupOfFields.make();

    /**
     * Constructor. Creates an instance of a group.
     */
    @JsIgnore
    protected GroupOf(final GroupType type, final IStorageEngine<T> stor) {
        super(NodeType.GROUP, stor);

        m_type = type;
    }

    @Override
    public C draw() {
        final Layer layer = getLayer();

        if (null != layer) {
            layer.draw();
        }
        return cast();
    }

    @Override
    public C batch() {
        final Layer layer = getLayer();

        if (null != layer) {
            layer.batch();
        }
        return cast();
    }

    /**
     * Only sub-classes that wish to extend a Shape should use this.
     *
     * @param type
     */
    protected void setGroupType(final GroupType type) {
        m_type = type;
    }

    public GroupType getGroupType() {
        return m_type;
    }

    @Override
    public boolean isDragging() {
        return m_opts.isDragging();
    }

    @Override
    public C setDragging(final boolean drag) {
        m_opts.setDragging(drag);

        return cast();
    }

    /**
     * Returns this group as an {@link IPrimitive}.
     *
     * @return IPrimitive
     */
    @Override
    public IPrimitive<?> asPrimitive() {
        return this;
    }

    /**
     * Returns this group as a {@link IContainer}
     *
     * @return IContainer<IPrimitive>
     */
    @Override
    public IContainer<C, T> asContainer() {
        return cast();
    }

    @Override
    public GroupOf<IPrimitive<?>, ?> asGroupOf() {
        return cast();
    }

    @Override
    public Group asGroup() {
        if (this instanceof Group) {
            return cast();
        }
        return null;
    }

    /**
     * Adds a primitive to the collection. Override to ensure primitive is putString in Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every addBoundingBox we would have draws impacting performance.
     */
    @JsMethod // TODO: Only this method exposed for now.
    @Override
    public C add(final T child) {
        child.removeFromParent();

        super.add(child);

        child.attachToLayerColorMap();

        return cast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public C add(final T child, final T... children) {
        add(child);

        for (T node : children) {
            add(node);
        }
        return cast();
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
     * Removes a primitive from the container. Override to ensure primitive is removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every addBoundingBox we would have draws impacting performance.
     */
    @Override
    public C remove(final T child) {
        child.detachFromLayerColorMap();

        super.remove(child);

        return cast();
    }

    /**
     * Removes all primitives from the collection. Override to ensure all primitives are removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every addBoundingBox we would have draws impacting performance.
     */
    @Override
    public C removeAll() {
        detachFromLayerColorMap();

        super.removeAll();

        return cast();
    }

    @Override
    public void destroy() {
        destroy(this);
    }

    public static <T extends IPrimitive<?>, C extends GroupOf<T, C>> void destroy(final GroupOf<T, C> group) {
        final NFastArrayList<T> children = group.getChildNodes();
        for (final T child : children.asList()) {
            if (child instanceof IDestroyable) {
                ((IDestroyable) child).destroy();
            }
        }
        group.removeAll();
        group.removeFromParent();
    }

    /**
     * Attaches all primitives to the Layers Color Map
     */
    @Override
    public void attachToLayerColorMap() {
        final Layer layer = getLayer();

        if (null != layer) {
            final NFastArrayList<T> list = getChildNodes();

            if (null != list) {
                final int size = list.size();

                for (int i = 0; i < size; i++) {
                    list.get(i).attachToLayerColorMap();
                }
            }
        }
    }

    /**
     * Detaches all primitives from the Layers Color Map
     */
    @Override
    public void detachFromLayerColorMap() {
        final Layer layer = getLayer();

        if (null != layer) {
            final NFastArrayList<T> list = getChildNodes();

            if (null != list) {
                final int size = list.size();

                for (int i = 0; i < size; i++) {
                    list.get(i).detachFromLayerColorMap();
                }
            }
        }
    }

    /**
     * Moves this group's {@link Layer} one level up
     *
     * @return Group this Group
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
     * Moves this group's {@link Layer} one level down
     *
     * @return Group this Group
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
     * Moves this group's {@link Layer} to the top of the layer stack.
     *
     * @return Group this Group
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
     * Moves this group's {@link Layer} to the bottom of the layer stack.
     *
     * @return Group this Group
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
    protected void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff) {
        if (predicate.test(this)) {
            buff.add(this);
        }
        final NFastArrayList<T> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++) {
            final T prim = list.get(i);

            if (null != prim) {
                final Node<?> node = prim.asNode();

                if (null != node) {
                    if (predicate.test(node)) {
                        buff.add(node);
                    }
                    final ContainerNode<?, ?> cont = node.asContainerNode();

                    if (null != cont) {
                        cont.find(predicate, buff);
                    }
                }
            }
        }
    }

    @Override
    public DragConstraintEnforcer getDragConstraints() {
        final DragConstraintEnforcer enforcer = m_opts.getDragConstraintEnforcer();

        if (enforcer == null) {
            return new DefaultDragConstraintEnforcer();
        } else {
            return enforcer;
        }
    }

    @Override
    public C setDragConstraints(final DragConstraintEnforcer enforcer) {
        m_opts.setDragConstraintEnforcer(enforcer);

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
            types = new ArrayList<>(new HashSet<>(types));
        }
        IControlHandleFactory factory = getControlHandleFactory();

        if (null == factory) {
            return null;
        }
        return factory.getControlHandles(types);
    }

    @Override
    public IControlHandleFactory getControlHandleFactory() {
        return m_opts.getControlHandleFactory();
    }

    @Override
    public C setControlHandleFactory(IControlHandleFactory factory) {
        m_opts.setControlHandleFactory(factory);

        return cast();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return new ArrayList<Attribute>();
    }

    @Override
    public List<Attribute> getTransformingAttributes() {
        return LienzoCore.STANDARD_TRANSFORMING_ATTRIBUTES;
    }

    @Override
    public C refresh() {
        final NFastArrayList<T> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++) {
            list.get(i).refresh();
        }
        return cast();
    }

    @JsType
    private static class OptionalGroupOfFields {

        @JsProperty
        private boolean drag;

        @JsIgnore
        private DragConstraintEnforcer denf;

        @JsIgnore
        private IControlHandleFactory hand;

        public static final OptionalGroupOfFields make() {
            return new OptionalGroupOfFields();
        }

        protected OptionalGroupOfFields() {
        }

        protected final boolean isDragging() {
            return this.drag;
        }

        protected final void setDragging(boolean drag) {
            this.drag = drag;
        }

        protected final DragConstraintEnforcer getDragConstraintEnforcer() {
            return this.denf;
        }

        protected final void setDragConstraintEnforcer(DragConstraintEnforcer denf) {
            this.denf = denf;
        }

        protected final IControlHandleFactory getControlHandleFactory() {
            return this.hand;
        }

        protected final void setControlHandleFactory(IControlHandleFactory hand) {
            this.hand = hand;
        }
    }
}

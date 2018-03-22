/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.client.core.shape.ContainerNode;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public final class SVGContainer extends SVGPrimitive<Group> {

    private final String id;
    private final List<SVGPrimitive<?>> children;

    public SVGContainer(final String id,
                        final Group primitive,
                        final boolean scalable,
                        final LayoutContainer.Layout layout) {
        super(primitive, scalable, layout);
        this.id = id;
        this.children = new LinkedList<>();
    }

    @SuppressWarnings("unchecked")
    public SVGContainer addPrimitive(final IPrimitive<?> primitive) {
        final ContainerNode container = (ContainerNode) getPrimitive(id);
        if (null != container) {
            container.add(primitive);
        }
        return this;
    }

    public SVGContainer add(final SVGPrimitiveShape primitive) {
        children.add(primitive);
        addPrimitive(primitive.get());
        return this;
    }

    public String getId() {
        return id;
    }

    public List<SVGPrimitive<?>> getChildren() {
        return children;
    }

    public IPrimitive getPrimitive(final String uuid) {
        return getPrimitive(get(), uuid);
    }

    @SuppressWarnings("unchecked")
    private static IPrimitive getPrimitive(final IContainer<?, IPrimitive<?>> container,
                                           final String uuid) {
        if (null != container.getID() && container.getID().equals(uuid)) {
            return (IPrimitive) container;
        }
        final NFastArrayList<IPrimitive<?>> childNodes = container.getChildNodes();
        if (null != childNodes) {
            for (final IPrimitive node : childNodes) {
                if (null != node.getID() && node.getID().equals(uuid)) {
                    return node;
                } else if (node instanceof IContainer) {
                    final IPrimitive p = getPrimitive((IContainer<?, IPrimitive<?>>) node,
                                                      uuid);
                    if (null != p) {
                        return p;
                    }
                }
            }
        }
        return null;
    }
}

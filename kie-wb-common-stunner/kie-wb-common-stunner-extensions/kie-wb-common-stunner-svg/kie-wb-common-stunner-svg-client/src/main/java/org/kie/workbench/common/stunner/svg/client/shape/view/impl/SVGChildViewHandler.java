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

package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ait.lienzo.client.core.shape.ContainerNode;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;

public class SVGChildViewHandler {

    private static Logger LOGGER = Logger.getLogger(SVGChildViewHandler.class.getName());

    private final IContainer<?, IPrimitive<?>> svgContainer;
    private final double width;
    private final double height;
    private final List<SVGBasicShapeView> children = new LinkedList<>();

    public SVGChildViewHandler(final IContainer<?, IPrimitive<?>> svgContainer,
                               final double width,
                               final double height) {
        this.svgContainer = svgContainer;
        this.width = width;
        this.height = height;
    }

    @SuppressWarnings("unchecked")
    public void addSVGChild(final String parent,
                            final SVGBasicShapeView child) {
        if (!hasChild(child)) {
            children.add(child);
            final ContainerNode container = (ContainerNode) getPrimitive(parent);
            if (null != container) {
                final IPrimitive childContainer = (IPrimitive) child.getContainer();
                container.add(childContainer);
            } else {
                LOGGER.log(Level.SEVERE,
                           "The expected container node [" + parent + "] has not been found.");
            }
        }
    }

    public Collection<SVGBasicShapeView> getSVGChildren() {
        return children;
    }

    public void clear() {
        children.clear();
    }

    private boolean hasChild(final SVGBasicShapeView child) {
        final String name = child.getName();
        return children.stream().filter(c -> name.equals(c.getName())).findAny().isPresent();
    }

    private IPrimitive getPrimitive(final String uuid) {
        return getPrimitive(svgContainer,
                            uuid);
    }

    @SuppressWarnings("unchecked")
    private IPrimitive getPrimitive(final IContainer<?, IPrimitive<?>> container,
                                    final String uuid) {
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

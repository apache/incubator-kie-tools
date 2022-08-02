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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

/**
 * Extension for a Node Shape that can contain child shapes.
 * @param <W> The bean type.
 * @param <D> The mutable shape definition type..
 * @param <V> The view type.
 * @param <S> The shape's type that it can contain.
 */
public class ContainerShape<W, V extends ShapeView, S extends Shape>
        extends NodeShapeImpl<W, V>
        implements HasChildren<S> {

    private final List<S> children = new LinkedList<S>();

    public ContainerShape(final AbstractShape<V> shape) {
        super(shape);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addChild(final S child,
                         final Layout layout) {
        final HasChildren<ShapeView<?>> view = (HasChildren<ShapeView<?>>) getShapeView();
        view.addChild(child.getShapeView(),
                      layout);
        children.add(child);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeChild(final S child) {
        final HasChildren<ShapeView<?>> view = (HasChildren<ShapeView<?>>) getShapeView();
        view.removeChild(child.getShapeView());
        children.remove(child);
    }

    @Override
    public Iterable<S> getChildren() {
        return children;
    }

    public S getChild(final String uuid) {
        return children.stream()
                .filter(c -> c.getUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}

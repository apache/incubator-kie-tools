/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.drag;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

@Dependent
public class ShapeDragProxyImpl implements ShapeDragProxy<AbstractCanvas> {

    ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory;

    @Inject
    public ShapeDragProxyImpl(final ShapeViewDragProxy<AbstractCanvas> shapeViewDragProxyFactory) {
        this.shapeViewDragProxyFactory = shapeViewDragProxyFactory;
    }

    @Override
    public DragProxy<AbstractCanvas, Shape<?>, DragProxyCallback> proxyFor(final AbstractCanvas context) {
        this.shapeViewDragProxyFactory.proxyFor(context);
        return this;
    }

    @Override
    public DragProxy<AbstractCanvas, Shape<?>, DragProxyCallback> show(final Shape<?> item,
                                                                       final int x,
                                                                       final int y,
                                                                       final DragProxyCallback callback) {
        clear();
        shapeViewDragProxyFactory.show(item.getShapeView(),
                                       x,
                                       y,
                                       callback);
        return this;
    }

    @Override
    public void clear() {
        if (null != this.shapeViewDragProxyFactory) {
            this.shapeViewDragProxyFactory.clear();
        }
    }

    @Override
    public void destroy() {
        if (null != this.shapeViewDragProxyFactory) {
            this.shapeViewDragProxyFactory.destroy();
        }
        this.shapeViewDragProxyFactory = null;
    }
}

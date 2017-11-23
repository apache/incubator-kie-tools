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

import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandlersDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class AbstractElementShape<W, C extends View<W>, E extends Element<C>, D extends ShapeViewDef<W, V>, V extends ShapeView>
        implements ElementShape<W, C, E, V>,
                   Lifecycle {

    private final ShapeImpl<V> shape;
    private final ShapeViewHandlersDef<W, V, D> shapeHandlersDef;

    protected AbstractElementShape(final D shapeDef,
                                   final V view) {
        this.shapeHandlersDef = new ShapeViewHandlersDef<>(shapeDef);
        this.shape = new ShapeImpl<V>(view,
                                      new ShapeStateStrokeHandler<>());
        getShape().getShapeStateHandler().forShape(this);
    }

    protected AbstractElementShape(final D shapeDef,
                                   final V view,
                                   final ShapeStateHandler<V, Shape<V>> shapeStateHelper) {
        this.shapeHandlersDef = new ShapeViewHandlersDef<>(shapeDef);
        this.shape = new ShapeImpl<V>(view,
                                      shapeStateHelper);
        getShape().getShapeStateHandler().forShape(this);
    }

    @Override
    public void setUUID(final String uuid) {
        shape.setUUID(uuid);
    }

    @Override
    public String getUUID() {
        return shape.getUUID();
    }

    @Override
    public void applyTitle(final String title,
                           final E element,
                           final MutationContext mutationContext) {
        getShapeHandlersDef()
                .titleHandler()
                .ifPresent(h -> h.accept(title, getShapeView()));
        getShapeHandlersDef()
                .fontHandler()
                .ifPresent(h -> h.accept(getDefinition(element), getShapeView()));
    }

    @Override
    public void beforeDraw() {
        shape.beforeDraw();
    }

    @Override
    public void afterDraw() {
        shape.afterDraw();
    }

    @Override
    public void applyProperties(final E element,
                                final MutationContext mutationContext) {
        final ShapeState shapeState = getShape().getShapeStateHandler().reset();
        // Apply generic view operations.
        getShapeHandlersDef()
                .viewHandler()
                .accept(getDefinition(element), getShapeView());
        // Apply custom view operations.
        applyCustomProperties(element, mutationContext);
        // Apply size operations.
        getShapeHandlersDef()
                .sizeHandler()
                .ifPresent(h -> h.accept(element.getContent(), getShapeView()));
        getShape()
                .getShapeStateHandler()
                .shapeUpdated()
                .applyState(shapeState);
    }

    protected void applyCustomProperties(final E element,
                                         final MutationContext mutationContext) {
    }

    @Override
    public V getShapeView() {
        return shape.getShapeView();
    }

    @Override
    public void destroy() {
        shape.destroy();
    }

    public ShapeImpl<V> getShape() {
        return shape;
    }

    public ShapeViewHandlersDef<W, V, D> getShapeHandlersDef() {
        return shapeHandlersDef;
    }

    public D getShapeDefinition() {
        return getShapeHandlersDef().getShapeViewDef();
    }

    protected W getDefinition(final Element<? extends View<W>> element) {
        return element.getContent().getDefinition();
    }
}

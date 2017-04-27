/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class AbstractElementShape<W, C extends View<W>, E extends Element<C>, D extends MutableShapeDef<W>, V extends ShapeView<?>>
        implements ElementShape<W, C, E, V>,
                   Lifecycle {

    private final ShapeImpl<V> shape;
    private final ShapeDefViewHandler<W, V, D> defViewHandler;

    protected AbstractElementShape(final D shapeDef,
                                   final V view) {
        this.shape = new ShapeImpl<V>(view,
                                      new ShapeStateHelper<V, Shape<V>>());
        this.defViewHandler = new ShapeDefViewHandler<W, V, D>(shapeDef,
                                                               view);
    }

    protected AbstractElementShape(final D shapeDef,
                                   final V view,
                                   final ShapeStateHelper<V, Shape<V>> shapeStateHelper) {
        this.shape = new ShapeImpl<V>(view,
                                      shapeStateHelper);
        this.defViewHandler = new ShapeDefViewHandler<W, V, D>(shapeDef,
                                                               view);
    }

    private void init() {
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
        getDefViewHandler().applyTitle(title,
                                       getDefinition(element),
                                       mutationContext);
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
        getDefViewHandler().applyProperties(getDefinition(element),
                                            mutationContext);
        getShape().getShapeStateHelper().save(ShapeState.NONE::equals);
    }

    @Override
    public V getShapeView() {
        return shape.getShapeView();
    }

    @Override
    public void destroy() {
        shape.destroy();
    }

    public ShapeDefViewHandler<W, V, D> getDefViewHandler() {
        return defViewHandler;
    }

    public ShapeImpl<V> getShape() {
        return shape;
    }

    public D getShapeDefinition() {
        return defViewHandler.getShapeDefinition();
    }

    protected W getDefinition(final Element<? extends View<W>> element) {
        return element.getContent().getDefinition();
    }
}

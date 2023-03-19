/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class AbstractElementShape<W, C extends View<W>, E extends Element<C>, V extends ShapeView>
        implements ElementShape<W, C, E, V>,
                   Lifecycle {

    protected AbstractElementShape() {
    }

    protected abstract AbstractShape<V> getShape();

    @Override
    public void setUUID(final String uuid) {
        getShape().setUUID(uuid);
    }

    @Override
    public String getUUID() {
        return getShape().getUUID();
    }

    @Override
    public void applyTitle(final String title,
                           final E element,
                           final MutationContext mutationContext) {
    }

    @Override
    public void beforeDraw() {
        getShape().beforeDraw();
    }

    @Override
    public void afterDraw() {
        getShape().afterDraw();
    }

    @Override
    public void applyProperties(final E element,
                                final MutationContext mutationContext) {
        final ShapeState shapeState = getShape().getShapeStateHandler().reset();
        // Apply custom view operations.
        applyCustomProperties(element, mutationContext);
        getShape()
                .getShapeStateHandler()
                .shapeAttributesChanged()
                .applyState(shapeState);
    }

    protected void applyCustomProperties(final E element,
                                         final MutationContext mutationContext) {
    }

    @Override
    public V getShapeView() {
        return getShape().getShapeView();
    }

    protected W getDefinition(final Element<? extends View<W>> element) {
        return element.getContent().getDefinition();
    }
}

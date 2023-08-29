/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandlersDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class AbstractElementShape<W, C extends View<W>, E extends Element<C>, D extends ShapeViewDef<W, V>, V extends ShapeView>
        implements ElementShape<W, C, E, V>,
                   Lifecycle {

    private final ShapeViewHandlersDef<W, V, D> shapeHandlersDef;

    protected AbstractElementShape(final D shapeDef) {
        this.shapeHandlersDef = new ShapeViewHandlersDef<>(shapeDef);
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
        //first set the font properties
        getShapeHandlersDef()
                .fontHandler()
                .ifPresent(h -> h.accept(getDefinition(element), getShapeView()));
        //after set the title
        getShapeHandlersDef()
                .titleHandler()
                .ifPresent(h -> h.accept(title, getShapeView()));
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

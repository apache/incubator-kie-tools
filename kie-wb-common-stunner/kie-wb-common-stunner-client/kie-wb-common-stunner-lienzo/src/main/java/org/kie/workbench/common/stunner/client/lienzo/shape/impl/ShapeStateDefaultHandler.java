/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.client.lienzo.util.ShapeViewUserDataEncoder;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttributes;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributesFactory;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.HasShadow;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class ShapeStateDefaultHandler
        implements ShapeStateHandler {

    public enum RenderType {
        FILL(ShapeStateAttributesFactory::buildFillAttributes),
        STROKE(ShapeStateAttributesFactory::buildStrokeAttributes);

        private final Function<ShapeState, ShapeStateAttributes> stateAttributesProvider;

        RenderType(final Function<ShapeState, ShapeStateAttributes> stateAttributesProvider) {
            this.stateAttributesProvider = state -> {
                final ShapeStateAttributes attributes = stateAttributesProvider.apply(state);
                if (isStateSelected(state)) {
                    attributes
                            .unset(ShapeStateAttributeHandler.ShapeStateAttribute.FILL_COLOR)
                            .unset(ShapeStateAttributeHandler.ShapeStateAttribute.STROKE_COLOR);
                }
                return attributes;
            };
        }
    }

    public enum ShapeType {
        BACKGROUND,
        BORDER,
        CONTAINER
    }

    private static final Shadow SHADOW_HIGHLIGHT = new Shadow(ColorName.BLACK.getColor().setA(0.40), 10, 0, 0);
    private static final Shadow SHADOW_SELECTED = new Shadow(ColorName.BLACK.getColor().setA(0.40), 5, 2, 2);

    private final ShapeStateAttributeAnimationHandler<LienzoShapeView<?>> handler;
    private Supplier<LienzoShapeView<?>> backgroundShapeSupplier;
    private Supplier<LienzoShapeView<?>> borderShapeSupplier;
    private ShapeViewUserDataEncoder shapeViewDataEncoder;

    public ShapeStateDefaultHandler() {
        this(new ShapeStateAttributeAnimationHandler<>());
    }

    ShapeStateDefaultHandler(final ShapeStateAttributeAnimationHandler<LienzoShapeView<?>> handler) {
        this.handler = handler.onComplete(this::applyShadow);
        this.shapeViewDataEncoder = ShapeViewUserDataEncoder.get();
        setRenderType(RenderType.STROKE);
    }

    public ShapeStateDefaultHandler setRenderType(final RenderType renderType) {
        handler.getAttributesHandler().useAttributes(renderType.stateAttributesProvider);

        /**
         * TODO: need to fix when resolution of JBPM-7681 is available
         * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
         */
//        shapeViewDataEncoder.applyShapeViewRenderType(borderShapeSupplier, renderType);
        return this;
    }

    public ShapeStateDefaultHandler setBorderShape(final Supplier<LienzoShapeView<?>> shapeSupplier) {
        handler.getAttributesHandler().setView(shapeSupplier);
        borderShapeSupplier = shapeSupplier;

        /**
         * TODO: need to fix when resolution of JBPM-7681 is available
         * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
         */
//        shapeViewDataEncoder.applyShapeViewType(shapeSupplier, ShapeType.BORDER);
        return this;
    }

    public ShapeStateDefaultHandler setBackgroundShape(final Supplier<LienzoShapeView<?>> shapeSupplier) {
        backgroundShapeSupplier = shapeSupplier;

        /**
         * TODO: need to fix when resolution of JBPM-7681 is available
         * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
         */
//        shapeViewDataEncoder.applyShapeViewType(shapeSupplier, ShapeType.BACKGROUND);
        return this;
    }

    //TODO: this should be called on the SVGShapeView when a subprocess is identified
    public ShapeStateDefaultHandler setContainerShape(final Supplier<LienzoShapeView<?>> shapeSupplier) {
        shapeViewDataEncoder.applyShapeViewType(shapeSupplier, ShapeType.CONTAINER);
        return this;
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        handler.applyState(shapeState);
    }

    @Override
    public ShapeStateHandler shapeAttributesChanged() {
        handler.shapeAttributesChanged();
        return this;
    }

    @Override
    public ShapeState reset() {
        getShadowShape().ifPresent(this::removeShadow);
        return handler.reset();
    }

    @Override
    public ShapeState getShapeState() {
        return handler.getShapeState();
    }

    private void applyShadow() {
        getShadowShape().ifPresent(this::updateShadow);
    }

    private void removeShadow(final HasShadow shape) {
        shape.removeShadow();
    }

    private void updateShadow(final HasShadow shape) {
        if (isStateSelected(handler.getShapeState())) {
            shape.setShadow(SHADOW_SELECTED.getColor(),
                            SHADOW_SELECTED.getBlur(),
                            SHADOW_SELECTED.getOffset().getX(),
                            SHADOW_SELECTED.getOffset().getY());
        } else if (isStateHighlight(handler.getShapeState())) {
            shape.setShadow(SHADOW_HIGHLIGHT.getColor(),
                            SHADOW_HIGHLIGHT.getBlur(),
                            SHADOW_HIGHLIGHT.getOffset().getX(),
                            SHADOW_HIGHLIGHT.getOffset().getY());
        } else {
            removeShadow(shape);
        }
    }

    private Optional<HasShadow> getShadowShape() {
        return getShadowShape(handler.getAttributesHandler().getShapeView());
    }

    private Optional<HasShadow> getShadowShape(final ShapeView<?> shape) {
        final ShapeView<?> candidate = null != getBackgroundShape() ? getBackgroundShape() : shape;
        if (candidate instanceof HasShadow) {
            return Optional.of((HasShadow) candidate);
        }
        return Optional.empty();
    }

    LienzoShapeView<?> getBackgroundShape() {
        return null != backgroundShapeSupplier ? backgroundShapeSupplier.get() : null;
    }

    private static boolean isStateSelected(ShapeState state) {
        return ShapeState.SELECTED.equals(state);
    }

    private static boolean isStateHighlight(ShapeState state) {
        return ShapeState.HIGHLIGHT.equals(state);
    }
}
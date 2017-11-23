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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class ShapeStateStrokeHandler<V extends ShapeView<?>, S extends Shape<V>>
        implements ShapeStateHandler<V, S> {

    private static final double ACTIVE_STROKE_WIDTH_PCT = 1d;
    private static final double ACTIVE_STROKE_ALPHA = 1d;

    private final BiConsumer<S, ShapeStrokeState> strokeShapeStateHandler;
    private final Function<ShapeState, String> stateColorProvider;
    private final ShapeStrokeState strokeState;
    private ShapeState state;
    private Optional<S> shape = Optional.empty();

    public ShapeStateStrokeHandler() {
        this(ShapeStateStrokeHandler::applyViewStrokeState);
    }

    public ShapeStateStrokeHandler(final BiConsumer<S, ShapeStrokeState> strokeShapeStateHandler) {
        this(strokeShapeStateHandler, ShapeState::getColor);
    }

    public ShapeStateStrokeHandler(final BiConsumer<S, ShapeStrokeState> strokeShapeStateHandler,
                                   final Function<ShapeState, String> stateColorProvider) {
        this.strokeState = new ShapeStrokeState().setActiveStrokeAlpha(ACTIVE_STROKE_ALPHA);
        this.stateColorProvider = stateColorProvider;
        this.strokeShapeStateHandler = strokeShapeStateHandler;
    }

    public void setStrokeWidthForActiveState(final double activeStrokeWidth) {
        this.strokeState.activeStrokeWidth = activeStrokeWidth;
    }

    @Override
    public ShapeStateHandler<V, S> forShape(final S shape) {
        this.shape = Optional.ofNullable(shape);
        this.state = ShapeState.NONE;
        saveState();
        return this;
    }

    @Override
    public ShapeStateHandler<V, S> shapeUpdated() {
        if (state.equals(ShapeState.NONE)) {
            saveState();
        }
        return this;
    }

    private void saveState() {
        shape.ifPresent((s) -> this.strokeState.strokeWidth = s.getShapeView().getStrokeWidth());
        shape.ifPresent((s) -> this.strokeState.activeStrokeWidth =
                strokeState.strokeWidth + (strokeState.strokeWidth * ACTIVE_STROKE_WIDTH_PCT));
        shape.ifPresent((s) -> this.strokeState.strokeAlpha = s.getShapeView().getStrokeAlpha());
        shape.ifPresent((s) -> this.strokeState.strokeColor = s.getShapeView().getStrokeColor());
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        if (!this.state.equals(shapeState)) {
            this.state = shapeState;
            switch (shapeState) {
                case SELECTED:
                case HIGHLIGHT:
                case INVALID:
                    runState(shapeState);
                    break;
                default:
                    runStrokeStateHandler(strokeState);
                    break;
            }
        }
    }

    @Override
    public ShapeState reset() {
        final ShapeState result = this.state;
        applyViewStroke(getShape(),
                        strokeState.strokeColor,
                        strokeState.strokeWidth,
                        strokeState.strokeAlpha);
        this.state = ShapeState.NONE;
        return result;
    }

    @Override
    public ShapeState getShapeState() {
        return state;
    }

    protected S getShape() {
        return shape.orElseThrow(() -> new IllegalArgumentException("Shape has not been set."));
    }

    ShapeStrokeState getStrokeState() {
        return strokeState;
    }

    private void runState(ShapeState state) {
        final String color = stateColorProvider.apply(state);
        if (null != color && color.trim().length() > 0) {
            runStrokeStateHandler(strokeState.copy().setStrokeColor(color));
        }
    }

    private void runStrokeStateHandler(final ShapeStrokeState state) {
        strokeShapeStateHandler.accept(getShape(), state);
    }

    private static void applyViewStrokeState(final Shape<? extends ShapeView<?>> shape,
                                             final ShapeStrokeState state) {
        applyViewStroke(shape,
                        state.strokeColor,
                        state.activeStrokeWidth,
                        state.activeStrokeAlpha);
    }

    private static void applyViewStroke(final Shape<? extends ShapeView<?>> shape,
                                        final String color,
                                        final double width,
                                        final double alpha) {
        shape.getShapeView().setStrokeColor(color);
        shape.getShapeView().setStrokeWidth(width);
        shape.getShapeView().setStrokeAlpha(alpha);
    }

    public static class ShapeStrokeState {

        private String strokeColor;
        private double strokeWidth;
        private double strokeAlpha;
        private double activeStrokeAlpha;
        private double activeStrokeWidth;

        public ShapeStrokeState() {
        }

        public ShapeStrokeState(String strokeColor,
                                double strokeWidth,
                                double strokeAlpha) {
            this.strokeColor = strokeColor;
            this.strokeWidth = strokeWidth;
            this.strokeAlpha = strokeAlpha;
        }

        public ShapeStrokeState copy() {
            final ShapeStrokeState s = new ShapeStrokeState(strokeColor,
                                                            strokeWidth,
                                                            strokeAlpha);
            s.activeStrokeAlpha = activeStrokeAlpha;
            s.activeStrokeWidth = activeStrokeWidth;
            return s;
        }

        public double getStrokeWidth() {
            return strokeWidth;
        }

        public ShapeStrokeState setStrokeWidth(double strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public double getActiveStrokeWidth() {
            return activeStrokeWidth;
        }

        public ShapeStrokeState setActiveStrokeWidth(double activeStrokeWidth) {
            this.activeStrokeWidth = activeStrokeWidth;
            return this;
        }

        public double getActiveStrokeAlpha() {
            return activeStrokeAlpha;
        }

        public ShapeStrokeState setActiveStrokeAlpha(double activeStrokeAlpha) {
            this.activeStrokeAlpha = activeStrokeAlpha;
            return this;
        }

        public double getStrokeAlpha() {
            return strokeAlpha;
        }

        public ShapeStrokeState setStrokeAlpha(double strokeAlpha) {
            this.strokeAlpha = strokeAlpha;
            return this;
        }

        public String getStrokeColor() {
            return strokeColor;
        }

        public ShapeStrokeState setStrokeColor(String strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }
    }
}

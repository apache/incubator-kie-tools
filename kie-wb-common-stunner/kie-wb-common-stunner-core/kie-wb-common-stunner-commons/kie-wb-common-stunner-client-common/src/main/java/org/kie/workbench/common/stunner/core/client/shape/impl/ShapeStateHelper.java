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

import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class ShapeStateHelper<V extends ShapeView, S extends Shape<V>> {

    public static final double ACTIVE_STROKE_WIDTH = 5d;
    public static final double ACTIVE_STROKE_ALPHA = 1d;

    /*
        The following instance members:
            - strokeWidth
            - strokeAlpha
            - strokeColor
        Are used to keep the original stroke attributes from the
        domain model object because the behavior for changing
        this shape state is based on updating the shape's borders.
        Eg: when the shape is in SELECTED state, the borders are
        using different colors/sizes, so to be able to get
        back to NONE state, it just reverts the border attributes
        to these private instance members.
     */
    private Double strokeWidth;
    private Double strokeAlpha;
    private String strokeColor;
    private ShapeState state;
    private double activeStrokeWidth;
    private S shape;

    public ShapeStateHelper() {
        init();
        this.activeStrokeWidth = ACTIVE_STROKE_WIDTH;
    }

    public ShapeStateHelper(final S shape) {
        this();
        this.shape = shape;
    }

    public ShapeStateHelper forShape(final S shape) {
        this.shape = shape;
        return this;
    }

    public void setStrokeWidthForActiveState(final double activeStrokeWidth) {
        this.activeStrokeWidth = activeStrokeWidth;
    }

    public ShapeStateHelper save(final Predicate<ShapeState> stateFilter) {
        if (stateFilter.test(this.state)) {
            this.strokeWidth = getShapeView().getStrokeWidth();
            this.strokeAlpha = getShapeView().getStrokeAlpha();
            this.strokeColor = getShapeView().getStrokeColor();
        }
        return this;
    }

    public ShapeStateHelper applyState(final ShapeState shapeState) {
        if (!this.state.equals(shapeState)) {
            this.state = shapeState;
            if (ShapeState.SELECTED.equals(shapeState)) {
                applySelectedState();
            } else if (ShapeState.HIGHLIGHT.equals(shapeState)) {
                applyHighlightState();
            } else if (ShapeState.INVALID.equals(shapeState)) {
                applyInvalidState();
            } else {
                applyNoneState(strokeColor,
                               null != this.strokeWidth ? this.strokeWidth : 1,
                               null != this.strokeAlpha ? this.strokeAlpha : 1);
            }
        }
        return this;
    }

    public ShapeState getState() {
        return state;
    }

    protected void applyActiveState(final String color) {
        getShapeView().setStrokeColor(color);
        getShapeView().setStrokeWidth(activeStrokeWidth);
        getShapeView().setStrokeAlpha(ACTIVE_STROKE_ALPHA);
    }

    protected void applyNoneState(final String color,
                                  final double width,
                                  final double alpha) {
        getShapeView().setStrokeColor(color);
        getShapeView().setStrokeWidth(width);
        getShapeView().setStrokeAlpha(alpha);
    }

    protected void init() {
        this.state = ShapeState.NONE;
    }

    protected double getActiveStrokeWidth() {
        return activeStrokeWidth;
    }

    protected S getShape() {
        return shape;
    }

    private void applySelectedState() {
        applyActiveState(ShapeState.SELECTED.getColor());
    }

    private void applyInvalidState() {
        applyActiveState(ShapeState.INVALID.getColor());
    }

    private void applyHighlightState() {
        applyActiveState(ShapeState.HIGHLIGHT.getColor());
    }

    private V getShapeView() {
        return shape.getShapeView();
    }

}

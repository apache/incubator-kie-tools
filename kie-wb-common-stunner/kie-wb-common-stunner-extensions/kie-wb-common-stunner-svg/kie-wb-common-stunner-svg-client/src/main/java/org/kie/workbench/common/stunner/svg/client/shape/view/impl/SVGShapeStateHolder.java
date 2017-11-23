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

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;

public class SVGShapeStateHolder {

    private static final byte ALPHA = 0;
    private static final byte FILL_COLOR = 1;
    private static final byte FILL_ALPHA = 2;
    private static final byte STROKE_COLOR = 3;
    private static final byte STROKE_ALPHA = 4;
    private static final byte STROKE_WIDTH = 5;
    private final ShapeState shapeState;
    private final Object[] holder;

    public static class Builder {

        private final ShapeState shapeState;
        private final Object[] holder;

        public Builder(ShapeState shapeState) {
            this.shapeState = shapeState;
            this.holder = new Object[]{null, null, null, null, null, null};
        }

        public Builder alpha(double alpha) {
            this.holder[ALPHA] = alpha;
            return this;
        }

        public Builder fillColor(String value) {
            this.holder[FILL_COLOR] = value;
            return this;
        }

        public Builder fillAlpha(double value) {
            this.holder[FILL_ALPHA] = value;
            return this;
        }

        public Builder strokeColor(String value) {
            this.holder[STROKE_COLOR] = value;
            return this;
        }

        public Builder strokeAlpha(double value) {
            this.holder[STROKE_ALPHA] = value;
            return this;
        }

        public Builder strokeWidth(double value) {
            this.holder[STROKE_WIDTH] = value;
            return this;
        }

        public SVGShapeStateHolder build() {
            return new SVGShapeStateHolder(shapeState,
                                           (Double) holder[ALPHA],
                                           (String) holder[FILL_COLOR],
                                           (Double) holder[FILL_ALPHA],
                                           (String) holder[STROKE_COLOR],
                                           (Double) holder[STROKE_ALPHA],
                                           (Double) holder[STROKE_WIDTH]);
        }
    }

    public SVGShapeStateHolder(final ShapeState shapeState,
                               final Double alpha,
                               final String fillColor,
                               final Double fillAlpha,
                               final String strokeColor,
                               final Double strokeAlpha,
                               final Double strokeWidth) {
        this.shapeState = shapeState;
        this.holder = new Object[6];
        this.holder[ALPHA] = alpha;
        this.holder[FILL_COLOR] = fillColor;
        this.holder[FILL_ALPHA] = fillAlpha;
        this.holder[STROKE_COLOR] = strokeColor;
        this.holder[STROKE_ALPHA] = strokeAlpha;
        this.holder[STROKE_WIDTH] = strokeWidth;
    }

    public ShapeState getState() {
        return shapeState;
    }

    public boolean hasAlpha() {
        return null != holder[ALPHA];
    }

    public double getAlpha() {
        return (double) holder[ALPHA];
    }

    public boolean hasFillColor() {
        return null != holder[FILL_COLOR];
    }

    public String getFillColor() {
        return (String) holder[FILL_COLOR];
    }

    public boolean hasFillAlpha() {
        return null != holder[FILL_ALPHA];
    }

    public double getFillAlpha() {
        return (double) holder[FILL_ALPHA];
    }

    public boolean hasStrokeColor() {
        return null != holder[STROKE_COLOR];
    }

    public String getStrokeColor() {
        return (String) holder[STROKE_COLOR];
    }

    public boolean hasStrokeAlpha() {
        return null != holder[STROKE_ALPHA];
    }

    public double getStrokeAlpha() {
        return (double) holder[STROKE_ALPHA];
    }

    public boolean hasStrokeWidth() {
        return null != holder[STROKE_WIDTH];
    }

    public double getStrokeWidth() {
        return (double) holder[STROKE_WIDTH];
    }
}

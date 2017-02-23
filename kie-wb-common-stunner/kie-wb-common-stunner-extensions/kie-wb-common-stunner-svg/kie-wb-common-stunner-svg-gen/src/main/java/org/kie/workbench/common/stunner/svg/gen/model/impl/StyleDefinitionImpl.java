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

package org.kie.workbench.common.stunner.svg.gen.model.impl;

import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;

public class StyleDefinitionImpl implements StyleDefinition {

    private final double alpha;
    private final String fillColor;
    private final double fillAlpha;
    private final String strokeColor;
    private final double strokeAlpha;
    private final double strokeWidth;

    private StyleDefinitionImpl(final double alpha,
                                final String fillColor,
                                final double fillAlpha,
                                final String strokeColor,
                                final double strokeAlpha,
                                final double strokeWidth) {
        this.alpha = alpha;
        this.fillColor = fillColor;
        this.fillAlpha = fillAlpha;
        this.strokeColor = strokeColor;
        this.strokeAlpha = strokeAlpha;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public double getFillAlpha() {
        return fillAlpha;
    }

    @Override
    public String getStrokeColor() {
        return strokeColor;
    }

    @Override
    public double getStrokeWidth() {
        return strokeWidth;
    }

    @Override
    public double getStrokeAlpha() {
        return strokeAlpha;
    }

    public static final class Builder {

        public static final double ATTR_OPACITY_DEFAULT = 1d;
        public static final String ATTR_FILL_DEFAULT = "#000000";
        public static final String ATTR_STROKE_DEFAULT = "#000000";
        public static final double ATTR_STROKE_OPACITY_DEFAULT = 1d;
        public static final double ATTR_STROKE_WIDTH_DEFAULT = 1d;

        private double alpha = ATTR_OPACITY_DEFAULT;
        private String fillColor = ATTR_FILL_DEFAULT;
        private double fillAlpha = ATTR_OPACITY_DEFAULT;
        private String strokeColor = ATTR_STROKE_DEFAULT;
        private double strokeAlpha = ATTR_STROKE_OPACITY_DEFAULT;
        private double strokeWidth = ATTR_STROKE_WIDTH_DEFAULT;

        public Builder setAlpha(final double alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder setFillColor(final String fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public Builder setFillAlpha(final double fillAlpha) {
            this.fillAlpha = fillAlpha;
            return this;
        }

        public Builder setStrokeColor(final String strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Builder setStrokeAlpha(final double strokeAlpha) {
            this.strokeAlpha = strokeAlpha;
            return this;
        }

        public Builder setStrokeWidth(final double strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public StyleDefinitionImpl build() {
            return new StyleDefinitionImpl(alpha,
                                           fillColor,
                                           fillAlpha,
                                           strokeColor,
                                           strokeAlpha,
                                           strokeWidth);
        }
    }
}

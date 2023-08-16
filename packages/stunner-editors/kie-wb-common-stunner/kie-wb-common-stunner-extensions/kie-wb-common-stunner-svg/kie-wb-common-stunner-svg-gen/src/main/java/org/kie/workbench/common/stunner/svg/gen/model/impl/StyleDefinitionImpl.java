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


package org.kie.workbench.common.stunner.svg.gen.model.impl;

import java.util.stream.Stream;

import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;

public class StyleDefinitionImpl implements StyleDefinition {

    private Double alpha;
    private String fillColor;
    private Double fillAlpha;
    private String strokeColor;
    private Double strokeAlpha;
    private Double strokeWidth;
    private Double strokeDashArray[];
    private String fontFamily;
    private Double fontSize;

    private StyleDefinitionImpl(final Double alpha,
                                final String fillColor,
                                final Double fillAlpha,
                                final String strokeColor,
                                final Double strokeAlpha,
                                final Double strokeWidth,
                                final Double strokeDashArray[],
                                final String fontFamily,
                                final Double fontSize) {
        this.alpha = alpha;
        this.fillColor = fillColor;
        this.fillAlpha = fillAlpha;
        this.strokeColor = strokeColor;
        this.strokeAlpha = strokeAlpha;
        this.strokeWidth = strokeWidth;
        this.strokeDashArray = strokeDashArray;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }

    @Override
    public Double getAlpha() {
        return alpha;
    }

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public Double getFillAlpha() {
        return fillAlpha;
    }

    @Override
    public String getStrokeColor() {
        return strokeColor;
    }

    @Override
    public Double getStrokeWidth() {
        return strokeWidth;
    }

    @Override
    public Double getStrokeAlpha() {
        return strokeAlpha;
    }

    @Override
    public Double[] getStrokeDashArray() {
        return strokeDashArray;
    }

    @Override
    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    public Double getFontSize() {
        return fontSize;
    }

    @Override
    public StyleDefinition add(final StyleDefinition other) {
        if (null != other.getAlpha()) {
            alpha = other.getAlpha();
        }
        if (null != other.getFillAlpha()) {
            fillAlpha = other.getFillAlpha();
        }
        if (null != other.getFillColor()) {
            fillColor = other.getFillColor();
        }
        if (null != other.getStrokeAlpha()) {
            strokeAlpha = other.getStrokeAlpha();
        }
        if (null != other.getStrokeWidth()) {
            strokeWidth = other.getStrokeWidth();
        }
        if (null != other.getStrokeColor()) {
            strokeColor = other.getStrokeColor();
        }
        if (null != other.getStrokeDashArray()) {
            strokeDashArray = other.getStrokeDashArray();
        }
        if (null != other.getFontFamily()) {
            fontFamily = other.getFontFamily();
        }
        if (null != other.getFontSize()) {
            fontSize = other.getFontSize();
        }
        return this;
    }

    @Override
    public StyleDefinition copy() {
        return new StyleDefinitionImpl(alpha,
                                       fillColor,
                                       fillAlpha,
                                       strokeColor,
                                       strokeAlpha,
                                       strokeWidth,
                                       strokeDashArray,
                                       fontFamily,
                                       fontSize);
    }

    public static final class Builder {

        private Double alpha;
        private String fillColor;
        private Double fillAlpha;
        private String strokeColor;
        private Double strokeAlpha;
        private Double strokeWidth;
        private Double[] strokeDashArray;
        private String fontFamily;
        private Double fontSize;

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

        public Builder setFontFamily(final String family) {
            this.fontFamily = family;
            return this;
        }

        public Builder setFontSize(final double size) {
            this.fontSize = size;
            return this;
        }

        public Builder setStrokeDashArray(final String strokeDashArray) {
            String[] tempStrokeDashArray = strokeDashArray.split(",");
            this.strokeDashArray = Stream.of(tempStrokeDashArray).map(Double::valueOf).toArray(Double[]::new);
            return this;
        }

        public StyleDefinitionImpl build() {
            return new StyleDefinitionImpl(alpha,
                                           fillColor,
                                           fillAlpha,
                                           strokeColor,
                                           strokeAlpha,
                                           strokeWidth,
                                           strokeDashArray,
                                           fontFamily,
                                           fontSize
            );
        }
    }
}

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

import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitivePolicy;

public enum SVGPrimitivePolicies {

    NONE,
    SAME_COLOR,
    STROKE_COLOR;

    public static class Builder {

        public static SVGPrimitivePolicy build(SVGPrimitivePolicies policy) {
            switch (policy) {
                case SAME_COLOR:
                    return buildSameColorPolicy();
                case STROKE_COLOR:
                    return buildStrokeColorPolicy();
            }
            return buildNonePolicy();
        }

        public static SVGPrimitivePolicy buildNonePolicy() {
            return new NoneSVGPrimitivePolicy();
        }

        public static SVGPrimitivePolicy buildStrokeColorPolicy() {
            return new StrokeColorSVGPrimitivePolicy();
        }

        public static SVGPrimitivePolicy buildSameColorPolicy() {
            return new SameColorSVGPrimitivePolicy();
        }
    }

    public static class NoneSVGPrimitivePolicy implements SVGPrimitivePolicy {

        @Override
        public void accept(final SVGBasicShapeView svgView,
                           final Shape<?> shape) {
            // No changes to primitive.
        }
    }

    public static class StrokeColorSVGPrimitivePolicy implements SVGPrimitivePolicy {

        @Override
        public void accept(final SVGBasicShapeView svgView,
                           final Shape<?> shape) {
            shape.setFillColor(svgView.getStrokeColor());
            shape.setStrokeColor(svgView.getStrokeColor());
        }
    }

    public static class SameColorSVGPrimitivePolicy implements SVGPrimitivePolicy {

        @Override
        public void accept(final SVGBasicShapeView svgView,
                           final Shape<?> shape) {
            shape.setFillColor(svgView.getFillColor());
            shape.setStrokeColor(svgView.getStrokeColor());
        }
    }

}

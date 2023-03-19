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

package org.kie.workbench.common.stunner.core.client.shape.view;

import java.util.Map;
import java.util.Objects;

import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;

public interface HasTitle<T> {

    enum HorizontalAlignment {
        RIGHT,
        CENTER,
        LEFT
    }

    enum VerticalAlignment {
        TOP,
        MIDDLE,
        BOTTOM
    }

    enum ReferencePosition {
        INSIDE,
        OUTSIDE
    }

    enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    class Size {

        public enum SizeType {
            PERCENTAGE,
            RAW
        }

        private double height;
        private double width;
        private SizeType type;

        public Size(final double width, final double height, final SizeType type) {
            this.width = width;
            this.height = height;
            this.type = type;
        }

        public double getHeight() {
            return height;
        }

        public double getWidth() {
            return width;
        }

        public SizeType getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Size size = (Size) o;
            return Double.compare(size.height, height) == 0 &&
                    Double.compare(size.width, width) == 0 &&
                    type == size.type;
        }

        @Override
        public int hashCode() {

            return Objects.hash(height, width, type);
        }
    }

    T setTitleSizeConstraints(final Size sizeConstraints);

    T setTitlePosition(final VerticalAlignment verticalAlignment,
                       final HorizontalAlignment horizontalAlignment,
                       final ReferencePosition referencePosition,
                       final Orientation orientation);

    T setTitle(final String title);

    default void setTitleBackgroundColor(String color) {

    }

    T setMargins(final Map<Enum, Double> margins);

    T setTitleXOffsetPosition(final Double xOffset);

    T setTitleYOffsetPosition(final Double yOffset);

    T setTitleRotation(final double degrees);

    T setTitleAlpha(final double alpha);

    T setTitleFontFamily(final String fontFamily);

    T setTitleFontSize(final double fontSize);

    T setTitleFontColor(final String fillColor);

    T setTitleStrokeWidth(final double strokeWidth);

    String getTitleFontFamily();

    double getTitleFontSize();

    String getTitlePosition();

    String getOrientation();

    double getMarginX();

    String getFontPosition();

    String getFontAlignment();

    void batch();

    default T setTitleStrokeAlpha(final double alpha) {
        return (T) this;
    }

    default T setTitleWrapper(final TextWrapperStrategy strategy) {
        return (T) this;
    }

    T setTitleStrokeColor(final String color);

    T moveTitleToTop();

    default void setTitleBoundaries(final double width, final double height) {

    }
}

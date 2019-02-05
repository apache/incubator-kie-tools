/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;

public interface HasTitle<T> {

    enum Position {
        CENTER,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    T setTitle(final String title);

    T setTitlePosition(final Position position);

    T setTitleXOffsetPosition(final Double xOffset);

    T setTitleYOffsetPosition(final Double yOffset);

    T setTitleRotation(final double degrees);

    T setTitleAlpha(final double alpha);

    T setTitleFontFamily(final String fontFamily);

    T setTitleFontSize(final double fontSize);

    T setTitleFontColor(final String fillColor);

    T setTitleStrokeWidth(final double strokeWidth);

    default T setTitleStrokeAlpha(final double alpha) {
        return (T) this;
    }

    default T setTextWrapper(final TextWrapperStrategy strategy) {
        return (T) this;
    }

    T setTitleStrokeColor(final String color);

    T moveTitleToTop();
}

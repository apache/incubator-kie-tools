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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttributes;

public class ShapeStateAttributesFactory {

    static final String COLOR_SELECTED = "#0066CC";
    static final String COLOR_HIGHLIGHT = "#4F5255";
    static final String COLOR_INVALID = "#FF0000";

    static final String FILL_COLOR_SELECTED = "#E7F1FA";
    static final String FILL_COLOR_HIGHLIGHT = "#fff";
    static final String FILL_COLOR_INVALID = "#fff";

    public static ShapeStateAttributes buildStateAttributes(final ShapeState state) {
        final String COLOR = getAttributeColorByState(state);
        final String FILL_COLOR = getFillAttributeColorByState(state);
        if (null == COLOR) {
            return buildAttributes();
        }

        return buildAttributes()
                .set(ShapeStateAttribute.STROKE_ALPHA, 1d)
                .set(ShapeStateAttribute.STROKE_COLOR, COLOR)
                .set(ShapeStateAttribute.FILL_COLOR, FILL_COLOR)
                .set(ShapeStateAttribute.FILL_ALPHA, 1d);
    }

    private static String getAttributeColorByState(final ShapeState state) {
        switch (state) {
            case SELECTED:
                return COLOR_SELECTED;
            case HIGHLIGHT:
                return COLOR_HIGHLIGHT;
            case INVALID:
                return COLOR_INVALID;
            default:
                return null;
        }
    }

    private static String getFillAttributeColorByState(final ShapeState state) {
        switch (state) {
            case SELECTED:
                return FILL_COLOR_SELECTED;
            case HIGHLIGHT:
                return FILL_COLOR_HIGHLIGHT;
            case INVALID:
                return FILL_COLOR_INVALID;
            default:
                return null;
        }
    }

    private static ShapeStateAttributes buildAttributes() {
        return new ShapeStateAttributes();
    }
}
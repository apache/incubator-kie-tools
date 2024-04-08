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


package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttributes;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;

public class ShapeStateAttributesFactory {

    public static ShapeStateAttributes buildStateAttributes(final ShapeState state) {
        final String COLOR = getAttributeColorByState(state);
        final String FILL_COLOR = getFillAttributeColorByState(state);
        if (null == COLOR) {
            return buildAttributes();
        }

        if (null == FILL_COLOR) {
            return buildAttributes()
                    .set(ShapeStateAttribute.STROKE_ALPHA, 1d)
                    .set(ShapeStateAttribute.STROKE_COLOR, COLOR);
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
                return StunnerTheme.getTheme().getShapeStrokeColorSelected();
            case HIGHLIGHT:
                return StunnerTheme.getTheme().getShapeStrokeColorHighlight();
            case INVALID:
                return StunnerTheme.getTheme().getShapeStrokeColorInvalid();
            default:
                return null;
        }
    }

    private static String getFillAttributeColorByState(final ShapeState state) {
        switch (state) {
            case SELECTED:
                return StunnerTheme.getTheme().getShapeFillColorSelected();
            case INVALID:
                return StunnerTheme.getTheme().getShapeFillColorInvalid();
            default:
                return null;
        }
    }

    private static ShapeStateAttributes buildAttributes() {
        return new ShapeStateAttributes();
    }
}
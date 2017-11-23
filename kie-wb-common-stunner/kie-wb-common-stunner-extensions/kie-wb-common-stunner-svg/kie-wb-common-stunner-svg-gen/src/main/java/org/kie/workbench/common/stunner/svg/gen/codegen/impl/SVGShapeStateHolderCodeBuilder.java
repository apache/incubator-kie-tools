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

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewShapeStateDefinition;

public class SVGShapeStateHolderCodeBuilder {

    private static final String BUILDER_START = "final SVGShapeStateHolder %1s = new SVGShapeStateHolder.Builder(ShapeState.%1s)";
    private static final String BUILDER_ALPHA = ".alpha(%1sd)";
    private static final String BUILDER_FILL_COLOR = ".fillColor(\"%1s\")";
    private static final String BUILDER_FILL_ALPHA = ".fillAlpha(%1sd)";
    private static final String BUILDER_STROKE_COLOR = ".strokeColor(\"%1s\")";
    private static final String BUILDER_STROKE_ALPHA = ".strokeAlpha(%1sd)";
    private static final String BUILDER_STROKE_WIDTH = ".strokeWidth(%1sd)";
    private static final String BUILDER_END = ".build();";
    private static final String VIEW_REGISTER = "%1s.getShapeStateHandler().registerStateHolder(%1s);";

    public static String generateStateHolders(final String viewInstanceName,
                                              final ViewShapeStateDefinition shapeStateDefinition) {
        String result = "";
        if (null != shapeStateDefinition) {
            for (ShapeState state : ShapeState.values()) {
                final StyleDefinition styleDefinition = shapeStateDefinition.getStyleDefinition(state);
                if (null != styleDefinition) {
                    final String holdInstanceRaw = generateStateHolderInstance(state, styleDefinition);
                    final String holdRegistrationRaw = generateStateHolderViewRegistration(viewInstanceName,
                                                                                           getHolderInstanceName(state));
                    result += holdInstanceRaw + "\n"
                            + holdRegistrationRaw + "\n";
                }
            }
        }
        return result;
    }

    private static String generateStateHolderViewRegistration(final String viewInstanceName,
                                                              final String stateHolderInstanceName) {
        return AbstractGenerator.formatString(VIEW_REGISTER,
                                              viewInstanceName,
                                              stateHolderInstanceName);
    }

    private static String generateStateHolderInstance(final ShapeState shapeState,
                                                      final StyleDefinition styleDefinition) {
        final String instanceId = getHolderInstanceName(shapeState);
        String result = AbstractGenerator.formatString(BUILDER_START,
                                                       instanceId,
                                                       shapeState.name());
        if (null != styleDefinition.getAlpha()) {
            result += AbstractGenerator.formatDouble(BUILDER_ALPHA,
                                                     styleDefinition.getAlpha());
        }
        if (null != styleDefinition.getFillColor()) {
            result += AbstractGenerator.formatString(BUILDER_FILL_COLOR,
                                                     styleDefinition.getFillColor());
        }
        if (null != styleDefinition.getFillAlpha()) {
            result += AbstractGenerator.formatDouble(BUILDER_FILL_ALPHA,
                                                     styleDefinition.getFillAlpha());
        }
        if (null != styleDefinition.getStrokeColor()) {
            result += AbstractGenerator.formatString(BUILDER_STROKE_COLOR,
                                                     styleDefinition.getStrokeColor());
        }
        if (null != styleDefinition.getStrokeAlpha()) {
            result += AbstractGenerator.formatDouble(BUILDER_STROKE_ALPHA,
                                                     styleDefinition.getStrokeAlpha());
        }
        if (null != styleDefinition.getStrokeWidth()) {
            result += AbstractGenerator.formatDouble(BUILDER_STROKE_WIDTH,
                                                     styleDefinition.getStrokeWidth());
        }
        result += BUILDER_END;
        return result;
    }

    private static String getStateName(final ShapeState shapeState) {
        return shapeState.name().toLowerCase();
    }

    private static String getHolderInstanceName(final ShapeState shapeState) {
        final String stateName = getStateName(shapeState);
        return stateName + "ShapeState";
    }
}

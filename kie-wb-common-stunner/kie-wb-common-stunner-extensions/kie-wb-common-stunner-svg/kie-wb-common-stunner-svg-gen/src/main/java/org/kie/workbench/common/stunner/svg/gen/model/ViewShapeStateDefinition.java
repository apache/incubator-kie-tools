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

package org.kie.workbench.common.stunner.svg.gen.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;

public final class ViewShapeStateDefinition {

    public static final String STATE_SELECTED = "shape-state-selected";
    public static final String STATE_HIGHLIGHT = "shape-state-highlight";
    public static final String STATE_INVALID = "shape-state-invalid";

    public static final Map<ShapeState, String> SHAPE_STATES_CSS_DECLARATIONS =
            new HashMap<ShapeState, String>(4) {{
                put(ShapeState.NONE, "");
                put(ShapeState.SELECTED, STATE_SELECTED);
                put(ShapeState.HIGHLIGHT, STATE_HIGHLIGHT);
                put(ShapeState.INVALID, STATE_INVALID);
            }};

    private final String selector;
    private final Map<ShapeState, StyleDefinition> stateStyleDefinitionMap;

    public ViewShapeStateDefinition(final String selector) {
        this.selector = selector;
        this.stateStyleDefinitionMap = new LinkedHashMap<>();
    }

    public String getSelector() {
        return selector;
    }

    public ViewShapeStateDefinition setStyleDefinition(final ShapeState shapeState,
                                                       final StyleDefinition styleDefinition) {
        stateStyleDefinitionMap.put(shapeState, styleDefinition);
        return this;
    }

    public StyleDefinition getStyleDefinition(final ShapeState shapeState) {
        return stateStyleDefinitionMap.get(shapeState);
    }

    public static ViewShapeStateDefinition build(final String svgId,
                                                 final StyleSheetDefinition styleSheetDefinition) {
        // Check for custom/global shape state definitions.
        final String selector = "." + svgId;
        final ViewShapeStateDefinition result = new ViewShapeStateDefinition(selector);
        SHAPE_STATES_CSS_DECLARATIONS.forEach((state, shapeStateSelector) ->
                                                      processStateClassDefinitions(state,
                                                                                   styleSheetDefinition,
                                                                                   selector,
                                                                                   shapeStateSelector,
                                                                                   result)

        );
        return result;
    }

    private static void processStateClassDefinitions(final ShapeState state,
                                                     final StyleSheetDefinition styleSheetDefinition,
                                                     final String elementSelector,
                                                     final String shapeStateSelector,
                                                     final ViewShapeStateDefinition result) {
        final String cssSelector = shapeStateSelector.trim().length() > 0 ?
                "." + shapeStateSelector : elementSelector;
        StyleDefinition style = styleSheetDefinition.getStyle(cssSelector);
        if (null != style) {
            result.setStyleDefinition(state,
                                      style);
        }
    }
}

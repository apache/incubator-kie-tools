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

package org.kie.workbench.common.stunner.sw.client.theme;

public class LightMode implements ColorTheme {

    private static ColorTheme instance;

    public static final String NAME = "light";

    // Canvas
    private static final String CANVAS_BACKGROUND_COLOR = "#f2f2f2";
    // Shape
    private static final String SHAPE_STROKE_COLOR = "#ccc";
    private static final String SHAPE_FILL_COLOR = "#fff";
    // Shape text
    private static final String SHAPE_TEXT_COLOR = "#383B3D";
    // Shape states
    private static final String SHAPE_STROKE_COLOR_SELECTED = "#0066CC";
    private static final String SHAPE_STROKE_COLOR_HIGHLIGHT = "#4F5255";
    private static final String SHAPE_STROKE_COLOR_INVALID = "#FF0000";
    private static final String SHAPE_FILL_COLOR_SELECTED = "#E7F1FA";
    private static final String SHAPE_FILL_COLOR_INVALID = "#fff";
    // State icon
    private static final String BACKGROUND_ICON_CIRCLE_STROKE_COLOR = "#D5D5D5";
    private static final String BACKGROUND_ICON_CIRCLE_FILL_COLOR = "#D5D5D5";
    private static final String ICON_PICTURE_STROKE_COLOR = "#FFF";
    private static final String ICON_PICTURE_FILL_COLOR = "#FFF";
    private static final String CALLBACK_STATE_ICON_FILL_COLOR = "#EC7A08";
    private static final String EVENT_STATE_ICON_FILL_COLOR = "#F4C145";
    private static final String FOREACH_STATE_ICON_FILL_COLOR = "#8F4700";
    private static final String INJECT_STATE_ICON_FILL_COLOR = "#8BC1F7";
    private static final String OPERATION_STATE_ICON_FILL_COLOR = "#0066CC";
    private static final String PARALLEL_STATE_ICON_FILL_COLOR = "#4CB140";
    private static final String SLEEP_STATE_ICON_FILL_COLOR = "#5752D1";
    private static final String SWITCH_STATE_ICON_FILL_COLOR = "#009596";
    // Static icons
    private static final String STATIC_ICON_FILL_COLOR = "#4F5255";
    // Corner icons with tooltip
    private static final String CORNER_ICON_FILL_COLOR = "#CCC";
    private static final String CORNER_ICON_HOVER_FILL_COLOR = "#4F5255";
    // Tooltip
    private static final String TOOLTIP_TEXT_COLOR = "#FFF";
    // Bottom text
    private static final String BOTTOM_TEXT_FILL_COLOR = "#4F5255";
    // Transitions
    private static final String START_TRANSITION_COLOR = "#757575";
    private static final String ERROR_TRANSITION_COLOR = "#c9190b";
    private static final String EVENT_CONDITION_TRANSITION_COLOR = "#828282";
    private static final String DATA_CONDITION_TRANSITION_COLOR = "#757575";
    private static final String DEFAULT_CONDITION_TRANSITION_COLOR = "#12DE70";
    private static final String COMPENSATION_TRANSITION_COLOR = "#f0ab00";
    // Transition text box
    private static final String EDGE_TEXT_FILL_COLOR = "white";
    private static final String EDGE_TEXT_STROKE_COLOR = "white";
    private static final String TRANSITION_BOX_COLOR = "gray";
    private static final String ERROR_TRANSITION_BOX_COLOR = "red";
    private static final String EVENT_CONDITION_TRANSITION_BOX_COLOR = "orange";

    private LightMode() {
    }

    public static ColorTheme getInstance() {
        if (null == instance) {
            instance = new LightMode();
        }

        return instance;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCanvasBackgroundColor() {
        return CANVAS_BACKGROUND_COLOR;
    }

    @Override
    public String getShapeStrokeColor() {
        return SHAPE_STROKE_COLOR;
    }

    @Override
    public String getShapeFillColor() {
        return SHAPE_FILL_COLOR;
    }

    @Override
    public String getShapeStrokeColorSelected() {
        return SHAPE_STROKE_COLOR_SELECTED;
    }

    @Override
    public String getShapeStrokeColorHighlight() {
        return SHAPE_STROKE_COLOR_HIGHLIGHT;
    }

    @Override
    public String getShapeStrokeColorInvalid() {
        return SHAPE_STROKE_COLOR_INVALID;
    }

    @Override
    public String getShapeFillColorSelected() {
        return SHAPE_FILL_COLOR_SELECTED;
    }

    @Override
    public String getShapeFillColorInvalid() {
        return SHAPE_FILL_COLOR_INVALID;
    }

    @Override
    public String getShapeTextColor() {
        return SHAPE_TEXT_COLOR;
    }

    @Override
    public String getTooltipTextColor() {
        return TOOLTIP_TEXT_COLOR;
    }

    @Override
    public String getBackgroundIconCircleStrokeColor() {
        return BACKGROUND_ICON_CIRCLE_STROKE_COLOR;
    }

    @Override
    public String getBackgroundIconCircleFillColor() {
        return BACKGROUND_ICON_CIRCLE_FILL_COLOR;
    }

    @Override
    public String getIconPictureStrokeColor() {
        return ICON_PICTURE_STROKE_COLOR;
    }

    @Override
    public String getIconPictureFillColor() {
        return ICON_PICTURE_FILL_COLOR;
    }

    @Override
    public String getStaticIconFillColor() {
        return STATIC_ICON_FILL_COLOR;
    }

    @Override
    public String getBottomTextFillColor() {
        return BOTTOM_TEXT_FILL_COLOR;
    }

    @Override
    public String getCornerIconFillColor() {
        return CORNER_ICON_FILL_COLOR;
    }

    @Override
    public String getCornerIconHoverFillColor() {
        return CORNER_ICON_HOVER_FILL_COLOR;
    }

    @Override
    public String getCallbackStateIconFillColor() {
        return CALLBACK_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getEventStateIconFillColor() {
        return EVENT_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getForeachStateIconFillColor() {
        return FOREACH_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getInjectStateIconFillColor() {
        return INJECT_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getOperationStateIconFillColor() {
        return OPERATION_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getParallelStateIconFillColor() {
        return PARALLEL_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getSleepStateIconFillColor() {
        return SLEEP_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getSwitchStateIconFillColor() {
        return SWITCH_STATE_ICON_FILL_COLOR;
    }

    @Override
    public String getStartTransitionColor() {
        return START_TRANSITION_COLOR;
    }

    @Override
    public String getErrorTransitionColor() {
        return ERROR_TRANSITION_COLOR;
    }

    @Override
    public String getEventConditionTransitionColor() {
        return EVENT_CONDITION_TRANSITION_COLOR;
    }

    @Override
    public String getDataConditionTransitionColor() {
        return DATA_CONDITION_TRANSITION_COLOR;
    }

    @Override
    public String getDefaultConditionTransitionColor() {
        return DEFAULT_CONDITION_TRANSITION_COLOR;
    }

    @Override
    public String getCompensationTransitionColor() {
        return COMPENSATION_TRANSITION_COLOR;
    }

    @Override
    public String getTransitionBoxColor() {
        return TRANSITION_BOX_COLOR;
    }

    @Override
    public String getErrorTransitionBoxColor() {
        return ERROR_TRANSITION_BOX_COLOR;
    }

    @Override
    public String getEventConditionTransitionBoxColor() {
        return EVENT_CONDITION_TRANSITION_BOX_COLOR;
    }

    @Override
    public String getEdgeTextFillColor() {
        return EDGE_TEXT_FILL_COLOR;
    }

    @Override
    public String getEdgeTextStrokeColor() {
        return EDGE_TEXT_STROKE_COLOR;
    }

    @Override
    public String getScrollbarColor() {
        return null;
    }

    @Override
    public String getScrollbarBackgroundColor() {
        return null;
    }

    @Override
    public boolean isDarkTheme() {
        return false;
    }
}

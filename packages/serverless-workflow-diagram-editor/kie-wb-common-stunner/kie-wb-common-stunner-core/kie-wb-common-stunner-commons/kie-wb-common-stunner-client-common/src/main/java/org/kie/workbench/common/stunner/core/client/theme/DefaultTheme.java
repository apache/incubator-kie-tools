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

package org.kie.workbench.common.stunner.core.client.theme;

public class DefaultTheme implements StunnerColorTheme {

    static StunnerColorTheme instance;

    // Shape states
    static final String SHAPE_STROKE_COLOR_SELECTED = "#0066CC";
    static final String SHAPE_STROKE_COLOR_HIGHLIGHT = "#4F5255";
    static final String SHAPE_STROKE_COLOR_INVALID = "#FF0000";
    static final String SHAPE_FILL_COLOR_SELECTED = "#E7F1FA";
    static final String SHAPE_FILL_COLOR_INVALID = "#fff";
    // Transition text box
    static final String EDGE_TEXT_FILL_COLOR = "white";
    static final String EDGE_TEXT_STROKE_COLOR = "white";
    //Canvas
    static final String CANVAS_BACKGROUND_COLOR = "white";

    private DefaultTheme() {
    }

    public static StunnerColorTheme getInstance() {
        if (null == instance) {
            instance = new DefaultTheme();
        }

        return instance;
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
    public String getCanvasBackgroundColor() {
        return null;
    }

    @Override
    public String getScrollbarBackgroundColor() {
        return CANVAS_BACKGROUND_COLOR;
    }

    @Override
    public boolean isDarkTheme() {
        return false;
    }
}

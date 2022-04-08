/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.api.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.uberfire.ext.layout.editor.api.css.CssAlignment.CENTER;
import static org.uberfire.ext.layout.editor.api.css.CssAlignment.LEFT;
import static org.uberfire.ext.layout.editor.api.css.CssAlignment.RIGHT;
import static org.uberfire.ext.layout.editor.api.css.CssFloat.INHERIT;
import static org.uberfire.ext.layout.editor.api.css.CssFloat.INITIAL;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.LARGE;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.MEDIUM;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.SMALL;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.XX_LARGE;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.XX_SMALL;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.X_LARGE;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.X_SMALL;
import static org.uberfire.ext.layout.editor.api.css.CssFontWeight.BOLDER;
import static org.uberfire.ext.layout.editor.api.css.CssFontWeight.LIGHTER;
import static org.uberfire.ext.layout.editor.api.css.CssFontWeight.NORMAL;
import static org.uberfire.ext.layout.editor.api.css.CssTextDecoration.LINE_THROUGH;
import static org.uberfire.ext.layout.editor.api.css.CssTextDecoration.NONE;
import static org.uberfire.ext.layout.editor.api.css.CssTextDecoration.OVERLINE;
import static org.uberfire.ext.layout.editor.api.css.CssTextDecoration.UNDERLINE;
import static org.uberfire.ext.layout.editor.api.css.CssValueType.LENGTH;
import static org.uberfire.ext.layout.editor.api.css.CssValueType.PERCENTAGE;

/**
 * A CSS property definition including what value types are supported and what is the list of allowed values available
 * by default (if any).
 *
 * <p>For example, the "color" property {@link #supportedValueTypes} is {@link CssValueType#COLOR}
 * and its {@link #allowedValues} is the entire list of {@link CssColour} entries.</p>
 *
 * <p>On the other hand, the "width" property {@link #supportedValueTypes} are {@link CssValueType#LENGTH} and {@link CssValueType#PERCENTAGE}
 * and its {@link #allowedValues} are "auto" or "inherit". That means the following values are all considered valid:
 * "100px", "auto", "50%".
 */
public enum CssProperty {

    WIDTH(LENGTH, PERCENTAGE),
    HEIGHT(LENGTH, PERCENTAGE),

    BACKGROUND_COLOR(CssValueType.COLOR),

    MARGIN_TOP(LENGTH),
    MARGIN_BOTTOM(LENGTH),
    MARGIN_LEFT(LENGTH),
    MARGIN_RIGHT(LENGTH),

    PADDING_TOP(LENGTH),
    PADDING_BOTTOM(LENGTH),
    PADDING_LEFT(LENGTH),
    PADDING_RIGHT(LENGTH),

    FLOAT(NONE, LEFT, RIGHT, INITIAL, INHERIT),
    TEXT_ALIGN(LEFT, CENTER, RIGHT),
    TEXT_DECORATION(NONE, UNDERLINE, OVERLINE, LINE_THROUGH),
    COLOR(CssValueType.COLOR),

    FONT_SIZE(XX_SMALL, X_SMALL, SMALL, MEDIUM, LARGE, X_LARGE, XX_LARGE),
    FONT_WEIGHT(NORMAL, BOLDER, LIGHTER),

    // Adding only for support on layout editor - this needs to be changed
    BACKGROUND(),
    BACKGROUND_ATTACHMENT(),
    BACKGROUND_IMAGE(),
    BACKGROUND_POSITION(),
    BACKGROUND_POSITION_X(),
    BACKGROUND_POSITION_Y(),
    BACKGROUND_REPEAT(),
    BEHAVIOR(),
    BORDER(),
    BORDER_BOTTOM(),
    BORDER_BOTTOM_COLOR(),
    BORDER_BOTTOM_STYLE(),
    BORDER_BOTTOM_WIDTH(),
    BORDER_COLLAPSE(),
    BORDER_COLOR(),
    BORDER_LEFT(),
    BORDER_LEFT_COLOR(),
    BORDER_LEFT_STYLE(),
    BORDER_WIDTH(),
    BORDER_LEFT_WIDTH(),
    BORDER_RIGHT(),
    BORDER_RIGHT_COLOR(),
    BORDER_RIGHT_STYLE(),
    BORDER_RIGHT_WIDTH(),
    BORDER_SPACING(),
    BORDER_STYLE(),
    BORDER_TOP(),
    BORDER_TOP_COLOR(),
    BORDER_TOP_STYLE(),
    BORDER_TOP_WIDTH(),
    BOTTOM(),
    BOX_SIZING(),
    CAPTION_SIDE(),
    CLEAR(),
    CLIP(),
    CONTENT(),
    COUNTER_INCREMENT(),
    COUNTER_RESET(),
    CURSOR(),
    DIRECTION(),
    DISPLAY(),
    EMPTY_CELLS(),
    FONT(),
    FONT_FAMILY(),
    FONT_SIZE_ADJUST(),
    FONT_STRETCH(),
    FONT_STYLE(),
    FONT_VARIANT(),
    IME_MODE(),
    LAYOUT_FLOW(),
    LAYOUT_GRID(),
    LAYOUT_GRID_CHAR(),
    LAYOUT_GRID_LINE(),
    LAYOUT_GRID_MODE(),
    LAYOUT_GRID_TYPE(),
    LETTER_SPACING(),
    LINE_BREAK(),
    LIST_STYLE(),
    LIST_STYLE_IMAGE(),
    LIST_STYLE_POSITION(),
    LIST_STYLE_TYPE(),
    MARGIN(),
    MARKER_OFFSET(),
    MARKS(),
    MAX_HEIGHT(),
    MAX_WIDTH(),
    MIN_HEIGHT(),
    MIN_WIDTH(),
    OPACITY(),
    ORPHANS(),
    OUTLINE(),
    OUTLINE_COLOR(),
    OUTLINE_OFFSET(),
    OUTLINE_STYLE(),
    OUTLINE_WIDTH(),
    OVERFLOW(),
    OVERFLOW_X(),
    OVERFLOW_Y(),
    PADDING(),
    PAGE(),
    PAGE_BREAK_AFTER(),
    PAGE_BREAK_BEFORE(),
    PAGE_BREAK_INSIDE(),
    POINTER_EVENTS(),
    POSITION(),
    RUBY_ALIGN(),
    RUBY_OVERHANG(),
    RUBY_POSITION(),
    SCROLLBAR3D_LIGHT_COLOR(),
    SCROLLBAR_ARROW_COLOR(),
    SCROLLBAR_BASE_COLOR(),
    SCROLLBAR_DARK_SHADOW_COLOR(),
    SCROLLBAR_FACE_COLOR(),
    SCROLLBAR_HIGHLIGHT_COLOR(),
    SCROLLBAR_SHADOW_COLOR(),
    SCROLLBAR_TRACK_COLOR(),
    SIZE(),
    TABLE_LAYOUT(),
    TEXT_ALIGN_LAST(),
    TEXT_AUTOSPACE(),
    TEXT_INDENT(),
    TEXT_JUSTIFY(),
    TEXT_JUSTIFY_TRIM(),
    TEXT_KASHIDA(),
    TEXT_KASHIDA_SPACE(),
    TEXT_OVERFLOW(),
    TEXT_SHADOW(),
    TEXT_TRANSFORM(),
    TEXT_UNDERLINE_POSITION(),
    TOP(),
    VERTICAL_ALIGN(),
    VISIBILITY(),
    WHITE_SPACE(),
    WIDOWS(),
    WORD_SPACING(),
    WORD_WRAP(),
    WRITING_MODE(),
    Z_INDEX(),
    ZOOM();

    private List<CssAllowedValue> allowedValues = new ArrayList<>();
    private List<CssValueType> supportedValueTypes = new ArrayList<>();

    private static List<String> ALL_NAMES = Arrays.stream(CssProperty.values())
                                                  .map(CssProperty::getName)
                                                  .collect(Collectors.toList());

    CssProperty() {
        // empty
    }

    CssProperty(CssValueType... supportedTypes) {
        for (CssValueType supportedValueType : supportedTypes) {
            supportedValueTypes.add(supportedValueType);
        }
    }

    CssProperty(CssAllowedValue... allowedValues) {
        for (CssAllowedValue value : allowedValues) {
            this.allowedValues.add(value);
        }
    }

    CssProperty(List<CssAllowedValue> allowedValues, CssValueType... supportedValueTypes) {
        this(supportedValueTypes);
        this.allowedValues = allowedValues;
    }

    public String getName() {
        return this.toString().toLowerCase().replace('_', '-');
    }

    public static CssProperty get(String property) {
        if (ALL_NAMES.contains(property)) {
            return valueOf(property.toUpperCase().replace('-', '_'));
        }
        return null;
    }

    public List<CssAllowedValue> getAllowedValues() {
        return allowedValues;
    }

    public boolean supportsValueType(CssValueType type) {
        return supportedValueTypes.contains(type);
    }

    public List<CssValueType> getSupportedValueTypes() {
        return supportedValueTypes;
    }
}

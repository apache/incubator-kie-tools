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

import static org.uberfire.ext.layout.editor.api.css.CssValueType.*;
import static org.uberfire.ext.layout.editor.api.css.CssAlignment.*;
import static org.uberfire.ext.layout.editor.api.css.CssTextDecoration.*;
import static org.uberfire.ext.layout.editor.api.css.CssFontWeight.*;
import static org.uberfire.ext.layout.editor.api.css.CssFontSize.*;

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

    TEXT_ALIGN(LEFT, CENTER, RIGHT),
    TEXT_DECORATION(NONE, UNDERLINE, OVERLINE, LINE_THROUGH),
    COLOR(CssValueType.COLOR),

    FONT_SIZE(XX_SMALL, X_SMALL, SMALL, MEDIUM, LARGE, X_LARGE, XX_LARGE),
    FONT_WEIGHT(NORMAL, BOLDER, LIGHTER);

    private List<CssAllowedValue> allowedValues = new ArrayList<>();
    private List<CssValueType> supportedValueTypes = new ArrayList<>();

    private static List<String> ALL_NAMES = Arrays.stream(CssProperty.values())
            .map(CssProperty::getName)
            .collect(Collectors.toList());

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

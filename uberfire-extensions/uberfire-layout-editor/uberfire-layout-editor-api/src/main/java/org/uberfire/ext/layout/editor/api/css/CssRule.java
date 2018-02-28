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

/**
 * A CSS rule
 */
public class CssRule extends ArrayList<CssValue> {

    private String selector;

    public CssRule(String selector) {
        super();
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    public CssValue setValue(CssProperty property, String value) {
        CssValue cssValue = getValue(property);
        if (cssValue == null) {
            if (value != null && value.length() > 0) {
                cssValue = new CssValue(property.getName(), value);
                this.add(cssValue);
                return cssValue;
            }
            return null;
        }
        else {
            if (value != null && value.length() > 0) {
                cssValue.setValue(value);
                return cssValue;
            } else {
                this.remove(cssValue);
                return null;
            }
        }
    }

    public CssValue getValue(CssProperty property) {
        for (CssValue cssValue : this) {
            if (cssValue.getProperty().equals(property.getName())) {
                return cssValue;
            }
        }
        return null;
    }

    public CssRule readValues(String ruleString) {
        this.clear();
        if (ruleString != null && ruleString.length() > 0) {
            for (String valuePair : ruleString.split(";")) {
                CssValue cssValue = new CssValue().readPair(valuePair);
                this.add(cssValue);
            }
        }
        return this;
    }

    public String formatValues() {
        StringBuilder out = new StringBuilder();
        forEach(item -> out.append(item.getProperty()).append(":").append(item.getValue()).append(";"));
        return out.toString();
    }

    public String toString() {
        StringBuffer out = new StringBuffer(selector);
        out.append(" {\n");
        this.forEach(v -> out.append("  ").append(v.getProperty()).append(":").append(v.getValue()).append(";\n"));
        out.append("}");
        return out.toString();
    }
}

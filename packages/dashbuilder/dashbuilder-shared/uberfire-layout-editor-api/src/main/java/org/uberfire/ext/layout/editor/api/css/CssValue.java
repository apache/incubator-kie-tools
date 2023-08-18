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

public class CssValue {

    private String property;
    private String value;

    public CssValue() {
    }

    public CssValue(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public String getPropertyInCamelCase() {
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i<property.length()) {
            String ch = property.substring(i, i+1);
            if (ch.equals("-") && i<property.length()-1) {
                ch = property.substring(++i, i+1);
                out.append(ch.toUpperCase());
            } else {
                out.append(ch);
            }
            i++;
        }
        return out.toString();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CssValue readPair(String valuePair) {
        int idx = valuePair.indexOf(":");
        if (idx > 0) {
            property = valuePair.substring(0, idx).trim();
            value = valuePair.substring(idx+1).trim();
        }
        return this;
    }
}

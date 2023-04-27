/*
 * Copyright 2018 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.layout.editor.client.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.layout.editor.api.css.CssAllowedValue;
import org.uberfire.ext.layout.editor.api.css.CssProperty;
import org.uberfire.ext.layout.editor.api.css.CssValue;

@ApplicationScoped
public class LayoutEditorCssHelper {
    
    public String formatCssAllowedValue(CssProperty property, CssAllowedValue value) {
        return property.toString() + "__" + value.toString();
    }

    public CssAllowedValue parseCssAllowedValue(String property, String value) {
        CssProperty cssProperty = CssProperty.get(property);
        if (cssProperty != null) {
            return parseCssAllowedValue(cssProperty, value);
        }
        return null;
    }

    public CssAllowedValue parseCssAllowedValue(CssProperty property, String value) {
        for (CssAllowedValue cssAllowedValue : property.getAllowedValues()) {
            String targetValue = formatCssAllowedValue(property, cssAllowedValue);
            if (value.equals(targetValue)) {
                return cssAllowedValue;
            }
        }
        return null;
    }

    public List<CssValue> readCssValues(Map<String, String> propertyMap) {
        if (propertyMap == null || propertyMap.isEmpty()) {
            return new ArrayList<>();
        }

        return propertyMap.entrySet().stream()
                .filter(entry -> CssProperty.get(entry.getKey()) != null)
                .map(entry -> new CssValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}

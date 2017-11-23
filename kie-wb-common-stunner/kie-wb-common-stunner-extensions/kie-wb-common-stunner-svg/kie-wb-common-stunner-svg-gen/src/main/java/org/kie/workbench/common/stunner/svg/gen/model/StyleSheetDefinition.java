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

import java.util.LinkedHashMap;
import java.util.Map;

public final class StyleSheetDefinition {

    private final String cssPath;
    private final Map<String, StyleDefinition> styleDefinitions;

    public StyleSheetDefinition(final String cssPath) {
        this.styleDefinitions = new LinkedHashMap<>();
        this.cssPath = cssPath;
    }

    public StyleSheetDefinition addStyle(final String name,
                                         final StyleDefinition styleDefinition) {
        styleDefinitions.put(name, styleDefinition);
        return this;
    }

    public StyleDefinition getStyle(final String name) {
        return styleDefinitions.get(name.trim());
    }

    public String getCssPath() {
        return cssPath;
    }
}

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


package org.kie.workbench.common.stunner.svg.gen.model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
        Arrays.stream(name.split(","))
                .forEach(n -> styleDefinitions.put(n.trim(), styleDefinition));
        return this;
    }

    public StyleDefinition getStyle(final String name) {
        return styleDefinitions.get(name.trim());
    }

    public StyleDefinition getStyle(final List<String> names) {
        StyleDefinition result = null;
        for (String name : names) {
            final StyleDefinition style = getStyle(name);
            if (null != style && null == result) {
                result = style.copy();
            } else if (null != style) {
                result.add(style.copy());
            }
        }
        return result;
    }

    public String getCssPath() {
        return cssPath;
    }
}

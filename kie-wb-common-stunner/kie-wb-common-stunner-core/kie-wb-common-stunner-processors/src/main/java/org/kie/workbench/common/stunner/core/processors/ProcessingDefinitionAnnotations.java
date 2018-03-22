/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProcessingDefinitionAnnotations {

    private final Map<String, String> baseTypes = new HashMap<>();
    private final Map<String, Set<String>> propertySetFieldNames = new HashMap<>();
    private final Map<String, Set<String>> propertyFieldNames = new HashMap<>();
    private final Map<String, String> graphFactoryFieldNames = new HashMap<>();
    private final Map<String, String> idFieldNames = new HashMap<>();
    private final Map<String, String> labelsFieldNames = new HashMap<>();
    private final Map<String, String> titleFieldNames = new HashMap<>();
    private final Map<String, String> categoryFieldNames = new HashMap<>();
    private final Map<String, String> descriptionFieldNames = new HashMap<>();
    private final Map<String, String> builderFieldNames = new HashMap<>();
    private final Map<String, String[]> shapeDefs = new HashMap<>();

    public Map<String, String> getBaseTypes() {
        return baseTypes;
    }

    public Map<String, Set<String>> getPropertySetFieldNames() {
        return propertySetFieldNames;
    }

    public Map<String, Set<String>> getPropertyFieldNames() {
        return propertyFieldNames;
    }

    public Map<String, String> getGraphFactoryFieldNames() {
        return graphFactoryFieldNames;
    }

    public Map<String, String> getIdFieldNames() {
        return idFieldNames;
    }

    public Map<String, String> getLabelsFieldNames() {
        return labelsFieldNames;
    }

    public Map<String, String> getTitleFieldNames() {
        return titleFieldNames;
    }

    public Map<String, String> getCategoryFieldNames() {
        return categoryFieldNames;
    }

    public Map<String, String> getDescriptionFieldNames() {
        return descriptionFieldNames;
    }

    public Map<String, String> getBuilderFieldNames() {
        return builderFieldNames;
    }

    public Map<String, String[]> getShapeDefinitions() {
        return shapeDefs;
    }
}

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


package org.kie.workbench.common.stunner.core.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.core.processors.definition.TypeConstructor;

public class ProcessingDefinitionAnnotations {

    private final Map<String, String> baseTypes = new HashMap<>();
    private final Map<String, List<String>> propertyFieldNames = new HashMap<>();
    private final Map<String, List<Boolean>> typedPropertyFields = new HashMap<>();
    private final Map<String, String> graphFactoryFieldNames = new HashMap<>();
    private final Map<String, String> idFieldNames = new HashMap<>();
    private final Map<String, String> labelsFieldNames = new HashMap<>();
    private final Map<String, String> titleFieldNames = new HashMap<>();
    private final Map<String, String> categoryFieldNames = new HashMap<>();
    private final Map<String, String> descriptionFieldNames = new HashMap<>();
    private final Map<String, TypeConstructor> builderFieldNames = new HashMap<>();
    private final Map<String, String[]> shapeDefs = new HashMap<>();

    public Map<String, String> getBaseTypes() {
        return baseTypes;
    }

    public Map<String, List<String>> getPropertyFieldNames() {
        return propertyFieldNames;
    }

    public Map<String, List<Boolean>> getTypedPropertyFields() {
        return typedPropertyFields;
    }

    public Map<String, String> getGraphFactory() {
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

    public Map<String, TypeConstructor> getBuilderFieldNames() {
        return builderFieldNames;
    }

    public Map<String, String[]> getShapeDefinitions() {
        return shapeDefs;
    }
}

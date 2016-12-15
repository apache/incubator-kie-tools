/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProcessingDefinitionSetAnnotations {

    private final Map<String, String> descriptionFieldNames = new HashMap<>();
    private final Set<String> definitionIds = new HashSet<>();
    private final Map<String, String> builderFieldNames = new HashMap<>();
    private final Map<String, String> graphTypes = new HashMap<>();
    private boolean hasShapeSet = false;

    public Map<String, String> getDescriptionFieldNames() {
        return descriptionFieldNames;
    }

    public Set<String> getDefinitionIds() {
        return definitionIds;
    }

    public Map<String, String> getBuilderFieldNames() {
        return builderFieldNames;
    }

    public Map<String, String> getGraphFactoryTypes() {
        return graphTypes;
    }

    public boolean hasShapeSet() {
        return hasShapeSet;
    }

    public void setHasShapeSet( boolean hasShapeSet ) {
        this.hasShapeSet = hasShapeSet;
    }
}

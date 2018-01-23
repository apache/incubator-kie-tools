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

public class ProcessingPropertyAnnotations {

    private final Map<String, String> valueFieldNames = new HashMap<>();
    private final Map<String, String> typeFieldNames = new HashMap<>();
    private final Map<String, String> types = new HashMap<>();
    private final Map<String, String> captionFieldNames = new HashMap<>();
    private final Map<String, String> descriptionFieldNames = new HashMap<>();
    private final Map<String, String> readOnlyFieldNames = new HashMap<>();
    private final Map<String, String> optionalFieldNames = new HashMap<>();
    private final Map<String, String> allowedValuesFieldNames = new HashMap<>();

    public Map<String, String> getValueFieldNames() {
        return valueFieldNames;
    }

    public Map<String, String> getTypes() {
        return types;
    }

    public Map<String, String> getTypeFieldNames() {
        return typeFieldNames;
    }

    public Map<String, String> getCaptionFieldNames() {
        return captionFieldNames;
    }

    public Map<String, String> getDescriptionFieldNames() {
        return descriptionFieldNames;
    }

    public Map<String, String> getReadOnlyFieldNames() {
        return readOnlyFieldNames;
    }

    public Map<String, String> getOptionalFieldNames() {
        return optionalFieldNames;
    }

    public Map<String, String> getAllowedValuesFieldNames() {
        return allowedValuesFieldNames;
    }
}

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.backend.converters.customproperties;

import org.eclipse.bpmn2.BaseElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ElementDefinition;

public abstract class CaseManagementElementDefinition<T> extends ElementDefinition<T> {

    public CaseManagementElementDefinition(String name, T defaultValue) {
        super(name, defaultValue);
    }
}

class CaseManagementBooleanElement extends CaseManagementElementDefinition<Boolean> {

    CaseManagementBooleanElement(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Boolean getValue(BaseElement element) {
        return getStringValue(element)
                .map(this::stripCData)
                .map(java.lang.Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, Boolean value) {
        setStringValue(element, String.valueOf(value));
    }
}

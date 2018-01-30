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

package org.kie.workbench.common.forms.integration.tests.valueprocessing;

public enum FormEngineFields implements FormFields {
    FORM_ENGINE_EDITED_OBJECT("__FormEngine-EditedObject", false, false, false),
    FORM_ENGINE_OBJECT_INDEX("__FormEngine-ObjectIndex", 0, 1, 2);

    private final String binding;
    private final Object
            firstLineValue,
            secondLineValue,
            thirdLineValue;

    @Override
    public String getBinding() {
        return binding;
    }

    @Override
    public Object getFirstLineValue() {
        return firstLineValue;
    }

    @Override
    public Object getSecondLineValue() {
        return secondLineValue;
    }

    @Override
    public Object getThirdLineValue() {
        return thirdLineValue;
    }

    @Override
    public Object getSubformValue() {
        throw new UnsupportedOperationException("FormEngineFields are not available for Subform.");
    }

    FormEngineFields(String label, Object firstLineValue, Object secondLineValue, Object thirdLineValue) {
        this.binding = label;
        this.firstLineValue = firstLineValue;
        this.secondLineValue = secondLineValue;
        this.thirdLineValue = thirdLineValue;
    }
}

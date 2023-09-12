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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;

public abstract class AbstractSliderFieldInitializer<FIELD extends SliderBaseDefinition<T>, T extends Number> implements FieldInitializer<FIELD> {

    @Override
    public void initialize(FIELD field,
                           FieldElement fieldElement,
                           FormGenerationContext context) {
        String min = fieldElement.getParams().get("min");

        T value = getValue(min);

        if (value != null) {
            field.setMin(value);
        }

        String max = fieldElement.getParams().get("max");

        value = getValue(max);

        if (value != null) {
            field.setMax(value);
        }

        String precision = fieldElement.getParams().get("precision");

        value = getValue(precision);

        if (value != null) {
            field.setPrecision(value);
        }

        String step = fieldElement.getParams().get("step");

        value = getValue(step);

        if (value != null) {
            field.setStep(value);
        }
    }

    private T getValue(String rawValue) {

        if (rawValue == null || rawValue.isEmpty()) {
            return null;
        }

        try {
            return parseValue(rawValue);
        } catch (Exception e) {

        }

        return null;
    }

    abstract T parseValue(String rawValue);
}

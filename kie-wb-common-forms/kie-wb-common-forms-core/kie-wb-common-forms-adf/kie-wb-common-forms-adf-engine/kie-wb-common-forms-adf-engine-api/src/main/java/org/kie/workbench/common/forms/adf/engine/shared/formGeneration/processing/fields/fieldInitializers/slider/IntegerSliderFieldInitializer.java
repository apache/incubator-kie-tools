/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.IntegerSliderDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Dependent
public class IntegerSliderFieldInitializer implements FieldInitializer<IntegerSliderDefinition> {

    @Override
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof IntegerSliderDefinition;
    }

    @Override
    public void initialize(IntegerSliderDefinition field,
                           FieldElement fieldElement,
                           FormGenerationContext context) {
        String min = fieldElement.getParams().get("min");

        Integer value = getValue(min);

        if (value != null) {
            field.setMin(value);
        }

        String max = fieldElement.getParams().get("max");

        value = getValue(max);

        if (value != null) {
            field.setMax(value);
        }

        String precission = fieldElement.getParams().get("precission");

        value = getValue(precission);

        if (value != null) {
            field.setPrecision(value);
        }

        String step = fieldElement.getParams().get("step");

        value = getValue(step);

        if (value != null) {
            field.setStep(value);
        }
    }

    private Integer getValue(String rawValue) {

        try {
            return Integer.valueOf(rawValue);
        } catch (Exception e) {

        }

        return null;
    }
}

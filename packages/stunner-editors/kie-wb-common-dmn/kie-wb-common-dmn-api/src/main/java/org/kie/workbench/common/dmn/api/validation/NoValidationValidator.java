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
package org.kie.workbench.common.dmn.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;

public class NoValidationValidator implements ConstraintValidator<NoValidation, DMNDefinition> {

    @Override
    public void initialize(final NoValidation constraintAnnotation) {
        //No initialization
    }

    @Override
    public boolean isValid(final DMNDefinition value,
                           final ConstraintValidatorContext context) {
        //Everything annotated with @NoValidation is valid!
        return true;
    }
}

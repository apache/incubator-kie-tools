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


package org.kie.workbench.common.stunner.bpmn.forms.validation.reassignment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;

public class ReassignmentValueValidator implements ConstraintValidator<ValidReassignmentValue, ReassignmentValue> {

    public static final String NOT_NEGATIVE = "The value should not be negative.";

    public static final String INVALID_CHARACTERS = "Period property contains invalid characters. Only positive number can be used as the value.";

    private String errorMessage;

    @Override
    public void initialize(ValidReassignmentValue constraintAnnotation) {

    }

    @Override
    public boolean isValid(ReassignmentValue value, ConstraintValidatorContext context) {
        isNegative(value.getDuration());
        isTooBig(value.getDuration());

        if (errorMessage != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private void isTooBig(String value) {
        if (value != null) {
            String duration = value.substring(0, value.length() - 1);
            try {
                Integer.valueOf(duration);
            } catch (NumberFormatException nfe) {
                errorMessage = INVALID_CHARACTERS;
            }
        }
    }

    private void isNegative(String value) {
        if (value != null && value.startsWith("-")) {
            errorMessage = NOT_NEGATIVE;
        }
    }
}

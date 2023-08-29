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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;

import static org.junit.Assert.assertEquals;

public class SubFormFieldDefinitionTest extends AbstractFieldDefinitionTest<SubFormFieldDefinition> {

    @Override
    protected SubFormFieldDefinition getEmptyFieldDefinition() {
        return new SubFormFieldDefinition();
    }

    @Override
    protected SubFormFieldDefinition getFullFieldDefinition() {
        SubFormFieldDefinition subFormFieldDefinition = new SubFormFieldDefinition();

        subFormFieldDefinition.setNestedForm("nestedForm");

        return subFormFieldDefinition;
    }

    @Test
    public void testValidation() {
        SubFormFieldDefinition fieldDefinition = getNewFieldDefinition();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<SubFormFieldDefinition>> violations = validator.validate(fieldDefinition);

        assertEquals(0,
                     violations.size());

        fieldDefinition.setNestedForm(null);
        assertEquals(0,
                     violations.size());

        fieldDefinition.setNestedForm("");
        assertEquals(0,
                     violations.size());
    }
}

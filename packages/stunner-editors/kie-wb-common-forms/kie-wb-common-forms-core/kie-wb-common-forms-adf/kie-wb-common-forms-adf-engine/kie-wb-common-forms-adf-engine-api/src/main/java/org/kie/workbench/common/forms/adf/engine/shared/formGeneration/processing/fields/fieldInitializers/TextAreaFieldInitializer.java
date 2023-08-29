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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Dependent
public class TextAreaFieldInitializer implements FieldInitializer<TextAreaFieldDefinition> {

    @Override
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof TextAreaFieldDefinition;
    }

    @Override
    public void initialize(TextAreaFieldDefinition field,
                           FieldElement fieldElement,
                           FormGenerationContext context) {
        String rows = fieldElement.getParams().getOrDefault("rows",
                                                            "3");

        Integer value = Integer.valueOf(rows);

        if (value != null) {
            field.setRows(value);
        }
    }
}

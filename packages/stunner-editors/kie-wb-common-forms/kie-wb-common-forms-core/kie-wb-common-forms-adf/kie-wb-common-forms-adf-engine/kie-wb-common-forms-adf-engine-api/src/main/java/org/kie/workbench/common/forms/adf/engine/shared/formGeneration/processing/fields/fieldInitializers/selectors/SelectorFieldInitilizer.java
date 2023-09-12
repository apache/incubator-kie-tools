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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.selectors;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Dependent
public class SelectorFieldInitilizer implements FieldInitializer<SelectorFieldBaseDefinition> {

    @Override
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof SelectorFieldBaseDefinition;
    }

    @Override
    public void initialize(SelectorFieldBaseDefinition selectorFieldBaseDefinition,
                           FieldElement fieldElement,
                           FormGenerationContext context) {
        String dataProvider = fieldElement.getParams().get(SelectorDataProvider.class.getName());

        if (dataProvider != null && !dataProvider.isEmpty()) {
            selectorFieldBaseDefinition.setDataProvider(dataProvider);
        }

        String relatedField = fieldElement.getParams().get("relatedField");

        if (relatedField != null) {
            selectorFieldBaseDefinition.setRelatedField(relatedField);
        }
    }
}

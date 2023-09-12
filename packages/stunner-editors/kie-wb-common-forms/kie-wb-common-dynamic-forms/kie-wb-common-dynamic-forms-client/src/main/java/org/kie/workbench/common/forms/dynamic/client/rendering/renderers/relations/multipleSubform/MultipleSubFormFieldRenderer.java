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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroup;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;

@Dependent
@Renderer(type = MultipleSubFormFieldType.class)
public class MultipleSubFormFieldRenderer extends FieldRenderer<MultipleSubFormFieldDefinition, FieldSetFormGroup> {

    static String RENDERER_NAME = "Multiple SubForm";

    @Inject
    private MultipleSubFormWidget multipleSubFormWidget;

    @Override
    public String getName() {
        return RENDERER_NAME;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        FieldSetFormGroup formGroup = formGroupsInstance.get();

        multipleSubFormWidget.config(field,
                                     renderingContext);

        formGroup.render(multipleSubFormWidget,
                         field);
        return formGroup;
    }

    @Override
    protected List<String> getConfigErrors() {
        List<String> configErrors = new ArrayList<>();

        if (field.getColumnMetas() == null || field.getColumnMetas().isEmpty()) {
            configErrors.add(FormRenderingConstants.MultipleSubformNoColumns);
        }
        if (field.getCreationForm() == null || field.getCreationForm().isEmpty()) {
            configErrors.add(FormRenderingConstants.MultipleSubformNoCreationForm);
        } else if (!renderingContext.getAvailableForms().containsKey(field.getCreationForm())) {
            configErrors.add(FormRenderingConstants.MultipleSubformWrongCreationForm);
        }
        if (field.getEditionForm() == null || field.getEditionForm().isEmpty()) {
            configErrors.add(FormRenderingConstants.MultipleSubformNoEditionForm);
        } else if (!renderingContext.getAvailableForms().containsKey(field.getEditionForm())) {
            configErrors.add(FormRenderingConstants.MultipleSubformWongEditionForm);
        }
        return configErrors;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        multipleSubFormWidget.setReadOnly(readOnly);
    }
}

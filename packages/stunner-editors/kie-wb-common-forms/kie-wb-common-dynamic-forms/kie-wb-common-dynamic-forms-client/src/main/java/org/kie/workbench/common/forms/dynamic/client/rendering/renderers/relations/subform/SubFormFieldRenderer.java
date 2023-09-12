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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.AbstractNestedFormFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.widget.SubFormWidget;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.Container;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;

@Dependent
@Renderer(type = SubFormFieldType.class)
public class SubFormFieldRenderer extends FieldRenderer<SubFormFieldDefinition, AbstractNestedFormFormGroup> {

    @Inject
    private SubFormWidget subFormWidget;

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        String nestedNS = renderingContext.getNamespace() + FormRenderingContext.NAMESPACE_SEPARATOR + field.getName();

        FormRenderingContext nestedContext = renderingContext.getCopyFor(nestedNS, field.getNestedForm(), null);

        if (field.getReadOnly()) {
            nestedContext.setRenderMode(RenderMode.READ_ONLY_MODE);
        }

        subFormWidget.render(nestedContext);

        AbstractNestedFormFormGroup formGroup;

        if (Container.COLLAPSIBLE.equals(field.getContainer())) {
            formGroup = formGroupsInstance.select(CollapsibleFormGroup.class).get();
        } else {
            formGroup = formGroupsInstance.select(FieldSetFormGroup.class).get();
        }

        formGroup.render(subFormWidget, field);

        return formGroup;
    }

    @Override
    protected List<String> getConfigErrors() {
        List<String> configErrors = new ArrayList<>();

        if (field.getNestedForm() == null || field.getNestedForm().isEmpty()) {
            configErrors.add(FormRenderingConstants.SubFormNoForm);
        } else if (!renderingContext.getAvailableForms().containsKey(field.getNestedForm())) {
            configErrors.add(FormRenderingConstants.SubFormWrongForm);
        }
        return configErrors;
    }

    @Override
    protected boolean isContentValid() {
        return subFormWidget.isValid();
    }

    @Override
    public String getName() {
        return "SubForm";
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        subFormWidget.setReadOnly(readOnly);
    }
}

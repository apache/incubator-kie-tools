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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormFieldImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.forms.processing.engine.handling.ValidationResult;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.formGroup.AssigneeFormGroup;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget.AssigneeEditorWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldType;

@Dependent
@Renderer(type = AssigneeEditorFieldType.class)
public class AssigneeEditorFieldRenderer extends FieldRenderer<AssigneeEditorFieldDefinition, AssigneeFormGroup> {

    private AssigneeEditorWidget widget;

    private TranslationService translationService;

    @Inject
    public AssigneeEditorFieldRenderer(final AssigneeEditorWidget assigneeEditor, TranslationService translationService) {
        this.widget = assigneeEditor;
        this.translationService = translationService;
    }

    @Override
    public String getName() {
        return AssigneeEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        AssigneeFormGroup formGroup = formGroupsInstance.get();

        widget.init(field.getType(), field.getMax());

        formGroup.render(widget.asWidget(), field);

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        widget.setReadOnly(readOnly);
    }

    @Override
    protected void registerCustomFieldValidators(FormFieldImpl field) {
        field.getCustomValidators().add((CustomFieldValidator<String>) value -> {
            String[] assignees = value.split(",");

            Set<String> assigneeSet = new TreeSet<>(Arrays.asList(assignees));

            if (assigneeSet.size() != assignees.length) {
                return ValidationResult.error(translationService.getTranslation(StunnerBPMNConstants.ASSIGNEE_WITH_DUPLICATES));
            }

            return ValidationResult.valid();
        });
    }
}

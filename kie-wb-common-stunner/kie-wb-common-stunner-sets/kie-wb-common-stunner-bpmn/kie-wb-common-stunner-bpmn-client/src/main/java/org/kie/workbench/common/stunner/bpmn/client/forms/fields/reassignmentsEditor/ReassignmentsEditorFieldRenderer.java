/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.ReassignmentsEditorFieldDefinition;

@Dependent
public class ReassignmentsEditorFieldRenderer extends FieldRenderer<ReassignmentsEditorFieldDefinition, DefaultFormGroup> {

    private ReassignmentsEditorWidget reassignmentsEditorWidget;

    @Inject
    public ReassignmentsEditorFieldRenderer(final ReassignmentsEditorWidget reassignmentsEditorWidget) {
        this.reassignmentsEditorWidget = reassignmentsEditorWidget;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        formGroup.render(reassignmentsEditorWidget, field);
        return formGroup;
    }

    @Override
    public String getName() {
        return ReassignmentsEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public String getSupportedCode() {
        return ReassignmentsEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        reassignmentsEditorWidget.setReadOnly(readOnly);
    }
}

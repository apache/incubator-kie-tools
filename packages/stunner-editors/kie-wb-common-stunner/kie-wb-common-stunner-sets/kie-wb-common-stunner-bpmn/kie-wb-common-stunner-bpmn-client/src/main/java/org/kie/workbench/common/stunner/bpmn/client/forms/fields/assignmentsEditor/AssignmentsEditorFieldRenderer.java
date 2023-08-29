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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.BPMNFormsContextUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssignmentsEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssignmentsEditorFieldType;

@Dependent
@Renderer(type = AssignmentsEditorFieldType.class)
public class AssignmentsEditorFieldRenderer extends FieldRenderer<AssignmentsEditorFieldDefinition, DefaultFormGroup> {

    private AssignmentsEditorWidget assignmentsEditor;

    @Inject
    public AssignmentsEditorFieldRenderer(final AssignmentsEditorWidget assignmentsEditor) {
        this.assignmentsEditor = assignmentsEditor;
    }

    @Override
    public String getName() {
        return AssignmentsEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        assignmentsEditor.setBPMNModel(null);
        Object model = BPMNFormsContextUtils.getModel(renderingContext);
        if (model instanceof BPMNDefinition) {
            assignmentsEditor.setBPMNModel((BPMNDefinition) model);
        }

        formGroup.render(assignmentsEditor, field);

        return formGroup;
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        assignmentsEditor.setReadOnly(readOnly);
    }
}

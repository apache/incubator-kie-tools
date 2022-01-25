/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionEditorFieldType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;

@Renderer(type = ConditionEditorFieldType.class)
public class ConditionEditorFieldEditorRenderer
        extends FieldRenderer<ConditionEditorFieldDefinition, DefaultFormGroup> {

    private final ConditionEditorFieldEditorWidget widget;
    private final SessionManager sessionManager;

    @Inject
    public ConditionEditorFieldEditorRenderer(final ConditionEditorFieldEditorWidget widget,
                                              final SessionManager sessionManager) {
        this.widget = widget;
        this.sessionManager = sessionManager;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        formGroup.render(widget,
                         field);
        return formGroup;
    }

    @Override
    public String getName() {
        return ConditionEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public void init(FormRenderingContext renderingContext,
                     ConditionEditorFieldDefinition field) {
        super.init(renderingContext,
                   field);
        widget.init(sessionManager.getCurrentSession());
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        widget.setReadOnly(readOnly);
    }
}

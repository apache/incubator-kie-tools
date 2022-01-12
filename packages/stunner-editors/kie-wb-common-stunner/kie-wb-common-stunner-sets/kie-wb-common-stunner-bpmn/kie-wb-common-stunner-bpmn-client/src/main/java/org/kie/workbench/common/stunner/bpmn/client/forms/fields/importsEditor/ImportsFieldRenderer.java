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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor;

import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.ImportsFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ImportsFieldType;

@Renderer(type = ImportsFieldType.class)
public class ImportsFieldRenderer
        extends FieldRenderer<ImportsFieldDefinition, DefaultFormGroup> {

    private final ImportsFieldEditorWidget widget;

    @Inject
    public ImportsFieldRenderer(final ImportsFieldEditorWidget widget) {
        this.widget = widget;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        formGroup.render(widget, field);
        return formGroup;
    }

    @Override
    public String getName() {
        return ImportsFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
    }
}
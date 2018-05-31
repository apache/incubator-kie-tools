/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor;

import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.TimerSettingsFieldDefinition;

public class TimerSettingsFieldRenderer
        extends FieldRenderer<TimerSettingsFieldDefinition, DefaultFormGroup> {

    private final TimerSettingsFieldEditorWidget widget;

    @Inject
    public TimerSettingsFieldRenderer(final TimerSettingsFieldEditorWidget widget) {
        this.widget = widget;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup  = formGroupsInstance.get();

        formGroup.render(widget, field);

        return formGroup;
    }

    @Override
    public String getName() {
        return TimerSettingsFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        widget.setReadOnly(readOnly);
    }

    @Override
    public String getSupportedCode() {
        return TimerSettingsFieldDefinition.FIELD_TYPE.getTypeName();
    }
}

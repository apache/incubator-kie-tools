/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.property.dmn;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.property.dmn.DecisionParametersListFieldType;
import org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.DecisionServiceParametersListWidget;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;

import static org.kie.workbench.common.dmn.client.property.dmn.DecisionParametersFieldDefinition.FIELD_TYPE;

@Dependent
@Renderer(type = DecisionParametersListFieldType.class)
public class DecisionParameterFieldRenderer extends FieldRenderer<DecisionParametersFieldDefinition, DefaultFormGroup> {

    private DecisionServiceParametersListWidget widget;

    public DecisionParameterFieldRenderer() {
        //CDI proxy
    }

    @Inject
    public DecisionParameterFieldRenderer(final DecisionServiceParametersListWidget widget) {
        this.widget = widget;
    }

    @Override
    protected FormGroup getFormGroup(final RenderMode renderMode) {
        widget.setEnabled(renderMode.equals(RenderMode.EDIT_MODE));

        final DefaultFormGroup formGroup = getFormGroupInstance();

        formGroup.render(widget, field);

        return formGroup;
    }

    DefaultFormGroup getFormGroupInstance() {
        return formGroupsInstance.get();
    }

    @Override
    public String getName() {
        return FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        widget.setEnabled(!readOnly);
    }
}

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

package org.kie.workbench.common.dmn.client.property.dmn;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;

@Dependent
public class DocumentationLinksFieldRenderer extends FieldRenderer<DocumentationLinksFieldDefinition, DefaultFormGroup> {

    private DocumentationLinksWidget widget;

    public DocumentationLinksFieldRenderer() {
        //CDI proxy
    }

    @Inject
    public DocumentationLinksFieldRenderer(final DocumentationLinksWidget widget) {
        this.widget = widget;
    }

    @Override
    public void init(final FormRenderingContext renderingContext,
                     final DocumentationLinksFieldDefinition field) {

        final Object model = renderingContext.getModel();
        if (model instanceof DRGElement) {
            widget.setDMNModel((DRGElement) model);
        }

        superInit(renderingContext, field);
    }

    void superInit(final FormRenderingContext renderingContext,
                   final DocumentationLinksFieldDefinition field) {
        super.init(renderingContext, field);
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
        return DocumentationLinksFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public String getSupportedCode() {
        return DocumentationLinksFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        widget.setEnabled(!readOnly);
    }
}

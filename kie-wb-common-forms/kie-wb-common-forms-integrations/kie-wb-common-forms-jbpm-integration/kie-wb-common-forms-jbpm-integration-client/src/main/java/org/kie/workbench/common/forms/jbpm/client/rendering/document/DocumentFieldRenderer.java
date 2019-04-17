/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.client.rendering.document;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;

@Dependent
public class DocumentFieldRenderer extends FieldRenderer<DocumentFieldDefinition, DefaultFormGroup> {

    private DocumentFieldRendererView view;

    @Inject
    public DocumentFieldRenderer(DocumentFieldRendererView view) {
        this.view = view;
    }

    @PostConstruct
    protected void doInit() {
        view.setRenderer(this);
    }

    @Override
    public String getName() {
        return "Document";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        view.setReadOnly(field.getReadOnly() || !renderingContext.getRenderMode().equals(RenderMode.EDIT_MODE));

        formGroup.render(view.asWidget(), field);

        return formGroup;
    }

    @Override
    public String getSupportedCode() {
        return DocumentFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }
}

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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.serviceEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.GenericServiceTaskEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.GenericServiceTaskEditorFieldType;

@Dependent
@Renderer(type = GenericServiceTaskEditorFieldType.class)
public class GenericServiceTaskEditorFieldRenderer extends FieldRenderer<GenericServiceTaskEditorFieldDefinition, DefaultFormGroup> {

    private GenericServiceTaskEditorWidget editorWidget;

    @Inject
    public GenericServiceTaskEditorFieldRenderer(final GenericServiceTaskEditorWidget editor) {
        this.editorWidget = editor;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        formGroup.render(editorWidget, field);
        return formGroup;
    }

    @Override
    public String getName() {
        return GenericServiceTaskEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        editorWidget.setReadOnly(readOnly);
    }
}

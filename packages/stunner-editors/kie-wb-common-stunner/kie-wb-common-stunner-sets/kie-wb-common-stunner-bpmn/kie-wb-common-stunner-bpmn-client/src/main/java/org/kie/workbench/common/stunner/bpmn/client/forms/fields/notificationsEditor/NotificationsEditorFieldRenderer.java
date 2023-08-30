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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.NotificationsEditorFieldType;

@Dependent
@Renderer(type = NotificationsEditorFieldType.class)
public class NotificationsEditorFieldRenderer extends FieldRenderer<NotificationsEditorFieldDefinition, DefaultFormGroup> {

    private NotificationsEditorWidget notificationsEditorWidget;

    @Inject
    public NotificationsEditorFieldRenderer(final NotificationsEditorWidget notificationsEditorWidget) {
        this.notificationsEditorWidget = notificationsEditorWidget;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        formGroup.render(notificationsEditorWidget, field);
        return formGroup;
    }

    @Override
    public String getName() {
        return NotificationsEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        notificationsEditorWidget.setReadOnly(readOnly);
    }
}

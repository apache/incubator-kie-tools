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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.kie.workbench.common.stunner.bpmn.forms.model.cm.RolesEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.cm.RolesEditorFieldType;
import org.kie.workbench.common.stunner.bpmn.forms.serializer.cm.CaseRoleSerializer;

@Dependent
@Renderer(type = RolesEditorFieldType.class)
public class RolesEditorFieldRenderer extends FieldRenderer<RolesEditorFieldDefinition, DefaultFormGroup>
        implements RolesEditorWidgetView.Presenter {

    private RolesEditorWidgetView view;
    private CaseRoleSerializer serializer;

    @Inject
    public RolesEditorFieldRenderer(RolesEditorWidgetView editor,
                                    CaseRoleSerializer serializer) {
        this.view = editor;
        this.serializer = serializer;
    }

    @Override
    public String getName() {
        return RolesEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();
        view.init(this);
        formGroup.render(view.asWidget(), field);
        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public List<KeyValueRow> deserialize(String value) {
        return serializer.deserialize(value, (k, v) -> new KeyValueRow(k, v));
    }

    @Override
    public String serialize(List<KeyValueRow> rows) {
        return serializer.serialize(Optional.ofNullable(rows), KeyValueRow::getKey, KeyValueRow::getValue);
    }
}
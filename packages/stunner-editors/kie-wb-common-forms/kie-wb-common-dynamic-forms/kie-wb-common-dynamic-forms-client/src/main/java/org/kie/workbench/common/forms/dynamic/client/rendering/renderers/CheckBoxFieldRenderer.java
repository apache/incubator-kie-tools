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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.SimpleCheckBox;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.checkbox.CheckBoxFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;

@Dependent
@Renderer(type = CheckBoxFieldType.class)
public class CheckBoxFieldRenderer extends FieldRenderer<CheckBoxFieldDefinition, CheckBoxFormGroup> {

    @Inject
    private SimpleCheckBox checkbox;

    @Override
    public String getName() {
        return "CheckBox";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        checkbox.setId(generateUniqueId());
        checkbox.setName(fieldNS);
        checkbox.setEnabled(!field.getReadOnly() && renderingContext.getRenderMode().equals(RenderMode.EDIT_MODE));

        CheckBoxFormGroup formGroup = formGroupsInstance.get();

        formGroup.render(checkbox, field);
        
        registerFieldRendererPart(checkbox);

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        checkbox.setEnabled(!readOnly);
    }
}

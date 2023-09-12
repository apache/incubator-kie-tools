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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapper;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;

@Dependent
@Renderer(type = DatePickerFieldType.class)
public class DatePickerFieldRenderer extends FieldRenderer<DatePickerFieldDefinition, DefaultFormGroup> {

    protected WidgetHandler handler;

    private DatePickerWrapper datePickerWrapper;

    @Inject
    public DatePickerFieldRenderer(DatePickerWrapper datePickerWrapper) {
        this.datePickerWrapper = datePickerWrapper;
    }

    @Override
    public String getName() {
        return "DatePicker";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();
        String inputId = generateUniqueId();
        datePickerWrapper.setDatePickerWidget(field.getShowTime());
        datePickerWrapper.setId(inputId);
        datePickerWrapper.setName(fieldNS);
        datePickerWrapper.setPlaceholder(field.getPlaceHolder());
        datePickerWrapper.setEnabled(!field.getReadOnly());
        handler = readOnly -> datePickerWrapper.setEnabled(!readOnly);

        if (renderMode.equals(RenderMode.READ_ONLY_MODE)) {
            datePickerWrapper.disableActions();
        }
        formGroup.render(inputId,
                         datePickerWrapper.asWidget(),
                         field);

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        handler.setReadOnly(readOnly);
    }

    protected interface WidgetHandler {

        void setReadOnly(boolean readOnly);
    }
}

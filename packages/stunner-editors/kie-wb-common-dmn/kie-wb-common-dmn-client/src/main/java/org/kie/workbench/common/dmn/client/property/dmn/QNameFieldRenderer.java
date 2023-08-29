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

package org.kie.workbench.common.dmn.client.property.dmn;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QNameFieldType;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;

@Dependent
@Renderer(type = QNameFieldType.class)
public class QNameFieldRenderer extends FieldRenderer<QNameFieldDefinition, DefaultFormGroup> {

    private DataTypePickerWidget typePicker;

    public QNameFieldRenderer() {
        //CDI proxy
    }

    @Inject
    public QNameFieldRenderer(final DataTypePickerWidget typePicker) {
        this.typePicker = typePicker;
    }

    //Required for Unit Testing
    void setFormGroup(final ManagedInstance<DefaultFormGroup> formGroupInstance) {
        this.formGroupsInstance = formGroupInstance;
    }

    @Override
    public void init(final FormRenderingContext renderingContext,
                     final QNameFieldDefinition field) {
        // Extract the DMNModelInstrumentedBase from the FormRenderingContext.
        Object model = renderingContext.getModel();
        if (Objects.isNull(model)) {
            model = renderingContext.getParentContext().getModel();
        }
        if (model instanceof DMNModelInstrumentedBase) {
            typePicker.setDMNModel((DMNModelInstrumentedBase) model);
        }
        superInit(renderingContext, field);
    }

    void superInit(final FormRenderingContext renderingContext,
                   final QNameFieldDefinition field) {
        super.init(renderingContext, field);
    }

    @Override
    public String getName() {
        return QNameFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(final RenderMode renderMode) {
        typePicker.setEnabled(renderMode.equals(RenderMode.EDIT_MODE));

        final DefaultFormGroup formGroup = formGroupsInstance.get();

        formGroup.render(typePicker, field);

        return formGroup;
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        typePicker.setEnabled(!readOnly);
    }
}

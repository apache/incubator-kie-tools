/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.client.editors.types.TypePickerWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;

@Dependent
public class QNameFieldRenderer extends FieldRenderer<QNameFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    private TypePickerWidget typePicker;
    private QNameFieldConverter qNameFieldConverter;

    public QNameFieldRenderer() {
        //CDI proxy
    }

    @Inject
    public QNameFieldRenderer(final TypePickerWidget typePicker,
                              final QNameFieldConverter qNameFieldConverter) {
        this.typePicker = typePicker;
        this.qNameFieldConverter = qNameFieldConverter;
    }

    //Required for Unit Testing
    void setFormGroup(final ManagedInstance<DefaultFormGroup> formGroupInstance) {
        this.formGroupsInstance = formGroupInstance;
    }

    @Override
    public void init(final FormRenderingContext renderingContext,
                     final QNameFieldDefinition field) {
        // Extract the DMNModelInstrumentedBase from the FormRenderingContext. Only the parent model is
        // available as Forms' SubFormFieldRenderer does not provide the model on the context. This should
        // be sufficient in all cases, as the InformationItem cannot have its NameSpace context set.
        final Object model = renderingContext.getParentContext().getModel();
        if (model instanceof DMNModelInstrumentedBase) {
            qNameFieldConverter.setDMNModel((DMNModelInstrumentedBase) model);
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
    public String getSupportedCode() {
        return QNameFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        typePicker.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return qNameFieldConverter;
    }
}

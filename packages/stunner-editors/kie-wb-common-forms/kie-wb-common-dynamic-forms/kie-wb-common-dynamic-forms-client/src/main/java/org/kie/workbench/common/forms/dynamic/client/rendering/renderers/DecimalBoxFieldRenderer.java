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

import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType;

@Dependent
@Renderer(type = DecimalBoxFieldType.class)
public class DecimalBoxFieldRenderer extends FieldRenderer<DecimalBoxFieldDefinition, DefaultFormGroup>
        implements RequiresValueConverter {

    private DecimalBox decimalBox;

    @Inject
    public DecimalBoxFieldRenderer(DecimalBox decimalBox) {
        this.decimalBox = decimalBox;
    }

    @Override
    public String getName() {
        return "DecimalBox";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            formGroup.render(new HTML(),
                             field);
        } else {
            String inputId = generateUniqueId();
            decimalBox.setId(inputId);
            decimalBox.setPlaceholder(field.getPlaceHolder());
            decimalBox.setMaxLength(field.getMaxLength());
            decimalBox.setEnabled(!field.getReadOnly());
            formGroup.render(inputId,
                             decimalBox.asWidget(),
                             field);
            registerFieldRendererPart(decimalBox);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        decimalBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
}

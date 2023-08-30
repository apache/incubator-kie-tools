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
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;

@Dependent
@Renderer(type = TextBoxFieldType.class)
public class TextBoxFieldRenderer extends FieldRenderer<TextBoxBaseDefinition, DefaultFormGroup> implements RequiresValueConverter {

    @Inject
    protected TextBox textBox;

    @Override
    public String getName() {
        return "TextBox";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            HTML html = new HTML();
            formGroup.render(html,
                             field);
        } else {
            String inputId = generateUniqueId();
            textBox.setName(fieldNS);
            textBox.setId(inputId);
            textBox.setPlaceholder(field.getPlaceHolder());
            textBox.setMaxLength(field.getMaxLength());
            textBox.setEnabled(!field.getReadOnly());
            
            registerFieldRendererPart(textBox);

            formGroup.render(inputId, textBox, field);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        textBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
    
}

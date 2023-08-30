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
import org.gwtbootstrap3.client.ui.TextArea;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ObjectToStringConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;

@Dependent
@Renderer(type = TextAreaFieldType.class)
public class TextAreaFieldRenderer extends FieldRenderer<TextAreaFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    @Inject
    private TextArea textArea;

    @Override
    public String getName() {
        return "TextArea";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            formGroup.render(new HTML(), field);
        } else {
            String inputId = generateUniqueId();

            textArea.setId(inputId);
            textArea.setName(fieldNS);
            textArea.setPlaceholder(field.getPlaceHolder());
            textArea.setVisibleLines(field.getRows());
            textArea.setEnabled(!field.getReadOnly());

            formGroup.render(inputId, textArea, field);

            registerFieldRendererPart(textArea);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        textArea.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return field.getStandaloneClassName().equals(Object.class.getName()) ? new ObjectToStringConverter() : null;
    }
}

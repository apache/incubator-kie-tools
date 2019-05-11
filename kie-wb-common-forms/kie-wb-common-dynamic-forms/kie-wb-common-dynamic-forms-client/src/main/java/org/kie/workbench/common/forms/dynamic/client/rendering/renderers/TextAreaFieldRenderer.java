/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.TextArea;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ObjectToStringConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;

@Dependent
public class TextAreaFieldRenderer extends FieldRenderer<TextAreaFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    @Override
    public String getName() {
        return "TextArea";
    }

    private TextArea textArea;

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            HTML html = new HTML();
            formGroup.render(html,
                             field);
        } else {
            String inputId = generateUniqueId();

            textArea = new TextArea();
            textArea.setId(inputId);
            textArea.setName(fieldNS);
            textArea.setPlaceholder(field.getPlaceHolder());
            textArea.setVisibleLines(field.getRows());
            textArea.setEnabled(!field.getReadOnly());
            textArea.setVisibleLines(field.getRows());

            formGroup.render(inputId, textArea, field);
            
            registerFieldRendererPart(textArea);
        }

        return formGroup;
    }

    @Override
    public String getSupportedCode() {
        return TextAreaFieldDefinition.FIELD_TYPE.getTypeName();
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

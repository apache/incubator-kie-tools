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

import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameFieldType;
import org.kie.workbench.common.dmn.client.editors.expressions.util.NameUtils;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;

@Dependent
@Renderer(type = NameFieldType.class)
public class NameFieldRenderer extends FieldRenderer<NameFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    private static final NameToStringConverter CONVERTER = new NameToStringConverter();

    protected TextBox textBox;

    @Inject
    public NameFieldRenderer(final TextBox textBox) {
        this.textBox = textBox;

        this.textBox.addChangeHandler(e -> {
            final String name = textBox.getValue();
            final String normalisedName = NameUtils.normaliseName(name);
            if (!Objects.equals(normalisedName, name)) {
                textBox.setValue(normalisedName);
            }
        });
    }

    @Override
    public String getName() {
        return NameFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(final RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            final HTML html = new HTML();
            formGroup.render(html, field);
        } else {
            final String inputId = generateUniqueId();
            textBox.setName(fieldNS);
            textBox.setId(inputId);
            textBox.setEnabled(!field.getReadOnly());

            registerFieldRendererPart(textBox);

            formGroup.render(inputId, textBox, field);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        textBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return CONVERTER;
    }

    private static final class NameToStringConverter implements Converter<Name, String> {

        @Override
        public Class<Name> getModelType() {
            return Name.class;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }

        @Override
        public Name toModelValue(final String componentValue) {
            return new Name(componentValue);
        }

        @Override
        public String toWidgetValue(final Name modelValue) {
            return modelValue.getValue();
        }
    }
}

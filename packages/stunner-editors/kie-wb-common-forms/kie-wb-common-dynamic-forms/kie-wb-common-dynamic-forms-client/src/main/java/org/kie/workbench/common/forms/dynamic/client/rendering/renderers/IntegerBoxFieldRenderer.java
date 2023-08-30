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
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType;

@Dependent
@Renderer(type = IntegerBoxFieldType.class)
public class IntegerBoxFieldRenderer extends FieldRenderer<IntegerBoxFieldDefinition, DefaultFormGroup>
        implements RequiresValueConverter {

    private IntegerBox integerBox;

    @Inject
    public IntegerBoxFieldRenderer(IntegerBox integerBox) {
        this.integerBox = integerBox;
    }

    @Override
    public String getName() {
        return "IntegerBox";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        Widget widget;

        String inputId = generateUniqueId();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            widget = new HTML();
            widget.getElement().setId(inputId);
        } else {
            integerBox.setId(inputId);
            integerBox.setPlaceholder(field.getPlaceHolder());
            integerBox.setMaxLength(field.getMaxLength());
            integerBox.setEnabled(!field.getReadOnly());
            widget = integerBox.asWidget();
        }

        DefaultFormGroup formGroup = formGroupsInstance.get();

        formGroup.render(inputId, widget, field);
        registerFieldRendererPart(widget);

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        integerBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
}

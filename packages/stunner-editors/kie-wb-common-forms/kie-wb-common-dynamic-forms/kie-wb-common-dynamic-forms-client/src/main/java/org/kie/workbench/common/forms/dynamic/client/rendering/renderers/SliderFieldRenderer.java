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

import com.google.gwt.i18n.client.NumberFormat;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.Slider;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.converters.IntegerToDoubleConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;

@Dependent
@Renderer(type = SliderFieldType.class)
public class SliderFieldRenderer extends FieldRenderer<SliderBaseDefinition, SliderFormGroup>
        implements RequiresValueConverter {

    private Slider slider;

    @Override
    public String getName() {
        return SliderBaseDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        slider = new Slider(field.getMin().doubleValue(),
                            field.getMax().doubleValue(),
                            field.getPrecision().doubleValue(),
                            field.getStep().doubleValue());

        slider.setId(generateUniqueId());
        slider.setEnabled(!field.getReadOnly() && renderingContext.getRenderMode().equals(RenderMode.EDIT_MODE));

        int precision = field.getPrecision().intValue();
        NumberFormat format = createFormatter(precision);
        slider.setFormatter((Double value) -> format.format(value));

        SliderFormGroup formGroup = formGroupsInstance.get();

        formGroup.render(slider,
                         field);
        
        registerFieldRendererPart(slider);

        return formGroup;
    }

    private NumberFormat createFormatter(int precision) {
        String pattern = "0";
        if (precision > 0) {
            pattern += ".";
            while (precision > 0) {
                pattern += "0";
                precision--;
            }
        }
        return NumberFormat.getFormat(pattern);
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        slider.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        if (field.getStandaloneClassName() == Integer.class.getName()
                || field.getStandaloneClassName() == "int") {
            return new IntegerToDoubleConverter();
        }
        return null;
    }
}

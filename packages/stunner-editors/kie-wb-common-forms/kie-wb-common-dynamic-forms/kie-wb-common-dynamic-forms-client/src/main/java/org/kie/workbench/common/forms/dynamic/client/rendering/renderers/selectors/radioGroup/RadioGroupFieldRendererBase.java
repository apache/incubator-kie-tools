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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup;

import java.util.Map;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.Radio;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup.RadioGroupBase;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.SelectorFieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.RadioGroupBaseDefinition;

public abstract class RadioGroupFieldRendererBase<FIELD extends RadioGroupBaseDefinition<OPTION, TYPE>, OPTION extends SelectorOption<TYPE>, TYPE>
        extends SelectorFieldRenderer<FIELD, OPTION, TYPE>
        implements RequiresValueConverter {

    private RadioGroupBase<TYPE> input;

    abstract protected RadioGroupBase<TYPE> getRadioGroup();

    protected void refreshInput(Map<TYPE, String> optionsValues,
                                TYPE selectedValue) {
        input.clear();
        for (TYPE key : optionsValues.keySet()) {
            Radio radio;
            SafeHtml text = getOptionLabel(optionsValues.get(key));
            if (field.getInline()) {
                radio = new InlineRadio(field.getId(),
                                        text);
            } else {
                radio = new Radio(field.getId(),
                                  text);
            }
            radio.setFormValue(key.toString());
            radio.setEnabled(!field.getReadOnly());
            input.add(radio);
        }

        if (optionsValues.containsKey(selectedValue)) {
            input.setValue(selectedValue,
                           true);
        }
        registerFieldRendererPart(input);
    }

    protected SafeHtml getOptionLabel(String text) {
        if (text == null || text.isEmpty()) {
            return SafeHtmlUtils.fromTrustedString("&nbsp;");
        }
        return SafeHtmlUtils.fromString(text);
    }

    @Override
    public String getName() {
        return "RadioGroup";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            formGroup.render(new HTML(),
                             field);
        } else {
            input = getRadioGroup();
            refreshSelectorOptions();
            formGroup.render(input,
                             field);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        input.getRadioChildren().forEach(radio -> radio.setEnabled(!readOnly));
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
}

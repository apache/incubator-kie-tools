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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup;

import java.util.Map;

import javax.enterprise.context.Dependent;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.Radio;
import org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup.StringRadioGroup;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;

@Dependent
public class StringRadioGroupFieldRenderer extends RadioGroupFieldRendererBase<StringRadioGroupFieldDefinition, StringSelectorOption, String> {

    private StringRadioGroup input;

    @Override
    public String getName() {
        return "RadioGroup";
    }

    @Override
    public void initInputWidget() {
        input = new StringRadioGroup(field.getName());
        refreshSelectorOptions();
    }

    @Override
    public IsWidget getInputWidget() {
        return input;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new HTML();
    }

    @Override
    public String getSupportedCode() {
        return StringRadioGroupFieldDefinition.FIELD_TYPE.getTypeName();
    }

    protected void refreshInput(Map<String, String> optionsValues,
                                String selectedValue) {
        input.clear();
        for (String key : optionsValues.keySet()) {
            Radio radio;
            SafeHtml text = getOptionLabel(optionsValues.get(key));
            if (field.getInline()) {
                radio = new InlineRadio(field.getId(),
                                        text);
            } else {
                radio = new Radio(field.getId(),
                                  text);
            }
            radio.setFormValue(key);
            radio.setEnabled(!field.getReadOnly());
            input.add(radio);
        }

        if (optionsValues.containsKey(selectedValue)) {
            input.setValue(selectedValue,
                           true);
        }
    }

    protected SafeHtml getOptionLabel(String text) {
        if (text == null || text.isEmpty()) {
            return SafeHtmlUtils.fromTrustedString("&nbsp;");
        }
        return SafeHtmlUtils.fromString(text);
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        input.getRadioChildren().forEach(radio -> radio.setEnabled(!readOnly));
    }

    @Override
    public Class<StringRadioGroupFieldDefinition> getSupportedFieldDefinition() {
        return StringRadioGroupFieldDefinition.class;
    }
}

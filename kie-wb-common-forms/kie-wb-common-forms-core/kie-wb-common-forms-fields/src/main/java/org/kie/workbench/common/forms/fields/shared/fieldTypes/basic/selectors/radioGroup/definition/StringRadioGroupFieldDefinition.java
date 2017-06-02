/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class StringRadioGroupFieldDefinition extends RadioGroupBaseDefinition<StringSelectorOption, String> {

    @FormField(
            labelKey = "selector.options",
            afterElement = "label"
    )
    protected List<StringSelectorOption> options = new ArrayList<>();

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.forms.editor.client.editor.dataProviders.SelectorOptionsProvider")
    @FormField(
            type = ListBoxFieldType.class,
            labelKey = "defaultValue",
            afterElement = "options",
            settings = {@FieldParam(name = "relatedField", value = "options")}
    )
    protected String defaultValue;

    public StringRadioGroupFieldDefinition() {
        super(String.class.getName());
    }

    @Override
    public List<StringSelectorOption> getOptions() {
        return options;
    }

    @Override
    public void setOptions(List<StringSelectorOption> options) {
        this.options = options;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}

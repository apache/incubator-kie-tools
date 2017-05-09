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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption;
import org.kie.workbench.common.forms.model.FieldDataType;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties"),
        startElement = "label"
)
public class EnumListBoxFieldDefinition extends ListBoxBaseDefinition<DefaultSelectorOption> {

    @FormField(
            labelKey = "selector.options",
            afterElement = "label"
    )
    protected List<DefaultSelectorOption> options = new ArrayList<>();

    public EnumListBoxFieldDefinition() {
        super(Object.class.getName());
    }

    @Override
    public List<DefaultSelectorOption> getOptions() {
        return options;
    }

    @Override
    public void setOptions(List<DefaultSelectorOption> options) {
        this.options = options;
    }

    @Override
    public FieldDataType getFieldTypeInfo() {
        return new FieldDataType(standaloneClassName,
                                 false,
                                 true);
    }
}

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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Column;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Layout;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties.selector.options"),
        layout = @Layout(value = {@Column, @Column}),
        startElement = "value"
)
public class CharacterSelectorOption implements SelectorOption<Character> {

    @FormField(
            labelKey = "value",
            required = true
    )
    @NotNull
    private Character value;

    @FormField(
            labelKey = "text",
            afterElement = "value",
            required = true
    )
    @NotEmpty
    @NotNull
    private String text;

    public CharacterSelectorOption() {
    }

    public CharacterSelectorOption(@MapsTo("value") Character value,
                                   @MapsTo("text") String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CharacterSelectorOption that = (CharacterSelectorOption) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

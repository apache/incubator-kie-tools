/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.adf.processors.models;

import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;

@FormDefinition(
        i18n = @I18nSettings(bundle = "test.properties", keyPreffix = "ChildI18n"),
        defaultFieldSettings = {@FieldParam(name = "param1", value = "value1")},
        startElement = "school"
)
public class Children extends AbstractPerson {

    @FormField
    private String school;

    @FormField(type = TextAreaFieldType.class, afterElement = "school")
    private String favouriteToys;

    @SkipFormField
    private String secret;

    public String getFavouriteToys() {
        return favouriteToys;
    }

    public void setFavouriteToys(String favouriteToys) {
        this.favouriteToys = favouriteToys;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

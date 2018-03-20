/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.UnSupportedFieldAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;

public class TextAreaFieldAdapter extends AbstractFieldAdapter implements UnSupportedFieldAdapter {

    @Override
    protected FieldDefinition getFieldDefinition(Field originalField) {
        TextAreaFieldDefinition fieldDefinition = new TextAreaFieldDefinition();
        if (!StringUtils.isEmpty(originalField.getHeight()) && StringUtils.isNumeric(originalField.getHeight())) {
            fieldDefinition.setRows(Integer.decode(originalField.getHeight()));
        }
        return fieldDefinition;
    }

    @Override
    public String[] getLegacyFieldTypeCodes() {
        return new String[]{FieldTypeBuilder.INPUT_TEXT_AREA};
    }

    @Override
    public String[] getLegacySupportedFieldTypeCodes() {
        return new String[]{FieldTypeBuilder.HTML_EDITOR};
    }

    @Override
    public String getNewFieldType() {
        return TextAreaFieldType.NAME;
    }
}

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

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.model.FieldDefinition;

public class IntegerTextFieldAdapter extends AbstractFieldAdapter {

    @Override
    protected FieldDefinition getFieldDefinition(Field originalField) {
        IntegerBoxFieldDefinition fieldDefinition = new IntegerBoxFieldDefinition();
        if (originalField.getMaxlength() != null) {
            fieldDefinition.setMaxLength(originalField.getMaxlength().intValue());
        }
        return fieldDefinition;
    }

    @Override
    public String[] getLegacyFieldTypeCodes() {
        return new String[]{FieldTypeBuilder.INPUT_TEXT_BIG_INTEGER, FieldTypeBuilder.INPUT_TEXT_BYTE,
                FieldTypeBuilder.INPUT_TEXT_PRIMITIVE_BYTE, FieldTypeBuilder.INPUT_TEXT_SHORT, FieldTypeBuilder.INPUT_TEXT_PRIMITIVE_SHORT,
                FieldTypeBuilder.INPUT_TEXT_INTEGER, FieldTypeBuilder.INPUT_TEXT_PRIMITIVE_INTEGER,
                FieldTypeBuilder.INPUT_TEXT_LONG, FieldTypeBuilder.INPUT_TEXT_PRIMITIVE_LONG};
    }
}

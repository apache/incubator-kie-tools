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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.BooleanMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.CharacterMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.DateMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.DecimalMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.IntegerMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.StringMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.model.FieldDefinition;

public class MultiplesValuesAdapter extends AbstractFieldAdapter {

    private Map<String, Supplier<FieldDefinition>> suppliers = new HashMap<>();

    public MultiplesValuesAdapter() {
        suppliers.put(Boolean.class.getName(), BooleanMultipleInputFieldDefinition::new);
        suppliers.put(Character.class.getName(), CharacterMultipleInputFieldDefinition::new);
        suppliers.put(String.class.getName(), StringMultipleInputFieldDefinition::new);
        suppliers.put(BigInteger.class.getName(), IntegerMultipleInputFieldDefinition::new);
        suppliers.put(Byte.class.getName(), IntegerMultipleInputFieldDefinition::new);
        suppliers.put(Integer.class.getName(), IntegerMultipleInputFieldDefinition::new);
        suppliers.put(Long.class.getName(), IntegerMultipleInputFieldDefinition::new);
        suppliers.put(Short.class.getName(), IntegerMultipleInputFieldDefinition::new);

        suppliers.put(BigDecimal.class.getName(), DecimalMultipleInputFieldDefinition::new);
        suppliers.put(Double.class.getName(), DecimalMultipleInputFieldDefinition::new);
        suppliers.put(Float.class.getName(), DecimalMultipleInputFieldDefinition::new);

        suppliers.put(Date.class.getName(), DateMultipleInputFieldDefinition::new);
        suppliers.put(LocalDate.class.getName(), DateMultipleInputFieldDefinition::new);
        suppliers.put(LocalDateTime.class.getName(), DateMultipleInputFieldDefinition::new);
        suppliers.put(LocalTime.class.getName(), DateMultipleInputFieldDefinition::new);
        suppliers.put(OffsetDateTime.class.getName(), DateMultipleInputFieldDefinition::new);
    }

    @Override
    protected FieldDefinition getFieldDefinition(Field originalField) {

        Supplier<FieldDefinition> supplier = suppliers.get(originalField.getBag());

        if (supplier != null) {
            return supplier.get();
        }

        return null;
    }

    @Override
    public String[] getLegacyFieldTypeCodes() {
        return new String[]{FieldTypeBuilder.MULTIPLE_INPUT};
    }
}

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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractDateMultipleFieldValueMarshaller<F extends FieldDefinition> extends AbstractFieldValueMarshaller<List<?>, List<Date>, F> {

    @Override
    public List<Date> toFlatValue() {

        if (originalValue == null) {
            return new ArrayList<>();
        }

        if (fieldDefinition.getStandaloneClassName().equals(Date.class.getName())) {
            return (List<Date>) originalValue;
        }

        return originalValue.stream()
                .map(rawValue -> getMarshaller(rawValue).toFlatValue())
                .collect(Collectors.toList());
    }

    @Override
    public List<?> toRawValue(List<Date> dates) {
        if (dates == null) {
            return new ArrayList<>();
        }

        if (fieldDefinition.getStandaloneClassName().equals(Date.class.getName())) {
            return dates;
        }

        return dates.stream()
                .map(flatValue -> getMarshaller(null).toRawValue(flatValue))
                .collect(Collectors.toList());
    }

    private LocalDateFieldValueMarshaller getMarshaller(Object rawValue) {
        LocalDateFieldValueMarshaller marshaller = new LocalDateFieldValueMarshaller();
        DatePickerFieldDefinition datePickerFieldDefinition = new DatePickerFieldDefinition();
        datePickerFieldDefinition.setStandaloneClassName(fieldDefinition.getStandaloneClassName());
        marshaller.init(rawValue, datePickerFieldDefinition, currentForm, context);
        return marshaller;
    }
}

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.AbstractFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.LocalDateConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.LocalDateTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.LocalTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.OffsetDateTimeConverter;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;

@Dependent
public class LocalDateFieldValueMarshaller extends AbstractFieldValueMarshaller<Object, Date, DatePickerFieldDefinition> {

    private Map<String, TimeConverter> converters = new HashMap<>();

    public LocalDateFieldValueMarshaller() {
        registerTimeConverter(new LocalDateConverter());
        registerTimeConverter(new LocalDateTimeConverter());
        registerTimeConverter(new LocalTimeConverter());
        registerTimeConverter(new OffsetDateTimeConverter());
    }

    public void registerTimeConverter(TimeConverter converter) {
        converters.put(converter.getType().getName(),
                       converter);
    }

    @Override
    public Date toFlatValue() {
        if (originalValue != null) {

            if (Date.class.getName().equals(fieldDefinition.getStandaloneClassName())) {
                return (Date) originalValue;
            }

            TimeConverter converter = converters.get(fieldDefinition.getStandaloneClassName());

            if (converter != null) {
                return converter.toFlatValue(originalValue);
            }
        }

        return null;
    }

    @Override
    public Object toRawValue(Date flatValue) {
        if (flatValue != null) {

            if (Date.class.getName().equals(fieldDefinition.getStandaloneClassName())) {
                return flatValue;
            }

            TimeConverter converter = converters.get(fieldDefinition.getStandaloneClassName());

            if (converter != null) {
                return converter.toRawValue(flatValue);
            }
        }

        return null;
    }

    @Override
    public Class<DatePickerFieldDefinition> getSupportedField() {
        return DatePickerFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<Object, Date, DatePickerFieldDefinition>> newInstanceSupplier() {
        return LocalDateFieldValueMarshaller::new;
    }

    public interface TimeConverter<T> {

        Class<T> getType();

        Date toFlatValue(T rawValue);

        T toRawValue(Date flatValue);
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time.converters.LocalDateConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time.converters.LocalDateTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time.converters.LocalTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time.converters.OffsetDateTimeConverter;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;

@Dependent
public class LocalDateFieldValueProcessor implements FieldValueProcessor<DatePickerFieldDefinition, Object, Date> {

    private Map<String, TimeConverter> converters = new HashMap<>();

    @PostConstruct
    public void init() {
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
    public Class<DatePickerFieldDefinition> getSupportedField() {
        return DatePickerFieldDefinition.class;
    }

    @Override
    public Date toFlatValue(DatePickerFieldDefinition field,
                            Object value,
                            BackendFormRenderingContext context) {
        if (value != null) {
            if (value instanceof Date) {
                return (Date) value;
            }

            TimeConverter converter = converters.get(field.getStandaloneClassName());
            if (converter != null) {
                return converter.toFlatValue(value);
            }
        }
        return null;
    }

    @Override
    public Object toRawValue(DatePickerFieldDefinition field,
                             Date flatValue,
                             Object originalValue,
                             BackendFormRenderingContext context) {

        if (flatValue == null || field.getStandaloneClassName().equals(Date.class.getName())) {
            return flatValue;
        }

        TimeConverter converter = converters.get(field.getStandaloneClassName());
        if (converter != null) {
            return converter.toRawValue(flatValue);
        }

        return null;
    }

    public interface TimeConverter<T> {

        Class<T> getType();

        Date toFlatValue(T rawValue);

        T toRawValue(Date flatValue);
    }
}
